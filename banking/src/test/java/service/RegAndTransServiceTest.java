package service;

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
import com.bank.service.RegisterAndTransactionService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegAndTransServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private RegisterAndTransactionService service;

    static Register register;
    static MoneyTransfer moneyTransfer;

    @BeforeAll
    public static void init() {
        register = new Register("Alice", "9999999999", 20000.00);
        moneyTransfer = new MoneyTransfer(123456789L, 8764321111L, 3000.00);
    }

    @Test
    void testRegisterAccountSuccess() {


        AccountOwner savedOwner = new AccountOwner(register.getName(), register.getPhoneNumber());
        savedOwner.setAccountNumber(123456789L);

        doReturn(savedOwner).when(accountRepository).save(any(AccountOwner.class));

        service.registerAccount(register);

        verify(accountRepository).save(any(AccountOwner.class));
        verify(transactionRepository).save(argThat(txn ->
                txn.getOccurredFor().equals(123456789L) &&

                        txn.getTxnType() == TransactionType.DEPOSIT.toString() &&
                        txn.getClosingBalance().equals(20000.0)
        ));
    }

    @Test
    void testRegisterAccountWithLessDeposit() {

        Register register = mock(Register.class);
        when(register.getCreditAmount()).thenReturn(5000.0);

        MinimumBalanceException ex = assertThrows(MinimumBalanceException.class, () ->
                service.registerAccount(register));

        assertEquals("Minimum balance must be ₹10,000 to create an account.", ex.getMessage());

    }

    @Test
    void testTransferMoney_Success() {
        TransactionHistory from = new TransactionHistory();
        from.setClosingBalance(20000.0);
        List<TransactionHistory> fromHistory = List.of(from);

        TransactionHistory to = new TransactionHistory();
        to.setClosingBalance(33000.0);
        List<TransactionHistory> toHistory = List.of(to);

        when(accountRepository.findByAccountNumber(moneyTransfer.getFromAccount())).thenReturn(new AccountOwner());
        when(accountRepository.findByAccountNumber(moneyTransfer.getToAccount())).thenReturn(new AccountOwner());

        when(transactionRepository.findByOccurredForOrderByTxnOccurredAtDesc(moneyTransfer.getFromAccount())).
                thenReturn(fromHistory);

        when(transactionRepository.findByOccurredForOrderByTxnOccurredAtDesc(moneyTransfer.getToAccount())).
                thenReturn(toHistory);

        service.transferMoney(moneyTransfer);

        ArgumentCaptor<TransactionHistory> captor = ArgumentCaptor.forClass(TransactionHistory.class);
        verify(transactionRepository, times(2)).save(captor.capture());

        List<TransactionHistory> savedTransactions = captor.getAllValues();

        TransactionHistory creditTxn = savedTransactions.get(0);
        assertEquals(36000.0, creditTxn.getClosingBalance());

        TransactionHistory debitTxn = savedTransactions.get(1);
        assertEquals(17000.0, debitTxn.getClosingBalance());
    }

    @Test
    void testInsufficientBalance() {
        TransactionHistory from = new TransactionHistory();
        from.setClosingBalance(100.00);
        List<TransactionHistory> fromHistory = List.of(from);

        TransactionHistory to = new TransactionHistory();
        to.setClosingBalance(33000.0);
        List<TransactionHistory> toHistory = List.of(to);

        when(accountRepository.findByAccountNumber(moneyTransfer.getFromAccount())).thenReturn(new AccountOwner());

        when(transactionRepository.findByOccurredForOrderByTxnOccurredAtDesc(moneyTransfer.getFromAccount())).
                thenReturn(fromHistory);
        InsufficientBalanceException insufficientBalanceException = assertThrows(InsufficientBalanceException.class, () -> {
            service.transferMoney(moneyTransfer);
        });
    }

    @Test
    void testAccNotFoundBankStmnt() {
        when(accountRepository.findByAccountNumber(moneyTransfer.getFromAccount())).thenReturn(null);
        assertThrows(AccountNotFoundException.class, () ->
        {
            service.getBankStatement(moneyTransfer.getFromAccount());
        });
    }

    @Test
    void getBankStmnt() {
        TransactionHistory transactionHistory = new TransactionHistory(1L, "TXN6735486", 645445464L, 5464564L, new Date(), 5368.00, TransactionType.DEPOSIT.toString(), 3000000.00);
        List<TransactionHistory> transactionHistories = List.of(transactionHistory);
        when(accountRepository.findByAccountNumber(moneyTransfer.getFromAccount())).thenReturn(new AccountOwner());
        when(transactionRepository.findByOccurredForOrderByTxnOccurredAtDesc
                (moneyTransfer.getFromAccount())).thenReturn(transactionHistories);

        List<BankStatement> bankStatement = service.getBankStatement(moneyTransfer.getFromAccount());
        assertEquals(1, bankStatement.size());

    }

    @Test
    void transferMoneyFromNonExistingAcc() {
        when(accountRepository.findByAccountNumber(moneyTransfer.getFromAccount())).thenReturn(null);

        assertThrows(AccountNotFoundException.class, () ->
        {
            service.transferMoney(moneyTransfer);
        });
    }

    @Test
    void shoppingPayment(){
        PaymentRequest paymentRequest = new PaymentRequest(645445464L, 100.00, "Shopping");
        when(accountRepository.findByAccountNumber(645445464L)).thenReturn(new AccountOwner());

        TransactionHistory transactionHistory = new TransactionHistory(1L, "TXN6735486", 645445464L, 5464564L, new Date(), 5368.00, TransactionType.DEPOSIT.toString(), 3000000.00);
        List<TransactionHistory> transactionHistories = List.of(transactionHistory);
        when(transactionRepository.findByOccurredForOrderByTxnOccurredAtDesc
                (645445464L)).thenReturn(transactionHistories);

        service.makeFoodPayment(paymentRequest);
    }

}
