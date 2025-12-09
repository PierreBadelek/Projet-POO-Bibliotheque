package com.example.projetpoobibliotheque.controller;

import com.example.projetpoobibliotheque.model.Document;
import com.example.projetpoobibliotheque.model.Emprunt;
import com.example.projetpoobibliotheque.model.Membre;
import com.example.projetpoobibliotheque.service.BibliothequeService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EmpruntsController {

    @FXML
    private TableView<Emprunt> empruntsTable;
    @FXML
    private TableColumn<Emprunt, Integer> idColumn;
    @FXML
    private TableColumn<Emprunt, String> membreColumn;
    @FXML
    private TableColumn<Emprunt, String> documentColumn;
    @FXML
    private TableColumn<Emprunt, LocalDate> dateEmpruntColumn;
    @FXML
    private TableColumn<Emprunt, LocalDate> dateRetourColumn;
    @FXML
    private TableColumn<Emprunt, LocalDate> dateRetourEffectiveColumn;
    @FXML
    private TextField searchField;

    private BibliothequeService service;
    private ObservableList<Emprunt> empruntsList;

    @FXML
    public void initialize() {
        service = new BibliothequeService();

        // Configuration des colonnes
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        membreColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getMembre().getNom() + " " +
                cellData.getValue().getMembre().getPrenom()
            )
        );
        documentColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDocument().getTitre()
            )
        );
        dateEmpruntColumn.setCellValueFactory(new PropertyValueFactory<>("dateEmprunt"));
        dateRetourColumn.setCellValueFactory(new PropertyValueFactory<>("dateRetourPrevue"));

        loadEmprunts();
    }

    @FXML
    private void loadEmprunts() {
        empruntsList = FXCollections.observableArrayList(service.listerTousEmprunts());
        empruntsTable.setItems(empruntsList);
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().toLowerCase();
        if (keyword.isEmpty()) {
            loadEmprunts();
        } else {
            ObservableList<Emprunt> filtered = FXCollections.observableArrayList();
            for (Emprunt e : empruntsList) {
                if (e.getMembre().getNom().toLowerCase().contains(keyword) ||
                    e.getMembre().getPrenom().toLowerCase().contains(keyword) ||
                    e.getDocument().getTitre().toLowerCase().contains(keyword)) {
                    filtered.add(e);
                }
            }
            empruntsTable.setItems(filtered);
        }
    }

    @FXML
    private void handleDelete() {
        Emprunt selected = empruntsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Supprimer cet emprunt?");
            alert.setContentText("ID: " + selected.getId());

            if (alert.showAndWait().get() == ButtonType.OK) {
                try {
                    service.supprimerEmprunt(selected);
                    loadEmprunts();
                    showAlert("Succès", "Emprunt supprimé avec succès!");
                } catch (Exception e) {
                    showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage());
                }
            }
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner un emprunt à supprimer.");
        }
    }

    @FXML
    private void handleRefresh() {
        loadEmprunts();
        searchField.clear();
    }

    @FXML
    private void handleAdd() {
        // Récupérer les listes de membres et documents disponibles
        List<Membre> membres = service.listerTousMembres();
        List<Document> documents = service.listerDocumentsDisponibles();

        if (membres.isEmpty()) {
            showAlert("Erreur", "Aucun membre disponible. Ajoutez d'abord des membres.");
            return;
        }

        if (documents.isEmpty()) {
            showAlert("Erreur", "Aucun document disponible.");
            return;
        }

        // Créer un dialog personnalisé
        Dialog<Emprunt> dialog = new Dialog<>();
        dialog.setTitle("Nouvel Emprunt");
        dialog.setHeaderText("Créer un nouvel emprunt");

        ButtonType createButtonType = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Créer les champs du formulaire
        ComboBox<Membre> membreCombo = new ComboBox<>(FXCollections.observableArrayList(membres));
        membreCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Membre m) {
                return m == null ? "" : m.getNom() + " " + m.getPrenom();
            }
            @Override
            public Membre fromString(String string) {
                return null;
            }
        });

        ComboBox<Document> documentCombo = new ComboBox<>(FXCollections.observableArrayList(documents));
        documentCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Document d) {
                return d == null ? "" : d.getTitre() + " (" + d.getClass().getSimpleName() + ")";
            }
            @Override
            public Document fromString(String string) {
                return null;
            }
        });

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Membre:"), 0, 0);
        grid.add(membreCombo, 1, 0);
        grid.add(new Label("Document:"), 0, 1);
        grid.add(documentCombo, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                Membre selectedMembre = membreCombo.getValue();
                Document selectedDocument = documentCombo.getValue();

                if (selectedMembre != null && selectedDocument != null) {
                    Emprunt emprunt = new Emprunt(selectedDocument, selectedMembre);
                    return emprunt;
                }
            }
            return null;
        });

        Optional<Emprunt> result = dialog.showAndWait();
        result.ifPresent(emprunt -> {
            try {
                service.enregistrerEmprunt(emprunt.getDocument(), emprunt.getMembre());
                loadEmprunts();
                showAlert("Succès", "Emprunt créé avec succès!");
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la création: " + e.getMessage());
            }
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
