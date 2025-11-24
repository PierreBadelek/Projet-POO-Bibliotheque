package com.example.projetpoobibliotheque;

import com.example.projetpoobibliotheque.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.hibernate.Session;

import java.util.List;

public class MainController {

    @FXML private TableView<Document> tableDocuments;
    @FXML private TableColumn<Document, String> titreCol;
    @FXML private TableColumn<Document, String> auteurCol;
    @FXML private TableColumn<Document, Boolean> disponibleCol;

    @FXML private TableView<Emprunt> tableEmprunts;
    @FXML private TableColumn<Emprunt, String> empruntDocCol;
    @FXML private TableColumn<Emprunt, String> empruntMembreCol;
    @FXML private TableColumn<Emprunt, String> empruntDateCol;
    @FXML private TableColumn<Emprunt, String> empruntRetourCol;

    @FXML private TableView<Membre> tableMembres;
    @FXML private TableColumn<Membre, String> membreNomCol;
    @FXML private TableColumn<Membre, String> membreEmailCol;

    @FXML private TableView<Livre> tableLivres;
    @FXML private TableColumn<Livre, String> livreTitreCol;
    @FXML private TableColumn<Livre, String> livreAuteurCol;
    @FXML private TableColumn<Livre, String> livreIsbnCol;

    @FXML private TableView<Magazine> tableMagazines;
    @FXML private TableColumn<Magazine, String> magTitreCol;
    @FXML private TableColumn<Magazine, String> magAuteurCol;
    @FXML private TableColumn<Magazine, Integer> magNumCol;

    @FXML
    public void initialize() {
        // Documents
        titreCol.setCellValueFactory(new PropertyValueFactory<>("titre"));
        auteurCol.setCellValueFactory(new PropertyValueFactory<>("auteur"));
        disponibleCol.setCellValueFactory(new PropertyValueFactory<>("disponible"));

        // Emprunts
        empruntDocCol.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getDocument().getTitre()));
        empruntMembreCol.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getMembre().getNom()));
        empruntDateCol.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getDateEmprunt().toString()));
        empruntRetourCol.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(
                        cell.getValue().getDateRetour() != null ? cell.getValue().getDateRetour().toString() : "En cours"
                ));

        // Membres
        membreNomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        membreEmailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Livres
        livreTitreCol.setCellValueFactory(new PropertyValueFactory<>("titre"));
        livreAuteurCol.setCellValueFactory(new PropertyValueFactory<>("auteur"));
        livreIsbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));

        // Magazines
        magTitreCol.setCellValueFactory(new PropertyValueFactory<>("titre"));
        magAuteurCol.setCellValueFactory(new PropertyValueFactory<>("auteur"));
        magNumCol.setCellValueFactory(new PropertyValueFactory<>("numero"));
    }

    @FXML
    private void loadDocuments() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Document> documents = session.createQuery("from Document", Document.class).list();
            tableDocuments.getItems().setAll(documents);
        }
    }

    @FXML
    private void loadEmprunts() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Emprunt> emprunts = session.createQuery("from Emprunt", Emprunt.class).list();
            tableEmprunts.getItems().setAll(emprunts);
        }
    }

    @FXML
    private void loadMembres() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Membre> membres = session.createQuery("from Membre", Membre.class).list();
            tableMembres.getItems().setAll(membres);
        }
    }

    @FXML
    private void loadLivres() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Livre> livres = session.createQuery("from Livre", Livre.class).list();
            tableLivres.getItems().setAll(livres);
        }
    }

    @FXML
    private void loadMagazines() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Magazine> magazines = session.createQuery("from Magazine", Magazine.class).list();
            tableMagazines.getItems().setAll(magazines);
        }
    }
}
