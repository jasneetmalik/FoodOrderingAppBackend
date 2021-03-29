package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private RestaurantService restaurantService;

    // Get All Categories
    public List<CategoryEntity> getAllCategoriesOrderedByName() {
        return categoryDao.getAllCategories();
    }
    //Get Category by Id
    public CategoryEntity getCategoryById(String uuid) throws CategoryNotFoundException {
        if (uuid.equals("")) {
            throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
        }

        CategoryEntity categoryEntity = categoryDao.getCategoryByUuid(uuid);
        if (categoryEntity == null) {
            throw new CategoryNotFoundException("CNF-002", "No category by this id");
        }
        return categoryEntity;
    }
    public List<CategoryEntity> getCategoriesByRestaurant(String restaurantUUID) throws RestaurantNotFoundException {
        RestaurantEntity restaurant = restaurantService.restaurantByUUID(restaurantUUID);
        if (restaurant == null)
            throw new RestaurantNotFoundException("RNF-001","No restaurant by this id");
        List<CategoryEntity> categoryList = restaurant.getCategories();
        return categoryList;
    }
}

