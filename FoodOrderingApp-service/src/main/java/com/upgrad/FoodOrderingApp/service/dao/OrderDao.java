package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class OrderDao {
    @PersistenceContext
    private EntityManager entityManager;

    //Get Coupon by Coupon Name - “/order/coupon/{coupon_name}
    public CouponEntity getCouponByName(String couponName) {
        try {
            return entityManager.createNamedQuery("getCouponByName", CouponEntity.class)
                    .setParameter("couponName", couponName)
                    .getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }

    }

    public List<OrdersEntity> getOrdersByCustomer(CustomerEntity customerEntity) {

        try {
            return entityManager.createNamedQuery("getOrdersByCustomer", OrdersEntity.class).setParameter("customer", customerEntity).getResultList();
        }
        catch (Exception e) {
            return null;
        }
    }
}