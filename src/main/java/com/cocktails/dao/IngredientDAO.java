
package com.cocktails.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.cocktails.model.Ingredient;
import com.cocktails.util.HibernateUtil;

public class IngredientDAO {

    public Long create(Ingredient ingredient) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                session.persist(ingredient);
                transaction.commit();
                return ingredient.getId();
            } catch (RuntimeException e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    public Ingredient findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.find(Ingredient.class, id);
        }
    }

    public List<Ingredient> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery(
                "FROM Ingredient ORDER BY name",
                Ingredient.class
            ).getResultList();
        }
    }

    public boolean update(Long id, String name, String type) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                Ingredient ingredient = session.find(Ingredient.class, id);

                if (ingredient == null) {
                    transaction.rollback();
                    return false;
                }

                ingredient.setName(name);
                ingredient.setType(type);

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
                Ingredient ingredient = session.find(Ingredient.class, id);

                if (ingredient == null) {
                    transaction.rollback();
                    return false;
                }

                Long references = session.createSelectionQuery(
                    "SELECT COUNT(di) FROM DrinkIngredient di WHERE di.ingredient.id = :id",
                    Long.class
                ).setParameter("id", id).getSingleResult();

                if (references > 0) {
                    transaction.rollback();
                    return false;
                }

                session.remove(ingredient);
                transaction.commit();
                return true;
            } catch (RuntimeException e) {
                transaction.rollback();
                throw e;
            }
        }
    }
    public boolean existsByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createSelectionQuery(
                "SELECT COUNT(i) FROM Ingredient i WHERE LOWER(i.name) = LOWER(:name)",
                Long.class
            ).setParameter("name", name).getSingleResult();

            return count > 0;
        }
    }

    public boolean existsByNameExcludingId(String name, Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createSelectionQuery(
                "SELECT COUNT(i) FROM Ingredient i " +
                "WHERE LOWER(i.name) = LOWER(:name) AND i.id <> :id",
                Long.class
            ).setParameter("name", name)
            .setParameter("id", id)
            .getSingleResult();

            return count > 0;
        }
    }
}
