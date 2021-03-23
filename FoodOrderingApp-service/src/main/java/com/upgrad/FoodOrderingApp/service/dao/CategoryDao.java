package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * This method fetches CategoryEntity from database based Category UUID.
     * @return CategoryEntity or null if there is no category in database by given categoryUuid.
     */
    public CategoryEntity getCategoryByUuid(final String categoryUuid) {
        try {
            return entityManager.createNamedQuery("getCategoryByUuid", CategoryEntity.class)
                    .setParameter("uuid", categoryUuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }


    /**
     * This method fetches all CategoryEntity from db
     * @return List of categoryEntity
     */
    public List<CategoryEntity> getAllCategories() {
        try {
            return entityManager.createNamedQuery("allCategories", CategoryEntity.class)
                    .getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }

}
