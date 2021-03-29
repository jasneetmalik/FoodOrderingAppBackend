package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.api.model.ItemListResponse;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.OrderService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrdersEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/")
public class ItemController {

    @Autowired
    private ItemService itemService;
    @Autowired private RestaurantService restaurantService;

    @Autowired
    private OrderService orderService;

    /* The method handles get Top Five Items By Popularity request & takes restaurant_id as the path variable
    & produces response in ItemListResponse and returns list of 5 items sold by restaurant on basis of popularity  with details from the db. If error returns error code and error message.
    */
    @CrossOrigin
    @RequestMapping(
            method = RequestMethod.GET,
            path = "/item/restaurant/{restaurant_id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<ItemListResponse> getTopFiveItemsForRestaurant(
            @PathVariable("restaurant_id") final String restaurantId) throws RestaurantNotFoundException {
        RestaurantEntity restaurant = restaurantService.restaurantByUUID(restaurantId);
        List<OrdersEntity> ordersEntityList = itemService.getOrdersOfRestaurant(restaurant);
        List<List<OrderItemEntity>> list = new ArrayList<>();
        for (OrdersEntity ordersEntity : ordersEntityList) {
            list.add(orderService.getOrderItem(ordersEntity));
        }
        List<ItemEntity> testList = new ArrayList<>();
        try {
             testList = itemService.getItemsByPopularity(restaurant);
        }
        catch (Exception e) {

        }

        HashMap<ItemEntity, Integer> hashMap = new HashMap<>();
        HashSet<ItemEntity> hashSet = new HashSet<>();

        for (List<OrderItemEntity> list1 : list) {
            for (OrderItemEntity orderItemEntity : list1) {
                ItemEntity itemEntity = orderItemEntity.getItemId();
                int quantity = orderItemEntity.getQuantity();
                if(hashMap.containsKey(itemEntity)) {
                    hashMap.put(itemEntity, (hashMap.get(itemEntity) + quantity));
                }
                else {
                    hashMap.put(itemEntity, quantity);
                }
                hashSet.add(itemEntity);
            }
        }
        int max = Integer.MIN_VALUE;
        ItemEntity itemEntity1 = null;
        ItemListResponse itemListResponse = new ItemListResponse();
        if(hashSet.size() >= 5) {
            for (int i = 0; i < 5; i++) {
                itemEntity1 = null;
                for (ItemEntity itemEntity : hashSet) {
                    if (hashMap.get(itemEntity) > max) {
                        max = hashMap.get(itemEntity);
                        itemEntity1 = itemEntity;
                    }
                }
                max = Integer.MIN_VALUE;
                ItemList itemList = new ItemList();
                itemList.setItemName(itemEntity1.getItemName());
                itemList.setItemType(ItemList.ItemTypeEnum.fromValue(itemEntity1.getType().toString()));
                itemList.setId(UUID.fromString(itemEntity1.getUuid()));
                itemList.setPrice(itemEntity1.getPrice());
                hashSet.remove(itemEntity1);
                itemListResponse.add(i, itemList);

            }
        }
       else  {
           max = Integer.MIN_VALUE;
            if(hashSet.size() == 0) {
                int k = 0;
                for (ItemEntity i : testList) {
                    ItemList itemList = new ItemList();
                    itemList.setItemName(i.getItemName());
                    itemList.setItemType(ItemList.ItemTypeEnum.fromValue(i.getType().toString()));
                    itemList.setId(UUID.fromString(i.getUuid()));
                    itemList.setPrice(i.getPrice());
                    itemListResponse.add(k++, itemList);
                }
            }
            for (int i = 0; i < hashSet.size(); i++) {
                itemEntity1 = null;
                for (ItemEntity itemEntity : hashSet) {
                    if(hashMap.get(itemEntity) > max) {
                        max = hashMap.get(itemEntity);
                        itemEntity1 = itemEntity;
                    }
                }
                ItemList itemList = new ItemList();
                itemList.setItemName(itemEntity1.getItemName());
                itemList.setItemType(ItemList.ItemTypeEnum.fromValue(itemEntity1.getType().toString()));
                itemList.setId(UUID.fromString(itemEntity1.getUuid()));
                itemList.setPrice(itemEntity1.getPrice());
                hashSet.remove(itemEntity1);
                itemListResponse.add(i, itemList);

            }

        }

        return new ResponseEntity<ItemListResponse>(itemListResponse,HttpStatus.OK);

    }
}
