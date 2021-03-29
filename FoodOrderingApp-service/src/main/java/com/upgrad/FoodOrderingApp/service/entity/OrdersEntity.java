package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Date;

@Entity
@Table(name = "orders")
@NamedQueries({@NamedQuery(name = "getOrdersByCustomer", query = "select o from OrdersEntity o where o.customer = :customer"),
@NamedQuery(name = "GetOrdersOfRestaurant", query = "select o from OrdersEntity o where o.restaurant = :restaurant")})
public class OrdersEntity implements Comparable<OrdersEntity> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @Column(name = "uuid")
    @NotNull
    @Size(max = 200)
    private String uuid;

    @Column(name = "bill")
    @NotNull
    private double bill;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "coupon_id")
    private CouponEntity couponId;

    @Column(name = "discount")
    private double discount;

    @Column(name = "date")
    private Date date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_id")
    private PaymentEntity paymentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    private AddressEntity address;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant_id")
    private RestaurantEntity restaurant;

    public OrdersEntity() {
    }

    public OrdersEntity(@NotNull @Size(max = 200) String uuid, @NotNull double bill, CouponEntity couponId, double discount, Date date, PaymentEntity paymentId, CustomerEntity customer, AddressEntity address, RestaurantEntity restaurant) {
        this.uuid = uuid;
        this.bill = bill;
        this.couponId = couponId;
        this.discount = discount;
        this.date = date;
        this.paymentId = paymentId;
        this.customer = customer;
        this.address = address;
        this.restaurant = restaurant;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public double getBill() {
        return bill;
    }

    public void setBill(double bill) {
        this.bill = bill;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getDiscount() {
        return discount;
    }

    public Date getDate() {
        return date;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public CouponEntity getCouponId() {
        return couponId;
    }

    public void setCouponId(CouponEntity couponId) {
        this.couponId = couponId;
    }

    public PaymentEntity getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(PaymentEntity paymentId) {
        this.paymentId = paymentId;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    public RestaurantEntity getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(RestaurantEntity restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public int compareTo(OrdersEntity o) {
       if(o.getDate().after(this.getDate())) {
           return 1;
       }
       else {
           return -1;
       }
    }
}
