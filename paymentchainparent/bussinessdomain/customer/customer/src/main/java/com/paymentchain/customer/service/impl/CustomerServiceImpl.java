package com.paymentchain.customer.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.entities.CustomerProduct;
import com.paymentchain.customer.repositories.CustomerRepository;
import com.paymentchain.customer.service.ICustomerService;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements ICustomerService {

    @Autowired
    CustomerRepository customerRepository;

    private final WebClient.Builder webClientBuilder;


    public CustomerServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> getCustomer(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public Customer newCustomer(Customer customer) {
        if (customer.getId() != null && customerRepository.existsById(customer.getId())) {
            throw new IllegalStateException("El cliente ya existe. Usa PUT para actualizar.");
        }
        return customerRepository.save(customer);
    }

    @Override
    public boolean isExists(Long id) {
        return customerRepository.existsById(id);
    }

    @Override
    public Customer partialUpdate(Long id, Customer customer) {
        customer.setId(id);

        return customerRepository.findById(id).map(existingCustomer -> {
            Optional.ofNullable(customer.getName()).ifPresent(existingCustomer::setName);
            Optional.ofNullable(customer.getPhone()).ifPresent(existingCustomer::setPhone);
            Optional.ofNullable(customer.getIban()).ifPresent(existingCustomer::setIban);
            Optional.ofNullable(customer.getAddress()).ifPresent(existingCustomer::setAddress);
            Optional.ofNullable(customer.getSurname()).ifPresent(existingCustomer::setSurname);
            return customerRepository.save(existingCustomer);
        }).orElseThrow(() -> new RuntimeException("Customer does not exists"));
    }

    @Override
    @Transactional
    public Customer fullUpdate(Long id, Customer updatedCustomerData) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        // Actualizá campos simples
        existingCustomer.setCode(updatedCustomerData.getCode());
        existingCustomer.setName(updatedCustomerData.getName());
        existingCustomer.setSurname(updatedCustomerData.getSurname());
        existingCustomer.setPhone(updatedCustomerData.getPhone());
        existingCustomer.setIban(updatedCustomerData.getIban());
        existingCustomer.setAddress(updatedCustomerData.getAddress());

        // Guardá
        return customerRepository.save(existingCustomer);
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

    public String getProductName(Long id){
        WebClient build = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl("http://localhost:8082/product")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables((Collections.singletonMap("url","http://localhost:8082/product")))
                .build();
        JsonNode block = build.method(HttpMethod.GET).uri("/" + id)
                .retrieve().bodyToMono(JsonNode.class).block();

        if (block == null || block.get("name") == null) {
            return "Unknown Product"; // o null, según el caso
        }
        String name = block.get("name").asText();
        return name;
    }

    @Override
    public Customer getByCode(String code) {
        Customer customer = customerRepository.findByCode(code);

        if (customer == null) {
            throw new RuntimeException("Customer not found for code: " + code);
        }

        List<CustomerProduct> products = customer.getProducts();
        products.forEach(x -> {
            String productName = getProductName(x.getProductId()); // ← CORRECCIÓN CLAVE
            x.setProductName(productName);
        });

        return customer;
    }


    @Override
    public void deleteById (Long id){
        customerRepository.deleteById(id);
    }
}
