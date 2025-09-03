package com.bank.controller;


import com.bank.dto.MoneyTransfer;
import com.bank.dto.PaymentRequest;
import com.bank.dto.Register;
import com.bank.service.RegisterAndTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/bank")
public class BankController {
    @Autowired
    RegisterAndTransactionService regAndTxnService;
    private static final Logger logger = LoggerFactory.getLogger(BankController.class);

    @PostMapping("/register")
    public ResponseEntity createAnAccount(@RequestBody Register register){
        return new ResponseEntity(regAndTxnService.registerAccount(register),HttpStatus.CREATED) ;
    }
    @PostMapping("/sendMoney")
    public ResponseEntity sendMoney(@RequestBody MoneyTransfer moneyTransfer){
        regAndTxnService.transferMoney(moneyTransfer);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/statement/{accNum}")
    public ResponseEntity getBankStatement(@PathVariable("accNum") Long accNum){
        return  new ResponseEntity(regAndTxnService.getBankStatement(accNum), HttpStatus.OK);
    }

    /*@GetMapping("/statement/by-date-range")
    public ResponseEntity getTransactionsBetweenDates(@RequestParam Long occurredFor,
   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {

return new ResponseEntity(regAndTxnService.getBankStatementBetweenDates(startDate, endDate, occurredFor),HttpStatus.OK) ;
    }*/

    @PostMapping("/pay")
    public ResponseEntity<String> processPayment(@RequestBody PaymentRequest paymentRequest) {
      regAndTxnService.makeFoodPayment(paymentRequest);
      return new ResponseEntity<>(null, HttpStatus.OK);
    }

}