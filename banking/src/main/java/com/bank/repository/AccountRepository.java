package com.bank.repository;

import com.bank.model.AccountOwner;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface AccountRepository extends MongoRepository<AccountOwner,Long> {

    AccountOwner findByAccountNumber(Long accNum);
}
