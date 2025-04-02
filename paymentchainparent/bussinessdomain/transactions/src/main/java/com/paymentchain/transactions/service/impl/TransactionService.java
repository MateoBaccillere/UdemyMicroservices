package com.paymentchain.transactions.service.impl;


import com.paymentchain.transactions.entity.Transaction;
import com.paymentchain.transactions.repository.TransactionRepository;
import com.paymentchain.transactions.service.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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
            Optional.ofNullable(transaction.getAccountIban()).ifPresent(existingTransaction::setAccountIban);
            Optional.ofNullable(transaction.getChannel()).ifPresent(existingTransaction::setChannel);
            Optional.ofNullable(transaction.getDescription()).ifPresent(existingTransaction::setDescription);
            Optional.ofNullable(transaction.getStatus()).ifPresent(existingTransaction::setStatus);

            return transactionRepository.save(existingTransaction);
        }).orElseThrow(()-> new RuntimeException("Transaction does not exists"));
    }



    @Override
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }




    public List<Transaction> getByAccount(String ibanAccount){
        List<Transaction> transaction = transactionRepository.findByIbanAccount(ibanAccount);
        if (transaction ==  null){
            throw new RuntimeException("Transaction not found");
        }
        return transaction;

    }


}
