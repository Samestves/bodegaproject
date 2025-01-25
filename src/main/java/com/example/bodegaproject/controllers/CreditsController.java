package com.example.bodegaproject.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CreditsController {

    public Button btnAgregarCliente;
    @FXML
    private VBox clientesContainer;

    @FXML
    private VBox detalleJuan;

    @FXML
    private void toggleDetalleJuan() {
        boolean isVisible = detalleJuan.isVisible();
        detalleJuan.setVisible(!isVisible);
        detalleJuan.setManaged(!isVisible); // Esto asegura que no ocupe espacio cuando está oculto
    }

    // Ejemplo de agregar un cliente dinámicamente
    public void agregarCliente(String nombre, String deuda, String productos, String fechaUltimaCompra) {
        VBox clienteBox = new VBox();
        clienteBox.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5;");

        HBox clienteHeader = new HBox();
        clienteHeader.setSpacing(10);
        Label nombreLabel = new Label(nombre);
        Button toggleButton = new Button("▼");
        VBox detalleBox = new VBox();
        detalleBox.setVisible(false);
        detalleBox.setManaged(false);

        // Configurar detalles
        detalleBox.getChildren().addAll(
                new Label("Deuda: " + deuda),
                new Label("Productos: " + productos),
                new Label("Fecha Última Compra: " + fechaUltimaCompra)
        );

        toggleButton.setOnAction(event -> {
            boolean isVisible = detalleBox.isVisible();
            detalleBox.setVisible(!isVisible);
            detalleBox.setManaged(!isVisible);
        });

        clienteHeader.getChildren().addAll(nombreLabel, toggleButton);
        clienteBox.getChildren().addAll(clienteHeader, detalleBox);

        // Agregar cliente al contenedor principal
        clientesContainer.getChildren().add(clienteBox);
    }
}
