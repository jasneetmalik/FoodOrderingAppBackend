package com.upgrad.FoodOrderingApp.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upgrad.FoodOrderingApp.api.model.CustomerOrderResponse;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.businness.OrderService;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// This class contains all the test cases regarding the order controller
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService mockOrderService;

    @MockBean
    private CustomerService mockCustomerService;

    // ------------------------------------------ GET /order/coupon/{coupon_name} ------------------------------------------

    //This test case passes when you are able to retrieve coupon details by coupon name.
    @Test
    public void shouldGetCouponByName() throws Exception {
        when(mockCustomerService.getCustomer("database_accesstoken2"))
                .thenReturn(new CustomerEntity());

        final String couponId = UUID.randomUUID().toString();
        final CouponEntity couponEntity = new CouponEntity(couponId, "myCoupon", 10);
        when(mockOrderService.getCouponByCouponName("myCoupon")).thenReturn(couponEntity);

        mockMvc
                .perform(get("/order/coupon/myCoupon")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .header("authorization", "Bearer database_accesstoken2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(couponId))
                .andExpect(jsonPath("coupon_name").value("myCoupon"));
        verify(mockCustomerService, times(1)).getCustomer("database_accesstoken2");
        verify(mockOrderService, times(1)).getCouponByCouponName("myCoupon");
    }

    //This test case passes when you have handled the exception of trying to fetch coupon details if you are not logged in.
    @Test
    public void shouldNotGetCouponByNameIfCustomerIsNotLoggedIn() throws Exception {
        when(mockCustomerService.getCustomer("invalid_auth"))
                .thenThrow(new AuthorizationFailedException("ATHR-001", "Customer is not Logged in."));
        mockMvc
                .perform(get("/order/coupon/myCoupon")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .header("authorization", "Bearer invalid_auth"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("code").value("ATHR-001"));

        verify(mockCustomerService, times(1)).getCustomer("invalid_auth");
        verify(mockOrderService, times(0)).getCouponByCouponName(anyString());
    }
}
