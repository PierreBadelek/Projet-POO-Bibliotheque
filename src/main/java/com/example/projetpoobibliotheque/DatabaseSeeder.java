package com.example.projetpoobibliotheque;

import com.example.projetpoobibliotheque.model.*;
import com.example.projetpoobibliotheque.service.BibliothequeService;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;

public class DatabaseSeeder {

    /**
     * Peuple la base de données avec différents cas d'utilisation
     */
    public static void seed() {
        System.out.println("========== DÉBUT DU SEEDING ==========");

        BibliothequeService service = new BibliothequeService();

        try {
            // 1. Créer des membres
            Membre alice = new Membre("Dupont", "Alice", "alice.dupont@email.com");
            Membre bob = new Membre("Martin", "Bob", "bob.martin@email.com");
            Membre charlie = new Membre("Dubois", "Charlie", "charlie.dubois@email.com");
            Membre diane = new Membre("Leclerc", "Diane", "diane.leclerc@email.com");
            Membre eve = new Membre("Rousseau", "Eve", "eve.rousseau@email.com");

            service.ajouterMembre(alice);
            service.ajouterMembre(bob);
            service.ajouterMembre(charlie);
            service.ajouterMembre(diane);
            service.ajouterMembre(eve);

            Livre livre1 = new Livre("1984", "George Orwell", "978-0141036144");
            Livre livre2 = new Livre("Le Seigneur des Anneaux", "J.R.R. Tolkien", "978-0261103252");
            Livre livre3 = new Livre("Harry Potter à l'école des sorciers", "J.K. Rowling", "978-2070541270");
            Livre livre4 = new Livre("L'Étranger", "Albert Camus", "978-2070360024");
            Livre livre5 = new Livre("Fondation", "Isaac Asimov", "978-2070360260");
            Livre livre6 = new Livre("Le Petit Prince", "Antoine de Saint-Exupéry", "978-2070612758");
            Livre livre7 = new Livre("Dune", "Frank Herbert", "978-2221003039");
            Livre livre8 = new Livre("Les Misérables", "Victor Hugo", "978-2070409228");

            service.ajouterDocument(livre1);
            service.ajouterDocument(livre2);
            service.ajouterDocument(livre3);
            service.ajouterDocument(livre4);
            service.ajouterDocument(livre5);
            service.ajouterDocument(livre6);
            service.ajouterDocument(livre7);
            service.ajouterDocument(livre8);

            Magazine mag1 = new Magazine("Sciences & Vie", "Collectif", 1234);
            Magazine mag2 = new Magazine("National Geographic", "Collectif", 567);
            Magazine mag3 = new Magazine("Le Monde Diplomatique", "Collectif", 890);
            Magazine mag4 = new Magazine("Challenges", "Collectif", 432);
            Magazine mag5 = new Magazine("Historia", "Collectif", 789);

            service.ajouterDocument(mag1);
            service.ajouterDocument(mag2);
            service.ajouterDocument(mag3);
            service.ajouterDocument(mag4);
            service.ajouterDocument(mag5);

            // 4. Créer des emprunts avec différents cas

            // CAS 1: Emprunt en cours, dans les délais
            Emprunt emprunt1 = service.enregistrerEmprunt(livre1, alice);

            // CAS 2: Emprunt en cours, bientôt en retard
            Emprunt emprunt2 = service.enregistrerEmprunt(livre2, bob);
            modifierDateEmprunt(emprunt2, LocalDate.now().minusDays(12)); // Emprunté il y a 12 jours

            // CAS 3: Emprunt EN RETARD
            Emprunt emprunt3 = service.enregistrerEmprunt(livre3, charlie);
            modifierDateEmprunt(emprunt3, LocalDate.now().minusDays(20)); // Emprunté il y a 20 jours

            // CAS 4: Emprunt très en retard
            Emprunt emprunt4 = service.enregistrerEmprunt(mag1, diane);
            modifierDateEmprunt(emprunt4, LocalDate.now().minusDays(35)); // Emprunté il y a 35 jours

            // CAS 5: Emprunt retourné à temps
            Emprunt emprunt5 = service.enregistrerEmprunt(livre4, eve);
            modifierDateEmprunt(emprunt5, LocalDate.now().minusDays(10));
            service.enregistrerRetour(emprunt5);

            // CAS 6: Emprunt retourné en retard
            Emprunt emprunt6 = service.enregistrerEmprunt(livre5, alice);
            modifierDateEmprunt(emprunt6, LocalDate.now().minusDays(25));
            service.enregistrerRetour(emprunt6);

            // CAS 7: Membre avec plusieurs emprunts
            Emprunt emprunt7 = service.enregistrerEmprunt(mag2, bob);

            // CAS 8: Document très populaire (plusieurs emprunts successifs)
            Emprunt emprunt8 = service.enregistrerEmprunt(livre6, charlie);
            modifierDateEmprunt(emprunt8, LocalDate.now().minusDays(8));
            service.enregistrerRetour(emprunt8);
            Emprunt emprunt9 = service.enregistrerEmprunt(livre6, diane);



            BibliothequeService.StatistiquesBibliotheque stats = service.genererStatistiques();
            System.out.println(stats);



        } catch (Exception e) {
            System.err.println("❌ ERREUR lors du seeding: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Modifie la date d'emprunt pour simuler des emprunts anciens
     * Nécessaire pour créer des cas de retard
     */
    private static void modifierDateEmprunt(Emprunt emprunt, LocalDate nouvelleDate) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Emprunt empruntToUpdate = session.get(Emprunt.class, emprunt.getId());
            if (empruntToUpdate != null) {
                empruntToUpdate.setDateEmprunt(nouvelleDate);
                empruntToUpdate.setDateRetourPrevue(nouvelleDate.plusDays(14));
                session.merge(empruntToUpdate);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Erreur lors de la modification de la date d'emprunt", e);
        }
    }

    /**
     * Nettoie toutes les données de la base
     * ATTENTION: Supprime toutes les données !
     */
    public static void cleanDatabase() {
        System.out.println("⚠️  NETTOYAGE DE LA BASE DE DONNÉES...");
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Ordre important: supprimer d'abord les emprunts (dépendances)
            session.createMutationQuery("DELETE FROM Emprunt").executeUpdate();
            session.createMutationQuery("DELETE FROM Document").executeUpdate();
            session.createMutationQuery("DELETE FROM Membre").executeUpdate();

            transaction.commit();
            System.out.println("✓ Base de données nettoyée");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.err.println("❌ Erreur lors du nettoyage: " + e.getMessage());
        }
    }
}
