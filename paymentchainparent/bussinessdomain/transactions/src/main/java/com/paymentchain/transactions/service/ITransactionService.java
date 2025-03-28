package com.paymentchain.transactions.service;

import com.paymentchain.transactions.entity.Transaction;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface ITransactionService {

    Transaction createTransaction(Transaction transaction);

    List<Transaction> getAllTransactions();

    Optional<Transaction> getTransaction(Long id);

    Transaction updateTransaction(Long id, Transaction transaction);

    void deleteTransaction(Long id);

}
