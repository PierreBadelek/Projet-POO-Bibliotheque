package com.example.projetpoobibliotheque.repository;

import com.example.projetpoobibliotheque.model.Membre;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class MembreRepository extends GenericRepository<Membre, Integer> {

    public MembreRepository() {
        super(Membre.class);
    }

    public Optional<Membre> findByEmail(String email) {
        try (Session session = openSession()) {
            Query<Membre> query = session.createQuery(
                "FROM Membre WHERE email = :email", Membre.class);
            query.setParameter("email", email);
            return query.uniqueResultOptional();
        }
    }

    public List<Membre> findByNom(String nom) {
        try (Session session = openSession()) {
            Query<Membre> query = session.createQuery(
                "FROM Membre WHERE nom LIKE :nom", Membre.class);
            query.setParameter("nom", "%" + nom + "%");
            return query.list();
        }
    }

    public List<Membre> findMembresAvecEmpruntsEnCours() {
        try (Session session = openSession()) {
            return session.createQuery(
                "SELECT DISTINCT m FROM Membre m JOIN m.emprunts e WHERE e.dateRetourEffective IS NULL",
                Membre.class).list();
        }
    }
}
