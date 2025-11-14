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
    
    @Column
    private LocalDate dateRetour;
    
    public Emprunt() {}
    
    public Emprunt(Document document, Membre membre) {
        this.document = document;
        this.membre = membre;
        this.dateEmprunt = LocalDate.now();
    }
    
    public void retourner() {
        this.dateRetour = LocalDate.now();
        document.setDisponible(true);
    }
    
    public boolean estEnCours() { return dateRetour == null; }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Document getDocument() { return document; }
    public void setDocument(Document document) { this.document = document; }
    public Membre getMembre() { return membre; }
    public void setMembre(Membre membre) { this.membre = membre; }
    public LocalDate getDateEmprunt() { return dateEmprunt; }
    public void setDateEmprunt(LocalDate dateEmprunt) { this.dateEmprunt = dateEmprunt; }
    public LocalDate getDateRetour() { return dateRetour; }
    public void setDateRetour(LocalDate dateRetour) { this.dateRetour = dateRetour; }
    
    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String statut = estEnCours() ? "EN COURS" : "Retourné le " + dateRetour.format(fmt);
        return String.format("%s | %s | Emprunté le %s | %s",
            document.getTitre(), membre.getNom(), dateEmprunt.format(fmt), statut);
    }
}