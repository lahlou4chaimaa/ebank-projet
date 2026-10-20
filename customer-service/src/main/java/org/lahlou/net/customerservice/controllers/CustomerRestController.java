package org.lahlou.net.customerservice.controllers;

import org.lahlou.net.customerservice.entities.Customer;
import org.lahlou.net.customerservice.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
public class CustomerRestController {
    private CustomerService customerService;
    @Autowired
    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/customers")

    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }
@GetMapping("/customers/{id}")
    public Customer findCustomerById(@PathVariable Long id) {
        return customerService.findCustomerById(id);
    }
@PostMapping("/customers")
    public Customer saveCustomer(@RequestBody Customer customer) {
        return customerService.saveCustomer(customer);
    }
}
