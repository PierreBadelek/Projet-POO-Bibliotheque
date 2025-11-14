module com.example.projetpoobibliotheque {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires java.naming;
    opens com.example.projetpoobibliotheque to javafx.fxml;
    exports com.example.projetpoobibliotheque;

    // IMPORTANT : Ouvrir vos packages à Hibernate

    requires java.sql;

    // Ouvrir vos entités à Hibernate (obligatoire pour la réflexion)
    opens com.example.projetpoobibliotheque.model to org.hibernate.orm.core;

    // Exporter vos packages
    exports com.example.projetpoobibliotheque.model;
}