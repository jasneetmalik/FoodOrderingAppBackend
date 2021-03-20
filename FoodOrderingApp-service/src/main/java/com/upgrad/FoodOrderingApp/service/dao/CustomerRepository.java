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

//Create Customer using Signup Endpoint
    public CustomerEntity createUser(CustomerEntity user) {
        entityManager.persist(user);
        return user;
    }
//Check if ContactNumber exists in Database
    public CustomerEntity checkContact(String contact) {
        try {
            return entityManager.createNamedQuery("getCustomerByContact", CustomerEntity.class).setParameter("contact", contact).getSingleResult();

        }
        catch (NoResultException noResultException) {
            return null;
        }
    }
//Check Password
    public CustomerEntity checkPassword(String password) {
        try {
            return entityManager.createNamedQuery("getCustomerByPassword", CustomerEntity.class).setParameter("password", password).getSingleResult();
        }
        catch (NoResultException noResultException) {
            return null;
        }
    }
//Autheticate using Contact Number and Password
    public boolean authenticate(String contact, String password) {
        try {
            entityManager.createNamedQuery("getCustomerByContactAndPassword", CustomerEntity.class).setParameter("contact", contact).setParameter("password", password).getSingleResult();
            return true;
        }
        catch (NoResultException noResultException) {
            return false;
        }
    }
//Save Authentication details to Customer Auth table
    public CustomerAuthEntity saveAuth(CustomerAuthEntity customerAuthEntity) {
        entityManager.persist(customerAuthEntity);
        return customerAuthEntity;
    }

//Check if a customer exists in CustomerAuth table for the specified Access Token
    public CustomerAuthEntity findCustomerAuthByAccessToken(final String accessToken) {
        final CustomerAuthEntity loggedInCustomerAuth;
        try {
            loggedInCustomerAuth = entityManager
                    .createNamedQuery("getCustomerAuthByToken", CustomerAuthEntity.class)
                    .setParameter("authentication", accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
        return loggedInCustomerAuth;
    }
//Update Authentication Details in CustomerAuth table
public CustomerAuthEntity update(CustomerAuthEntity customerAuthEntity) {
    entityManager.merge(customerAuthEntity);
    return customerAuthEntity;
}
}
