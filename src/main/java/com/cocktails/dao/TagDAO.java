
package com.cocktails.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.cocktails.model.Drink;
import com.cocktails.model.Tag;
import com.cocktails.util.HibernateUtil;

public class TagDAO {

    public Long create(Tag tag) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                session.persist(tag);
                transaction.commit();
                return tag.getId();
            } catch (RuntimeException e) {
                transaction.rollback();
                throw e;
            }
        }
    }

    public Tag findById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.find(Tag.class, id);
        }
    }

    public List<Tag> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createSelectionQuery(
                "FROM Tag ORDER BY name",
                Tag.class
            ).getResultList();
        }
    }

    public boolean update(Long id, String name, String type) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                Tag tag = session.find(Tag.class, id);

                if (tag == null) {
                    transaction.rollback();
                    return false;
                }

                tag.setName(name);
                tag.setType(type);

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
                Tag tag = session.find(Tag.class, id);

                if (tag == null) {
                    transaction.rollback();
                    return false;
                }

                List<Drink> drinks = session.createSelectionQuery(
                    "SELECT d FROM Drink d JOIN d.tags t WHERE t.id = :id",
                    Drink.class
                ).setParameter("id", id).getResultList();

                for (Drink drink : drinks) {
                    drink.removeTag(tag);
                }

                session.remove(tag);
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
                "SELECT COUNT(t) FROM Tag t WHERE LOWER(t.name) = LOWER(:name)",
                Long.class
            )
            .setParameter("name", name)
            .getSingleResult();

            return count > 0;
        }
    }

    public boolean existsByNameExcludingId(String name, Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createSelectionQuery(
                "SELECT COUNT(t) FROM Tag t " +
                "WHERE LOWER(t.name) = LOWER(:name) AND t.id <> :id",
                Long.class
            )
            .setParameter("name", name)
            .setParameter("id", id)
            .getSingleResult();

            return count > 0;
        }
    }
}
