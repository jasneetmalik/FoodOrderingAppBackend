package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CategoriesListResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryListResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.CategoryItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Get All Categories Sorted by Name
    @RequestMapping(method = RequestMethod.GET, path = "/category",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)

    public ResponseEntity<CategoriesListResponse> getAllCategories() {

        List<CategoryEntity> categoryList = categoryService.getAllCategoriesOrderedByName();

        CategoriesListResponse categoriesListResponse = new CategoriesListResponse();

        for (CategoryEntity categoryEntity : categoryList) {
            CategoryListResponse categoryListResponse = new CategoryListResponse()
                    .id(UUID.fromString(categoryEntity.getUuid()))
                    .categoryName(categoryEntity.getCategoryName());
            categoriesListResponse.addCategoriesItem(categoryListResponse);
        }
        return new ResponseEntity<CategoriesListResponse>(categoriesListResponse, HttpStatus.OK);
    }
    // Get Category By Category UUID
    @RequestMapping(method = RequestMethod.GET, path = "/category/{category_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<CategoryDetailsResponse> getCategoryById(
            @PathVariable("category_id") final String categoryId) throws CategoryNotFoundException {

            //Get Category By Id
            CategoryEntity categoryById = categoryService.getCategoryById(categoryId);
            
            //Create Response
            CategoryDetailsResponse categoryDetailsResponse = new CategoryDetailsResponse()
                    .id(UUID.fromString(categoryById.getUuid()))
                    .categoryName(categoryById.getCategoryName());

            //Get Items for Category using joins created in Category Entity Table
            List<ItemEntity> itemsList = categoryById.getItems();

            for (ItemEntity item : itemsList) {
                ItemList itemsInCategory = new ItemList()
                        .id(UUID.fromString(item.getUuid()))
                        .itemName(item.getItemName())
                        .price(item.getPrice())
                        .itemType(ItemList.ItemTypeEnum.fromValue(item.getType().getValue()));
                categoryDetailsResponse.addItemListItem(itemsInCategory);
            }
            return new ResponseEntity<CategoryDetailsResponse>(categoryDetailsResponse, HttpStatus.OK);

    }

}
