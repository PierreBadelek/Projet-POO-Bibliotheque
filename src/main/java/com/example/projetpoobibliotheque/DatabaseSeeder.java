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
            System.out.println("\n[1/5] Création des membres...");
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

            service.ajouterDocument(livre1);
            service.ajouterDocument(livre2);
            service.ajouterDocument(livre3);
            service.ajouterDocument(livre4);
            service.ajouterDocument(livre5);
            service.ajouterDocument(livre6);
            service.ajouterDocument(livre7);
            service.ajouterDocument(livre8);
            System.out.println("✓ 8 livres créés");

            // 3. Créer des magazines
            System.out.println("\n[3/5] Création des magazines...");
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
            System.out.println("✓ 5 magazines créés");

            // 4. Créer des emprunts avec différents cas
            System.out.println("\n[4/5] Création des emprunts (différents cas d'utilisation)...");

            // CAS 1: Emprunt en cours, dans les délais
            Emprunt emprunt1 = service.enregistrerEmprunt(livre1, alice);
            System.out.println("  ✓ Cas 1: Alice emprunte '1984' (en cours, dans les délais)");

            // CAS 2: Emprunt en cours, bientôt en retard
            Emprunt emprunt2 = service.enregistrerEmprunt(livre2, bob);
            modifierDateEmprunt(emprunt2, LocalDate.now().minusDays(12)); // Emprunté il y a 12 jours
            System.out.println("  ✓ Cas 2: Bob emprunte 'Le Seigneur des Anneaux' (bientôt en retard)");

            // CAS 3: Emprunt EN RETARD
            Emprunt emprunt3 = service.enregistrerEmprunt(livre3, charlie);
            modifierDateEmprunt(emprunt3, LocalDate.now().minusDays(20)); // Emprunté il y a 20 jours
            System.out.println("  ✓ Cas 3: Charlie emprunte 'Harry Potter' (EN RETARD depuis 6 jours)");

            // CAS 4: Emprunt très en retard
            Emprunt emprunt4 = service.enregistrerEmprunt(mag1, diane);
            modifierDateEmprunt(emprunt4, LocalDate.now().minusDays(35)); // Emprunté il y a 35 jours
            System.out.println("  ✓ Cas 4: Diane emprunte 'Sciences & Vie' (TRÈS EN RETARD depuis 21 jours)");

            // CAS 5: Emprunt retourné à temps
            Emprunt emprunt5 = service.enregistrerEmprunt(livre4, eve);
            modifierDateEmprunt(emprunt5, LocalDate.now().minusDays(10));
            service.enregistrerRetour(emprunt5);
            System.out.println("  ✓ Cas 5: Eve a emprunté et rendu 'L'Étranger' à temps");

            // CAS 6: Emprunt retourné en retard
            Emprunt emprunt6 = service.enregistrerEmprunt(livre5, alice);
            modifierDateEmprunt(emprunt6, LocalDate.now().minusDays(25));
            service.enregistrerRetour(emprunt6);
            System.out.println("  ✓ Cas 6: Alice a emprunté et rendu 'Fondation' en retard");

            // CAS 7: Membre avec plusieurs emprunts
            Emprunt emprunt7 = service.enregistrerEmprunt(mag2, bob);
            System.out.println("  ✓ Cas 7: Bob emprunte aussi 'National Geographic' (2e emprunt actif)");

            // CAS 8: Document très populaire (plusieurs emprunts successifs)
            Emprunt emprunt8 = service.enregistrerEmprunt(livre6, charlie);
            modifierDateEmprunt(emprunt8, LocalDate.now().minusDays(8));
            service.enregistrerRetour(emprunt8);
            Emprunt emprunt9 = service.enregistrerEmprunt(livre6, diane);
            System.out.println("  ✓ Cas 8: 'Le Petit Prince' est très populaire (plusieurs emprunts)");

            // CAS 9: Documents jamais empruntés (disponibles)
            System.out.println("  ✓ Cas 9: 'Dune', 'Les Misérables', et autres magazines restent disponibles");

            System.out.println("\n✓ 9 emprunts créés avec différents cas d'utilisation");

            // 5. Afficher les statistiques finales
            System.out.println("\n[5/5] Génération des statistiques...");
            BibliothequeService.StatistiquesBibliotheque stats = service.genererStatistiques();
            System.out.println(stats);

            System.out.println("\n========== SEEDING TERMINÉ AVEC SUCCÈS ==========");


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
