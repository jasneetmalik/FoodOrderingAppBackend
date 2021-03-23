package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.businness.JwtTokenProvider;
import com.upgrad.FoodOrderingApp.service.businness.PasswordCryptographyProvider;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    // ************************************ SIGNUP ***********************************
    @RequestMapping(
            method = RequestMethod.POST,
            path = "/customer/signup",
            consumes = APPLICATION_JSON_UTF8_VALUE,
            produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signup(@RequestBody(required = false) final SignupCustomerRequest signupCustomerRequest)
            throws SignUpRestrictedException, AuthenticationFailedException {

        final CustomerEntity customerEntity = new CustomerEntity();
        SignupCustomerRequest scr = signupCustomerRequest;

        if(scr.getContactNumber().isEmpty() || scr.getEmailAddress().isEmpty()||
                scr.getFirstName().isEmpty() || scr.getPassword().isEmpty()) {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        }
        customerEntity.setContactNumber(signupCustomerRequest.getContactNumber());
        customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
        customerEntity.setFirstName(signupCustomerRequest.getFirstName());
        customerEntity.setLastName(signupCustomerRequest.getLastName());
        customerEntity.setPassword(signupCustomerRequest.getPassword());
        customerEntity.setUuid(UUID.randomUUID().toString());

        CustomerEntity createdCustomer;
        createdCustomer = customerService.saveCustomer(customerEntity);
        SignupCustomerResponse signupResponse = new SignupCustomerResponse();

            signupResponse.id(createdCustomer.getUuid());
            signupResponse.status("CUSTOMER SUCCESSFULLY REGISTERED");

        ResponseEntity<SignupCustomerResponse> responseResponseEntity = new ResponseEntity<SignupCustomerResponse>(signupResponse, HttpStatus.CREATED);

        return responseResponseEntity;

    }
    // ********************************* SIGNIN *************************************************************
    @RequestMapping(method = POST, path = "/customer/login", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> signin(@RequestHeader(name = "authorization") String authorization) throws AuthenticationFailedException {

        final String username;
        final String password;
        final String[] decodedCredentials;
        // ArrayIndexOutOfBoundsException occurs if the username or password is left as empty or try to
        // authorize without Basic in prefix 'Basic Base64<contactNumber:password>' then it throws
        // AuthenticationFailedException with code as ATH-003
        try {
            decodedCredentials = new String(Base64.getDecoder().decode(authorization.split("Basic ")[1])).split(":");
            username = decodedCredentials[0];
            password = decodedCredentials[1];
        }
        catch (Exception exc) {
            throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
        }
        CustomerAuthEntity authorizedCustomer = customerService
                .authenticate(username, password);
        // If Valid, get the customer entity
        CustomerEntity customer = authorizedCustomer.getCustomer();
        // Formulate the response for successful login
        LoginResponse loginResponse = new LoginResponse()
                .id(customer.getUuid())
                .contactNumber(customer.getContactNumber())
                .emailAddress(customer.getEmail())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .message("LOGGED IN SUCCESSFULLY");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("access-token", authorizedCustomer.getAccessToken());
        httpHeaders.add("Access-Control-Expose-Headers", "*");
        return new ResponseEntity<LoginResponse>(loginResponse, httpHeaders, HttpStatus.OK);
    }

    // ******************************************* LOGOUT ***************************************
    @RequestMapping(method = POST, path = "/customer/logout", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> logout(@RequestHeader(name = "authorization") String authorization) throws AuthorizationFailedException {
        final String[] bearerTokens = authorization.split("Bearer ");
        final String accessToken;
        if (bearerTokens.length == 2) {
            accessToken = bearerTokens[1];
        } else {
            accessToken = authorization;
        }
        // Given access-token, logout the corresponding customer
        CustomerAuthEntity loggedOutCustomerAuth = customerService.logout(accessToken);
        // Generate the response of successful logout
        LogoutResponse logoutResponse = new LogoutResponse()
                .id(loggedOutCustomerAuth.getCustomer().getUuid())
                .message("LOGGED OUT SUCCESSFULLY");
        return new ResponseEntity<LogoutResponse>(logoutResponse, HttpStatus.OK);
    }
    // ************* UPDATE CUSTOMER ****************************************************
    @RequestMapping(method = RequestMethod.PUT, path = "/customer", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdateCustomerResponse> updateCustomer(
            @RequestHeader("authorization") final String authorization,
            @RequestBody(required = false) final UpdateCustomerRequest updateCustomerRequest)
            throws UpdateCustomerException, AuthorizationFailedException {

        // Get the access-token from authorization header(Bearer)
        final String[] bearerTokens = authorization.split("Bearer ");
        final String accessToken;
        if (bearerTokens.length == 2) {
            accessToken = bearerTokens[1];
        } else {
            accessToken = authorization;
        }
        // Check if the firstname field of request is not empty and accordingly raise an exception
        if (updateCustomerRequest.getFirstName() == null || updateCustomerRequest.getFirstName()
                .isEmpty()) {
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        }
        // Get customer based on access-token
        CustomerEntity customer = customerService.getCustomer(accessToken);
        // Update user/ customer details
        customer.setFirstName(updateCustomerRequest.getFirstName());
        customer.setLastName(updateCustomerRequest.getLastName());
        CustomerEntity updatedCustomer = customerService.updateCustomer(customer);
        // Generate response on successfully updating the customer details
        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse()
                .id(updatedCustomer.getUuid())
                .firstName(updatedCustomer.getFirstName())
                .lastName(updatedCustomer.getLastName())
                .status("CUSTOMER DETAILS UPDATED SUCCESSFULLY");
        return new ResponseEntity<UpdateCustomerResponse>(updateCustomerResponse, HttpStatus.OK);
    }
// ********************************* UPDATE CUSTOMER PASSWORD ************************
    @RequestMapping(method = RequestMethod.PUT, path = "/customer/password", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdatePasswordResponse> updatePassword(
        @RequestHeader("authorization") final String authorization,
        @RequestBody(required = false) final UpdatePasswordRequest updatePasswordRequest)
        throws AuthorizationFailedException, UpdateCustomerException {

    // Get access-token from authorization header
        final String[] bearerTokens = authorization.split("Bearer ");
        final String accessToken;
        if (bearerTokens.length == 2) {
            accessToken = bearerTokens[1];
        } else {
            accessToken = authorization;
        }
    // get the old and new passwords from the request body
    final String oldPassword = updatePasswordRequest.getOldPassword();
    final String newPassword = updatePasswordRequest.getNewPassword();
    // Check if both the input parameters aren't empty, and accordingly raise an exception
    if (oldPassword == "" || newPassword == "") {
        throw new UpdateCustomerException("UCR-003", "No field should be empty");
    }
    // Get customer based on access-token
    CustomerEntity customer = customerService.getCustomer(accessToken);
    // Update customer's password
    CustomerEntity updatedCustomer = customerService
            .updateCustomerPassword(oldPassword, newPassword, customer);
    // Generate response if password successfully updated
    UpdatePasswordResponse updatePasswordResponse = new UpdatePasswordResponse()
            .id(updatedCustomer.getUuid())
            .status("CUSTOMER PASSWORD UPDATED SUCCESSFULLY");
    return new ResponseEntity<UpdatePasswordResponse>(updatePasswordResponse, HttpStatus.OK);
    }
}