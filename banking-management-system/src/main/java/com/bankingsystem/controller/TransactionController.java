package com.bankingsystem.controller;

import com.bankingsystem.dto.request.DepositRequest;
import com.bankingsystem.dto.request.TransferRequest;
import com.bankingsystem.dto.request.WithdrawalRequest;
import com.bankingsystem.dto.response.MessageResponse;
import com.bankingsystem.dto.response.TransactionResponse;
import com.bankingsystem.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@Valid @RequestBody DepositRequest request) {
        try {
            TransactionResponse transaction = transactionService.deposit(request);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(@Valid @RequestBody WithdrawalRequest request) {
        try {
            TransactionResponse transaction = transactionService.withdraw(request);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@Valid @RequestBody TransferRequest request) {
        try {
            TransactionResponse transaction = transactionService.transfer(request);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<?> getAccountTransactions(@PathVariable String accountNumber) {
        try {
            List<TransactionResponse> transactions = transactionService.getAccountTransactions(accountNumber);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/{reference}")
    public ResponseEntity<?> getTransaction(@PathVariable String reference) {
        try {
            TransactionResponse transaction = transactionService.getTransactionByReference(reference);
            return ResponseEntity.ok(transaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
