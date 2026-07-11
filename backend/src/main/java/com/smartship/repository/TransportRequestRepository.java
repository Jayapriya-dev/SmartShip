package com.smartship.repository;

import com.smartship.model.TransportRequest;
import com.smartship.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransportRequestRepository extends JpaRepository<TransportRequest, Long> {

    // Get all requests for a specific customer
    List<TransportRequest> findByCustomerOrderByCreatedAtDesc(User customer);

    // Get all open/bidding requests for shippers to view
    List<TransportRequest> findByStatusInOrderByCreatedAtDesc(
            List<TransportRequest.RequestStatus> statuses);

    // Get all requests with bid count (for dashboards)
    @Query("SELECT tr FROM TransportRequest tr WHERE tr.status IN ('OPEN', 'BIDDING') ORDER BY tr.createdAt DESC")
    List<TransportRequest> findActiveRequests();
}
