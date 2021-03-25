package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;


@Repository
public class AddressRepository {
    @PersistenceContext
    EntityManager entityManager;

    public AddressEntity saveAddress(CustomerEntity customerEntity, AddressEntity addressEntity) {

        entityManager.persist(addressEntity);
        return addressEntity;
    }

    public StateEntity getStateByUuid(String uuid) {
        try {
            return entityManager.createNamedQuery("getStateById", StateEntity.class).setParameter("uuid", uuid).getSingleResult();
        }
        catch (Exception e) {

            return null;
        }

    }

    public CustomerAddressEntity saveCustomerAddressEntity(CustomerAddressEntity customerAddressEntity) {
        entityManager.persist(customerAddressEntity);
        return customerAddressEntity;
    }

    public List<AddressEntity> getAllAddress(CustomerEntity customer) {
        CustomerAddressEntity customerAddressEntity = null;
        List<CustomerAddressEntity> customerAddressEntities;
        try {
            customerAddressEntities = entityManager.createNamedQuery("getEntityByCustomer", CustomerAddressEntity.class).setParameter("customer", customer).getResultList();
        }
        catch (Exception e) {
            return null;
        }
        List<AddressEntity> addressEntityList = new ArrayList<>();
        for (int i = 0; i < customerAddressEntities.size(); i++){
            addressEntityList.add(customerAddressEntities.get(i).getAddress());
        }

        return addressEntityList;

    }

    public AddressEntity getAddress(String uuid, CustomerEntity customer) throws AuthorizationFailedException, AddressNotFoundException {

        List<CustomerAddressEntity> customerAddressEntities = null;
        AddressEntity addressEntity = null;
        boolean flag = false;
        try {
            customerAddressEntities = entityManager.createNamedQuery("getEntityByCustomer", CustomerAddressEntity.class).setParameter("customer", customer).getResultList();
        }
        catch (Exception e) {
            return null;
        }

        try{
            addressEntity= entityManager.createNamedQuery("getAddressFromUuid", AddressEntity.class).setParameter("uuid", uuid).getSingleResult();
        }
        catch (Exception e) {
            throw new AddressNotFoundException("ANF-003", "No address by this id");
        }
        for (int i = 0; i < customerAddressEntities.size(); i++) {
                if(customerAddressEntities.get(i).getAddress().equals(addressEntity)) {
                    flag = true;

                }
        }
        if(!flag) {
            throw new AuthorizationFailedException("ATHR-004", "You are not authorized to view/update/delete any one else's address");
        }
        return addressEntity;
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAddress(AddressEntity addressEntity) {
            entityManager.remove(addressEntity);
    }
}
