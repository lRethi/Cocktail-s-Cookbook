
package com.cocktails.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.cocktails.model.Drink;
import com.cocktails.model.DrinkIngredient;
import com.cocktails.util.HibernateUtil;
import com.cocktails.model.Tag;

public class DrinkDAO {

    public Long create(Drink drink) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                session.persist(drink);
                transaction.commit();
                return drink.getId();
            } catch (RuntimeException e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    public Long duplicate(Drink drink) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                Drink original = session.createSelectionQuery(
                    "SELECT DISTINCT d FROM Drink d " +
                    "LEFT JOIN FETCH d.ingredients di " +
                    "LEFT JOIN FETCH di.ingredient " +
                    "WHERE d.id = :id",
                    Drink.class
                )
                .setParameter("id", drink.getId())
                .uniqueResult();

                if (original == null) {
                    transaction.rollback();
                    return null;
                }

                Hibernate.initialize(original.getTags());

                Drink duplicate = new Drink(
                    original.getName() + "_copy",
                    original.getDescription(),
                    original.getCategory(),
                    original.getInstructions()
                );

                for (DrinkIngredient item : original.getIngredients()) {
                    DrinkIngredient copy = new DrinkIngredient(
                        item.getIngredient(),
                        item.getQuantity(),
                        item.getUnit(),
                        item.getPreparation()
                    );

                    duplicate.addIngredient(copy);
                }

                for (Tag tag : original.getTags()) {
                    duplicate.addTag(tag);
                }

                session.persist(duplicate);
                transaction.commit();

                return duplicate.getId();
            } catch (RuntimeException e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    public Drink findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Drink drink = session.createSelectionQuery(
                "SELECT DISTINCT d FROM Drink d " +
                "LEFT JOIN FETCH d.ingredients di " +
                "LEFT JOIN FETCH di.ingredient " +
                "WHERE d.id = :id",
                Drink.class
            ).setParameter("id", id).uniqueResult();

            if (drink != null) {
                Hibernate.initialize(drink.getTags());
            }

            return drink;
        }
    }

    public List<Drink> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery(
                "FROM Drink ORDER BY name",
                Drink.class
            ).getResultList();
        }
    }

    public boolean update(Long id, String name, String description, String category, String instructions) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                Drink drink = session.find(Drink.class, id);

                if (drink == null) {
                    transaction.rollback();
                    return false;
                }

                drink.setName(name);
                drink.setDescription(description);
                drink.setCategory(category);
                drink.setInstructions(instructions);

                transaction.commit();
                return true;
            } catch (RuntimeException e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    public boolean delete(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                Drink drink = session.find(Drink.class, id);

                if (drink == null) {
                    transaction.rollback();
                    return false;
                }

                session.remove(drink);
                transaction.commit();
                return true;
            } catch (RuntimeException e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    public boolean addTag(Long drinkId, Long tagId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                Drink drink = session.find(Drink.class, drinkId);
                Tag tag = session.find(Tag.class, tagId);

                if (drink == null || tag == null) {
                    transaction.rollback();
                    return false;
                }

                drink.addTag(tag);

                transaction.commit();
                return true;
            } catch (RuntimeException e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    public boolean removeTag(Long drinkId, Long tagId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                Drink drink = session.find(Drink.class, drinkId);
                Tag tag = session.find(Tag.class, tagId);

                if (drink == null || tag == null) {
                    transaction.rollback();
                    return false;
                }

                drink.removeTag(tag);

                transaction.commit();
                return true;
            } catch (RuntimeException e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    public List<Drink> findByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery(
                "FROM Drink d WHERE d.name LIKE :name ORDER BY d.name",
                Drink.class
            ).setParameter("name", "%" + name + "%").getResultList();
        }
    }

    public List<Drink> findByIngredientAndMinimumQuantityWithoutAnother(
        String includedIngredient,
        BigDecimal minimum,
        String unit,
        String excludedIngredient
    ) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery(
                "SELECT d FROM Drink d " +
                "WHERE EXISTS (" +
                "    SELECT di.id FROM DrinkIngredient di " +
                "    JOIN di.ingredient i " +
                "    WHERE di.drink = d " +
                "    AND LOWER(i.name) = LOWER(:includedIngredient) " +
                "    AND di.quantity > :minimum " +
                "    AND LOWER(di.unit) = LOWER(:unit)" +
                ") " +
                "AND NOT EXISTS (" +
                "    SELECT di2.id FROM DrinkIngredient di2 " +
                "    JOIN di2.ingredient i2 " +
                "    WHERE di2.drink = d " +
                "    AND LOWER(i2.name) = LOWER(:excludedIngredient)" +
                ") " +
                "ORDER BY d.name",
                Drink.class
            )
            .setParameter("includedIngredient", includedIngredient)
            .setParameter("minimum", minimum)
            .setParameter("unit", unit)
            .setParameter("excludedIngredient", excludedIngredient)
            .getResultList();
        }
    }

    public List<Drink> findWithoutIngredient(String ingredientName) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery(
                "SELECT d FROM Drink d " +
                "WHERE NOT EXISTS (" +
                "    SELECT di.id FROM DrinkIngredient di " +
                "    JOIN di.ingredient i " +
                "    WHERE di.drink = d " +
                "    AND LOWER(i.name) = LOWER(:ingredientName)" +
                ") " +
                "ORDER BY d.name",
                Drink.class
            )
            .setParameter("ingredientName", ingredientName)
            .getResultList();
        }
    }
}
