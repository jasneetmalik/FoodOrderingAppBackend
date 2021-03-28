package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.ItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemDao itemDao;

    public List<ItemEntity> getItemsByPopularity(RestaurantEntity restaurantEntity) {
        return itemDao.getTop5ItemsByRestaurant(restaurantEntity);
    }

    public ItemEntity getItemByUUID(String itemUUID) throws ItemNotFoundException {
        ItemEntity item = itemDao.getItemByUUID(itemUUID);
        if (item == null) {
            throw new ItemNotFoundException("INF-003", "No item by this id exist");
        }
        return item;
    }

    public List<OrdersEntity> getOrdersOfRestaurant(RestaurantEntity restaurantEntity) {
        return itemDao.getOrders(restaurantEntity);
    }

}
