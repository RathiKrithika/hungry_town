package com.bank.repository;

import com.bank.model.TransactionHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface TransactionRepository extends MongoRepository<TransactionHistory, String> {
    List<TransactionHistory> findByOccurredForOrderByTxnOccurredAtDesc(Long accountNum);

}
