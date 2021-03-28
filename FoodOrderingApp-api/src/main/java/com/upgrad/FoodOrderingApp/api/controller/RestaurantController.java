package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/")
public class RestaurantController {
    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private CategoryService categoryService;

    @RequestMapping(method = RequestMethod.GET, path = "/restaurant", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getAllRestaurants() {

        List<RestaurantEntity> restaurantsList = restaurantService.getAllRestaurants();

        RestaurantListResponse restaurantListResponse = new RestaurantListResponse();

        for (RestaurantEntity restaurant : restaurantsList) {
            /** Restaurant has following Response Objects -
             * RestaurantDetailsResponseAddressState, RestaurantDetailsResponseAddress,RestaurantListResponse
             * Get Address from Restaurant using ManytoOne relation, from Address table
             * use StateID Foreign key to get State UUID and State Name.  **/

            RestaurantDetailsResponseAddressState addressState = new RestaurantDetailsResponseAddressState()
                    .id(UUID.fromString(restaurant.getAddress().getStateId().getUuid()))
                    .stateName(restaurant.getAddress().getStateId().getStateName());

            RestaurantDetailsResponseAddress address = new RestaurantDetailsResponseAddress()
                    .id(UUID.fromString(restaurant.getAddress().getUuid()))
                    .city(restaurant.getAddress().getCity())
                    .flatBuildingName(restaurant.getAddress().getFlatBuilNo())
                    .locality(restaurant.getAddress().getLocality())
                    .pincode(restaurant.getAddress().getPincode())
                    .state(addressState);

            String categoriesString = "";
            List<CategoryEntity> categoryList = restaurant.getCategories();
            int i = 0;
            for (CategoryEntity category : categoryList) {
                i++;
                if (i == categoryList.size()) {
                    categoriesString += category.getCategoryName();
                } else {
                    categoriesString += category.getCategoryName() + ", ";
                }
            }
                RestaurantList restaurantList = new RestaurantList()
                        .id(UUID.fromString(restaurant.getUuid()))
                        .restaurantName(restaurant.getRestaurantName())
                        .photoURL(restaurant.getPhotoUrl())
                        .averagePrice(restaurant.getAvgPrice())
                        .customerRating(BigDecimal.valueOf(restaurant.getCustomerRating()))
                        .numberCustomersRated(restaurant.getNumberCustomersRated())
                        .address(address)
                        .categories(categoriesString);
                restaurantListResponse.addRestaurantsItem(restaurantList);
            }
            return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "restaurant/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantDetailsResponse> getRestaurantById(@PathVariable("restaurant_id") final String restaurantId)
        throws RestaurantNotFoundException {
        if (restaurantId == "") {
            throw new RestaurantNotFoundException("RNF-002", "Restaurant id field should not be empty");
        }
        RestaurantEntity restaurant = restaurantService.restaurantByUUID(restaurantId);
        if (restaurant == null) {
            throw new RestaurantNotFoundException("RNF-001","No restaurant by this id");
        }
        RestaurantDetailsResponseAddressState addressState = new RestaurantDetailsResponseAddressState()
                .id(UUID.fromString(restaurant.getAddress().getStateId().getUuid()))
                .stateName(restaurant.getAddress().getStateId().getStateName());

        RestaurantDetailsResponseAddress address = new RestaurantDetailsResponseAddress()
                .id(UUID.fromString(restaurant.getAddress().getUuid()))
                .city(restaurant.getAddress().getCity())
                .flatBuildingName(restaurant.getAddress().getFlatBuilNo())
                .locality(restaurant.getAddress().getLocality())
                .pincode(restaurant.getAddress().getPincode())
                .state(addressState);
        List<CategoryEntity> categoryList = restaurant.getCategories();
        CategoryList categories = new CategoryList();
        for (CategoryEntity category : categoryList) {
            List<ItemEntity> categoryItems = category.getItems();
            ItemList itemList = new ItemList();
            for (ItemEntity item: categoryItems) {
                itemList.id(UUID.fromString(item.getUuid()))
                        .itemName(item.getItemName())
                        .itemType(ItemList.ItemTypeEnum.fromValue(item.getType().getValue()))
                        .price(item.getPrice());
            }
            categories.id(UUID.fromString(category.getUuid()))
                    .categoryName(category.getCategoryName())
                    .addItemListItem(itemList);
        }

        RestaurantDetailsResponse restaurantDetailsResponse = new RestaurantDetailsResponse()
                .id(UUID.fromString(restaurant.getUuid()))
                .restaurantName(restaurant.getRestaurantName())
                .photoURL(restaurant.getPhotoUrl())
                .customerRating(BigDecimal.valueOf(restaurant.getCustomerRating()))
                .averagePrice(restaurant.getAvgPrice())
                .numberCustomersRated(restaurant.getNumberCustomersRated())
                .address(address)
                .addCategoriesItem(categories);
        return new ResponseEntity<RestaurantDetailsResponse>(restaurantDetailsResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "restaurant/category/{category_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantsByCategoryId(@PathVariable("category_id") final String categoryId)
            throws CategoryNotFoundException {

        if (categoryId.equals("")) {
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }

        CategoryEntity categoryEntity = categoryService.getCategoryByUuid(categoryId);
        if (categoryEntity == null) {
            throw new CategoryNotFoundException("CNF-001","No category by this id");
        }

        List<RestaurantCategoryEntity> restaurantsInCategory = restaurantService.getRestaurantsByCategoryId(categoryEntity.getId());
        if (restaurantsInCategory.size() == 0) {
            RestaurantListResponse emptyRestaurantDetailsResponse = new RestaurantListResponse();
            return new ResponseEntity<RestaurantListResponse>(emptyRestaurantDetailsResponse, HttpStatus.NO_CONTENT);
        }
        RestaurantListResponse restaurantListResponse = new RestaurantListResponse();

        List<RestaurantList> restaurantLists = new ArrayList<RestaurantList>();
        for (RestaurantCategoryEntity restaurantCategory: restaurantsInCategory) {
            RestaurantEntity restaurant = restaurantCategory.getRestaurantId();

            RestaurantList restaurantList = new RestaurantList();
            restaurantList.setId(UUID.fromString(restaurant.getUuid()));
            restaurantList.setRestaurantName(restaurant.getRestaurantName());
            restaurantList.setPhotoURL(restaurant.getPhotoUrl());
            restaurantList.setCustomerRating(BigDecimal.valueOf(restaurant.getCustomerRating()));
            restaurantList.setAveragePrice(restaurant.getAvgPrice());
            restaurantList.setNumberCustomersRated(restaurant.getNumberCustomersRated());

            RestaurantDetailsResponseAddressState addressState = new RestaurantDetailsResponseAddressState()
                    .id(UUID.fromString(restaurant.getAddress().getStateId().getUuid()))
                    .stateName(restaurant.getAddress().getStateId().getStateName());

            RestaurantDetailsResponseAddress address = new RestaurantDetailsResponseAddress()
                    .id(UUID.fromString(restaurant.getAddress().getUuid()))
                    .city(restaurant.getAddress().getCity())
                    .flatBuildingName(restaurant.getAddress().getFlatBuilNo())
                    .locality(restaurant.getAddress().getLocality())
                    .pincode(restaurant.getAddress().getPincode())
                    .state(addressState);

            List<String> categoryLists = new ArrayList();
            for (CategoryEntity category :restaurant.getCategories()) {
                categoryLists.add(category.getCategoryName());
            }

            Collections.sort(categoryLists);

            restaurantList.setAddress(address);
            restaurantList.setCategories(String.join(",", categoryLists));

            restaurantLists.add(restaurantList);
            restaurantListResponse.addRestaurantsItem(restaurantList);

        }

        return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
    }

}