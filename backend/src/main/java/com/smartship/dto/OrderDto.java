package com.smartship.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for Order responses
 */
@Data
public class OrderDto {
    private Long id;
    private Long requestId;
    private String pickupAddress;
    private String dropAddress;
    private String itemDescription;
    private Long winningBidId;
    private Long customerId;
    private String customerName;
    private Long shipperId;
    private String shipperName;
    private BigDecimal finalPrice;
    private Integer estimatedDeliveryHours;
    private String status;
    private LocalDateTime confirmedAt;
    private LocalDateTime deliveredAt;
}
