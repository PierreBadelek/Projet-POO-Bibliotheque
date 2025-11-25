package com.example.projetpoobibliotheque.model;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import com.example.projetpoobibliotheque.HibernateUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Bibliotheque {
    private String nom;

    public Bibliotheque(String nom) {
        this.nom = nom;
    }

    // ==================== GESTION DES MEMBRES ====================

    public void ajouterMembre(Membre membre) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(membre);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Erreur lors de l'ajout du membre: " + e.getMessage(), e);
        }
    }

    public void supprimerMembre(Membre membre) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Membre membreToDelete = session.get(Membre.class, membre.getId());
            if (membreToDelete != null) {
                session.remove(membreToDelete);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Erreur lors de la suppression du membre: " + e.getMessage(), e);
        }
    }

    public Membre rechercherMembre(String critere) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Membre> query = session.createQuery(
                "FROM Membre m WHERE m.nom LIKE :critere OR m.email LIKE :critere",
                Membre.class
            );
            query.setParameter("critere", "%" + critere + "%");
            query.setMaxResults(1);
            return query.uniqueResult();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche du membre: " + e.getMessage(), e);
        }
    }

    public List<Membre> listerTousMembres() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Membre", Membre.class).list();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des membres: " + e.getMessage(), e);
        }
    }

    // ==================== GESTION DES DOCUMENTS ====================

    public void ajouterDocument(Document document) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(document);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Erreur lors de l'ajout du document: " + e.getMessage(), e);
        }
    }

    public void supprimerDocument(Document document) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Document docToDelete = session.get(Document.class, document.getId());
            if (docToDelete != null) {
                session.remove(docToDelete);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Erreur lors de la suppression du document: " + e.getMessage(), e);
        }
    }

    public List<Document> rechercherDocument(String critere) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Document> query = session.createQuery(
                "FROM Document d WHERE d.titre LIKE :critere OR d.auteur LIKE :critere",
                Document.class
            );
            query.setParameter("critere", "%" + critere + "%");
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche de documents: " + e.getMessage(), e);
        }
    }

    public List<Document> listerDocumentsDisponibles() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Document> query = session.createQuery(
                "FROM Document d WHERE d.disponible = true",
                Document.class
            );
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des documents disponibles: " + e.getMessage(), e);
        }
    }

    public List<Document> listerTousDocuments() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Document", Document.class).list();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des documents: " + e.getMessage(), e);
        }
    }

    // ==================== GESTION DES EMPRUNTS ====================

    public Emprunt enregistrerEmprunt(Membre membre, Document document) {
        if (!document.isDisponible()) {
            throw new IllegalStateException("Le document n'est pas disponible");
        }

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Recharger les entités dans la session courante
            Membre membreMerged = session.merge(membre);
            Document documentMerged = session.merge(document);

            // Créer l'emprunt
            Emprunt emprunt = new Emprunt(documentMerged, membreMerged);
            documentMerged.setDisponible(false);

            session.persist(emprunt);
            session.merge(documentMerged);

            transaction.commit();
            return emprunt;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Erreur lors de l'enregistrement de l'emprunt: " + e.getMessage(), e);
        }
    }

    public void enregistrerRetour(Emprunt emprunt) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            Emprunt empruntToUpdate = session.get(Emprunt.class, emprunt.getId());
            if (empruntToUpdate != null) {
                empruntToUpdate.retourner();
                session.merge(empruntToUpdate);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Erreur lors de l'enregistrement du retour: " + e.getMessage(), e);
        }
    }

    public List<Emprunt> listerEmpruntsEnCours() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Emprunt> query = session.createQuery(
                "FROM Emprunt e WHERE e.dateRetourEffective IS NULL",
                Emprunt.class
            );
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des emprunts en cours: " + e.getMessage(), e);
        }
    }

    public List<Emprunt> listerEmpruntsEnRetard() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Emprunt> query = session.createQuery(
                "FROM Emprunt e WHERE e.dateRetourEffective IS NULL AND e.dateRetourPrevue < :today",
                Emprunt.class
            );
            query.setParameter("today", LocalDate.now());
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des emprunts en retard: " + e.getMessage(), e);
        }
    }

    public List<Emprunt> listerTousEmprunts() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Emprunt", Emprunt.class).list();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des emprunts: " + e.getMessage(), e);
        }
    }

    // ==================== STATISTIQUES ====================

    public void genererStatistiques() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            long totalDocuments = session.createQuery("SELECT COUNT(d) FROM Document d", Long.class).uniqueResult();
            long totalMembres = session.createQuery("SELECT COUNT(m) FROM Membre m", Long.class).uniqueResult();
            long totalEmpruntsEnCours = session.createQuery("SELECT COUNT(e) FROM Emprunt e WHERE e.dateRetourEffective IS NULL", Long.class).uniqueResult();
            long totalEmpruntsRetard = session.createQuery("SELECT COUNT(e) FROM Emprunt e WHERE e.dateRetourEffective IS NULL AND e.dateRetourPrevue < :today", Long.class)
                .setParameter("today", LocalDate.now())
                .uniqueResult();

            System.out.println("========== STATISTIQUES BIBLIOTHÈQUE " + nom + " ==========");
            System.out.println("Total documents: " + totalDocuments);
            System.out.println("Total membres: " + totalMembres);
            System.out.println("Emprunts en cours: " + totalEmpruntsEnCours);
            System.out.println("Emprunts en retard: " + totalEmpruntsRetard);
            System.out.println("=".repeat(50));
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération des statistiques: " + e.getMessage(), e);
        }
    }

    public List<Document> documentsLesPlusEmpruntes() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Document> query = session.createQuery(
                "SELECT e.document FROM Emprunt e GROUP BY e.document ORDER BY COUNT(e) DESC",
                Document.class
            );
            query.setMaxResults(10);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des documents les plus empruntés: " + e.getMessage(), e);
        }
    }

    // ==================== GETTERS/SETTERS ====================

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
}
