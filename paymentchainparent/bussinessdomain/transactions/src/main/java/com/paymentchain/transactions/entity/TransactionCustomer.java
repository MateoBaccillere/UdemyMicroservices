package com.paymentchain.transactions.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCustomer {

    private Long customerId;
    @Transient
    private String customerName;

    @JsonIgnore
    @ManyToOne(targetEntity = Transaction.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id",nullable = false)
    private Transaction transaction;

}
