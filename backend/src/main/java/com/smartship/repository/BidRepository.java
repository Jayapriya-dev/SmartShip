package com.smartship.repository;

import com.smartship.model.Bid;
import com.smartship.model.TransportRequest;
import com.smartship.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {

    List<Bid> findByRequestOrderByPriceAsc(TransportRequest request);

    List<Bid> findByRequestAndStatusNotOrderByPriceAsc(
            TransportRequest request, Bid.BidStatus status);

    @Query("SELECT b FROM Bid b WHERE b.request = :request " +
            "AND b.status = 'ACTIVE' ORDER BY b.price ASC")
    List<Bid> findLowestActiveBid(@Param("request") TransportRequest request);

    List<Bid> findByShipperOrderByCreatedAtDesc(User shipper);

    Optional<Bid> findByRequestAndShipperAndStatus(
            TransportRequest request, User shipper, Bid.BidStatus status);

    List<Bid> findByRequestAndStatus(
            TransportRequest request, Bid.BidStatus status);
}