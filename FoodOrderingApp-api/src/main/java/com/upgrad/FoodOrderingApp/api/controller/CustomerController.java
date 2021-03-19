package com.upgrad.FoodOrderingApp.api.controller;


import com.upgrad.FoodOrderingApp.api.model.LoginResponse;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerRequest;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerResponse;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.businness.JwtTokenProvider;
import com.upgrad.FoodOrderingApp.service.businness.PasswordCryptographyProvider;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;


@CrossOrigin
@RestController
public class CustomerController {

    @Autowired
    CustomerService customerService;
    @RequestMapping(method = POST, path = "/customer/signup", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signup(@RequestBody(required = false) final SignupCustomerRequest signupCustomerRequest) throws SignUpRestrictedException {
        if(customerService.checkContact(signupCustomerRequest.getContactNumber()) != null) {

            throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number.");
        }
        if(signupCustomerRequest.getContactNumber() == null || signupCustomerRequest.getEmailAddress() == null || signupCustomerRequest.getFirstName() == null || signupCustomerRequest.getPassword() == null) {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        }

        try{
        String emailFormat[] = signupCustomerRequest.getEmailAddress().split("[@,.]");



            if (!(emailFormat[0].length() >= 3 && emailFormat[1].length() >= 2 && emailFormat[2].length() >= 2)) {
                throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
            }
        }catch (Exception e) {

            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        }

        String contactNumber = signupCustomerRequest.getContactNumber();
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
        String password = signupCustomerRequest.getPassword();
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

        CustomerEntity customer = new CustomerEntity();
        customer.setContactNumber(signupCustomerRequest.getContactNumber());
        customer.setEmail(signupCustomerRequest.getEmailAddress());
        customer.setFirstName(signupCustomerRequest.getFirstName());
        customer.setLastName(signupCustomerRequest.getLastName());

        PasswordCryptographyProvider passwordCryptographyProvider = new PasswordCryptographyProvider();
        String encrypt[] = passwordCryptographyProvider.encrypt(signupCustomerRequest.getPassword());
        customer.setSalt(encrypt[0]);
        customer.setPassword(encrypt[1]);

        customer.setUuid(UUID.randomUUID().toString());

        customer = customerService.saveCustomer(customer);

        SignupCustomerResponse response = new SignupCustomerResponse();

        response.setId(customer.getUuid());
        response.setStatus("CUSTOMER SUCCESSFULLY REGISTERED");

        ResponseEntity<SignupCustomerResponse> responseResponseEntity = new ResponseEntity<SignupCustomerResponse>(response, HttpStatus.CREATED);

        return responseResponseEntity;


    }

    @RequestMapping(method = POST, path = "/customer/login", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> signin(@RequestHeader(name = "authorization") String authorization) throws AuthenticationFailedException {

        String deocde[] = authorization.split(" ");
        byte[] decode;
        String decoded;
        String encrypt[];
        String password;
        String contact;
        String encrypt1;
        CustomerEntity customerEntity = null;
        CustomerAuthEntity customerAuth = new CustomerAuthEntity();
        try {
            decode = Base64.getDecoder().decode(deocde[1]);
            decoded = new String(decode);
            encrypt = decoded.split(":");
            password = encrypt[1];
            contact = encrypt[0];
            System.out.println(password);
        }
        catch (Exception e) {
            throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
        }
        customerEntity = customerService.checkContact(contact);
        try{
        encrypt1 = PasswordCryptographyProvider.encrypt(password, customerEntity.getSalt());}
        catch (Exception e) {
            throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
        }
        if(customerEntity.equals(null)) {
            throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
        }
        else if(((customerEntity = customerService.checkPassword(encrypt1)) == null)) {
            throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
        }
        else {
                customerAuth.setCustomer(customerEntity);
                customerAuth.setUuid(customerEntity.getUuid());
                customerAuth.setLoginAt(ZonedDateTime.now());
                customerAuth.setExpiresAt(ZonedDateTime.now().plusHours(1));

            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(password);
            String token = jwtTokenProvider.generateToken(customerAuth.getUuid(), customerAuth.getLoginAt(), customerAuth.getExpiresAt());

            HttpHeaders headers = new HttpHeaders();
            headers.add("access-token", token);

            customerAuth.setAccessToken(token);
            customerService.saveAuth(customerAuth);

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setContactNumber(customerEntity.getContactNumber());
            loginResponse.setFirstName(customerEntity.getFirstName());
            loginResponse.setId(customerEntity.getUuid());
            loginResponse.setLastName(customerEntity.getLastName());
            loginResponse.setEmailAddress(customerEntity.getEmail());
            loginResponse.setMessage("LOGGED IN SUCCESSFULLY");

            return new ResponseEntity<>(loginResponse, headers, HttpStatus.OK);

        }


    }
}
