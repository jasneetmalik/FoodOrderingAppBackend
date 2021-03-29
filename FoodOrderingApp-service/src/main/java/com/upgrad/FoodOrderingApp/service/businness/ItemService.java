package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.ItemNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ItemService {

    @Autowired
    private ItemDao itemDao;
    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private CategoryService categoryService;

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

    public List<ItemEntity> getItemsByCategoryAndRestaurant(String restaurantId, String categoryId) throws RestaurantNotFoundException, CategoryNotFoundException {
        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurantId);
        if (restaurantEntity == null) throw new RestaurantNotFoundException("","");

        CategoryEntity categoryEntity = categoryService.getCategoryById(categoryId);
        if (categoryEntity == null) throw new CategoryNotFoundException("","");
        List<ItemEntity> restaurantItemList = new ArrayList<>();

        for (ItemEntity restaurantItem : restaurantEntity.getItems()) {
            for (ItemEntity categoryItem : categoryEntity.getItems()) {
                if (restaurantItem.getUuid().equals(categoryItem.getUuid())) {
                    restaurantItemList.add(restaurantItem);
                }
            }
        }
        restaurantItemList.sort(Comparator.comparing(ItemEntity::getItemName));
        return restaurantItemList;
    }
}
