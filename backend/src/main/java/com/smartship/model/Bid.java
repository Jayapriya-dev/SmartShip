package com.smartship.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@Entity
@Table(name = "bids")
@Data
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The transport request this bid is for
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "request_id", nullable = false)
    private TransportRequest request;

    // Shipper placing the bid
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shipper_id", nullable = false)
    private User shipper;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "estimated_delivery_hours", nullable = false)
    private Integer estimatedDeliveryHours;

    @Column(length = 500)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BidStatus status = BidStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum BidStatus {
        ACTIVE,    // Currently the lowest bid or competitive bid
        OUTBID,    // Someone placed a lower bid
        SELECTED,  // Customer chose this bid (winner)
        REJECTED,  // Customer rejected this bid
        WITHDRAWN  // Shipper withdrew their bid
    }
}
