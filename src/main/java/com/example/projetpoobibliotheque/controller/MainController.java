package com.example.projetpoobibliotheque.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class MainController {

    @FXML
    private BorderPane mainPane;

    @FXML
    public void showEmprunts() {
        loadView("/EmpruntsView.fxml");
    }

    @FXML
    public void showDocuments() {
        loadView("/DocumentsView.fxml");
    }

    @FXML
    public void showMembres() {
        loadView("/MembresView.fxml");
    }

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            VBox view = loader.load();
            mainPane.setCenter(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        // Charger la vue des emprunts par défaut
        showEmprunts();
    }
}
