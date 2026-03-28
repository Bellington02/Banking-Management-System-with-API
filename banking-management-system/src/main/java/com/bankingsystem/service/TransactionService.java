package com.bankingsystem.service;

import com.bankingsystem.dto.request.DepositRequest;
import com.bankingsystem.dto.request.TransferRequest;
import com.bankingsystem.dto.request.WithdrawalRequest;
import com.bankingsystem.dto.response.TransactionResponse;
import com.bankingsystem.entity.Account;
import com.bankingsystem.entity.Transaction;
import com.bankingsystem.entity.Transfer;
import com.bankingsystem.repository.AccountRepository;
import com.bankingsystem.repository.TransactionRepository;
import com.bankingsystem.repository.TransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransferRepository transferRepository;

    @Transactional
    public TransactionResponse deposit(DepositRequest request) {
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getStatus().equals("ACTIVE")) {
            throw new RuntimeException("Account is not active");
        }

        BigDecimal balanceBefore = account.getBalance();
        BigDecimal newBalance = balanceBefore.add(request.getAmount());

        Transaction transaction = new Transaction();
        transaction.setTransactionReference(generateTransactionReference());
        transaction.setAccount(account);
        transaction.setTransactionType("DEPOSIT");
        transaction.setAmount(request.getAmount());
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(newBalance);
        transaction.setDescription(request.getDescription() != null ? request.getDescription() : "Deposit");
        transaction.setStatus("COMPLETED");
        transaction.setTransactionDate(LocalDateTime.now());

        account.setBalance(newBalance);
        accountRepository.save(account);
        transaction = transactionRepository.save(transaction);

        return mapToTransactionResponse(transaction);
    }

    @Transactional
    public TransactionResponse withdraw(WithdrawalRequest request) {
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!account.getStatus().equals("ACTIVE")) {
            throw new RuntimeException("Account is not active");
        }

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        BigDecimal balanceBefore = account.getBalance();
        BigDecimal newBalance = balanceBefore.subtract(request.getAmount());

        Transaction transaction = new Transaction();
        transaction.setTransactionReference(generateTransactionReference());
        transaction.setAccount(account);
        transaction.setTransactionType("WITHDRAWAL");
        transaction.setAmount(request.getAmount());
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(newBalance);
        transaction.setDescription(request.getDescription() != null ? request.getDescription() : "Withdrawal");
        transaction.setStatus("COMPLETED");
        transaction.setTransactionDate(LocalDateTime.now());

        account.setBalance(newBalance);
        accountRepository.save(account);
        transaction = transactionRepository.save(transaction);

        return mapToTransactionResponse(transaction);
    }

    @Transactional
    public TransactionResponse transfer(TransferRequest request) {
        Account fromAccount = accountRepository.findByAccountNumber(request.getFromAccountNumber())
                .orElseThrow(() -> new RuntimeException("Source account not found"));

        Account toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new RuntimeException("Destination account not found"));

        if (!fromAccount.getStatus().equals("ACTIVE")) {
            throw new RuntimeException("Source account is not active");
        }

        if (!toAccount.getStatus().equals("ACTIVE")) {
            throw new RuntimeException("Destination account is not active");
        }

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        BigDecimal fromBalanceBefore = fromAccount.getBalance();
        BigDecimal fromBalanceAfter = fromBalanceBefore.subtract(request.getAmount());

        BigDecimal toBalanceBefore = toAccount.getBalance();
        BigDecimal toBalanceAfter = toBalanceBefore.add(request.getAmount());

        String transactionRef = generateTransactionReference();

        Transaction debitTransaction = new Transaction();
        debitTransaction.setTransactionReference(transactionRef);
        debitTransaction.setAccount(fromAccount);
        debitTransaction.setTransactionType("TRANSFER_OUT");
        debitTransaction.setAmount(request.getAmount());
        debitTransaction.setBalanceBefore(fromBalanceBefore);
        debitTransaction.setBalanceAfter(fromBalanceAfter);
        debitTransaction.setDescription(request.getDescription() != null ? request.getDescription() : "Transfer to " + request.getToAccountNumber());
        debitTransaction.setStatus("COMPLETED");
        debitTransaction.setTransactionDate(LocalDateTime.now());

        fromAccount.setBalance(fromBalanceAfter);
        accountRepository.save(fromAccount);
        debitTransaction = transactionRepository.save(debitTransaction);

        Transaction creditTransaction = new Transaction();
        creditTransaction.setTransactionReference(generateTransactionReference());
        creditTransaction.setAccount(toAccount);
        creditTransaction.setTransactionType("TRANSFER_IN");
        creditTransaction.setAmount(request.getAmount());
        creditTransaction.setBalanceBefore(toBalanceBefore);
        creditTransaction.setBalanceAfter(toBalanceAfter);
        creditTransaction.setDescription(request.getDescription() != null ? request.getDescription() : "Transfer from " + request.getFromAccountNumber());
        creditTransaction.setStatus("COMPLETED");
        creditTransaction.setTransactionDate(LocalDateTime.now());

        toAccount.setBalance(toBalanceAfter);
        accountRepository.save(toAccount);
        creditTransaction = transactionRepository.save(creditTransaction);

        Transfer transfer = new Transfer();
        transfer.setTransaction(debitTransaction);
        transfer.setFromAccount(fromAccount);
        transfer.setToAccount(toAccount);
        transfer.setTransferType("INTERNAL");
        transferRepository.save(transfer);

        return mapToTransactionResponse(debitTransaction);
    }

    public List<TransactionResponse> getAccountTransactions(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        List<Transaction> transactions = transactionRepository.findByAccountIdOrderByTransactionDateDesc(account.getId());
        return transactions.stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    public TransactionResponse getTransactionByReference(String reference) {
        Transaction transaction = transactionRepository.findByTransactionReference(reference)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        return mapToTransactionResponse(transaction);
    }

    private String generateTransactionReference() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setTransactionReference(transaction.getTransactionReference());
        response.setAccountNumber(transaction.getAccount().getAccountNumber());
        response.setTransactionType(transaction.getTransactionType());
        response.setAmount(transaction.getAmount());
        response.setBalanceBefore(transaction.getBalanceBefore());
        response.setBalanceAfter(transaction.getBalanceAfter());
        response.setDescription(transaction.getDescription());
        response.setStatus(transaction.getStatus());
        response.setTransactionDate(transaction.getTransactionDate());
        return response;
    }
}
