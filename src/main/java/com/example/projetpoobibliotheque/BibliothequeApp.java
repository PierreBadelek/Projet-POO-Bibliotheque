package com.example.projetpoobibliotheque;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.image.Image;

public class BibliothequeApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
        Scene scene = new Scene(loader.load(), 450, 350);
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/Image/AppBibliotheque.png")));
        primaryStage.setTitle("Bibliothèque POO 3A");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
