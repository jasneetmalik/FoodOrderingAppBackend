package com.upgrad.FoodOrderingApp.service.entity;

import java.io.Serializable;
import javax.persistence.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "category")

@NamedQueries({
        @NamedQuery(name = "getCategoryByUuid", query = "select q from CategoryEntity q where q.uuid = :uuid"),
        @NamedQuery(name = "allCategories", query = "SELECT q FROM CategoryEntity q ORDER BY q.categoryName"),
})

public class CategoryEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "uuid")
    @Size(max = 200)
    @NotNull
    private String uuid;

    @Column(name = "category_name")
    @Size(max = 255)
    @NotNull
    private String categoryName;

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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
