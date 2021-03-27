package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.OrderDao;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<OrdersEntity> getOrdersByCustomer(CustomerEntity customerEntity) {
        return orderDao.getOrdersByCustomer(customerEntity);
    }
}
