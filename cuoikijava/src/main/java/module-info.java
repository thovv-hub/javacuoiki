module org.example.cuoikijava {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.naming;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires jakarta.servlet;
    requires mysql.connector.j;
    requires java.sql;

    opens org.example.cuoikijava to org.hibernate.orm.core, javafx.fxml;
    exports org.example.cuoikijava;
    exports org.example.cuoikijava.controller;
    opens org.example.cuoikijava.controller to javafx.fxml, org.hibernate.orm.core;
    exports org.example.cuoikijava.dao;
    opens org.example.cuoikijava.dao to javafx.fxml, org.hibernate.orm.core;
    exports org.example.cuoikijava.model;
    opens org.example.cuoikijava.model to javafx.fxml, org.hibernate.orm.core;

    exports org.example.cuoikijava.util;
    opens org.example.cuoikijava.util to javafx.fxml, org.hibernate.orm.core;
}
