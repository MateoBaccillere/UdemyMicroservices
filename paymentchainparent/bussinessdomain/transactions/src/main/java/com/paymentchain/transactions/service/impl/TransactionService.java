package com.paymentchain.transactions.service.impl;

import com.paymentchain.transactions.entity.Transaction;
import com.paymentchain.transactions.repository.TransactionRepository;
import com.paymentchain.transactions.service.ITransactionService;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class TransactionService implements ITransactionService {

    private TransactionRepository transactionRepository;

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, WebClient.Builder webClientBuilder) {
        this.transactionRepository = transactionRepository;
        this.webClientBuilder = webClientBuilder;
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

    HttpClient client = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .option(EpollChannelOption.TCP_KEEPIDLE, 300)
            .option(EpollChannelOption.TCP_KEEPINTVL, 60) // Faltaba el valor
            .responseTimeout(Duration.ofSeconds(1))
            .doOnConnected(connection -> {
                connection.addHandlerLast(new ReadTimeoutHandler(5000));
                connection.addHandlerLast(new WriteTimeoutHandler(5000));
            });



    public String getCustomerName(){
        return null;
    }


}

