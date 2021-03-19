package com.upgrad.FoodOrderingApp.api.controller;


import com.upgrad.FoodOrderingApp.api.model.LoginResponse;
import com.upgrad.FoodOrderingApp.api.model.LogoutResponse;
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

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setContactNumber(signupCustomerRequest.getContactNumber());
        customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
        customerEntity.setFirstName(signupCustomerRequest.getFirstName());
        customerEntity.setLastName(signupCustomerRequest.getLastName());
        customerEntity.setPassword(signupCustomerRequest.getPassword());
        if(customerEntity.getContactNumber().equals("") || customerEntity.getEmail().equals("") || customerEntity.getFirstName().equals("") || customerEntity.getPassword().equals("")) {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        }
        CustomerEntity customer;
        customer = customerService.saveCustomer(customerEntity);

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
        CustomerEntity customerEntity;
        CustomerAuthEntity customerAuth;
        try {
            decode = Base64.getDecoder().decode(deocde[1]);
            decoded = new String(decode);
            encrypt = decoded.split(":");
            password = encrypt[1];
            contact = encrypt[0];
        }
        catch (Exception e) {
            throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
        }


        customerAuth = customerService.authenticate(contact, password);

        customerEntity = customerAuth.getCustomer();
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(password);
            String token = jwtTokenProvider.generateToken(customerAuth.getUuid(), ZonedDateTime.now(), ZonedDateTime.now().plusHours(1));

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


//    @RequestMapping(method = POST, path = "/customer/logout", produces = APPLICATION_JSON_UTF8_VALUE)
//    public ResponseEntity<LogoutResponse> logout(@RequestHeader(name = "authorization") String authentication) {
//
//
//    }

