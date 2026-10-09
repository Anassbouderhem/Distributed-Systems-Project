package net.bouderhem.customerservice.services;

import net.bouderhem.customerservice.entities.Customer;
import net.bouderhem.customerservice.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository){
        this.customerRepository = customerRepository;
    }
    public List<Customer> getAllCustomers(){
        return customerRepository.findAll();
    }

    public Customer findCustomerById(long id){
        return customerRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Customer Not found"));
    }
    public Customer saveCustomer(Customer customer){
        return customerRepository.save(customer);
    }
}
