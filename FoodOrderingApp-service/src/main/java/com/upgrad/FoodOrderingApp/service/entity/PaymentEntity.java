package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "payment")
@NamedQueries({
        @NamedQuery(name="getPaymentMethods", query="select p from PaymentEntity p"),
        @NamedQuery(name="getPaymentMethodByUUID", query="select p from PaymentEntity p where p.uuid = :paymentMethodUUID")})

public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "uuid")
    @NotNull
    @Size(max = 200)
    private String uuid;

    @Column(name = "payment_name")
    @Size(max = 255)
    private String paymentName;

    public PaymentEntity() {
    }

    public PaymentEntity(@NotNull @Size(max = 200) String uuid, @Size(max = 255) String paymentName) {
        this.uuid = uuid;
        this.paymentName = paymentName;
    }


    public Integer getId() {
        return id;
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

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;
    }
}
