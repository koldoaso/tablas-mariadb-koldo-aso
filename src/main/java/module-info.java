module dein.koldo.tablesmariadb {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.logging;
    requires org.mariadb.jdbc;

    exports dein.koldo.tablesmariadb;
    exports dein.koldo.tablesmariadb.controller;
    exports dein.koldo.tablesmariadb.model;

    opens dein.koldo.tablesmariadb.controller to javafx.fxml;
}