package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderDao orderDao;
    //Get Coupon by Coupon Name - “/order/coupon/{coupon_name}
    public CouponEntity getCouponByCouponName(String name) throws CouponNotFoundException {
        if (name.equals("")) {
            throw new CouponNotFoundException("CPF-002", "Coupon name field should not be empty");
        }
        CouponEntity coupon = orderDao.getCouponByName(name);
        if (coupon == null) {
            throw new CouponNotFoundException("CPF-001","No coupon by this name");
        }
        return coupon;
    }

    public List<OrdersEntity> getOrdersByCustomers(CustomerEntity customerEntity) {
        return orderDao.getOrdersByCustomer(customerEntity);
    }

    public List<OrderItemEntity> getOrderItem(OrdersEntity ordersEntity) {
        return orderDao.getOrderItem(ordersEntity);
    }

    public CouponEntity getCouponByCouponId(String uuid) throws CouponNotFoundException {
        CouponEntity couponEntity = orderDao.getCouponById(uuid);
        if(couponEntity == null) {
            throw new CouponNotFoundException("CPF-002", "No coupon by this id");
        }
        return couponEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public OrdersEntity saveOrder(OrdersEntity ordersEntity) {
        return orderDao.saveOrder(ordersEntity);

    }


    @Transactional(propagation = Propagation.REQUIRED)
    public OrderItemEntity saveOrderItem(OrderItemEntity orderItemEntity) {
        return orderDao.saveOrderItem(orderItemEntity);
    }
}
