package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.*;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;


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
}
