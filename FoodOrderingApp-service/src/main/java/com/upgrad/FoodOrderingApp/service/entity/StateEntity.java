package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "state")
@NamedQueries({@NamedQuery(name = "getStateById", query = "select s from StateEntity s where s.uuid = :uuid"),
@NamedQuery(name = "getAllStates", query = "select s from StateEntity s")})
public class StateEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Column(name="uuid")
    @NotNull
    @Size(max = 200)
    private String uuid;

    @Column(name = "state_name")
    @NotNull
    @Size(max = 30)
    private String stateName;

    @OneToMany(mappedBy = "stateId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final List<AddressEntity> Addresses = new ArrayList<>();

    //GETTERS AND SETTERS
    public Integer getId() {
        return Id;
    }
    public void setId(Integer id) {
        Id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
}
