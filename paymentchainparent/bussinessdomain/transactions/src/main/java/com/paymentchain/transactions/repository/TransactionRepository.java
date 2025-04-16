package com.paymentchain.transactions.repository;

import com.paymentchain.transactions.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    @Query("SELECT t FROM Transaction t WHERE t.accountIban = ?1")
    public List<Transaction> findByIbanAccount(String accountIban);

}
