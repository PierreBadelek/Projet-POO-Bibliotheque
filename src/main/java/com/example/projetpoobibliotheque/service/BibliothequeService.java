package com.example.projetpoobibliotheque.service;

import com.example.projetpoobibliotheque.model.Document;
import com.example.projetpoobibliotheque.model.Emprunt;
import com.example.projetpoobibliotheque.model.Membre;
import com.example.projetpoobibliotheque.repository.DocumentRepository;
import com.example.projetpoobibliotheque.repository.EmpruntRepository;
import com.example.projetpoobibliotheque.repository.MembreRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service Layer - Couche métier de la bibliothèque
 * Fait le lien entre les contrôleurs et les repositories
 */
public class BibliothequeService {

    private final DocumentRepository documentRepository;
    private final MembreRepository membreRepository;
    private final EmpruntRepository empruntRepository;

    public BibliothequeService() {
        this.documentRepository = new DocumentRepository();
        this.membreRepository = new MembreRepository();
        this.empruntRepository = new EmpruntRepository();
    }

    // ==================== GESTION DES MEMBRES ====================

    public void ajouterMembre(Membre membre) {
        // Validation : email unique
        if (membreRepository.findByEmail(membre.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Un membre avec cet email existe déjà");
        }
        membreRepository.save(membre);
    }

    public void modifierMembre(Membre membre) {
        // Validation : vérifier que l'email n'est pas déjà utilisé par un autre membre
        Optional<Membre> existant = membreRepository.findByEmail(membre.getEmail());
        if (existant.isPresent() && existant.get().getId() != membre.getId()) {
            throw new IllegalArgumentException("Cet email est déjà utilisé par un autre membre");
        }
        membreRepository.update(membre);
    }

    public void supprimerMembre(Membre membre) {
        // Validation : pas d'emprunts en cours
        List<Emprunt> empruntsEnCours = empruntRepository.findByMembreEnCours(membre);
        if (!empruntsEnCours.isEmpty()) {
            throw new IllegalStateException("Impossible de supprimer un membre avec des emprunts en cours");
        }
        membreRepository.delete(membre);
    }

    public Optional<Membre> rechercherMembreParEmail(String email) {
        return membreRepository.findByEmail(email);
    }

    public List<Membre> rechercherMembreParNom(String nom) {
        return membreRepository.findByNom(nom);
    }

    public List<Membre> listerTousMembres() {
        return membreRepository.findAll();
    }

    public List<Membre> listerMembresAvecEmpruntsEnCours() {
        return membreRepository.findMembresAvecEmpruntsEnCours();
    }

    // ==================== GESTION DES DOCUMENTS ====================

    public void ajouterDocument(Document document) {
        documentRepository.save(document);
    }

    public void modifierDocument(Document document) {
        documentRepository.update(document);
    }

    public void supprimerDocument(Document document) {
        // Validation : document disponible (pas emprunté)
        if (!document.isDisponible()) {
            throw new IllegalStateException("Impossible de supprimer un document emprunté");
        }
        documentRepository.delete(document);
    }

    public List<Document> rechercherDocumentParTitre(String titre) {
        return documentRepository.findByTitre(titre);
    }

    public List<Document> rechercherDocumentParAuteur(String auteur) {
        return documentRepository.findByAuteur(auteur);
    }

    public List<Document> listerDocumentsDisponibles() {
        return documentRepository.findDisponibles();
    }

    public List<Document> listerTousDocuments() {
        return documentRepository.findAll();
    }

    public Optional<Document> trouverDocumentParId(int id) {
        return documentRepository.findById(id);
    }

    // ==================== GESTION DES EMPRUNTS ====================

    public Emprunt enregistrerEmprunt(Document document, Membre membre) {
        return enregistrerEmprunt(document, membre, null);
    }

    public Emprunt enregistrerEmprunt(Document document, Membre membre, LocalDate dateRetourPrevue) {
        // Validation : document disponible
        if (!document.isDisponible()) {
            throw new IllegalStateException("Le document n'est pas disponible");
        }

        // Vérifier qu'il n'y a pas d'emprunt en cours pour ce document
        Optional<Emprunt> empruntExistant = empruntRepository.findEmpruntEnCoursByDocument(document);
        if (empruntExistant.isPresent()) {
            throw new IllegalStateException("Ce document est déjà emprunté");
        }

        // Créer l'emprunt
        Emprunt emprunt = new Emprunt(document, membre);
        if (dateRetourPrevue != null) {
            emprunt.setDateRetourPrevue(dateRetourPrevue);
        }

        // Marquer le document comme indisponible
        document.setDisponible(false);

        // Sauvegarder
        empruntRepository.save(emprunt);
        documentRepository.update(document);

        return emprunt;
    }

    public void enregistrerRetour(Emprunt emprunt) {
        // Validation : emprunt en cours
        if (!emprunt.estEnCours()) {
            throw new IllegalStateException("Cet emprunt est déjà terminé");
        }

        // Effectuer le retour
        emprunt.retourner();

        // Mettre à jour
        empruntRepository.update(emprunt);
        documentRepository.update(emprunt.getDocument());
    }

    public void supprimerEmprunt(Emprunt emprunt) {
        // Si l'emprunt est en cours, rendre le document disponible
        if (emprunt.estEnCours()) {
            emprunt.getDocument().setDisponible(true);
            documentRepository.update(emprunt.getDocument());
        }
        empruntRepository.delete(emprunt);
    }

    public List<Emprunt> listerEmpruntsEnCours() {
        return empruntRepository.findEmpruntsEnCours();
    }

    public List<Emprunt> listerEmpruntsEnRetard() {
        return empruntRepository.findEmpruntsEnRetard();
    }

    public List<Emprunt> listerEmpruntsTermines() {
        return empruntRepository.findEmpruntsTermines();
    }

    public List<Emprunt> listerTousEmprunts() {
        return empruntRepository.findAll();
    }

    public List<Emprunt> listerEmpruntsParMembre(Membre membre) {
        return empruntRepository.findByMembre(membre);
    }

    public List<Emprunt> listerEmpruntsParDocument(Document document) {
        return empruntRepository.findByDocument(document);
    }

    // ==================== STATISTIQUES ====================

    public StatistiquesBibliotheque genererStatistiques() {
        long totalDocuments = documentRepository.findAll().size();
        long documentsDisponibles = documentRepository.findDisponibles().size();
        long totalMembres = membreRepository.findAll().size();
        long empruntsEnCours = empruntRepository.findEmpruntsEnCours().size();
        long empruntsEnRetard = empruntRepository.findEmpruntsEnRetard().size();

        return new StatistiquesBibliotheque(
            totalDocuments,
            documentsDisponibles,
            totalMembres,
            empruntsEnCours,
            empruntsEnRetard
        );
    }

    // Classe interne pour les statistiques
    public static class StatistiquesBibliotheque {
        public final long totalDocuments;
        public final long documentsDisponibles;
        public final long totalMembres;
        public final long empruntsEnCours;
        public final long empruntsEnRetard;

        public StatistiquesBibliotheque(long totalDocuments, long documentsDisponibles,
                                        long totalMembres, long empruntsEnCours, long empruntsEnRetard) {
            this.totalDocuments = totalDocuments;
            this.documentsDisponibles = documentsDisponibles;
            this.totalMembres = totalMembres;
            this.empruntsEnCours = empruntsEnCours;
            this.empruntsEnRetard = empruntsEnRetard;
        }

        @Override
        public String toString() {
            return String.format(
                "Statistiques Bibliothèque:\n" +
                "- Total documents: %d (dont %d disponibles)\n" +
                "- Total membres: %d\n" +
                "- Emprunts en cours: %d\n" +
                "- Emprunts en retard: %d",
                totalDocuments, documentsDisponibles, totalMembres, empruntsEnCours, empruntsEnRetard
            );
        }
    }
}
