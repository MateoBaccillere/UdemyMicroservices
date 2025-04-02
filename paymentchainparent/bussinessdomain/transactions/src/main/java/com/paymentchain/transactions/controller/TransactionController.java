package com.paymentchain.transactions.controller;

import com.paymentchain.transactions.entity.Transaction;
import com.paymentchain.transactions.service.impl.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    TransactionService transactionService;


    @PostMapping("/create")
    public ResponseEntity<Transaction> createTransaction(Transaction transaction){

        Transaction saveTransaction = transactionService.createTransaction(transaction);

        return new ResponseEntity<>(saveTransaction, HttpStatus.CREATED);
    }

    @GetMapping("/customers/transactions")
    public List<Transaction> getTransactionsByIbanAccount(String ibanAccount){
        List<Transaction> transactions = transactionService.getByAccount(ibanAccount);

        return transactions;
    }

    @GetMapping("/all")
    public ResponseEntity<List<Transaction>> getAllTransactions(){

        List<Transaction> transactions = transactionService.getAllTransactions();

        return (ResponseEntity<List<Transaction>>) transactions.stream().collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Transaction>> getTransaction(@PathVariable("id") Long id){
        Optional<Transaction> transaction = transactionService.getTransaction(id);
        return new ResponseEntity<>(transaction,HttpStatus.OK);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@RequestBody Transaction transaction,
                                                         @PathVariable("id") Long id){
        transaction.setId(id);

        Transaction savedTransaction = transactionService.updateTransaction(id,transaction);

        return new ResponseEntity<>(savedTransaction,HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable("id") Long id){
        transactionService.deleteTransaction(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



}