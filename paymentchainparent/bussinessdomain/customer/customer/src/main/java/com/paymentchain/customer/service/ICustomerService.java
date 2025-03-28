package com.paymentchain.customer.service;

import com.paymentchain.customer.entities.Customer;

import java.util.List;
import java.util.Optional;

public interface ICustomerService {

    public List<Customer> getAllCustomers();

    public Optional<Customer> getCustomer(Long id);

    public Customer newCustomer(Customer customer);

    boolean isExists(Long id);

    public Customer partialUpdate(Long id, Customer customer);

    public Customer fullUpdate(Long id, Customer customer);

    public void deleteById(Long id);

    public Customer getByCode(String code);

}
