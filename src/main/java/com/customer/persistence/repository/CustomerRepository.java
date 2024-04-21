package com.customer.persistence.repository;

import com.customer.persistence.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    Optional<Customer> getCustomerByCustomerIdAndCustomerName(Integer customerId, String customerName);
}
