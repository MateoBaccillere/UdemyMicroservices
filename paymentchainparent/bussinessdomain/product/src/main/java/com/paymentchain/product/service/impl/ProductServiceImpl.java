package com.paymentchain.product.service.impl;

import com.paymentchain.product.entities.Product;
import com.paymentchain.product.repositories.ProductRepository;
import com.paymentchain.product.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    ProductRepository productRepository;

    @Override
    public List<Product> getAllCustomers() {
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getCustomer(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Product newCustomer(Product product) {
        if (product.getId() != null && productRepository.existsById(product.getId())) {
            throw new IllegalStateException("El cliente ya existe. Usa PUT para actualizar.");
        }

        return productRepository.save(product);
    }

    @Override
    public boolean isExists(Long id) {
        return productRepository.existsById(id);
    }

    @Override
    public Product partialUpdate(Long id, Product product) {
        product.setId(id);

        return productRepository.findById(id).map(existingCustomer -> {
            Optional.ofNullable(product.getName()).ifPresent(existingCustomer::setName);
            Optional.ofNullable(product.getCode()).ifPresent(existingCustomer::setCode);

            return productRepository.save(existingCustomer);
        }).orElseThrow(() -> new RuntimeException("Customer does not exists"));
    }

    @Override
    public Product fullUpdate(Long id, Product product) {
        product.setId(id);
        Product save = productRepository.save(product);
        return save;
    }

    @Override
    public void deleteById (Long id){
        productRepository.deleteById(id);
    }
}
