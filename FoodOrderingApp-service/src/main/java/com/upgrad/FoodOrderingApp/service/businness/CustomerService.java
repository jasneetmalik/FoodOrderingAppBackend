package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerRepository;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
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
//Check if Contact exists in database
    public CustomerEntity checkContact(String contact) {

        return customerRepository.checkContact(contact);
    }
//Check if password exists in database
    public CustomerEntity checkPassword(String password) {
        return customerRepository.checkPassword(password);
    }

//Authenticate using Contact Number and Password
    public boolean authenticate(String contact, String password) {
        return customerRepository.authenticate(contact, password);
    }
//Save Customer Authentication Details
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity saveAuth(CustomerAuthEntity customerAuthEntity) {
        return customerRepository.saveAuth(customerAuthEntity);
    }

    /**
     * This method implements the logic for 'logout' endpoint.
     *
     * @param accessToken Customers access token in 'Bearer <access-token>' format.
     * @return Updated CustomerAuthEntity object.
     * @throws AuthorizationFailedException if any of the validation fails on customer authorization.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity logout(final String accessToken) throws AuthorizationFailedException {
        final ZonedDateTime now;
        // finds customer based on access token
        CustomerAuthEntity loggedInCustomerAuth = customerRepository
                .findCustomerAuthByAccessToken(accessToken);
        // Check if the customer is logged in
        if (loggedInCustomerAuth == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }
        // Check if the customer has already logged out
        if (loggedInCustomerAuth.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002",
                    "Customer is logged out. Log in again to access this endpoint.");
        }
        // Check if the customer's session has got expired
        now = ZonedDateTime.now(ZoneId.systemDefault());
        if (loggedInCustomerAuth.getExpiresAt().isBefore(now) || loggedInCustomerAuth.getExpiresAt()
                .isEqual(now)) {
            throw new AuthorizationFailedException("ATHR-003",
                    "Your session is expired. Log in again to access this endpoint.");
        }
        //Set Logout time and Update CustomerAuth table
        loggedInCustomerAuth.setLogoutAt(ZonedDateTime.now(ZoneId.systemDefault()));
        CustomerAuthEntity loggedOutCustomerAuth = customerRepository.update(loggedInCustomerAuth);
        return loggedOutCustomerAuth;
    }
}
