package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
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

    public CouponEntity getCouponByUUID(String couponUUID) {
        try {
            return entityManager.createNamedQuery("getCouponById", CouponEntity.class)
                    .setParameter("couponUUID", couponUUID)
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

    public List<OrderItemEntity> getOrderItem(OrdersEntity ordersEntity) {
        try {
            return entityManager.createNamedQuery("getOrderItemEntity", OrderItemEntity.class).setParameter("orderEntity", ordersEntity).getResultList();
        }
        catch (Exception e) {
            return null;
        }
    }

    public CouponEntity getCouponById(String uuid) {
        try {
        return entityManager.createNamedQuery("getCouponById", CouponEntity.class).setParameter("uuid", uuid).getSingleResult();
        }catch (NoResultException noResultException) {
            return null;
        }
    }

    public OrdersEntity saveOrder(OrdersEntity ordersEntity) {
        entityManager.persist(ordersEntity);
        return ordersEntity;
    }

    public OrderItemEntity saveOrderItem(OrderItemEntity orderItemEntity) {
        entityManager.persist(orderItemEntity);
        return orderItemEntity;
    }
}