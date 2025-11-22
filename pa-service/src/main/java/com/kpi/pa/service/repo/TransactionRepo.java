package com.kpi.pa.service.repo;

import com.kpi.pa.service.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {
    
    
    List<Transaction> findAllByUser_IdAndCreatedAtBetween(
        Long userId, 
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
    
    List<Transaction> findByAccount_Id(Long accountId);
}