package com.upgrad.FoodOrderingApp.service.entity;

import com.upgrad.FoodOrderingApp.service.common.ItemType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name="item")
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "topFivePopularItemsByRestaurant",
                query =
                        "select * from item where id in "
                                + "(select item_id from order_item where order_id in "
                                + "(select id from orders where restaurant_id = ? ) "
                                + "group by order_item.item_id "
                                + "order by (count(order_item.order_id)) "
                                + "desc LIMIT 5)",
                resultClass = ItemEntity.class)
})
@NamedQueries({@NamedQuery(name="getItemById", query="SELECT i from ItemEntity i where i.id = :id"),
        @NamedQuery(name = "itemByUUID", query = "select i from ItemEntity i where i.uuid=:itemUUID"),

})
public class ItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Integer id;

    @NotNull
    @Size(max=200)
    @Column(name="uuid")
    private String uuid;

    @NotNull
    @Size(max=30)
    @Column(name="item_name")
    private String itemName;

    @NotNull
    @Column(name="price")
    private Integer price;

    @NotNull
    @Size(max=10)
    @Column(name="type")
    private ItemType type;

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

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }
}
