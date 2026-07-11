package com.smartship.service;

import com.smartship.dto.TransportRequestDto;
import com.smartship.exception.ResourceNotFoundException;
import com.smartship.model.*;
import com.smartship.repository.BidRepository;
import com.smartship.repository.TransportRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransportRequestService {

    @Autowired private TransportRequestRepository requestRepository;
    @Autowired private BidRepository bidRepository;
    @Autowired private AuthService authService;

    @Transactional
    public TransportRequestDto.Response createRequest(TransportRequestDto.CreateRequest dto) {
        User customer = authService.getCurrentUser();

        if (customer.getRole().getName() != Role.RoleName.CUSTOMER) {
            throw new IllegalArgumentException("Only customers can post transport requests");
        }

        TransportRequest request = new TransportRequest();
        request.setCustomer(customer);
        request.setPickupAddress(dto.getPickupAddress());
        request.setDropAddress(dto.getDropAddress());
        request.setItemDescription(dto.getItemDescription());
        request.setQuantity(dto.getQuantity());
        request.setWeightKg(dto.getWeightKg());
        request.setBudgetLimit(dto.getBudgetLimit());
        request.setRequiredBy(dto.getRequiredBy());
        request.setStatus(TransportRequest.RequestStatus.OPEN);

        return toResponse(requestRepository.save(request));
    }

    @Transactional(readOnly = true)
    public List<TransportRequestDto.Response> getActiveRequests() {
        return requestRepository.findByStatusInOrderByCreatedAtDesc(
                Arrays.asList(
                        TransportRequest.RequestStatus.OPEN,
                        TransportRequest.RequestStatus.BIDDING
                )
        ).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransportRequestDto.Response> getMyRequests() {
        User customer = authService.getCurrentUser();
        return requestRepository.findByCustomerOrderByCreatedAtDesc(customer)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TransportRequestDto.Response getRequestById(Long id) {
        return toResponse(findById(id));
    }

    public TransportRequest findById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transport request not found with id: " + id));
    }

    private TransportRequestDto.Response toResponse(TransportRequest req) {
        TransportRequestDto.Response dto = new TransportRequestDto.Response();
        dto.setId(req.getId());
        dto.setCustomerId(req.getCustomer().getId());
        dto.setCustomerName(req.getCustomer().getFullName());
        dto.setPickupAddress(req.getPickupAddress());
        dto.setDropAddress(req.getDropAddress());
        dto.setItemDescription(req.getItemDescription());
        dto.setQuantity(req.getQuantity());
        dto.setWeightKg(req.getWeightKg());
        dto.setStatus(req.getStatus().name());
        dto.setBudgetLimit(req.getBudgetLimit());
        dto.setRequiredBy(req.getRequiredBy());
        dto.setCreatedAt(req.getCreatedAt());

        List<Bid> bids = bidRepository
                .findByRequestAndStatusNotOrderByPriceAsc(req, Bid.BidStatus.WITHDRAWN);
        dto.setBidCount((long) bids.size());
        if (!bids.isEmpty()) {
            dto.setLowestBid(bids.get(0).getPrice());
        }
        return dto;
    }
}