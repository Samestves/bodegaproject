module com.example.bodegaproject {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires java.desktop;

    opens com.example.bodegaproject.controllers to javafx.fxml;
    exports com.example.bodegaproject;
}