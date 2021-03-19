package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;


@Repository
public class CustomerRepository {
    @PersistenceContext
    EntityManager entityManager;


    public CustomerEntity createUser(CustomerEntity user) {
        entityManager.persist(user);
        return user;
    }

    public CustomerEntity checkContact(String contact) {
        try {
            return entityManager.createNamedQuery("getCustomerByContact", CustomerEntity.class).setParameter("contact", contact).getSingleResult();

        }
        catch (NoResultException noResultException) {
            return null;
        }
    }

    public CustomerEntity checkPassword(String password) {
        try {
            return entityManager.createNamedQuery("getCustomerByPassword", CustomerEntity.class).setParameter("password", password).getSingleResult();
        }
        catch (NoResultException noResultException) {
            return null;
        }
    }

    public boolean authenticate(String contact, String password) {
        try {
            entityManager.createNamedQuery("getCustomerByContactAndPassword", CustomerEntity.class).setParameter("contact", contact).setParameter("password", password).getSingleResult();
            return true;
        }
        catch (NoResultException noResultException) {
            return false;
        }
    }

    public CustomerAuthEntity saveAuth(CustomerAuthEntity customerAuthEntity) {
        entityManager.persist(customerAuthEntity);
        return customerAuthEntity;
    }
}
