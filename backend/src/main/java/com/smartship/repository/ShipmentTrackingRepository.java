package com.smartship.repository;

import com.smartship.model.Order;
import com.smartship.model.ShipmentTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShipmentTrackingRepository extends JpaRepository<ShipmentTracking, Long> {

    // Get all tracking events for an order, newest first
    List<ShipmentTracking> findByOrderOrderByCreatedAtDesc(Order order);

    // Get all tracking events oldest first (for timeline display)
    List<ShipmentTracking> findByOrderOrderByCreatedAtAsc(Order order);
}