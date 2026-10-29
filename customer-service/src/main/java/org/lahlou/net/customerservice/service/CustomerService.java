
package org.lahlou.net.customerservice.service;

import org.lahlou.net.customerservice.entities.Customer;
import org.lahlou.net.customerservice.repository.CustomerRepository;
import org.springframework.ai.mcp.annotation.McpArg;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
@McpTool(description="Get All Customers")

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
@McpTool(description ="Find a Customer by id")
    public Customer findCustomerById(@McpToolParam(description ="The customer id") Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Customer not found!!"));
    }

    @McpTool(description="Save a new Customer")
    public Customer saveCustomer(@McpToolParam(description="The Customer to save (name, email") Customer customer) {

        return customerRepository.save(customer);
    }
}
