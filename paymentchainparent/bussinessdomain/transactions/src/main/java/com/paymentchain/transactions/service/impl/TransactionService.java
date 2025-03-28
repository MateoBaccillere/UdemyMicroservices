package com.paymentchain.transactions.service.impl;

import com.paymentchain.transactions.entity.Transaction;
import com.paymentchain.transactions.repository.TransactionRepository;
import com.paymentchain.transactions.service.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService implements ITransactionService {

    private TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }


    @Override
    public Transaction createTransaction(Transaction transaction) {
        Transaction saveTransaction = transactionRepository.save(transaction);

        if(saveTransaction.getFee() > 0){
            double newAmount = saveTransaction.getAmount() - saveTransaction.getFee();
            saveTransaction.setAmount(newAmount);}
        return saveTransaction;
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public Optional<Transaction> getTransaction(Long id) {
        return transactionRepository.findById(id);
    }

    @Override
    public Transaction updateTransaction(Long id, Transaction transaction) {
        transaction.setId(id);

        return transactionRepository.findById(id).map(existingTransaction -> {
            Optional.ofNullable(transaction.getFee()).ifPresent(existingTransaction::setFee);
            Optional.ofNullable(transaction.getAmount()).ifPresent(existingTransaction::setAmount);
            Optional.ofNullable(transaction.getReference()).ifPresent(existingTransaction::setReference);
            Optional.ofNullable(transaction.getDate()).ifPresent(existingTransaction::setDate);
            
        })
    }

    @Override
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }
}
