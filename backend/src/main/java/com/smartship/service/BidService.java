package com.smartship.service;

import com.smartship.dto.BidDto;
import com.smartship.exception.ResourceNotFoundException;
import com.smartship.model.*;
import com.smartship.repository.BidRepository;
import com.smartship.repository.TransportRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BidService {

    @Autowired private BidRepository bidRepository;
    @Autowired private TransportRequestRepository requestRepository;
    @Autowired private AuthService authService;

    @Transactional
    public BidDto.Response placeBid(BidDto.PlaceBidRequest dto) {
        User shipper = authService.getCurrentUser();

        if (shipper.getRole().getName() != Role.RoleName.SHIPPER) {
            throw new IllegalArgumentException("Only shippers can place bids");
        }

        TransportRequest request = requestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Transport request not found"));

        if (request.getStatus() == TransportRequest.RequestStatus.AWARDED ||
                request.getStatus() == TransportRequest.RequestStatus.CANCELLED) {
            throw new IllegalArgumentException("This request is no longer accepting bids");
        }

        BigDecimal newPrice = dto.getPrice();
        int        newHours = dto.getEstimatedDeliveryHours();

        // Validate against current lowest bid
        List<Bid> activeBids = bidRepository.findLowestActiveBid(request);
        if (!activeBids.isEmpty()) {
            BigDecimal currentLowest = activeBids.get(0).getPrice();
            int        currentHours  = activeBids.get(0).getEstimatedDeliveryHours();
            int        priceCmp      = newPrice.compareTo(currentLowest);

            if (priceCmp > 0) {
                throw new IllegalArgumentException(
                        "Your price (₹" + newPrice + ") must be lower than " +
                                "current lowest bid (₹" + currentLowest + ")");
            }
            if (priceCmp == 0 && newHours >= currentHours) {
                throw new IllegalArgumentException(
                        "Same price (₹" + newPrice + ") — your delivery time (" +
                                newHours + " hrs) must be faster than current (" +
                                currentHours + " hrs) to take the lead");
            }
        }

        // Mark all active bids as OUTBID
        List<Bid> previouslyActive = bidRepository.findByRequestAndStatus(
                request, Bid.BidStatus.ACTIVE);
        for (Bid b : previouslyActive) {
            b.setStatus(Bid.BidStatus.OUTBID);
            bidRepository.save(b);
        }

        // Save new bid
        Bid newBid = new Bid();
        newBid.setRequest(request);
        newBid.setShipper(shipper);
        newBid.setPrice(newPrice);
        newBid.setEstimatedDeliveryHours(newHours);
        newBid.setNotes(dto.getNotes());
        newBid.setStatus(Bid.BidStatus.ACTIVE);
        Bid saved = bidRepository.save(newBid);

        if (request.getStatus() == TransportRequest.RequestStatus.OPEN) {
            request.setStatus(TransportRequest.RequestStatus.BIDDING);
            requestRepository.save(request);
        }

        return toBidResponse(saved, true);
    }

    @Transactional(readOnly = true)
    public BidDto.BidListResponse getBidsForRequest(Long requestId) {
        TransportRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Transport request not found"));

        List<Bid> bids = bidRepository.findByRequestOrderByPriceAsc(request);

        BigDecimal lowestPrice = bids.stream()
                .filter(b -> b.getStatus() == Bid.BidStatus.ACTIVE)
                .map(Bid::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(null);

        final BigDecimal finalLowest = lowestPrice;

        List<BidDto.Response> bidResponses = bids.stream()
                .map(b -> toBidResponse(b,
                        finalLowest != null
                                && b.getPrice().compareTo(finalLowest) == 0
                                && b.getStatus() == Bid.BidStatus.ACTIVE))
                .collect(Collectors.toList());

        BidDto.BidListResponse response = new BidDto.BidListResponse();
        response.setRequestId(requestId);
        response.setCurrentLowestPrice(lowestPrice);
        response.setTotalBids(bids.size());
        response.setBids(bidResponses);
        return response;
    }

    @Transactional(readOnly = true)
    public List<BidDto.Response> getMyBids() {
        User shipper = authService.getCurrentUser();
        return bidRepository.findByShipperOrderByCreatedAtDesc(shipper)
                .stream()
                .map(b -> toBidResponse(b, b.getStatus() == Bid.BidStatus.ACTIVE))
                .collect(Collectors.toList());
    }

    private BidDto.Response toBidResponse(Bid bid, boolean isLowest) {
        BidDto.Response dto = new BidDto.Response();
        dto.setId(bid.getId());
        dto.setRequestId(bid.getRequest().getId());
        dto.setShipperId(bid.getShipper().getId());
        dto.setShipperName(bid.getShipper().getFullName());
        dto.setPrice(bid.getPrice());
        dto.setEstimatedDeliveryHours(bid.getEstimatedDeliveryHours());
        dto.setNotes(bid.getNotes());
        dto.setStatus(bid.getStatus().name());
        dto.setLowest(isLowest);
        dto.setCreatedAt(bid.getCreatedAt());
        dto.setUpdatedAt(bid.getUpdatedAt());
        return dto;
    }
}