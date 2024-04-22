package com.customer.persistence.repository;

import com.customer.persistence.model.LendingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LendingHistoryRepository extends JpaRepository<LendingHistory, String> {
	boolean existsByBookIdAndCustomerId(UUID bookId, Integer customerId);
	Optional<LendingHistory> findLendingHistoryByBookIdAndCustomerId(UUID bookId, Integer customerId);
}
