package com.smartship.service;

import com.smartship.dto.TrackingDto;
import com.smartship.exception.ResourceNotFoundException;
import com.smartship.model.*;
import com.smartship.repository.OrderRepository;
import com.smartship.repository.ShipmentTrackingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShipmentTrackingService {

    @Autowired private ShipmentTrackingRepository trackingRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private AuthService authService;

    // Status display labels
    private static final Map<String, String> STATUS_LABELS = Map.of(
            "CONFIRMED",        "✅ Order Confirmed",
            "PICKED_UP",        "📦 Picked Up",
            "IN_TRANSIT",       "🚚 In Transit",
            "OUT_FOR_DELIVERY", "🏠 Out for Delivery",
            "DELIVERED",        "🎉 Delivered",
            "CANCELLED",        "❌ Cancelled"
    );

    // Status order for validation (can't go backwards)
    private static final List<String> STATUS_ORDER = List.of(
            "CONFIRMED", "PICKED_UP", "IN_TRANSIT", "OUT_FOR_DELIVERY", "DELIVERED"
    );

    /**
     * Shipper updates tracking status
     */
    @Transactional
    public TrackingDto.TrackingResponse updateTracking(Long orderId,
                                                       TrackingDto.UpdateRequest dto) {
        User shipper = authService.getCurrentUser();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // Only the assigned shipper can update
        if (!order.getShipper().getId().equals(shipper.getId())) {
            throw new IllegalArgumentException(
                    "Only the assigned shipper can update tracking for this order");
        }

        // Cannot update delivered/cancelled orders
        if (order.getStatus() == Order.OrderStatus.DELIVERED ||
                order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Cannot update tracking — order is already " + order.getStatus());
        }

        // Validate status value
        ShipmentTracking.TrackingStatus newStatus;
        try {
            newStatus = ShipmentTracking.TrackingStatus.valueOf(dto.getStatus().toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Invalid status. Valid values: PICKED_UP, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED, CANCELLED");
        }

        // Cannot set back to CONFIRMED (that is auto-set on order creation)
        if (newStatus == ShipmentTracking.TrackingStatus.CONFIRMED) {
            throw new IllegalArgumentException("Cannot manually set status to CONFIRMED");
        }

        // Validate status progression (no going backwards)
        String currentStatus = order.getStatus().name();
        int currentIdx = STATUS_ORDER.indexOf(currentStatus);
        int newIdx     = STATUS_ORDER.indexOf(dto.getStatus().toUpperCase());

        if (newIdx != -1 && currentIdx != -1 && newIdx <= currentIdx) {
            throw new IllegalArgumentException(
                    "Cannot move status backwards from " + currentStatus +
                            " to " + dto.getStatus().toUpperCase());
        }

        // Save tracking event
        ShipmentTracking tracking = new ShipmentTracking();
        tracking.setOrder(order);
        tracking.setStatus(newStatus);
        tracking.setLocation(dto.getLocation());
        tracking.setDescription(dto.getDescription() != null
                ? dto.getDescription()
                : STATUS_LABELS.get(newStatus.name()));
        tracking.setUpdatedBy(shipper);
        trackingRepository.save(tracking);

        // Update order status
        order.setStatus(Order.OrderStatus.valueOf(newStatus.name()));
        if (newStatus == ShipmentTracking.TrackingStatus.DELIVERED) {
            order.setDeliveredAt(java.time.LocalDateTime.now());
        }
        orderRepository.save(order);

        return getTracking(orderId);
    }

    /**
     * Get full tracking timeline for an order
     */
    @Transactional(readOnly = true)
    public TrackingDto.TrackingResponse getTracking(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        List<ShipmentTracking> events =
                trackingRepository.findByOrderOrderByCreatedAtAsc(order);

        TrackingDto.TrackingResponse response = new TrackingDto.TrackingResponse();
        response.setOrderId(orderId);
        response.setCurrentStatus(order.getStatus().name());
        response.setCurrentStatusLabel(
                STATUS_LABELS.getOrDefault(order.getStatus().name(), order.getStatus().name()));
        response.setShipperName(order.getShipper().getFullName());
        response.setCustomerName(order.getCustomer().getFullName());
        response.setPickupAddress(order.getRequest().getPickupAddress());
        response.setDropAddress(order.getRequest().getDropAddress());
        response.setItemDescription(order.getRequest().getItemDescription());

        List<TrackingDto.TrackingEvent> timeline = events.stream()
                .map(e -> {
                    TrackingDto.TrackingEvent event = new TrackingDto.TrackingEvent();
                    event.setId(e.getId());
                    event.setStatus(e.getStatus().name());
                    event.setStatusLabel(
                            STATUS_LABELS.getOrDefault(e.getStatus().name(), e.getStatus().name()));
                    event.setLocation(e.getLocation());
                    event.setDescription(e.getDescription());
                    event.setUpdatedByName(e.getUpdatedBy().getFullName());
                    event.setCreatedAt(e.getCreatedAt());
                    return event;
                })
                .collect(Collectors.toList());

        response.setTimeline(timeline);
        return response;
    }

    /**
     * Get all orders with tracking for current shipper
     */
    @Transactional(readOnly = true)
    public List<TrackingDto.TrackingResponse> getMyShipments() {
        User shipper = authService.getCurrentUser();
        return orderRepository.findByShipperOrderByConfirmedAtDesc(shipper)
                .stream()
                .map(o -> getTracking(o.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Get all orders with tracking for current customer
     */
    @Transactional(readOnly = true)
    public List<TrackingDto.TrackingResponse> getMyOrderTracking() {
        User customer = authService.getCurrentUser();
        return orderRepository.findByCustomerOrderByConfirmedAtDesc(customer)
                .stream()
                .map(o -> getTracking(o.getId()))
                .collect(Collectors.toList());
    }
}