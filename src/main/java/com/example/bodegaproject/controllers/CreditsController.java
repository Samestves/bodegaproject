package com.example.bodegaproject.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class CreditsController {

    @FXML
    private VBox clientesContainer;    // Contenedor para los clientes

    // Método para agregar un cliente dinámicamente
    public void agregarCliente(String nombre, String deuda, String productos, String fechaUltimaCompra) {
        // Crear la caja principal del cliente con la clase de estilo 'client-card'
        VBox clienteBox = new VBox();
        clienteBox.getStyleClass().add("client-card"); // Añadir clase CSS 'client-card'

        // Crear la cabecera del cliente con el nombre y botón para expandir detalles
        HBox clienteHeader = new HBox();
        clienteHeader.setSpacing(10);
        Label nombreLabel = new Label(nombre);
        nombreLabel.getStyleClass().add("client-name"); // Añadir clase CSS 'client-name'

        Button toggleButton = new Button("▼");
        toggleButton.getStyleClass().add("toggle-button"); // Añadir clase CSS 'toggle-button'

        // Crear el panel de detalles que estará oculto inicialmente
        VBox detalleBox = new VBox();
        detalleBox.getStyleClass().add("client-details"); // Añadir clase CSS 'client-details'
        detalleBox.setVisible(false);
        detalleBox.setManaged(false);

        // Configurar los detalles del cliente
        detalleBox.getChildren().addAll(
                new Label("Deuda: " + deuda),
                new Label("Productos: " + productos),
                new Label("Fecha Última Compra: " + fechaUltimaCompra)
        );

        // Aplicar estilo a las etiquetas de detalle
        for (int i = 0; i < detalleBox.getChildren().size(); i++) {
            (detalleBox.getChildren().get(i)).getStyleClass().add("detail-label");
        }

        // Acción para el botón de expandir detalles
        toggleButton.setOnAction(event -> toggleDetalle(detalleBox));

        // Agregar el encabezado y el panel de detalles a la caja principal
        clienteHeader.getChildren().addAll(nombreLabel, toggleButton);
        clienteBox.getChildren().addAll(clienteHeader, detalleBox);

        // Agregar el cliente al contenedor principal
        clientesContainer.getChildren().add(clienteBox);
    }

    // Método reutilizable para mostrar/ocultar detalles
    private void toggleDetalle(VBox detalleBox) {
        boolean isVisible = detalleBox.isVisible();
        detalleBox.setVisible(!isVisible);
        detalleBox.setManaged(!isVisible);
    }

    // Configuración del botón "Agregar Cliente" (onAction)
    @FXML
    private void handleAgregarCliente() {
        // Crear un Dialog personalizado para agregar los datos del cliente
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Agregar Cliente");
        dialog.setHeaderText("Ingrese los datos del nuevo cliente");

        // Establecer el botón de Aceptar como el botón principal
        ButtonType aceptarButtonType = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelarButtonType = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(aceptarButtonType, cancelarButtonType);

        // Crear los campos de entrada
        VBox vbox = new VBox(10);
        TextField nombreField = new TextField();
        nombreField.setPromptText("Nombre del Cliente");
        TextField deudaField = new TextField();
        deudaField.setPromptText("Deuda");
        TextField productosField = new TextField();
        productosField.setPromptText("Productos");
        DatePicker fechaUltimaCompraPicker = new DatePicker();
        fechaUltimaCompraPicker.setPromptText("Fecha Última Compra");

        vbox.getChildren().addAll(
                new Label("Nombre:"), nombreField,
                new Label("Deuda:"), deudaField,
                new Label("Productos:"), productosField,
                new Label("Fecha Última Compra:"), fechaUltimaCompraPicker
        );

        dialog.getDialogPane().setContent(vbox);

        // Lógica para cuando se presiona el botón Aceptar
        dialog.setResultConverter(button -> {
            if (button == aceptarButtonType) {
                String nombre = nombreField.getText();
                String deuda = deudaField.getText();
                String productos = productosField.getText();
                LocalDate fechaUltimaCompra = fechaUltimaCompraPicker.getValue();

                // Verificamos que todos los campos estén completos
                if (nombre.isEmpty() || deuda.isEmpty() || productos.isEmpty() || fechaUltimaCompra == null) {
                    showAlert();
                    return null;
                }

                // Llamar al método agregarCliente con los datos ingresados
                agregarCliente(nombre, deuda, productos, fechaUltimaCompra.toString());
            }
            return null;
        });

        // Mostrar el diálogo
        dialog.showAndWait();
    }

    // Método para mostrar alertas de error
    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("Todos los campos son obligatorios.");
        alert.showAndWait();
    }
}