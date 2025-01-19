package com.example.bodegaproject.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class MainController {

    @FXML
    private StackPane contentPane;

    @FXML
    private BorderPane viewContainer;

    @FXML
    public void initialize() {

    }

    @FXML
    private void switchToInventory() {
        loadView("/views/inventory-view.fxml");
    }

    @FXML
    private void switchToSales() {
        loadView("/views/sales-view.fxml");
    }

    @FXML
    private void switchToCredits() {
        loadView("/views/credits-view.fxml");
    }

    private void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();


            // Configurar la vista cargada
            root.setOpacity(1.0);
            root.setDisable(false);
            root.setPickOnBounds(false); // Permitir interacción incluso con áreas invisibles
            root.requestFocus(); // Dar el foco a la vista cargada

            // Remover la vista anterior del StackPane
            if (!contentPane.getChildren().isEmpty()) {
                contentPane.getChildren().removeFirst();
            }

            // Agregar la nueva vista al StackPane
            contentPane.getChildren().addFirst(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}