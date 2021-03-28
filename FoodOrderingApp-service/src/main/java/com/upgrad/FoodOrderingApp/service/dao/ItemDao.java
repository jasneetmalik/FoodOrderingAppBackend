package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Collections;
import java.util.List;

@Repository
public class ItemDao {

    @PersistenceContext
    private EntityManager entityManager;

    public List<ItemEntity> getTop5ItemsByRestaurant(RestaurantEntity restaurant) {
        List<ItemEntity> items =
                entityManager
                        .createNamedQuery("topFivePopularItemsByRestaurant", ItemEntity.class)
                        .setParameter(0, restaurant.getId())
                        .getResultList();
        if (items != null) {
            return items;
        }
        return Collections.emptyList();
    }

    public ItemEntity getItemByUUID(String itemUUID) {
        try {
            ItemEntity item =
                    entityManager
                            .createNamedQuery("itemByUUID", ItemEntity.class)
                            .setParameter("itemUUID", itemUUID)
                            .getSingleResult();
            return item;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<OrdersEntity> getOrders(RestaurantEntity restaurantEntity) {
        try {
            return entityManager.createNamedQuery("GetOrdersOfRestaurant", OrdersEntity.class).setParameter("restaurant", restaurantEntity).getResultList();
        }
        catch (NoResultException noResultException) {
            return null;
        }
    }


}