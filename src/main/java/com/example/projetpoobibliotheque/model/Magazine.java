package com.example.projetpoobibliotheque.model;

import jakarta.persistence.*;
@Entity
@DiscriminatorValue("MAGAZINE")
public class Magazine extends Document {
    
    @Column
    private int numero;
    
    public Magazine() {}
    
    public Magazine(String titre, String auteur, int numero) {
        super(titre, auteur);
        this.numero = numero;
    }
    
    @Override
    public String getType() { return "Magazine"; }
    
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }
}