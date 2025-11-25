package com.example.projetpoobibliotheque;

import com.example.projetpoobibliotheque.model.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;

public class DatabaseSeeder {

    /**
     * Peuple la base de données avec différents cas d'utilisation
     */
    public static void seed() {
        System.out.println("========== DÉBUT DU SEEDING ==========");

        Bibliotheque biblio = new Bibliotheque("Bibliothèque Centrale");

        try {
            // 1. Créer des membres
            System.out.println("\n[1/5] Création des membres...");
            Membre alice = new Membre("Alice Dupont", "alice.dupont@email.com");
            Membre bob = new Membre("Bob Martin", "bob.martin@email.com");
            Membre charlie = new Membre("Charlie Dubois", "charlie.dubois@email.com");
            Membre diane = new Membre("Diane Leclerc", "diane.leclerc@email.com");
            Membre eve = new Membre("Eve Rousseau", "eve.rousseau@email.com");

            biblio.ajouterMembre(alice);
            biblio.ajouterMembre(bob);
            biblio.ajouterMembre(charlie);
            biblio.ajouterMembre(diane);
            biblio.ajouterMembre(eve);
            System.out.println("✓ 5 membres créés");

            // 2. Créer des livres (variété de genres)
            System.out.println("\n[2/5] Création des livres...");
            Livre livre1 = new Livre("1984", "George Orwell", "978-0141036144");
            Livre livre2 = new Livre("Le Seigneur des Anneaux", "J.R.R. Tolkien", "978-0261103252");
            Livre livre3 = new Livre("Harry Potter à l'école des sorciers", "J.K. Rowling", "978-2070541270");
            Livre livre4 = new Livre("L'Étranger", "Albert Camus", "978-2070360024");
            Livre livre5 = new Livre("Fondation", "Isaac Asimov", "978-2070360260");
            Livre livre6 = new Livre("Le Petit Prince", "Antoine de Saint-Exupéry", "978-2070612758");
            Livre livre7 = new Livre("Dune", "Frank Herbert", "978-2221003039");
            Livre livre8 = new Livre("Les Misérables", "Victor Hugo", "978-2070409228");

            biblio.ajouterDocument(livre1);
            biblio.ajouterDocument(livre2);
            biblio.ajouterDocument(livre3);
            biblio.ajouterDocument(livre4);
            biblio.ajouterDocument(livre5);
            biblio.ajouterDocument(livre6);
            biblio.ajouterDocument(livre7);
            biblio.ajouterDocument(livre8);
            System.out.println("✓ 8 livres créés");

            // 3. Créer des magazines
            System.out.println("\n[3/5] Création des magazines...");
            Magazine mag1 = new Magazine("Sciences & Vie", "Collectif", 1234);
            Magazine mag2 = new Magazine("National Geographic", "Collectif", 567);
            Magazine mag3 = new Magazine("Le Monde Diplomatique", "Collectif", 890);
            Magazine mag4 = new Magazine("Challenges", "Collectif", 432);
            Magazine mag5 = new Magazine("Historia", "Collectif", 789);

            biblio.ajouterDocument(mag1);
            biblio.ajouterDocument(mag2);
            biblio.ajouterDocument(mag3);
            biblio.ajouterDocument(mag4);
            biblio.ajouterDocument(mag5);
            System.out.println("✓ 5 magazines créés");

            // 4. Créer des emprunts avec différents cas
            System.out.println("\n[4/5] Création des emprunts (différents cas d'utilisation)...");

            // CAS 1: Emprunt en cours, dans les délais
            Emprunt emprunt1 = biblio.enregistrerEmprunt(alice, livre1);
            System.out.println("  ✓ Cas 1: Alice emprunte '1984' (en cours, dans les délais)");

            // CAS 2: Emprunt en cours, bientôt en retard
            Emprunt emprunt2 = biblio.enregistrerEmprunt(bob, livre2);
            modifierDateEmprunt(emprunt2, LocalDate.now().minusDays(12)); // Emprunté il y a 12 jours
            System.out.println("  ✓ Cas 2: Bob emprunte 'Le Seigneur des Anneaux' (bientôt en retard)");

            // CAS 3: Emprunt EN RETARD
            Emprunt emprunt3 = biblio.enregistrerEmprunt(charlie, livre3);
            modifierDateEmprunt(emprunt3, LocalDate.now().minusDays(20)); // Emprunté il y a 20 jours
            System.out.println("  ✓ Cas 3: Charlie emprunte 'Harry Potter' (EN RETARD depuis 6 jours)");

            // CAS 4: Emprunt très en retard
            Emprunt emprunt4 = biblio.enregistrerEmprunt(diane, mag1);
            modifierDateEmprunt(emprunt4, LocalDate.now().minusDays(35)); // Emprunté il y a 35 jours
            System.out.println("  ✓ Cas 4: Diane emprunte 'Sciences & Vie' (TRÈS EN RETARD depuis 21 jours)");

            // CAS 5: Emprunt retourné à temps
            Emprunt emprunt5 = biblio.enregistrerEmprunt(eve, livre4);
            modifierDateEmprunt(emprunt5, LocalDate.now().minusDays(10));
            biblio.enregistrerRetour(emprunt5);
            System.out.println("  ✓ Cas 5: Eve a emprunté et rendu 'L'Étranger' à temps");

            // CAS 6: Emprunt retourné en retard
            Emprunt emprunt6 = biblio.enregistrerEmprunt(alice, livre5);
            modifierDateEmprunt(emprunt6, LocalDate.now().minusDays(25));
            biblio.enregistrerRetour(emprunt6);
            System.out.println("  ✓ Cas 6: Alice a emprunté et rendu 'Fondation' en retard");

            // CAS 7: Membre avec plusieurs emprunts
            Emprunt emprunt7 = biblio.enregistrerEmprunt(bob, mag2);
            System.out.println("  ✓ Cas 7: Bob emprunte aussi 'National Geographic' (2e emprunt actif)");

            // CAS 8: Document très populaire (plusieurs emprunts successifs)
            Emprunt emprunt8 = biblio.enregistrerEmprunt(charlie, livre6);
            modifierDateEmprunt(emprunt8, LocalDate.now().minusDays(8));
            biblio.enregistrerRetour(emprunt8);
            Emprunt emprunt9 = biblio.enregistrerEmprunt(diane, livre6);
            System.out.println("  ✓ Cas 8: 'Le Petit Prince' est très populaire (plusieurs emprunts)");

            // CAS 9: Documents jamais empruntés (disponibles)
            System.out.println("  ✓ Cas 9: 'Dune', 'Les Misérables', et autres magazines restent disponibles");

            System.out.println("\n✓ 9 emprunts créés avec différents cas d'utilisation");

            // 5. Afficher les statistiques finales
            System.out.println("\n[5/5] Génération des statistiques...");
            biblio.genererStatistiques();

            System.out.println("\n========== SEEDING TERMINÉ AVEC SUCCÈS ==========");
            System.out.println("\nCAS D'UTILISATION CRÉÉS:");
            System.out.println("1. Emprunts en cours normaux");
            System.out.println("2. Emprunts bientôt en retard");
            System.out.println("3. Emprunts en retard");
            System.out.println("4. Emprunts très en retard");
            System.out.println("5. Emprunts retournés à temps");
            System.out.println("6. Emprunts retournés en retard");
            System.out.println("7. Membres avec plusieurs emprunts");
            System.out.println("8. Documents populaires");
            System.out.println("9. Documents jamais empruntés");

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
