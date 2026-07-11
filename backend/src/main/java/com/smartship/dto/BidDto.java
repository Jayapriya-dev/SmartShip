package com.smartship.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BidDto {

    @Data
    public static class PlaceBidRequest {

        @NotNull(message = "Request ID is required")
        private Long requestId;

        @NotNull(message = "Price is required")
        @DecimalMin(value = "1.0", message = "Price must be at least 1")
        private BigDecimal price;

        @NotNull(message = "Estimated delivery hours is required")
        @Min(value = 1, message = "Delivery time must be at least 1 hour")
        @Max(value = 720, message = "Delivery time cannot exceed 720 hours")
        private Integer estimatedDeliveryHours;

        @Size(max = 500, message = "Notes cannot exceed 500 characters")
        private String notes;
    }

    @Data
    public static class Response {
        private Long id;
        private Long requestId;
        private Long shipperId;
        private String shipperName;
        private BigDecimal price;
        private Integer estimatedDeliveryHours;
        private String notes;
        private String status;
        private boolean isLowest;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Data
    public static class BidListResponse {
        private Long requestId;
        private BigDecimal currentLowestPrice;
        private int totalBids;
        private java.util.List<Response> bids;
    }
}