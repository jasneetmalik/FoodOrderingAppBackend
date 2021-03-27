package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/")
public class RestaurantController {
    @Autowired
    private RestaurantService restaurantService;

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
                    .flatBuildingName(restaurant.getAddress().getFlatBuilNumber())
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
}