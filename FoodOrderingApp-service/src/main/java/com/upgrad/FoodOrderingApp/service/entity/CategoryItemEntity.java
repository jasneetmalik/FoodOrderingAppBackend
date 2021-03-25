package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;

@Entity
@Table(name = "category_item")
@NamedQueries({
        @NamedQuery(name="getItemsByCategory", query="SELECT i FROM CategoryItemEntity i WHERE i.categoryId = :categoryId")
})
public class CategoryItemEntity {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    //Link to item Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    private ItemEntity itemId;

    //Link to Category Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private CategoryEntity categoryId;

    //GETTERS AND SETTERS
    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public ItemEntity getItemId() {
        return itemId;
    }

    public void setItemId(ItemEntity itemId) {
        this.itemId = itemId;
    }

    public CategoryEntity getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(CategoryEntity categoryId) {
        this.categoryId = categoryId;
    }
}
