package com.food.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "banking",
        url = "http://localhost:8082")
public interface BankingClient {

    @PostMapping("/bankingproject/pay")
    void transfer(@RequestBody PaymentRequest request);
}
