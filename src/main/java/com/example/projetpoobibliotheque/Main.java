package com.example.projetpoobibliotheque;

import com.example.projetpoobibliotheque.model.Livre;
import com.example.projetpoobibliotheque.repository.DocumentRepository;

public class Main {
    public static void main(String[] args) {
        Livre livre = new Livre();
        livre.setTitre("Le petit prince");
        livre.setAuteur("");
        livre.setIsbn("1234567890");
        livre.setDisponible(true);

        DocumentRepository docRepo = new DocumentRepository();
        docRepo.save(livre); // Hérité de GenericRepository
        docRepo.findDisponibles(); // Méthode spécifique
    }
}
