package com.example.projetpoobibliotheque.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("LIVRE")
public class Livre extends Document {
    
    @Column(length = 20)
    private String isbn;
    
    public Livre() {}
    
    public Livre(String titre, String auteur, String isbn) {
        super(titre, auteur);
        this.isbn = isbn;
    }
    
    @Override
    public String getType() { return "Livre"; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
}