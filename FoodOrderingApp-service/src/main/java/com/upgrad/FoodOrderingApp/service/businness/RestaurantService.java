package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerRepository;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private CustomerRepository customerDao;

    @Autowired
    private CustomerService customerService;

    // Method to getAllRestaurants endpoint;
    // Parameters: none
    public List<RestaurantEntity> getAllRestaurants() {

        return restaurantDao.getAllRestaurants();
    }

    // Method to get Restaurants By Name;
    // Parameters: restaurantName
    public List<RestaurantEntity> getRestaurantsByName(String restaurantName) {
        return restaurantDao.getRestaurantsByName(restaurantName);
    }

    // Method to get Restaurants by Category
    // Parameters: categoryUUID
    public List<RestaurantCategoryEntity> getRestaurantsByCategoryId(final Integer categoryID) {
        return restaurantDao.getRestaurantByCategoryId(categoryID);
    }

    // Method to get Restaurant by UUID
    // Parameters: restaurantUUID
    public RestaurantEntity restaurantByUUID(String restaurantUUID) throws RestaurantNotFoundException {
        RestaurantEntity restaurantEntity =restaurantDao.getRestaurantByUUId(restaurantUUID);
        if (restaurantEntity == null) {
            throw new RestaurantNotFoundException("RNF-001","No restaurant by this id");
        }
        return restaurantEntity;
    }

    // Method to update Customer Rating
    // Parameters: customerRating, restaurant_id, accessToken
    @Transactional
    public RestaurantEntity updateCustomerRating (final Double customerRating, final String restaurant_id, final String accessToken)
            throws AuthorizationFailedException, RestaurantNotFoundException, InvalidRatingException {

        // Validate customer using the accessToken
        customerService.getCustomer(accessToken);

        // Throw exception if path variable(restaurant_id) is empty
        if(restaurant_id == null || restaurant_id.isEmpty() || restaurant_id.equalsIgnoreCase("\"\"")){
            throw new RestaurantNotFoundException("RNF-002", "Restaurant id field should not be empty");
        }

        //Get restaurantEntity Details using the restaurantUuid
        RestaurantEntity restaurantEntity =  restaurantDao.getRestaurantByUUId(restaurant_id);

        if (restaurantEntity == null) {
            throw new RestaurantNotFoundException("RNF-001", "No restaurant by this id");
        }

        // Throw exception if path variable(customerRating) is outside given range 1 to 5 or NaN
        if(customerRating == null || customerRating.isNaN() || customerRating < 1 || customerRating > 5 ){
            throw new InvalidRatingException("IRE-001", "Restaurant should be in the range of 1 to 5");
        }

        // Calculate new customer rating, set the updated rating and link it to the restaurantEntity
        Double oldRatingCalculation = (restaurantEntity.getCustomerRating() * (restaurantEntity.getNumberCustomersRated()));
        Double calculatedRating = ((oldRatingCalculation + customerRating) / (restaurantEntity.getNumberCustomersRated() + 1));
        restaurantEntity.setCustomerRating(calculatedRating);
        restaurantEntity.setNumberCustomersRated(restaurantEntity.getNumberCustomersRated() + 1);

        // Update restaurant in the database
        restaurantDao.updateRestaurant(restaurantEntity);
        return restaurantEntity;
    }

}
