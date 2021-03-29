package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
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
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/")
public class RestaurantController {
    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private CustomerService customerService;

    @RequestMapping(method = RequestMethod.GET, path = "/restaurant", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getAllRestaurants() throws RestaurantNotFoundException {

        List<RestaurantEntity> restaurantsList = restaurantService.restaurantsByRating();

        RestaurantListResponse restaurantListResponse = new RestaurantListResponse();

        for (RestaurantEntity restaurant : restaurantsList) {
            /** Restaurant has following Response Objects -
             * RestaurantDetailsResponseAddressState, RestaurantDetailsResponseAddress,RestaurantListResponse
             * Get Address from Restaurant using ManytoOne relation, from Address table
             * use StateID Foreign key to get State UUID and State Name.  **/

            RestaurantDetailsResponseAddressState addressState = new RestaurantDetailsResponseAddressState()
                    .id(UUID.fromString(restaurant.getAddress().getState().getUuid()))
                    .stateName(restaurant.getAddress().getState().getStateName());

            RestaurantDetailsResponseAddress address = new RestaurantDetailsResponseAddress()
                    .id(UUID.fromString(restaurant.getAddress().getUuid()))
                    .city(restaurant.getAddress().getCity())
                    .flatBuildingName(restaurant.getAddress().getFlatBuilNo())
                    .locality(restaurant.getAddress().getLocality())
                    .pincode(restaurant.getAddress().getPincode())
                    .state(addressState);

            String categoriesString = "";
            List<CategoryEntity> categoryList = categoryService.getCategoriesByRestaurant(restaurant.getUuid());
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
                .id(UUID.fromString(restaurant.getAddress().getState().getUuid()))
                .stateName(restaurant.getAddress().getState().getStateName());

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
            throws CategoryNotFoundException, RestaurantNotFoundException {

        if (categoryId.equals("")|| categoryId.isEmpty()) {
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }

        List<RestaurantEntity> restaurantsInCategory = restaurantService.restaurantByCategory(categoryId);

            if (restaurantsInCategory.size() == 0) {
                RestaurantListResponse emptyRestaurantDetailsResponse = new RestaurantListResponse();
                return new ResponseEntity<RestaurantListResponse>(emptyRestaurantDetailsResponse, HttpStatus.NO_CONTENT);
            }

        RestaurantListResponse restaurantListResponse = new RestaurantListResponse();

            List<RestaurantList> restaurantLists = new ArrayList<RestaurantList>();
            for (RestaurantEntity restaurant: restaurantsInCategory) {

                RestaurantList restaurantList = new RestaurantList();
                restaurantList.setId(UUID.fromString(restaurant.getUuid()));
                restaurantList.setRestaurantName(restaurant.getRestaurantName());
                restaurantList.setPhotoURL(restaurant.getPhotoUrl());
                restaurantList.setCustomerRating(BigDecimal.valueOf(restaurant.getCustomerRating()));
                restaurantList.setAveragePrice(restaurant.getAvgPrice());
                restaurantList.setNumberCustomersRated(restaurant.getNumberCustomersRated());

                RestaurantDetailsResponseAddressState addressState = new RestaurantDetailsResponseAddressState()
                        .id(UUID.fromString(restaurant.getAddress().getState().getUuid()))
                        .stateName(restaurant.getAddress().getState().getStateName());

                RestaurantDetailsResponseAddress address = new RestaurantDetailsResponseAddress()
                        .id(UUID.fromString(restaurant.getAddress().getUuid()))
                        .city(restaurant.getAddress().getCity())
                        .flatBuildingName(restaurant.getAddress().getFlatBuilNo())
                        .locality(restaurant.getAddress().getLocality())
                        .pincode(restaurant.getAddress().getPincode())
                        .state(addressState);

                List<CategoryEntity> categoryLists = categoryService.getCategoriesByRestaurant(restaurant.getUuid());

                restaurantList.setAddress(address);
                for (CategoryEntity category : categoryLists) {
                    String categories = category.getCategoryName();
                    restaurantList.setCategories(String.join(",", categories));
                }

                restaurantLists.add(restaurantList);
                restaurantListResponse.addRestaurantsItem(restaurantList);

            }
        return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/restaurant/{restaurant_id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantUpdatedResponse> updateRestaurantDetails(
            @RequestParam(name = "customer_rating") final Double customerRating,
            @PathVariable("restaurant_id") final String restaurantId,
            @RequestHeader("authorization") final String authorization)
            throws RestaurantNotFoundException, AuthorizationFailedException, InvalidRatingException {
        // Get the access-token from authorization header(Bearer)
        final String[] bearerTokens = authorization.split("Bearer ");
        final String accessToken;
        if (bearerTokens.length == 2) {
            accessToken = bearerTokens[1];
        } else {
            accessToken = authorization;
        }
        // Get customer based on access-token
        CustomerEntity customer = customerService.getCustomer(accessToken);

        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurantId);

        RestaurantEntity updatedRestaurantEntity = restaurantService
                .updateRestaurantRating(restaurantEntity, customerRating);

        RestaurantUpdatedResponse restaurantUpdatedResponse = new RestaurantUpdatedResponse()
                .id(UUID.fromString(restaurantId))
                .status("RESTAURANT RATING UPDATED SUCCESSFULLY");
        return new ResponseEntity<RestaurantUpdatedResponse>(restaurantUpdatedResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/name/{restaurant_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantsByName(
            @PathVariable("restaurant_name") final String restaurantName)
            throws RestaurantNotFoundException {

        List<RestaurantEntity> matchedRestaurantsByNameList = restaurantService
                .restaurantsByName(restaurantName);

        RestaurantListResponse listResponse = new RestaurantListResponse();

        if (matchedRestaurantsByNameList.isEmpty()) {
            return new ResponseEntity<RestaurantListResponse>(listResponse, HttpStatus.NOT_FOUND);
        }

        for (RestaurantEntity restaurantEntity : matchedRestaurantsByNameList) {

            RestaurantDetailsResponseAddressState responseAddressState = new RestaurantDetailsResponseAddressState()
                    .id(UUID.fromString(restaurantEntity.getAddress().getState().getUuid())).
                            stateName(restaurantEntity.getAddress().getState().getStateName());

            RestaurantDetailsResponseAddress responseAddress = new RestaurantDetailsResponseAddress().
                    id(UUID.fromString(restaurantEntity.getAddress().getUuid())).
                    flatBuildingName(restaurantEntity.getAddress().getFlatBuilNo()).
                    locality(restaurantEntity.getAddress().getLocality())
                    .city(restaurantEntity.getAddress().getCity()).
                            pincode(restaurantEntity.getAddress().getPincode()).state(responseAddressState);

            List<CategoryEntity> categories = categoryService.getCategoriesByRestaurant(restaurantEntity.getUuid());

            String CategoryString = categories.stream().map(rc -> String.valueOf(rc.getCategoryName()))
                    .collect(Collectors.joining(","));

            RestaurantList restaurantList = new RestaurantList()
                    .id(UUID.fromString(restaurantEntity.getUuid())).
                            restaurantName(restaurantEntity.getRestaurantName())
                    .photoURL(restaurantEntity.getPhotoUrl())
                    .customerRating(new BigDecimal(restaurantEntity.getCustomerRating())).
                            averagePrice(restaurantEntity.getAvgPrice())
                    .numberCustomersRated(restaurantEntity.getNumberCustomersRated())
                    .address(responseAddress).categories(CategoryString);

            listResponse.addRestaurantsItem(restaurantList);
        }
        return new ResponseEntity<RestaurantListResponse>(listResponse, HttpStatus.OK);
    }

}