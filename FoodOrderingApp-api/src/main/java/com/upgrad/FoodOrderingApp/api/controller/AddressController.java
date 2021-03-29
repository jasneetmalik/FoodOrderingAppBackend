package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
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

import java.util.ArrayList;
import java.util.List;
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
    public ResponseEntity<SaveAddressResponse> saveAddress(@RequestHeader("authorization") String token, @RequestBody()SaveAddressRequest saveAddressRequest) throws SaveAddressException, AuthorizationFailedException, AddressNotFoundException {
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
        addressEntity.setFlatBuilNo(saveAddressRequest.getFlatBuildingName());
        addressEntity.setPincode(saveAddressRequest.getPincode());
        addressEntity.setState(stateEntity);
        addressEntity.setUuid(UUID.randomUUID().toString());
        addressEntity.setActive(1);

            addressEntity = addressService.saveAddress(customer, addressEntity);

        SaveAddressResponse saveAddressResponse = new SaveAddressResponse();
        saveAddressResponse.setId(addressEntity.getUuid());
        saveAddressResponse.setStatus("ADDRESS SUCCESSFULLY REGISTERED");

        return new ResponseEntity<>(saveAddressResponse, HttpStatus.CREATED);


    }

    @RequestMapping(path = "/address/customer", produces = APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public ResponseEntity<AddressListResponse> getCustomerAddress(@RequestHeader("authorization") String token) throws AuthorizationFailedException {
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
        List<AddressEntity> list = addressService.getAllAddress(customer);
        AddressList addressList;
        AddressListResponse addressListResponse = new AddressListResponse();
        List<AddressList> al = new ArrayList<>();
        for(int i = 0; i < list.size(); i++) {
            addressList = new AddressList();
            addressList.setCity(list.get(i).getCity());
            addressList.setId(UUID.fromString(list.get(i).getUuid()));
            addressList.setPincode(list.get(i).getPincode());
            addressList.setLocality(list.get(i).getLocality());
            addressList.setFlatBuildingName(list.get(i).getFlatBuilNo());
            AddressListState state = new AddressListState();
            state.setId(UUID.fromString(list.get(i).getState().getUuid()));
            state.setStateName(list.get(i).getState().getStateName());
            addressList.setState(state);
            al.add(addressList);
        }

        addressListResponse.setAddresses(al);
        return new ResponseEntity<>(addressListResponse, HttpStatus.OK);
    }


    @RequestMapping(path = "/address/{addressId}", produces = APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.DELETE)
    public ResponseEntity<DeleteAddressResponse> deleteAddress(@PathVariable("addressId") String uuid, @RequestHeader("authorization") String token) throws AuthorizationFailedException, AddressNotFoundException {
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
        AddressEntity addressEntity = addressService.getAddressByUUID(uuid, customer);
        addressEntity = addressService.deleteAddress(addressEntity);

        DeleteAddressResponse deleteAddressResponse = new DeleteAddressResponse();
        deleteAddressResponse.setId(UUID.fromString(addressEntity.getUuid()));
        deleteAddressResponse.setStatus("ADDRESS DELETED SUCCESSFULLY");

        return new ResponseEntity<>(deleteAddressResponse, HttpStatus.OK);

    }


    @RequestMapping(method = RequestMethod.GET, path = "/states", produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatesListResponse> getStates() {

        List<StateEntity> stateEntities = null;
        StatesListResponse statesListResponse = new StatesListResponse();
        List<StatesList> statesLists = new ArrayList<>();
        stateEntities = addressService.getAllStates();

        for(StateEntity s : stateEntities) {
            StatesList statesList = new StatesList();
            statesList.setId(UUID.fromString(s.getUuid()));
            statesList.stateName(s.getStateName());
            statesLists.add(statesList);
        }
        if(statesLists.size() == 0) {
            statesListResponse.states(null);
        }
        else {
            statesListResponse.states(statesLists);
        }

        return new ResponseEntity<>(statesListResponse, HttpStatus.OK);

        }
}


