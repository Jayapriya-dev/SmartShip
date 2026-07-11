package com.smartship.controller;

import com.smartship.dto.BidDto;
import com.smartship.service.BidService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bids")
@CrossOrigin(origins = "*")
public class BidController {

    @Autowired
    private BidService bidService;

    @PostMapping
    @PreAuthorize("hasRole('SHIPPER')")
    public ResponseEntity<BidDto.Response> placeBid(@Valid @RequestBody BidDto.PlaceBidRequest request) {
        return ResponseEntity.ok(bidService.placeBid(request));
    }

    @GetMapping("/request/{requestId}")
    public ResponseEntity<BidDto.BidListResponse> getBidsForRequest(@PathVariable Long requestId) {
        return ResponseEntity.ok(bidService.getBidsForRequest(requestId));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('SHIPPER')")
    public ResponseEntity<List<BidDto.Response>> getMyBids() {
        return ResponseEntity.ok(bidService.getMyBids());
    }
}
