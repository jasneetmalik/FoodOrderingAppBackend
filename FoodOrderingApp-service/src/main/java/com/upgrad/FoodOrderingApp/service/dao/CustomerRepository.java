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
    public CustomerEntity createCustomer(CustomerEntity customer) {
        entityManager.persist(customer);
        return customer;
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
//Update Customer
    public CustomerEntity updateCustomer(CustomerEntity customer) {
        entityManager.merge(customer);
        return customer;
    }

}
