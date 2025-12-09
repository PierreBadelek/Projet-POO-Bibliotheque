package com.example.projetpoobibliotheque.controller;

import com.example.projetpoobibliotheque.model.Membre;
import com.example.projetpoobibliotheque.repository.MembreRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.util.Optional;

public class MembresController {

    @FXML
    private TableView<Membre> membresTable;
    @FXML
    private TableColumn<Membre, Integer> idColumn;
    @FXML
    private TableColumn<Membre, String> nomColumn;
    @FXML
    private TableColumn<Membre, String> prenomColumn;
    @FXML
    private TableColumn<Membre, String> emailColumn;
    @FXML
    private TextField searchField;

    private MembreRepository membreRepository;
    private ObservableList<Membre> membresList;

    @FXML
    public void initialize() {
        membreRepository = new MembreRepository();

        // Configuration des colonnes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        loadMembres();
    }

    @FXML
    private void loadMembres() {
        membresList = FXCollections.observableArrayList(membreRepository.findAll());
        membresTable.setItems(membresList);
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().toLowerCase();
        if (keyword.isEmpty()) {
            loadMembres();
        } else {
            ObservableList<Membre> filtered = FXCollections.observableArrayList();
            for (Membre m : membresList) {
                if (m.getNom().toLowerCase().contains(keyword) ||
                    m.getPrenom().toLowerCase().contains(keyword) ||
                    m.getEmail().toLowerCase().contains(keyword)) {
                    filtered.add(m);
                }
            }
            membresTable.setItems(filtered);
        }
    }

    @FXML
    private void handleDelete() {
        Membre selected = membresTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Supprimer ce membre?");
            alert.setContentText("Nom: " + selected.getNom() + " " + selected.getPrenom());

            if (alert.showAndWait().get() == ButtonType.OK) {
                membreRepository.delete(selected);
                loadMembres();
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner un membre à supprimer.");
        }
    }

    @FXML
    private void handleRefresh() {
        loadMembres();
        searchField.clear();
    }

    @FXML
    private void handleShowDetails() {
        Membre selected = membresTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Détails du Membre");
            alert.setHeaderText(selected.getNom() + " " + selected.getPrenom());
            alert.setContentText(
                "ID: " + selected.getId() + "\n" +
                "Email: " + selected.getEmail()
            );
            alert.showAndWait();
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner un membre.");
        }
    }

    @FXML
    private void handleAdd() {
        Dialog<Membre> dialog = new Dialog<>();
        dialog.setTitle("Nouveau Membre");
        dialog.setHeaderText("Ajouter un nouveau membre");

        ButtonType createButtonType = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField nomField = new TextField();
        TextField prenomField = new TextField();
        TextField emailField = new TextField();

        grid.add(new Label("Nom:"), 0, 0);
        grid.add(nomField, 1, 0);
        grid.add(new Label("Prénom:"), 0, 1);
        grid.add(prenomField, 1, 1);
        grid.add(new Label("Email:"), 0, 2);
        grid.add(emailField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                String nom = nomField.getText().trim();
                String prenom = prenomField.getText().trim();
                String email = emailField.getText().trim();

                if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty()) {
                    showAlert("Erreur", "Tous les champs sont obligatoires.");
                    return null;
                }

                if (!email.contains("@")) {
                    showAlert("Erreur", "L'email doit être valide.");
                    return null;
                }

                return new Membre(nom, prenom, email);
            }
            return null;
        });

        Optional<Membre> result = dialog.showAndWait();
        result.ifPresent(membre -> {
            membreRepository.save(membre);
            loadMembres();
            showAlert("Succès", "Membre créé avec succès!");
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
