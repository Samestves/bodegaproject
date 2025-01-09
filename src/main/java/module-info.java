module com.example.bodegaproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.bodegaproject.controllers to javafx.fxml;

    exports com.example.bodegaproject;
}
