package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.api.utility.Utility;
import com.upgrad.FoodOrderingApp.service.businness.*;
import com.upgrad.FoodOrderingApp.service.common.ItemType;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.DateFormatter;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private CustomerService customerService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ItemService itemService;

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

    @RequestMapping(method = RequestMethod.GET, path = "/order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CustomerOrderResponse> getUserOrders(@RequestHeader("authorization") String token) throws AuthorizationFailedException {
        final String[] bearerTokens = token.split("Bearer ");
        final String accessToken;
        if (bearerTokens.length == 2) {
            accessToken = bearerTokens[1];
        } else {
            accessToken = token;
        }

        CustomerEntity customer = customerService.getCustomer(accessToken);
        if(Utility.isNullOrEmpty(customer)) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }
        List<OrdersEntity> list = orderService.getOrdersByCustomers(customer);
        Collections.sort(list);
        List<OrderList> listOrder = new ArrayList<>();

        for (OrdersEntity ordersEntity : list) {
            OrderList orderList = new OrderList();
             orderList.setId(UUID.fromString(ordersEntity.getUuid()));
             orderList.setBill(ordersEntity.getBill());
             CouponEntity couponEntity = ordersEntity.getCouponId();
             OrderListCoupon orderListCoupon= new OrderListCoupon();
             orderListCoupon.setId(UUID.fromString(couponEntity.getUuid()));
             orderListCoupon.setCouponName(couponEntity.getCouponName());
             orderListCoupon.setPercent(couponEntity.getPercent());
             orderList.setCoupon(orderListCoupon);
             orderList.setDiscount(ordersEntity.getDiscount());
             orderList.setDate(ordersEntity.getDate().toLocalDateTime().toString());
            OrderListPayment orderListPayment = new OrderListPayment();
            PaymentEntity paymentEntity= ordersEntity.getPaymentId();
            orderListPayment.setId(UUID.fromString(paymentEntity.getUuid()));
            orderListPayment.setPaymentName(paymentEntity.getPaymentName());
            orderList.setPayment(orderListPayment);
            OrderListCustomer orderListCustomer = new OrderListCustomer();
            orderListCustomer.setId(UUID.fromString(customer.getUuid()));
            orderListCustomer.setFirstName(customer.getFirstName());
            orderListCustomer.setLastName(customer.getLastName());
            orderListCustomer.setEmailAddress(customer.getEmail());
            orderListCustomer.setContactNumber(customer.getContactNumber());
            orderList.setCustomer(orderListCustomer);
            AddressEntity addressEntity = ordersEntity.getAddress();
            OrderListAddress orderListAddress = new OrderListAddress();
            orderListAddress.setId(UUID.fromString(addressEntity.getUuid()));
            orderListAddress.setFlatBuildingName(addressEntity.getFlatBuilNumber());
            orderListAddress.setLocality(addressEntity.getLocality());
            orderListAddress.setCity(addressEntity.getCity());
            orderListAddress.setPincode(addressEntity.getPincode());
            OrderListAddressState orderListAddressState = new OrderListAddressState();
            orderListAddressState.setId(UUID.fromString(addressEntity.getStateId().getUuid()));
            orderListAddressState.stateName(addressEntity.getStateId().getStateName());
            orderListAddress.setState(orderListAddressState);
            orderList.setAddress(orderListAddress);
            List<OrderItemEntity> orderItemEntity = orderService.getOrderItem(ordersEntity);
            List<ItemQuantityResponse> itemQuantityResponse  = new ArrayList<>();
            for (OrderItemEntity orderItemEntity1 : orderItemEntity) {
                ItemQuantityResponse itemQuantityResponse1 = new ItemQuantityResponse();
                ItemQuantityResponseItem item = new ItemQuantityResponseItem();
                item.setId(UUID.fromString(orderItemEntity1.getItemId().getUuid()));
                item.setItemName(orderItemEntity1.getItemId().getItemName());
                item.setItemPrice(orderItemEntity1.getItemId().getPrice());
                ItemType type = orderItemEntity1.getItemId().getType();
                item.setType(ItemQuantityResponseItem.TypeEnum.valueOf(type.toString()));
                itemQuantityResponse1.setItem(item);
                itemQuantityResponse1.setPrice(orderItemEntity1.getItemId().getPrice());
                itemQuantityResponse1.setQuantity(orderItemEntity1.getQuantity());
                itemQuantityResponse.add(itemQuantityResponse1);
            }
            orderList.setItemQuantities(itemQuantityResponse);
            listOrder.add(orderList);

        }
     CustomerOrderResponse customerOrderResponse = new CustomerOrderResponse();
     customerOrderResponse.setOrders(listOrder);
     return new ResponseEntity<>(customerOrderResponse, HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.POST, path = "/order", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveOrderResponse> saveOrder(@RequestHeader("authorization") String token, @RequestBody() SaveOrderRequest saveOrderRequest) throws AuthorizationFailedException, CouponNotFoundException, AddressNotFoundException, PaymentMethodNotFoundException, ItemNotFoundException {
        final String[] bearerTokens = token.split("Bearer ");
        final String accessToken;
        if (bearerTokens.length == 2) {
            accessToken = bearerTokens[1];
        } else {
            accessToken = token;
        }

        CustomerEntity customer = customerService.getCustomer(accessToken);
        if(Utility.isNullOrEmpty(customer)) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }
            CouponEntity couponEntity = orderService.getCouponByCouponId(saveOrderRequest.getCouponId().toString());
            AddressEntity addressEntity = addressService.getAddressByUUID(saveOrderRequest.getAddressId(), customer);
            PaymentEntity paymentEntity = paymentService.getPaymentByUUID(saveOrderRequest.getPaymentId().toString());
            RestaurantEntity restaurantEntity = restaurantService.getRestaurantByUUId(saveOrderRequest.getRestaurantId().toString());

            OrdersEntity ordersEntity = new OrdersEntity();
            ordersEntity.setPaymentId(paymentEntity);
            ordersEntity.setUuid(UUID.randomUUID().toString());
            ordersEntity.setDate(ZonedDateTime.now());
            ordersEntity.setAddress(addressEntity);
            ordersEntity.setCustomer(customer);
            ordersEntity.setCouponId(couponEntity);
            ordersEntity.setRestaurant(restaurantEntity);
            ordersEntity.setBill(saveOrderRequest.getBill());
            ordersEntity.setDiscount(saveOrderRequest.getDiscount());

            ordersEntity = orderService.saveOrder(ordersEntity);

            List<ItemQuantity> list = saveOrderRequest.getItemQuantities();


            List<OrderItemEntity> orderItemEntityList = new ArrayList<>();
                    for (ItemQuantity itemQuantity : list) {
                        OrderItemEntity orderItemEntity = new OrderItemEntity();
                        ItemEntity itemEntity = itemService.getItemByUUID(itemQuantity.getItemId().toString());
                        orderItemEntity.setItemId(itemEntity);
                        orderItemEntity.setPrice(itemEntity.getPrice());
                        orderItemEntity.setQuantity(itemQuantity.getQuantity());
                        orderItemEntity.setOrderId(ordersEntity);
                        orderItemEntityList.add(orderItemEntity);
                    }

                    for (OrderItemEntity orderItemEntity : orderItemEntityList) {
                        orderService.saveOrderItem(orderItemEntity);
                    }

                 SaveOrderResponse saveOrderResponse = new SaveOrderResponse();
                    saveOrderResponse.setStatus("ORDER SUCCESSFULLY PLACED");
                    saveOrderResponse.setId(ordersEntity.getUuid());

                    return new ResponseEntity<>(saveOrderResponse, HttpStatus.CREATED);

        }
}


