package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;

@Entity
@Table(name = "customer")
@NamedQueries({@NamedQuery(name = "getCustomerByContact", query = "select c from CustomerEntity c where c.contactNumber = :contact"),
@NamedQuery(name = "getCustomerByPassword", query = "select c from CustomerEntity  c where c.password = :password"),
@NamedQuery(name = "getCustomerByContactAndPassword", query = "select c from CustomerEntity c where c.contactNumber = :contact and c.password = :password")})
public class CustomerEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "salt")
    private String salt;

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}