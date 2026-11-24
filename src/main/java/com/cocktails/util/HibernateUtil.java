
package com.cocktails.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.cocktails.model.Drink;
import com.cocktails.model.Ingredient;
import com.cocktails.model.DrinkIngredient;
import com.cocktails.model.Tag;

public class HibernateUtil {

    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        Configuration configuration = new Configuration();

        configuration.setProperty(
            "hibernate.connection.driver_class",
            "com.mysql.cj.jdbc.Driver"
        );

        configuration.setProperty(
            "hibernate.connection.url",
            requiredEnv("DB_URL")
        );

        configuration.setProperty(
            "hibernate.connection.username",
            requiredEnv("DB_USER")
        );

        configuration.setProperty(
            "hibernate.connection.password",
            requiredEnv("DB_PASSWORD")
        );

        configuration.setProperty("hibernate.hbm2ddl.auto", "update");
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.format_sql", "true");

        configuration.addAnnotatedClass(Drink.class);
        configuration.addAnnotatedClass(Ingredient.class);
        configuration.addAnnotatedClass(DrinkIngredient.class);
        configuration.addAnnotatedClass(Tag.class);

        return configuration.buildSessionFactory();
    }

    private static String requiredEnv(String name) {
        String value = System.getenv(name);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                "Variável de ambiente não configurada: " + name
            );
        }

        return value;
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static void shutdown() {
        SESSION_FACTORY.close();
    }
}
