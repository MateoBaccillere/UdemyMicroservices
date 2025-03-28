package com.paymentchain.customer.controller;

import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.service.impl.CustomerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/customer")
public class CustomerRestController {

    @Autowired
    CustomerServiceImpl customerService;


    @PostMapping
    public ResponseEntity<Customer> post(@RequestBody Customer customer) {
        customer.getProducts().forEach(x->x.setCustomer(customer));
        customer.setId(null);
        Customer save = customerService.newCustomer(customer);
        return ResponseEntity.ok(save);
    }
    @GetMapping()
    public List<Customer> list() {
        return customerService.getAllCustomers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") long id) {
        Optional<Customer> customer = customerService.getCustomer(id);
        if (customer.isPresent()) {
            return new ResponseEntity<>(customer.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> put(@PathVariable("id") long id, @RequestBody Customer customer) {
        if (!customerService.isExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Customer updateCustomer = customerService.fullUpdate(id, customer);

        return new ResponseEntity<>(updateCustomer, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patch(@PathVariable("id") long id, @RequestBody Customer customer) {
        if (!customerService.isExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        customer.setId(id);

        Customer updateCustomer = customerService.partialUpdate(id,customer);

        return new ResponseEntity<>(updateCustomer, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") long id) {
        customerService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/full")
    public Customer getByCode(@RequestParam("code") String code){
        Customer customer = customerService.getByCode(code);
        return customer;
    }





}
