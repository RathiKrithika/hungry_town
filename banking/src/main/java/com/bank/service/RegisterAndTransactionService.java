package com.bank.service;


import com.bank.dto.BankStatement;
import com.bank.dto.MoneyTransfer;
import com.bank.dto.PaymentRequest;
import com.bank.dto.Register;
import com.bank.exception.AccountNotFoundException;
import com.bank.exception.InsufficientBalanceException;
import com.bank.exception.MinimumBalanceException;
import com.bank.model.AccountOwner;
import com.bank.model.TransactionHistory;
import com.bank.model.TransactionType;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class RegisterAndTransactionService {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    TransactionRepository transactionRepository;


    @Transactional
    public Long registerAccount(Register reg)  {
        Double money =reg.getCreditAmount();
        if (money < 10000) {
            throw new MinimumBalanceException("Minimum balance must be ₹10,000 to create an account.");
        }
        //account creation
        AccountOwner accountOwner = accountRepository.save(new AccountOwner(reg.getName(), reg.getPhoneNumber()));
        Long accountNumber = accountOwner.getAccountNumber();
        //initial deposit capture
        transactionRepository.save( new TransactionHistory(accountNumber,null,money, TransactionType.DEPOSIT.toString(),money));
        return accountNumber;
    }


    @Transactional
    public void transferMoney(MoneyTransfer m) {

        Long fromAccount= m.getFromAccount();
        Long toAccount = m.getToAccount();
        Double moneyToTransfer = m.getMoneyToTransfer();

        if(checkAccountExistence(fromAccount))
            checkForSufficientBalance(fromAccount, m.getMoneyToTransfer());

        checkAccountExistence(toAccount);

        //transaction occurrence
        TransactionHistory receiver_last_txn = getTransactionHistory(toAccount).get(0);
        TransactionHistory sender_last_txn = getTransactionHistory(fromAccount).get(0);
        TransactionHistory receiver = new TransactionHistory(toAccount,fromAccount,receiver_last_txn.getClosingBalance()+moneyToTransfer,TransactionType.CREDIT.toString(),moneyToTransfer);
        transactionRepository.save(receiver);
        TransactionHistory sender = new TransactionHistory(fromAccount,toAccount,sender_last_txn.getClosingBalance()-moneyToTransfer,TransactionType.DEBIT.toString(),moneyToTransfer);
        transactionRepository.save(sender);
    }

    @Transactional
    public List<BankStatement> getBankStatement(Long accountNum){
        this.checkAccountExistence(accountNum);
        List<TransactionHistory> historyList =  getTransactionHistory( accountNum);
        return formTheBankStatement(historyList);

    }

   /* @Transactional
    public List<BankStatement> getBankStatementBetweenDates(Date startDate, Date endDate, Long accNNum){
List<TransactionHistory> txnHistory = transactionRepository.findByOccurredForAndTxnOccurredAtBetweenOrderByTxnOccurredAtDesc(accNNum, startDate, endDate);
   return formTheBankStatement(txnHistory);
    }
*/
    public List<BankStatement> formTheBankStatement(List<TransactionHistory> historyList){
 return historyList.stream().map(e->
                new BankStatement(e.getTransactionId(),e.getParticipant(),e.getTxnOccurredAt(),
                        e.getTxnType(),e.getAmount(),e.getClosingBalance())).collect(Collectors.toList());
    }

    @Transactional
    public boolean checkAccountExistence(Long accountNum) {
    AccountOwner accountOwner = accountRepository.findByAccountNumber(accountNum);
     if (accountOwner == null) throw new AccountNotFoundException("Account no:" +accountNum+ " " + "not found");
    else return true;
    }

    @Transactional
    public void checkForSufficientBalance(Long fromAccount, Double amount) {
   Double closingBalance = getTransactionHistory(fromAccount).get(0).getClosingBalance();

          if(closingBalance< amount)
              throw new InsufficientBalanceException("Your account balance is only " + closingBalance.toString());

    }

    @Transactional
    public List<TransactionHistory> getTransactionHistory(Long accNum) {
    return  transactionRepository.findByOccurredForOrderByTxnOccurredAtDesc(accNum);
    }

    @Transactional
    public void makeFoodPayment(PaymentRequest p){
        checkAccountExistence(p.getFromAccountNo());
        //checkForSufficientBalance(p.getFromAccountNo(),p.getAmount());
        TransactionHistory sender_last_txn = getTransactionHistory(p.getFromAccountNo()).get(0);
        TransactionHistory sender = new TransactionHistory(p.getFromAccountNo(),1234687678L,
                sender_last_txn.getClosingBalance()-p.getAmount(),TransactionType.DEBIT.toString(),p.getAmount());
        transactionRepository.save(sender);
    }

}