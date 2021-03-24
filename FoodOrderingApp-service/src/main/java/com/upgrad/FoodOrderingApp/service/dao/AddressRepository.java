package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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
}
