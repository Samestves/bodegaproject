package com.example.bodegaproject.utils;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class AlertHelper {

    /**
     * Muestra una alerta de error.
     */
    public static void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public static void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setTitle(title);
        alert.setHeaderText(null);

        // Establecer un icono personalizado en la barra de título de la alerta
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();  // Obtener el Stage
        Image icon = new Image(Objects.requireNonNull(AlertHelper.class.getResource("/images/updateExito.png")).toExternalForm());  // Ruta de tu icono
        stage.getIcons().add(icon);  // Establecer el icono en la barra de título

        alert.showAndWait();
    }
}