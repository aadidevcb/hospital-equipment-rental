package com.hospital.equipment.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hospital.equipment.entity.Customer;
import com.hospital.equipment.repository.CustomerRepository;

@Service
@Transactional
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    
    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
    
    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }
    
    public Optional<Customer> getCustomerByIdWithRentals(Long id) {
        return customerRepository.findByIdWithRentals(id);
    }
    
    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }
    
    public List<Customer> searchCustomersByName(String name) {
        return customerRepository.searchByName(name);
    }
    
    public Customer createCustomer(Customer customer) {
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Customer with email '" + customer.getEmail() + "' already exists");
        }
        return customerRepository.save(customer);
    }
    
    public Customer updateCustomer(Long id, Customer customerDetails) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        
        // Check if email is being changed and if it already exists
        if (!customer.getEmail().equals(customerDetails.getEmail()) && 
            customerRepository.existsByEmail(customerDetails.getEmail())) {
            throw new IllegalArgumentException("Customer with email '" + customerDetails.getEmail() + "' already exists");
        }
        
        customer.setFirstName(customerDetails.getFirstName());
        customer.setLastName(customerDetails.getLastName());
        customer.setEmail(customerDetails.getEmail());
        customer.setPhone(customerDetails.getPhone());
        customer.setAddress(customerDetails.getAddress());
        customer.setCity(customerDetails.getCity());
        customer.setState(customerDetails.getState());
        customer.setZipCode(customerDetails.getZipCode());
        
        return customerRepository.save(customer);
    }
    
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        
        // Check if customer has active rentals
        if (customer.getRentals() != null && 
            customer.getRentals().stream().anyMatch(rental -> 
                rental.getStatus() == com.hospital.equipment.entity.Rental.RentalStatus.ACTIVE ||
                rental.getStatus() == com.hospital.equipment.entity.Rental.RentalStatus.CONFIRMED)) {
            throw new IllegalStateException("Cannot delete customer with active rentals");
        }
        
        customerRepository.delete(customer);
    }
    
    public boolean existsByEmail(String email) {
        return customerRepository.existsByEmail(email);
    }
}