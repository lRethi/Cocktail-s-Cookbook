
package com.cocktails.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.cocktails.model.Drink;
import com.cocktails.model.DrinkIngredient;
import com.cocktails.model.Ingredient;
import com.cocktails.util.HibernateUtil;

public class DrinkIngredientDAO {

    public Long add(
        Long drinkId,
        Long ingredientId,
        BigDecimal quantity,
        String unit,
        String preparation
    ) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                Drink drink = session.find(Drink.class, drinkId);
                Ingredient ingredient = session.find(Ingredient.class, ingredientId);

                if (drink == null || ingredient == null) {
                    transaction.rollback();
                    return null;
                }

                DrinkIngredient association = new DrinkIngredient(
                    ingredient,
                    quantity,
                    unit,
                    preparation
                );

                drink.addIngredient(association);

                transaction.commit();
                return association.getId();
            } catch (RuntimeException e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    public List<DrinkIngredient> findByDrink(Long drinkId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery(
                "SELECT di FROM DrinkIngredient di " +
                "JOIN FETCH di.ingredient " +
                "WHERE di.drink.id = :drinkId ORDER BY di.id",
                DrinkIngredient.class
            ).setParameter("drinkId", drinkId).getResultList();
        }
    }

    public boolean update(
        Long id,
        BigDecimal quantity,
        String unit,
        String preparation
    ) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                DrinkIngredient association = session.find(
                    DrinkIngredient.class,
                    id
                );

                if (association == null) {
                    transaction.rollback();
                    return false;
                }

                association.setQuantity(quantity);
                association.setUnit(unit);
                association.setPreparation(preparation);

                transaction.commit();
                return true;
            } catch (RuntimeException e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    public boolean remove(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                DrinkIngredient association = session.find(
                    DrinkIngredient.class,
                    id
                );

                if (association == null) {
                    transaction.rollback();
                    return false;
                }

                association.getDrink().removeIngredient(association);

                transaction.commit();
                return true;
            } catch (RuntimeException e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    public List<DrinkIngredient> findByIngredientAndMinimumQuantity(
        String ingredientName,
        BigDecimal minimum,
        String unit
    ) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery(
                "SELECT di FROM DrinkIngredient di " +
                "JOIN FETCH di.drink d " +
                "JOIN FETCH di.ingredient i " +
                "WHERE LOWER(i.name) = LOWER(:ingredientName) " +
                "AND di.quantity > :minimum " +
                "AND LOWER(di.unit) = LOWER(:unit) " +
                "ORDER BY di.quantity DESC",
                DrinkIngredient.class
            )
            .setParameter("ingredientName", ingredientName)
            .setParameter("minimum", minimum)
            .setParameter("unit", unit)
            .getResultList();
        }
    }

    public List<DrinkIngredient> findByIngredientBetweenQuantities(
        String ingredientName,
        BigDecimal minQuantity,
        BigDecimal maxQuantity,
        String unit
    ) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery(
                "SELECT di FROM DrinkIngredient di " +
                "JOIN FETCH di.drink d " +
                "JOIN FETCH di.ingredient i " +
                "WHERE LOWER(i.name) = LOWER(:ingredientName) " +
                "AND di.quantity BETWEEN :minQuantity AND :maxQuantity " +
                "AND LOWER(di.unit) = LOWER(:unit) " +
                "ORDER BY di.quantity DESC",
                DrinkIngredient.class
            )
            .setParameter("ingredientName", ingredientName)
            .setParameter("minQuantity", minQuantity)
            .setParameter("maxQuantity", maxQuantity)
            .setParameter("unit", unit)
            .getResultList();
        }
    }

    public List<DrinkIngredient> findByIngredientAndMaximumQuantity(
        String ingredientName,
        BigDecimal maximum,
        String unit
    ) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery(
                "SELECT di FROM DrinkIngredient di " +
                "JOIN FETCH di.drink d " +
                "JOIN FETCH di.ingredient i " +
                "WHERE LOWER(i.name) = LOWER(:ingredientName) " +
                "AND di.quantity < :maximum " +
                "AND LOWER(di.unit) = LOWER(:unit) " +
                "ORDER BY di.quantity DESC",
                DrinkIngredient.class
            )
            .setParameter("ingredientName", ingredientName)
            .setParameter("maximum", maximum)
            .setParameter("unit", unit)
            .getResultList();
        }
    }

    public List<DrinkIngredient> findWithoutIngredient() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery(
                "SELECT di FROM DrinkIngredient di " +
                "JOIN FETCH di.drink d " +
                "WHERE di.ingredient IS NULL",
                DrinkIngredient.class
            ).getResultList();
        }
    }

    public List<DrinkIngredient> findDrinksByIngredient(Long ingredientId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery(
                "SELECT di FROM DrinkIngredient di " +
                "JOIN FETCH di.drink d " +
                "WHERE di.ingredient.id = :ingredientId",
                DrinkIngredient.class
            ).setParameter("ingredientId", ingredientId).getResultList();
        }
    }
}
