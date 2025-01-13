package com.example.bodegaproject.controllers;

import com.example.bodegaproject.models.Product;
import com.example.bodegaproject.service.ProductService;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterProductController {

    private static final Logger LOGGER = Logger.getLogger(RegisterProductController.class.getName());

    @FXML
    private TextField productNameField;

    @FXML
    private TextField productPriceField;

    @FXML
    private Label statusLabel;

    @FXML
    private ChoiceBox<String> categoryChoiceBox;

    private static final String[] CATEGORIES = {"Comida", "Bebida", "Limpieza", "Otro"};

    @FXML
    private void initialize() {
        categoryChoiceBox.getItems().addAll(CATEGORIES);
        categoryChoiceBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleRegister() {
        String name = productNameField.getText();
        String priceText = productPriceField.getText();

        if (!validateFields(name, priceText)) return;

        try {
            double price = Double.parseDouble(priceText);
            if (price <= 0) {
                setStatus("EL PRECIO DEBE SER UN NÚMERO POSITIVO.");
                return;
            }

            name = name.toUpperCase();

            if (ProductService.findProductByName(name)) {
                setStatus("EL PRODUCTO YA EXISTE. INTENTE CON OTRO NOMBRE.");
                return;
            }

            String code = generateProductCode();
            Product newProduct = new Product(code, name, (int) price);
            ProductService.addProduct(newProduct);

            clearFields();
            closeWindow(); // Cierra la ventana después de registrar el producto.

        } catch (NumberFormatException e) {
            setStatus("EL PRECIO DEBE SER UN NÚMERO VÁLIDO.");
        } catch (RuntimeException e) {
            setStatus("ERROR AL REGISTRAR EL PRODUCTO.");
            LOGGER.log(Level.SEVERE, "Error al cargar productos desde la base de datos", e);
        }
    }

    private boolean validateFields(String name, String priceText) {
        if (name.isEmpty() || priceText.isEmpty()) {
            setStatus("TODOS LOS CAMPOS SON OBLIGATORIOS.");
            return false;
        }

        if (!name.matches("[a-zA-Z ]+")) {
            setStatus("EL NOMBRE SOLO PUEDE CONTENER LETRAS.");
            return false;
        }

        return true;
    }

    private void setStatus(String message) {
        statusLabel.setText(message.toUpperCase());
    }

    private String generateProductCode() {
        String category = categoryChoiceBox.getValue();
        String prefix = getCategoryPrefix(category);
        String lastCode = ProductService.getLastProductCode(prefix);

        int nextNumber = 1;
        if (lastCode != null && lastCode.startsWith(prefix)) {
            nextNumber = Integer.parseInt(lastCode.substring(prefix.length())) + 1;
        }

        return prefix + String.format("%03d", nextNumber);
    }

    private String getCategoryPrefix(String category) {
        return switch (category) {
            case "Bebida" -> "B";
            case "Limpieza" -> "L";
            case "Otro" -> "O";
            default -> "C";
        };
    }

    private void clearFields() {
        productNameField.clear();
        productPriceField.clear();
    }

    private void closeWindow() {
        // Cierra la ventana actual
        productNameField.getScene().getWindow().hide();
    }
}