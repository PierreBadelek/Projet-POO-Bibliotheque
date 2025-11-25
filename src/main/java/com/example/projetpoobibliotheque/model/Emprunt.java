package com.example.projetpoobibliotheque.model;


import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "emprunts")
public class Emprunt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;
    
    @ManyToOne
    @JoinColumn(name = "membre_id", nullable = false)
    private Membre membre;
    
    @Column(nullable = false)
    private LocalDate dateEmprunt;

    @Column(nullable = false)
    private LocalDate dateRetourPrevue;

    @Column
    private LocalDate dateRetourEffective;

    public Emprunt() {}

    public Emprunt(Document document, Membre membre) {
        this.document = document;
        this.membre = membre;
        this.dateEmprunt = LocalDate.now();
        this.dateRetourPrevue = LocalDate.now().plusDays(14); // 14 jours par défaut
    }

    public void retourner() {
        this.dateRetourEffective = LocalDate.now();
        document.setDisponible(true);
    }

    public boolean estEnCours() { return dateRetourEffective == null; }

    public boolean estEnRetard() {
        if (dateRetourEffective != null) {
            return dateRetourEffective.isAfter(dateRetourPrevue);
        }
        return LocalDate.now().isAfter(dateRetourPrevue);
    }

    public int calculerDuree() {
        LocalDate dateFin = dateRetourEffective != null ? dateRetourEffective : LocalDate.now();
        return (int) java.time.temporal.ChronoUnit.DAYS.between(dateEmprunt, dateFin);
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Document getDocument() { return document; }
    public void setDocument(Document document) { this.document = document; }
    public Membre getMembre() { return membre; }
    public void setMembre(Membre membre) { this.membre = membre; }
    public LocalDate getDateEmprunt() { return dateEmprunt; }
    public void setDateEmprunt(LocalDate dateEmprunt) { this.dateEmprunt = dateEmprunt; }
    public LocalDate getDateRetourPrevue() { return dateRetourPrevue; }
    public void setDateRetourPrevue(LocalDate dateRetourPrevue) { this.dateRetourPrevue = dateRetourPrevue; }
    public LocalDate getDateRetourEffective() { return dateRetourEffective; }
    public void setDateRetourEffective(LocalDate dateRetourEffective) { this.dateRetourEffective = dateRetourEffective; }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String statut = estEnCours() ? "EN COURS" : "Retourné le " + dateRetourEffective.format(fmt);
        if (estEnRetard()) statut += " (RETARD)";
        return String.format("%s | %s | Emprunté le %s | Retour prévu: %s | %s",
            document.getTitre(), membre.getNom(), dateEmprunt.format(fmt), dateRetourPrevue.format(fmt), statut);
    }
}