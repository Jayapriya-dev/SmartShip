package com.smartship.service;

import com.smartship.dto.OrderDto;
import com.smartship.exception.ResourceNotFoundException;
import com.smartship.model.*;
import com.smartship.repository.BidRepository;
import com.smartship.repository.OrderRepository;
import com.smartship.repository.TransportRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for creating and viewing orders (awarded bids)
 */
@Service
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private BidRepository bidRepository;
    @Autowired private TransportRequestRepository requestRepository;
    @Autowired private AuthService authService;

    /**
     * Customer selects a winning bid → creates an Order
     */
    @Transactional
    public OrderDto selectWinningBid(Long bidId) {
        User customer = authService.getCurrentUser();

        Bid winningBid = bidRepository.findById(bidId)
                .orElseThrow(() -> new ResourceNotFoundException("Bid not found"));

        TransportRequest request = winningBid.getRequest();

        // Validate: only the request owner can select a bid
        if (!request.getCustomer().getId().equals(customer.getId())) {
            throw new IllegalArgumentException("You can only award bids on your own requests");
        }

        // Validate: request must still be open
        if (request.getStatus() == TransportRequest.RequestStatus.AWARDED) {
            throw new IllegalArgumentException("A winner has already been selected for this request");
        }

        // Mark the winning bid
        winningBid.setStatus(Bid.BidStatus.SELECTED);
        bidRepository.save(winningBid);

        // Mark all other bids as REJECTED
        List<Bid> allBids = bidRepository.findByRequestOrderByPriceAsc(request);
        allBids.stream()
                .filter(b -> !b.getId().equals(bidId))
                .forEach(b -> {
                    b.setStatus(Bid.BidStatus.REJECTED);
                    bidRepository.save(b);
                });

        // Update request status to AWARDED
        request.setStatus(TransportRequest.RequestStatus.AWARDED);
        requestRepository.save(request);

        // Create the order
        Order order = new Order();
        order.setRequest(request);
        order.setWinningBid(winningBid);
        order.setCustomer(customer);
        order.setShipper(winningBid.getShipper());
        order.setFinalPrice(winningBid.getPrice());
        order.setStatus(Order.OrderStatus.CONFIRMED);

        return toOrderDto(orderRepository.save(order));
    }

    /**
     * Get order history for current customer
     */
    @Transactional(readOnly = true)
    public List<OrderDto> getMyOrdersAsCustomer() {
        User customer = authService.getCurrentUser();
        return orderRepository.findByCustomerOrderByConfirmedAtDesc(customer)
                .stream().map(this::toOrderDto).collect(Collectors.toList());
    }
    // Public method for admin access
    public OrderDto toOrderDtoPublic(com.smartship.model.Order order) {
        return toOrderDto(order);
    }
    /**
     * Get shipment history for current shipper
     */
    @Transactional(readOnly = true)
    public List<OrderDto> getMyOrdersAsShipper() {
        User shipper = authService.getCurrentUser();
        return orderRepository.findByShipperOrderByConfirmedAtDesc(shipper)
                .stream().map(this::toOrderDto).collect(Collectors.toList());
    }

    public OrderDto toOrderDto(Order order) {
        OrderDto dto = new OrderDto();
        dto.setId(order.getId());
        dto.setRequestId(order.getRequest().getId());
        dto.setPickupAddress(order.getRequest().getPickupAddress());
        dto.setDropAddress(order.getRequest().getDropAddress());
        dto.setItemDescription(order.getRequest().getItemDescription());
        dto.setWinningBidId(order.getWinningBid().getId());
        dto.setCustomerId(order.getCustomer().getId());
        dto.setCustomerName(order.getCustomer().getFullName());
        dto.setShipperId(order.getShipper().getId());
        dto.setShipperName(order.getShipper().getFullName());
        dto.setFinalPrice(order.getFinalPrice());
        dto.setEstimatedDeliveryHours(order.getWinningBid().getEstimatedDeliveryHours());
        dto.setStatus(order.getStatus().name());
        dto.setConfirmedAt(order.getConfirmedAt());
        dto.setDeliveredAt(order.getDeliveredAt());
        return dto;
    }
}
