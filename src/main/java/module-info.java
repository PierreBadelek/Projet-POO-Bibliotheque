module com.example.projetpoobibliotheque {
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires java.naming;
    requires java.sql;
    requires javafx.graphics;
    requires javafx.fxml;
    requires javafx.controls;

    // Ouvrir vos entités à Hibernate (obligatoire pour la réflexion)
    opens com.example.projetpoobibliotheque.model to org.hibernate.orm.core;
    opens com.example.projetpoobibliotheque to javafx.fxml;
    opens com.example.projetpoobibliotheque.controller to javafx.fxml;

    // Exporter vos packages
    exports com.example.projetpoobibliotheque;
    exports com.example.projetpoobibliotheque.controller;
    exports com.example.projetpoobibliotheque.model;
}