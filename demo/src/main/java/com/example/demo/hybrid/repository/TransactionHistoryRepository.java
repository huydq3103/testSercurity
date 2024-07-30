package com.example.demo.hybrid.repository;

import com.example.demo.hybrid.entity.TransactionHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistoryEntity,Long> {

      List<TransactionHistoryEntity> findAllByTransactionId(String transactionId);
}
