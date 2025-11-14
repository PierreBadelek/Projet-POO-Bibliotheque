package com.example.projetpoobibliotheque;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory sessionFactory;

    static {
        try {
            // Créer la configuration
            Configuration configuration = new Configuration();

            // Charger le fichier hibernate.cfg.xml
            configuration.configure("hibernate.cfg.xml");

            // Construire la SessionFactory
            sessionFactory = configuration.buildSessionFactory();

            System.out.println("✅ Hibernate SessionFactory créée avec succès !");

        } catch (Throwable ex) {
            System.err.println("❌ Erreur lors de l'initialisation de Hibernate : " + ex);
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}