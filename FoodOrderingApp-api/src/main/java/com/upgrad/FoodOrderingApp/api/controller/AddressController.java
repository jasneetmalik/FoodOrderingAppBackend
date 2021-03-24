package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.SaveAddressRequest;
import com.upgrad.FoodOrderingApp.api.model.SaveAddressResponse;
import com.upgrad.FoodOrderingApp.api.utility.Utility;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@CrossOrigin
@RestController
public class AddressController {

    @Autowired
    CustomerService customerService;
    @Autowired
    AddressService addressService;
    @RequestMapping(path = "/address", produces = APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST)
    public ResponseEntity<SaveAddressResponse> save(@RequestHeader("authorization") String token, @RequestBody()SaveAddressRequest saveAddressRequest) throws SaveAddressException, AuthorizationFailedException, AddressNotFoundException {
        final String[] bearerTokens = token.split("Bearer ");
        final String accessToken;
        if (bearerTokens.length == 2) {
            accessToken = bearerTokens[1];
        } else {
            accessToken = token;
        }

        CustomerEntity customer = customerService.getCustomer(accessToken);
        if(Utility.isNullOrEmpty(customer)) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }
        AddressEntity addressEntity = new AddressEntity();

        StateEntity stateEntity = null;
            stateEntity = addressService.getStateByUUID(saveAddressRequest.getStateUuid());

        addressEntity.setCity(saveAddressRequest.getCity());
        addressEntity.setLocality(saveAddressRequest.getLocality());
        addressEntity.setFlatBuilNumber(saveAddressRequest.getFlatBuildingName());
        addressEntity.setPincode(saveAddressRequest.getPincode());
        addressEntity.setStateId(stateEntity);
        addressEntity.setUuid(UUID.randomUUID().toString());

            addressEntity = addressService.saveAddress(customer, addressEntity);

        SaveAddressResponse saveAddressResponse = new SaveAddressResponse();
        saveAddressResponse.setId(addressEntity.getUuid());
        saveAddressResponse.setStatus("ADDRESS SUCCESSFULLY REGISTERED");

        return new ResponseEntity<>(saveAddressResponse, HttpStatus.CREATED);


    }
}
