package com.customer.persistence.repository;

import com.customer.persistence.model.LendingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LendingHistoryRepository extends JpaRepository<LendingHistory, String> {
}
