package com.smartship.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransportRequestDto {

    @Data
    public static class CreateRequest {

        @NotBlank(message = "Pickup address is required")
        private String pickupAddress;

        @NotBlank(message = "Drop address is required")
        private String dropAddress;

        @NotBlank(message = "Item description is required")
        private String itemDescription;

        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;

        @DecimalMin(value = "0.1", message = "Weight must be positive")
        private BigDecimal weightKg;

        @DecimalMin(value = "0", message = "Budget must be positive")
        private BigDecimal budgetLimit;

        private LocalDateTime requiredBy;
    }

    @Data
    public static class Response {
        private Long id;
        private Long customerId;
        private String customerName;
        private String pickupAddress;
        private String dropAddress;
        private String itemDescription;
        private Integer quantity;
        private BigDecimal weightKg;
        private String status;
        private BigDecimal budgetLimit;
        private LocalDateTime requiredBy;
        private LocalDateTime createdAt;
        private Long bidCount;
        private BigDecimal lowestBid;
    }
}