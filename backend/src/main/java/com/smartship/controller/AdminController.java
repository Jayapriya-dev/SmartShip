package com.smartship.controller;

import com.smartship.dto.OrderDto;
import com.smartship.dto.TransportRequestDto;
import com.smartship.model.User;
import com.smartship.repository.OrderRepository;
import com.smartship.repository.TransportRequestRepository;
import com.smartship.repository.UserRepository;
import com.smartship.service.OrderService;
import com.smartship.service.TransportRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired private UserRepository userRepository;
    @Autowired private TransportRequestRepository requestRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private TransportRequestService requestService;
    @Autowired private OrderService orderService;

    // GET all users
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<Map<String, Object>> users = userRepository.findAll().stream()
                .map(u -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", u.getId());
                    map.put("username", u.getUsername());
                    map.put("fullName", u.getFullName());
                    map.put("email", u.getEmail());
                    map.put("phone", u.getPhone() != null ? u.getPhone() : "");
                    map.put("role", u.getRole().getName().name());
                    map.put("active", u.getIsActive());
                    map.put("createdAt", u.getCreatedAt() != null ? u.getCreatedAt().toString() : "");
                    return map;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TransportRequestDto.Response>> getAllRequests() {
        List<TransportRequestDto.Response> list = requestRepository.findAll().stream()
                .map(r -> requestService.getRequestById(r.getId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<OrderDto> list = orderRepository.findAll().stream()
                .map(o -> orderService.toOrderDtoPublic(o))
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
}