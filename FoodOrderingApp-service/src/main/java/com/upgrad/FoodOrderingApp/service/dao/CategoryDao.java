package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import com.upgrad.FoodOrderingApp.service.entity.CategoryItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryDao {

    @PersistenceContext
    private EntityManager entityManager;

    /*** This method fetches CategoryEntity from database based Category UUID.***/
    public CategoryEntity getCategoryByUuid(final String categoryUuid) {
        try {
            return entityManager.createNamedQuery("getCategoryByUuid", CategoryEntity.class)
                    .setParameter("uuid", categoryUuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
    /*** This method fetches all CategoryEntity from db ***/
    public List<CategoryEntity> getAllCategories() {
        try {
            return entityManager.createNamedQuery("allCategories", CategoryEntity.class)
                    .getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }
    public List<CategoryItemEntity> getItemsByCategoryId(Integer Id){
        try {
            return entityManager.createNamedQuery("getItemsByCategory", CategoryItemEntity.class)
                    .getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }
    public ItemEntity getItemById(Integer Id) {
        try {
            return entityManager.createNamedQuery("getItemById", ItemEntity.class).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

}
