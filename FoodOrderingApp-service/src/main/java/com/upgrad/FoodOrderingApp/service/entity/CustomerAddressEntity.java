package com.upgrad.FoodOrderingApp.service.entity;
import java.io.Serializable;
import javax.persistence.*;

@Entity
@Table(name = "customer_address")

//MAPPING TABLE BETWEEN CUSTOMER AND ADDRESSES

@NamedQueries({@NamedQuery(name = "getEntityByCustomer", query = "select cae from CustomerAddressEntity cae where cae.customer = :customer")})
public class CustomerAddressEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    //Foreign Key Link to Customer ID
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id")
    private CustomerEntity customer;

    //Foreign Key Link to Address ID
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    private AddressEntity address;

    //GETTERS AND SETTERS

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

}
