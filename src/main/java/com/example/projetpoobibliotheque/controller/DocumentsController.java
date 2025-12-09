package com.example.projetpoobibliotheque.controller;

import com.example.projetpoobibliotheque.model.Document;
import com.example.projetpoobibliotheque.model.Livre;
import com.example.projetpoobibliotheque.model.Magazine;
import com.example.projetpoobibliotheque.repository.DocumentRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class DocumentsController {

    @FXML
    private TableView<Document> documentsTable;
    @FXML
    private TableColumn<Document, Integer> idColumn;
    @FXML
    private TableColumn<Document, String> titreColumn;
    @FXML
    private TableColumn<Document, String> auteurColumn;
    @FXML
    private TableColumn<Document, String> typeColumn;
    @FXML
    private TableColumn<Document, Boolean> disponibleColumn;
    @FXML
    private TextField searchField;

    private DocumentRepository documentRepository;
    private ObservableList<Document> documentsList;

    @FXML
    public void initialize() {
        documentRepository = new DocumentRepository();

        // Configuration des colonnes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        auteurColumn.setCellValueFactory(new PropertyValueFactory<>("auteur"));
        typeColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getClass().getSimpleName()
            )
        );
        disponibleColumn.setCellValueFactory(new PropertyValueFactory<>("disponible"));

        loadDocuments();
    }

    @FXML
    private void loadDocuments() {
        documentsList = FXCollections.observableArrayList(documentRepository.findAll());
        documentsTable.setItems(documentsList);
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().toLowerCase();
        if (keyword.isEmpty()) {
            loadDocuments();
        } else {
            ObservableList<Document> filtered = FXCollections.observableArrayList();
            for (Document d : documentsList) {
                if (d.getTitre().toLowerCase().contains(keyword) ||
                    d.getAuteur().toLowerCase().contains(keyword)) {
                    filtered.add(d);
                }
            }
            documentsTable.setItems(filtered);
        }
    }

    @FXML
    private void handleDelete() {
        Document selected = documentsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Supprimer ce document?");
            alert.setContentText("Titre: " + selected.getTitre());

            if (alert.showAndWait().get() == ButtonType.OK) {
                documentRepository.delete(selected);
                loadDocuments();
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner un document à supprimer.");
        }
    }

    @FXML
    private void handleRefresh() {
        loadDocuments();
        searchField.clear();
    }

    @FXML
    private void handleShowDetails() {
        Document selected = documentsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Détails du Document");
            alert.setHeaderText(selected.getTitre());
            alert.setContentText(
                "ID: " + selected.getId() + "\n" +
                "Auteur: " + selected.getAuteur() + "\n" +
                "Type: " + selected.getClass().getSimpleName() + "\n" +
                "Disponible: " + (selected.isDisponible() ? "Oui" : "Non")
            );
            alert.showAndWait();
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner un document.");
        }
    }

    @FXML
    private void handleAdd() {
        Dialog<Document> dialog = new Dialog<>();
        dialog.setTitle("Nouveau Document");
        dialog.setHeaderText("Ajouter un nouveau document");

        ButtonType createButtonType = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        ComboBox<String> typeCombo = new ComboBox<>(FXCollections.observableArrayList("Livre", "Magazine"));
        TextField titreField = new TextField();
        TextField auteurField = new TextField();
        TextField extraField = new TextField();
        Label extraLabel = new Label("ISBN:");

        typeCombo.setValue("Livre");
        typeCombo.setOnAction(e -> {
            if (typeCombo.getValue().equals("Livre")) {
                extraLabel.setText("ISBN:");
                extraField.setPromptText("978-XXXXXXXXXX");
            } else {
                extraLabel.setText("Numéro:");
                extraField.setPromptText("1234");
            }
        });

        grid.add(new Label("Type:"), 0, 0);
        grid.add(typeCombo, 1, 0);
        grid.add(new Label("Titre:"), 0, 1);
        grid.add(titreField, 1, 1);
        grid.add(new Label("Auteur:"), 0, 2);
        grid.add(auteurField, 1, 2);
        grid.add(extraLabel, 0, 3);
        grid.add(extraField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                String titre = titreField.getText().trim();
                String auteur = auteurField.getText().trim();
                String extra = extraField.getText().trim();

                if (titre.isEmpty() || auteur.isEmpty() || extra.isEmpty()) {
                    showAlert("Erreur", "Tous les champs sont obligatoires.");
                    return null;
                }

                if (typeCombo.getValue().equals("Livre")) {
                    return new Livre(titre, auteur, extra);
                } else {
                    try {
                        int numero = Integer.parseInt(extra);
                        return new Magazine(titre, auteur, numero);
                    } catch (NumberFormatException e) {
                        showAlert("Erreur", "Le numéro doit être un nombre entier.");
                        return null;
                    }
                }
            }
            return null;
        });

        Optional<Document> result = dialog.showAndWait();
        result.ifPresent(document -> {
            documentRepository.save(document);
            loadDocuments();
            showAlert("Succès", "Document créé avec succès!");
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
