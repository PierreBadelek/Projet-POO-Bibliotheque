package com.example.projetpoobibliotheque.repository;

import com.example.projetpoobibliotheque.model.Emprunt;
import com.example.projetpoobibliotheque.model.Membre;
import com.example.projetpoobibliotheque.model.Document;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EmpruntRepository extends GenericRepository<Emprunt, Integer> {

    public EmpruntRepository() {
        super(Emprunt.class);
    }

    public List<Emprunt> findByMembre(Membre membre) {
        try (Session session = openSession()) {
            Query<Emprunt> query = session.createQuery(
                "FROM Emprunt WHERE membre = :membre ORDER BY dateEmprunt DESC", Emprunt.class);
            query.setParameter("membre", membre);
            return query.list();
        }
    }

    public List<Emprunt> findByDocument(Document document) {
        try (Session session = openSession()) {
            Query<Emprunt> query = session.createQuery(
                "FROM Emprunt WHERE document = :document ORDER BY dateEmprunt DESC", Emprunt.class);
            query.setParameter("document", document);
            return query.list();
        }
    }

    public List<Emprunt> findEmpruntsEnCours() {
        try (Session session = openSession()) {
            return session.createQuery(
                "FROM Emprunt WHERE dateRetourEffective IS NULL", Emprunt.class).list();
        }
    }

    public List<Emprunt> findEmpruntsEnRetard() {
        try (Session session = openSession()) {
            Query<Emprunt> query = session.createQuery(
                "FROM Emprunt WHERE dateRetourEffective IS NULL AND dateRetourPrevue < :today",
                Emprunt.class);
            query.setParameter("today", LocalDate.now());
            return query.list();
        }
    }

    public List<Emprunt> findEmpruntsTermines() {
        try (Session session = openSession()) {
            return session.createQuery(
                "FROM Emprunt WHERE dateRetourEffective IS NOT NULL", Emprunt.class).list();
        }
    }

    public List<Emprunt> findByMembreEnCours(Membre membre) {
        try (Session session = openSession()) {
            Query<Emprunt> query = session.createQuery(
                "FROM Emprunt WHERE membre = :membre AND dateRetourEffective IS NULL", Emprunt.class);
            query.setParameter("membre", membre);
            return query.list();
        }
    }

    public Optional<Emprunt> findEmpruntEnCoursByDocument(Document document) {
        try (Session session = openSession()) {
            Query<Emprunt> query = session.createQuery(
                "FROM Emprunt WHERE document = :document AND dateRetourEffective IS NULL",
                Emprunt.class);
            query.setParameter("document", document);
            return query.uniqueResultOptional();
        }
    }

    public List<Emprunt> findByDateEmprunt(LocalDate dateDebut, LocalDate dateFin) {
        try (Session session = openSession()) {
            Query<Emprunt> query = session.createQuery(
                "FROM Emprunt WHERE dateEmprunt BETWEEN :dateDebut AND :dateFin",
                Emprunt.class);
            query.setParameter("dateDebut", dateDebut);
            query.setParameter("dateFin", dateFin);
            return query.list();
        }
    }
}
