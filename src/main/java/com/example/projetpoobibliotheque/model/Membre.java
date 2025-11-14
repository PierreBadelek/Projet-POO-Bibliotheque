package com.example.projetpoobibliotheque.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "membres")
public class Membre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(nullable = false)
    private String nom;
    
    @Column(nullable = false)
    private String email;
    
    @OneToMany(mappedBy = "membre", cascade = CascadeType.ALL)
    private List<Emprunt> emprunts = new ArrayList<>();
    
    public Membre() {}
    
    public Membre(String nom, String email) {
        this.nom = nom;
        this.email = email;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Emprunt> getEmprunts() { return emprunts; }
    public void setEmprunts(List<Emprunt> emprunts) { this.emprunts = emprunts; }
    
    @Override
    public String toString() {
        return String.format("#%d - %s (%s)", id, nom, email);
    }
}