package com.smartship.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

public class TrackingDto {

    @Data
    public static class UpdateRequest {
        @NotNull(message = "Status is required")
        private String status; // PICKED_UP, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED, CANCELLED

        private String location;

        private String description;
    }

    @Data
    public static class TrackingEvent {
        private Long id;
        private String status;
        private String statusLabel;
        private String location;
        private String description;
        private String updatedByName;
        private LocalDateTime createdAt;
    }

    @Data
    public static class TrackingResponse {
        private Long orderId;
        private String currentStatus;
        private String currentStatusLabel;
        private String shipperName;
        private String customerName;
        private String pickupAddress;
        private String dropAddress;
        private String itemDescription;
        private List<TrackingEvent> timeline;
    }
}