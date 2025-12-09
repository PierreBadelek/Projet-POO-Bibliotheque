package com.example.projetpoobibliotheque.repository;

import com.example.projetpoobibliotheque.model.Document;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class DocumentRepository extends GenericRepository<Document, Integer> {

    public DocumentRepository() {
        super(Document.class);
    }

    public List<Document> findByTitre(String titre) {
        try (Session session = openSession()) {
            Query<Document> query = session.createQuery(
                "FROM Document WHERE titre LIKE :titre", Document.class);
            query.setParameter("titre", "%" + titre + "%");
            return query.list();
        }
    }

    public List<Document> findByAuteur(String auteur) {
        try (Session session = openSession()) {
            Query<Document> query = session.createQuery(
                "FROM Document WHERE auteur LIKE :auteur", Document.class);
            query.setParameter("auteur", "%" + auteur + "%");
            return query.list();
        }
    }

    public List<Document> findDisponibles() {
        try (Session session = openSession()) {
            return session.createQuery(
                "FROM Document WHERE disponible = true", Document.class).list();
        }
    }

    public List<Document> findByType(String type) {
        try (Session session = openSession()) {
            Query<Document> query = session.createQuery(
                "FROM Document WHERE TYPE(this) = :type", Document.class);
            query.setParameter("type", type);
            return query.list();
        }
    }
}
