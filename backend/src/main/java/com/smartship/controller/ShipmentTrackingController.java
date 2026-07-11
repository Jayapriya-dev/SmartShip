package com.smartship.controller;

import com.smartship.dto.TrackingDto;
import com.smartship.service.ShipmentTrackingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tracking")
@CrossOrigin(origins = "*")
public class ShipmentTrackingController {

    @Autowired
    private ShipmentTrackingService trackingService;

    /**
     * Shipper updates tracking status
     * POST /api/tracking/{orderId}
     */
    @PostMapping("/{orderId}")
    @PreAuthorize("hasRole('SHIPPER')")
    public ResponseEntity<TrackingDto.TrackingResponse> updateTracking(
            @PathVariable Long orderId,
            @Valid @RequestBody TrackingDto.UpdateRequest dto) {
        return ResponseEntity.ok(trackingService.updateTracking(orderId, dto));
    }

    /**
     * Anyone can view tracking for an order
     * GET /api/tracking/{orderId}
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<TrackingDto.TrackingResponse> getTracking(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(trackingService.getTracking(orderId));
    }

    /**
     * Shipper views all their shipments with tracking
     * GET /api/tracking/my/shipments
     */
    @GetMapping("/my/shipments")
    @PreAuthorize("hasRole('SHIPPER')")
    public ResponseEntity<List<TrackingDto.TrackingResponse>> getMyShipments() {
        return ResponseEntity.ok(trackingService.getMyShipments());
    }

    /**
     * Customer views all their orders with tracking
     * GET /api/tracking/my/orders
     */
    @GetMapping("/my/orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<TrackingDto.TrackingResponse>> getMyOrderTracking() {
        return ResponseEntity.ok(trackingService.getMyOrderTracking());
    }
}