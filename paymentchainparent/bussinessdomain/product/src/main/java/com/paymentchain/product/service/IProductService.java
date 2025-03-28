package com.paymentchain.product.service;

import com.paymentchain.product.entities.Product;

import java.util.List;
import java.util.Optional;

public interface IProductService {

    public List<Product> getAllCustomers();

    public Optional<Product> getCustomer(Long id);

    public Product newCustomer(Product product);

    boolean isExists(Long id);

    public Product partialUpdate(Long id, Product product);

    public Product fullUpdate(Long id, Product product);

    public void deleteById(Long id);

}
