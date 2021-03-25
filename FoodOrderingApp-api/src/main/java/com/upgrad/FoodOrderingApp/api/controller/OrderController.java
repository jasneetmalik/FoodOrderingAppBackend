package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CouponDetailsResponse;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.businness.OrderService;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private CustomerService customerService;

    @RequestMapping(method = RequestMethod.GET, path = "order/coupon/{coupon_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CouponDetailsResponse> getCouponByName(
            @PathVariable(value = "coupon_name") final String couponName,
            @RequestHeader(value = "authorization") final String authorization)
            throws AuthorizationFailedException, CouponNotFoundException {

        String accessToken = authorization.split("Bearer ")[1];
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        CouponEntity coupon = orderService.getCouponByCouponName(couponName);
        CouponDetailsResponse couponDetailsResponse = new CouponDetailsResponse()
                .couponName(coupon.getCouponName())
                .id(UUID.fromString(coupon.getUuid()))
                .percent(coupon.getPercent());
        return new ResponseEntity<CouponDetailsResponse>(couponDetailsResponse, HttpStatus.OK);
    }
}
