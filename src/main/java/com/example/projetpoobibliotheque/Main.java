package com.example.projetpoobibliotheque;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 Démarrage du test Hibernate...");

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            System.out.println("✅ Hibernate initialisé avec succès !");
            System.out.println("✅ Les tables sont créées dans PostgreSQL !");

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("❌ Erreur : " + e.getMessage());
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
        }
    }
}