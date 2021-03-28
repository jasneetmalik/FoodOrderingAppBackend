package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.AddressRepository;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
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
    public AddressEntity saveAddress(CustomerEntity customerEntity, AddressEntity addressEntity) throws SaveAddressException {
        if((addressEntity.getCity().isEmpty()) || (addressEntity.getLocality().isEmpty()) || (addressEntity.getPincode().isEmpty())  || (addressEntity.getFlatBuilNo().isEmpty())) {
            throw new SaveAddressException("SAR-001", "No field can be empty");
        }

        if((addressEntity.getPincode().length() != 6)) {
            throw new SaveAddressException("SAR-002","Invalid pincode");
        }
        try {
            Integer.parseInt(addressEntity.getPincode());
        }
        catch (Exception e) {
            throw new SaveAddressException("SAR-002","Invalid pincode");
        }
        CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();
        customerAddressEntity.setCustomer(customerEntity);
        addressEntity = repository.saveAddress(customerEntity, addressEntity);
        customerAddressEntity.setAddress(addressEntity);
        repository.saveCustomerAddressEntity(customerAddressEntity);
        return addressEntity;

    }

    public List<AddressEntity> getAllAddress(CustomerEntity customer) {


       return repository.getAllAddress(customer);


    }


    public AddressEntity getAddressByUUID(String uuid, CustomerEntity customerEntity) throws AddressNotFoundException, AuthorizationFailedException {

        if(uuid.isEmpty()) {
            throw new AddressNotFoundException("ANF-005", "Address id can not be empty");
        }
        AddressEntity addressEntity =repository.getAddress(uuid, customerEntity);

       return addressEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity deleteAddress(AddressEntity addressEntity) {
        repository.deleteAddress(addressEntity);
        AddressEntity addressEntity1 = new AddressEntity();
        addressEntity1.setUuid(UUID.randomUUID().toString());
        return addressEntity1;
    }

    public List<StateEntity> getAllStates() {
        return repository.getAllStates();
    }
}
