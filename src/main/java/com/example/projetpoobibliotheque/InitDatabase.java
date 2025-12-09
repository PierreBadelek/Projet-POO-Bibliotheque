package com.example.projetpoobibliotheque;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class InitDatabase {
    public static void main(String[] args) {
        System.out.println("Démarrage de l'application Bibliothèque...");

        try {
            // Initialiser Hibernate
            Transaction transaction = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                transaction = session.beginTransaction();
                System.out.println("Hibernate initialisé avec succès !");
                System.out.println("Les tables sont créées dans PostgreSQL !");
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }

            // Optionnel: Nettoyer et peupler la base
            // Décommenter ces lignes pour réinitialiser les données

            //DatabaseSeeder.cleanDatabase();

            System.out.println("\nPeuplement de la base de données...");
            DatabaseSeeder.seed();

            System.out.println("\nApplication prête à l'emploi !");

        } catch (Exception e) {
            System.err.println("❌ Erreur : " + e.getMessage());
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown();
        }
    }
}