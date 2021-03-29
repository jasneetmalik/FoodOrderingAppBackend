package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name="address")

@NamedQueries({@NamedQuery(name = "getAddressFromUuid", query = "select a from AddressEntity a where a.uuid = :uuid")})
public class AddressEntity implements Serializable {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid")
    @NotNull
    @Size(max = 200)
    private String uuid;

    @Column(name = "flat_buil_number")
    @Size(max = 255)
    private String flatBuilNo;

    @Column(name = "locality")
    @Size(max = 255)
    private String locality;

    @Column(name = "city")
    @Size(max = 30)
    private String city;

    @Column(name = "pincode")
    private String pincode;

    //REFERENCES STATE TABLE
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "state_id")
    @NotNull
    private StateEntity state;

    @Column(name = "active")
    private int active;

    public AddressEntity() {
    }

    public AddressEntity(@NotNull @Size(max = 200) String uuid, @Size(max = 255) String flatBuilNumber, @Size(max = 255) String locality, @Size(max = 30) String city, String pincode, @NotNull StateEntity stateId) {
        this.uuid = uuid;
        this.flatBuilNo = flatBuilNumber;
        this.locality = locality;
        this.city = city;
        this.pincode = pincode;
        this.state = stateId;
    }

    //GETTERS AND SETTERS
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

    public String getFlatBuilNo() {
        return flatBuilNo;
    }

    public void setFlatBuilNo(String flatBuilNumber) {
        this.flatBuilNo = flatBuilNumber;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public StateEntity getState() {
        return state;
    }

    public void setState(StateEntity stateId) {
        this.state = stateId;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }
}