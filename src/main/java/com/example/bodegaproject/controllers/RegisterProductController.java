package com.example.bodegaproject.controllers;

import com.example.bodegaproject.models.Product;
import com.example.bodegaproject.service.ProductService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterProductController {

    private static final Logger LOGGER = Logger.getLogger(RegisterProductController.class.getName());

    @FXML
    private TextField productNameField;

    @FXML
    private TextField productPriceField;

    @FXML
    private TextField pesoField; // Campo para peso

    @FXML
    private ChoiceBox<String> pesoBox; // Selección de unidad de peso

    @FXML
    private TextField itemCantidad; // Cantidad de productos

    @FXML
    private Label statusLabel;

    @FXML
    private ChoiceBox<String> categoryChoiceBox;

    private static final String[] CATEGORIES = {"Comida", "Bebida", "Limpieza", "Otro"};
    private static final String[] PESO_UNIDADES = {"kg", "L"};

    @FXML
    private void initialize() {
        // Configurar categorías
        categoryChoiceBox.getItems().addAll(CATEGORIES);
        categoryChoiceBox.getSelectionModel().selectFirst();

        // Configurar unidades de peso
        pesoBox.getItems().addAll(PESO_UNIDADES);
        pesoBox.getSelectionModel().selectFirst();

        // Validar entradas en tiempo real
        pesoField.addEventFilter(KeyEvent.KEY_TYPED, this::restrictToDouble);
        itemCantidad.addEventFilter(KeyEvent.KEY_TYPED, this::restrictToInteger);
        productPriceField.addEventFilter(KeyEvent.KEY_TYPED, this::restrictToDouble);
    }

    @FXML
    private void handleRegister() {
        String name = productNameField.getText();
        String priceText = productPriceField.getText();
        String pesoText = pesoField.getText();
        String pesoUnidad = pesoBox.getValue();
        String cantidadText = itemCantidad.getText();

        if (!validateFields(name, priceText, pesoText, cantidadText, pesoUnidad)) return;

        try {
            double price = Double.parseDouble(priceText);
            int cantidad = Integer.parseInt(cantidadText);
            double peso = Double.parseDouble(pesoText);

            String pesoVolumen = peso + " " + pesoUnidad;
            name = name.toUpperCase();

            if (ProductService.findProductByName(name)) {
                setStatus("EL PRODUCTO YA EXISTE. INTENTE CON OTRO NOMBRE.");
                return;
            }

            String code = generateProductCode();

            Product newProduct = new Product(code, name, (int) price, cantidad, pesoVolumen);

            ProductService.addProduct(newProduct);

            clearFields();
            closeWindow();

        } catch (NumberFormatException e) {
            setStatus("LOS CAMPOS NUMÉRICOS DEBEN CONTENER VALORES VÁLIDOS.");
        } catch (RuntimeException e) {
            setStatus("ERROR AL REGISTRAR EL PRODUCTO.");
            LOGGER.log(Level.SEVERE, "Error al registrar el producto", e);
        }
    }

    private boolean validateFields(String name, String priceText, String pesoText, String cantidadText, String pesoUnidad) {
        if (name.isEmpty() || priceText.isEmpty() || pesoText.isEmpty() || cantidadText.isEmpty() || pesoUnidad == null) {
            setStatus("TODOS LOS CAMPOS SON OBLIGATORIOS.");
            return false;
        }

        if (!name.matches("[a-zA-Z ]+")) {
            setStatus("EL NOMBRE SOLO PUEDE CONTENER LETRAS.");
            return false;
        }

        try {
            if (Double.parseDouble(priceText) <= 0) {
                setStatus("EL PRECIO DEBE SER UN NÚMERO POSITIVO.");
                return false;
            }
        } catch (NumberFormatException e) {
            setStatus("EL PRECIO DEBE SER UN NÚMERO VÁLIDO.");
            return false;
        }

        try {
            if (Double.parseDouble(pesoText) <= 0) {
                setStatus("EL PESO DEBE SER UN NÚMERO POSITIVO.");
                return false;
            }
        } catch (NumberFormatException e) {
            setStatus("EL PESO DEBE SER UN NÚMERO VÁLIDO.");
            return false;
        }

        try {
            if (Integer.parseInt(cantidadText) <= 0) {
                setStatus("LA CANTIDAD DEBE SER UN NÚMERO POSITIVO.");
                return false;
            }
        } catch (NumberFormatException e) {
            setStatus("LA CANTIDAD DEBE SER UN NÚMERO VÁLIDO.");
            return false;
        }

        return true;
    }

    private void restrictToDouble(KeyEvent event) {
        String input = event.getCharacter();
        if (!input.matches("[0-9.]") || (input.equals(".") && pesoField.getText().contains("."))) {
            event.consume();
        }
    }

    private void restrictToInteger(KeyEvent event) {
        String input = event.getCharacter();
        if (!input.matches("\\d")) {
            event.consume();
        }
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
        pesoField.clear();
        itemCantidad.clear();
    }

    private void closeWindow() {
        productNameField.getScene().getWindow().hide();
    }
}
