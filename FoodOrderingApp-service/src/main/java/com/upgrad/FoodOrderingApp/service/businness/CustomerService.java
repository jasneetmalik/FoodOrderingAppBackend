package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerRepository;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.UUID;


@Service
public class CustomerService {
    @Autowired
    CustomerRepository customerRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity saveCustomer(CustomerEntity customer) throws SignUpRestrictedException {
        if(checkContact(customer.getContactNumber()) != null) {

            throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number.");
        }


        try{
            String emailFormat[] = customer.getEmail().split("[@,.]");



            if (!(emailFormat[0].length() >= 3 && emailFormat[1].length() >= 2 && emailFormat[2].length() >= 2)) {
                throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
            }
        }catch (Exception e) {

            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        }

        String contactNumber = customer.getContactNumber();
        if(contactNumber.length() != 10) {
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
        }
        char contact[] = contactNumber.toCharArray();
        for(char a : contact) {
            try {
                Integer.parseInt("" + a);
            }
            catch (Exception e) {
                throw new SignUpRestrictedException("SGR-003", "Invalid contact number!");
            }
        }
        ArrayList<Character> list = new ArrayList();
        list.add('#');
        list.add('@');
        list.add('$');
        list.add('%');
        list.add('&');
        list.add('*');
        list.add('!');
        list.add('^');
        String password = customer.getPassword();
        char pass[] = password.toCharArray();
        int count1 = 0, count2 = 0, count3 = 0;
        for(char a : pass) {
            if(a >= '0' && a <= '9') {
                count1++;
            }
            else if(a >= 'A' && a <= 'Z') {
                count2++;
            }
            else if(list.contains(a)) {
                count3++;
            }

        }
        if(!(count1 > 0 && count2 > 0 && count3 > 0 && password.length() >= 8)) {
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
        }

        PasswordCryptographyProvider passwordCryptographyProvider = new PasswordCryptographyProvider();
        String encrypt[] = passwordCryptographyProvider.encrypt(customer.getPassword());
        customer.setSalt(encrypt[0]);
        customer.setPassword(encrypt[1]);

        customer.setUuid(UUID.randomUUID().toString());
            return customerRepository.createUser(customer);


    }

    public CustomerEntity checkContact(String contact) {

        return customerRepository.checkContact(contact);
    }

    public CustomerEntity checkPassword(String password) {
        return customerRepository.checkPassword(password);
    }

    public CustomerAuthEntity authenticate(String contact, String password) throws AuthenticationFailedException {

        CustomerEntity customerEntity;
        customerEntity = checkContact(contact);
        CustomerAuthEntity customerAuth = new CustomerAuthEntity();
        String encrypt1;
        try {
            encrypt1 = PasswordCryptographyProvider.encrypt(password, customerEntity.getSalt());
        } catch (Exception e) {
            throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
        }
        if (((customerEntity = checkPassword(encrypt1)) == null)) {
            throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
        } else {
            customerAuth.setCustomer(customerEntity);
            customerAuth.setUuid(customerEntity.getUuid());
            customerAuth.setLoginAt(ZonedDateTime.now());
            customerAuth.setExpiresAt(ZonedDateTime.now().plusHours(1));
            return customerAuth;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity saveAuth(CustomerAuthEntity customerAuthEntity) {
        return customerRepository.saveAuth(customerAuthEntity);
    }

//    public CustomerAuthEntity logout
}
