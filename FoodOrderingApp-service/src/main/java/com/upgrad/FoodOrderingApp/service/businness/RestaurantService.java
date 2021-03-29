package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerRepository;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantCategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private CustomerRepository customerDao;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CategoryDao categoryDao;

    // Method to getAllRestaurants endpoint;
    // Parameters: none
    public List<RestaurantEntity> restaurantsByRating() {
        return restaurantDao.getAllRestaurants();
    }

    // Method to get Restaurants By Name;
    // Parameters: restaurantName
    public List<RestaurantEntity> restaurantsByName(String restaurantName) throws RestaurantNotFoundException {
        if (restaurantName == "" || restaurantName == null) {
            throw new RestaurantNotFoundException("RNF-003","Restaurant name field should not be empty");}
        return restaurantDao.getRestaurantsByName(restaurantName);
    }

    /**
     * Gets all the restaurants in DB based on Category Uuid
     *
     * @return List of all the restaurants based on the input category Id
     */
    public List<RestaurantEntity> restaurantByCategory(final String categoryId)
            throws CategoryNotFoundException {

        if (categoryId.equals("")|| categoryId.isEmpty()) {
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }

        CategoryEntity categoryEntity = categoryDao.getCategoryByUuid(categoryId);

        if (categoryEntity == null) {
            throw new CategoryNotFoundException("CNF-002", "No Category By this id");
        }

        List<RestaurantEntity> restaurantListByCategoryId = categoryEntity.getRestaurants();
        restaurantListByCategoryId.sort(Comparator.comparing(RestaurantEntity::getRestaurantName));
        return restaurantListByCategoryId;
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
    public RestaurantEntity updateRestaurantRating(RestaurantEntity restaurantEntity, Double customerRating)
            throws InvalidRatingException {

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
