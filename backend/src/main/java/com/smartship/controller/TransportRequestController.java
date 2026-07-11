package com.smartship.controller;

import com.smartship.dto.TransportRequestDto;
import com.smartship.service.TransportRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Transport Request operations
 */
@RestController
@RequestMapping("/api/requests")
@CrossOrigin(origins = "*")
public class TransportRequestController {

    @Autowired
    private TransportRequestService requestService;

    /**
     * POST /api/requests
     * Customer posts a new transport request
     */
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<TransportRequestDto.Response> createRequest(
            @Valid @RequestBody TransportRequestDto.CreateRequest dto) {
        return ResponseEntity.ok(requestService.createRequest(dto));
    }

    /**
     * GET /api/requests/active
     * All active requests visible to shippers
     */
    @GetMapping("/active")
    public ResponseEntity<List<TransportRequestDto.Response>> getActiveRequests() {
        return ResponseEntity.ok(requestService.getActiveRequests());
    }

    /**
     * GET /api/requests/my
     * Customer's own requests
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<TransportRequestDto.Response>> getMyRequests() {
        return ResponseEntity.ok(requestService.getMyRequests());
    }

    /**
     * GET /api/requests/{id}
     * Single request details (visible to all authenticated users)
     */
    @GetMapping("/{id}")
    public ResponseEntity<TransportRequestDto.Response> getRequestById(@PathVariable Long id) {
        return ResponseEntity.ok(requestService.getRequestById(id));
    }
}
