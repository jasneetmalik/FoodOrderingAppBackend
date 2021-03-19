package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerRepository;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class CustomerService {
    @Autowired
    CustomerRepository customerRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity saveCustomer(CustomerEntity customer) {
        return customerRepository.createUser(customer);

    }

    public CustomerEntity checkContact(String contact) {
        return customerRepository.checkContact(contact);
    }

    public CustomerEntity checkPassword(String password) {
        return customerRepository.checkPassword(password);
    }

    public boolean authenticate(String contact, String password) {
        return customerRepository.authenticate(contact, password);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity saveAuth(CustomerAuthEntity customerAuthEntity) {
        return customerRepository.saveAuth(customerAuthEntity);
    }
}
