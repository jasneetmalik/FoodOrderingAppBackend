package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerRepository;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.upgrad.FoodOrderingApp.service.businness.PasswordCryptographyProvider;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class CustomerService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    // ****************************** SAVE CUSTOMER ***********************************************************
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity saveCustomer(CustomerEntity customer) throws SignUpRestrictedException, AuthenticationFailedException {

        //Check if ContactNumber already exists
        CustomerEntity existingCustomer = customerRepository.checkContact(customer.getContactNumber());
        if(existingCustomer != null) {
            throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number.");
        }
        //Validate Email Address Format
        try{
            String emailFormat[] = customer.getEmail().split("[@,.]");

            if (!(emailFormat[0].length() >= 3 && emailFormat[1].length() >= 2 && emailFormat[2].length() >= 2)) {
                throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
            }
        }catch (Exception e) {

            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        }
        //Validate Length of Contact Number
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
        //Validate Password Complexity {
        if (!validPassword(customer.getPassword())) {
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
        }
        //Encrypt Password
        encryptPassword(customer);

        return customerRepository.createCustomer(customer);

    }

// ************Authenticate using Contact Number and Password ************************************************
    public CustomerAuthEntity authenticate(String contact, String password) throws AuthenticationFailedException {
        //Check is Contact Exists
        CustomerEntity registeredCustomer = customerRepository.checkContact(contact);
        if (registeredCustomer == null) {
            throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
        }
        final String encryptedPassword = PasswordCryptographyProvider.encrypt(password, registeredCustomer.getSalt());
        // Verify if Password Matches
        if (registeredCustomer.getPassword().equals(encryptedPassword)) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            CustomerAuthEntity customerAuthEntity = new CustomerAuthEntity();
            customerAuthEntity.setUuid(UUID.randomUUID().toString());
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            customerAuthEntity.setAccessToken(
                    jwtTokenProvider.generateToken(registeredCustomer.getUuid(), now, expiresAt));
            customerAuthEntity.setExpiresAt(expiresAt);
            customerAuthEntity.setCustomer(registeredCustomer);
            customerAuthEntity.setLoginAt(now);
            CustomerAuthEntity authCustomer = customerRepository.saveAuth(customerAuthEntity);
            return authCustomer;
        } else {
            throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
        }
    }
// ********** Save Customer Authentication Details ****************************************************
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity saveAuth(CustomerAuthEntity customerAuthEntity) {
        return customerRepository.saveAuth(customerAuthEntity);
    }

    //************* LOGOUT CUSTOMER *****************************************
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
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomer(final CustomerEntity customer)
            throws AuthorizationFailedException, UpdateCustomerException {
        // update customer details in the database
        CustomerEntity updatedCustomer = customerRepository.updateCustomer(customer);
        return updatedCustomer;
    }
    //***** GET CUSTOMER (USED BY ALL ENDPOINTS REQUIRING AUTHENTICATION USING ACCESS TOKEN) ***********
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity getCustomer(final String accessToken) throws AuthorizationFailedException {
        // Get the customer details based on access token
        CustomerAuthEntity customerAuth = customerRepository.findCustomerAuthByAccessToken(accessToken);
        final ZonedDateTime now;
        // Validates if customer is logged in
        if (customerAuth == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }
        // Validates if customer has logged out
        if (customerAuth.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002","Customer is logged out. Log in again to access this endpoint.");
        }
        now = ZonedDateTime.now(ZoneId.systemDefault());
        // Verifies if customer session has expired
        if (customerAuth.getExpiresAt().isBefore(now) || customerAuth.getExpiresAt().isEqual(now)) {
            throw new AuthorizationFailedException("ATHR-003","Your session is expired. Log in again to access this endpoint.");
        }
        return customerAuth.getCustomer();
    }

    // ************ UPDATE PASSWORD ************************

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomerPassword(final String oldPassword, final String newPassword,
                                                 final CustomerEntity customer) throws AuthorizationFailedException, UpdateCustomerException {
        // Update customer password in the database
        if (!validPassword(newPassword)) {
            throw new UpdateCustomerException("UCR-001", "Weak password!");
        }

        String encryptedOldPassword = PasswordCryptographyProvider
                .encrypt(oldPassword, customer.getSalt());
        if (!encryptedOldPassword.equals(customer.getPassword())) {
            throw new UpdateCustomerException("UCR-004", "Incorrect old password!");
        }

        customer.setPassword(newPassword);
        encryptPassword(customer);
        CustomerEntity updatedCustomer = customerRepository.updateCustomer(customer);
        return updatedCustomer;
    }
    // Verify validity of password
    private boolean validPassword(String password) {
        String regex = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#@$%&*!^]).{8,}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password);
        return m.matches();
    }

    // Encrypts the provided password
    private void encryptPassword(final CustomerEntity newCustomer) {
        String password = newCustomer.getPassword();
        final String[] encryptedData = passwordCryptographyProvider.encrypt(password);
        newCustomer.setSalt(encryptedData[0]);
        newCustomer.setPassword(encryptedData[1]);
    }
}
