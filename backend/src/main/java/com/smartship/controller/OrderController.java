package com.smartship.controller;

import com.smartship.dto.OrderDto;
import com.smartship.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;


    @PostMapping("/select-bid/{bidId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderDto> selectWinningBid(@PathVariable Long bidId) {
        return ResponseEntity.ok(orderService.selectWinningBid(bidId));
    }


    @GetMapping("/my/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<OrderDto>> getMyOrdersAsCustomer() {
        return ResponseEntity.ok(orderService.getMyOrdersAsCustomer());
    }

    @GetMapping("/my/shipper")
    @PreAuthorize("hasRole('SHIPPER')")
    public ResponseEntity<List<OrderDto>> getMyOrdersAsShipper() {
        return ResponseEntity.ok(orderService.getMyOrdersAsShipper());
    }
}
