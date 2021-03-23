package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressRepository;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AddressService {

    @Autowired
    AddressRepository repository;

    public StateEntity getStateByUUID(String uuid) throws AddressNotFoundException {
        StateEntity stateEntity = repository.getStateByUuid(uuid);
        if(stateEntity == null) {
       throw new AddressNotFoundException("ANF-002", "No state by this id");
    }
    else return stateEntity;
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity saveAddress(CustomerEntity customerEntity, AddressEntity addressEntity) {
        CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();
        customerAddressEntity.setCustomer(customerEntity);
        addressEntity = repository.saveAddress(customerEntity, addressEntity);
        customerAddressEntity.setAddress(addressEntity);
        repository.saveCustomerAddressEntity(customerAddressEntity);
        return addressEntity;

    }
}
