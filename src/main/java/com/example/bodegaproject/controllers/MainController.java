package com.example.bodegaproject.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Tooltip;

import java.io.IOException;

public class MainController {

    public BorderPane viewContainer;

    public HBox buttonBar;

    @FXML
    private ImageView mainImageView;

    @FXML
    private StackPane contentPane;

    @FXML
    private Button homeButton;

    @FXML
    private Button inventoryButton;

    @FXML
    private Button salesButton;

    @FXML
    private Button creditsButton;

    @FXML
    public void initialize() {
        // Configurar Tooltips para los botones con el estilo deseado
        setupTooltip(inventoryButton, "Inventario");
        setupTooltip(salesButton, "Ventas");
        setupTooltip(creditsButton, "Créditos");
    }

    private void setupTooltip(Button button, String tooltipText) {
        // Crear el Tooltip con fondo #f4f4f4 y texto en negro
        Tooltip tooltip = new Tooltip(tooltipText);
        tooltip.setStyle("-fx-background-color: #f4f4f4; -fx-text-fill: black; -fx-font-size: 11px; -fx-font-family: Montserrat Bold;");

        // Establecer el tiempo de aparición del Tooltip
        Tooltip.install(button, tooltip);
        tooltip.setShowDelay(javafx.util.Duration.millis(400)); // Tiempo de 0.3 segundos
    }

    // Método para cambiar al contenido principal (Home)
    @FXML
    private void switchToMain() {
        // Limpia el contenido del StackPane
        contentPane.getChildren().clear();

        // Añade el ImageView al StackPane
        contentPane.getChildren().add(mainImageView);

        // Asegúrate de que el ImageView esté visible
        mainImageView.setVisible(true);

        // Cambiar el color de fondo del StackPane solo si es diferente
        String currentBackground = contentPane.getStyle();
        if (!"-fx-background-color: #212529;".equals(currentBackground)) {
            contentPane.setStyle("-fx-background-color: #212529;");
        }
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

            // Cambiar el color de fondo del StackPane solo si es diferente
            String currentBackground = contentPane.getStyle();
            if (!"-fx-background-color: white;".equals(currentBackground)) {
                contentPane.setStyle("-fx-background-color: white;");
            }

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
