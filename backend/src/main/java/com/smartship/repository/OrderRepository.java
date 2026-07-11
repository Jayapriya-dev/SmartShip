package com.smartship.repository;

import com.smartship.model.Order;
import com.smartship.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Get all orders for a customer
    List<Order> findByCustomerOrderByConfirmedAtDesc(User customer);

    // Get all orders for a shipper
    List<Order> findByShipperOrderByConfirmedAtDesc(User shipper);
}
