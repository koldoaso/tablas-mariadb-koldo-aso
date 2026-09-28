module dein.koldo.tablesmariadb {
    requires javafx.controls;
    requires javafx.fxml;


    opens dein.koldo.tablesmariadb to javafx.fxml;
    exports dein.koldo.tablesmariadb;
}