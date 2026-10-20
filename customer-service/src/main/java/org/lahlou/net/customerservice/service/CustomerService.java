
package org.lahlou.net.customerservice.service;

import org.lahlou.net.customerservice.entities.Customer;
import org.lahlou.net.customerservice.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer findCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found!!"));
    }

    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
}
