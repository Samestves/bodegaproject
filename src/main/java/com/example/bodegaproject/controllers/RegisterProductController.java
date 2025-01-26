package com.example.bodegaproject.controllers;

import com.example.bodegaproject.models.Product;
import com.example.bodegaproject.service.ProductService;
import com.example.bodegaproject.utils.ProductServiceException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterProductController {
    private static final Logger LOGGER = Logger.getLogger(RegisterProductController.class.getName());

    @FXML
    private TextField productNameField;

    @FXML
    private TextField productPriceField;

    @FXML
    private TextField pesoField;

    @FXML
    private ChoiceBox<String> pesoBox;

    @FXML
    private TextField itemCantidad;

    @FXML
    private Label statusLabel;

    @FXML
    private ChoiceBox<String> categoryChoiceBox;

    private static final String[] CATEGORIES = {"Comida", "Bebida", "Limpieza", "Otro"};
    private static final String[] PESO_UNIDADES = {"G", "KG", "L"};

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

        if (!validateFields(name, priceText, cantidadText)) return;

        try {
            double price = Double.parseDouble(priceText);
            int cantidad = Integer.parseInt(cantidadText);

            // Manejar peso opcional
            String pesoVolumen = formatPesoVolumen(pesoText, pesoUnidad);

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
        } catch (ProductServiceException e) {
            setStatus("ERROR AL BUSCAR EL PRODUCTO: " + e.getMessage());
            LOGGER.log(Level.SEVERE, "Error al buscar el producto", e);
        } catch (RuntimeException e) {
            setStatus("ERROR AL REGISTRAR EL PRODUCTO.");
            LOGGER.log(Level.SEVERE, "Error al registrar el producto", e);
        } catch (SQLException e) {
            setStatus("ERROR DE BASE DE DATOS AL REGISTRAR EL PRODUCTO.");
            LOGGER.log(Level.SEVERE, "Error de base de datos", e);
        }
    }

    private boolean validateFields(String name, String priceText, String cantidadText) {
        if (name.isEmpty() || priceText.isEmpty() || cantidadText.isEmpty()) {
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

    private String formatPesoVolumen(String pesoText, String pesoUnidad) {
        if (pesoText.isEmpty() || pesoUnidad == null || pesoUnidad.isEmpty()) {
            return "-"; // Asignar valor por defecto
        }

        try {
            double peso = Double.parseDouble(pesoText);
            if (peso > 0) {
                return peso + " " + pesoUnidad;
            }
        } catch (NumberFormatException ignored) {
            // Ignorar el error si no es un número
        }

        return "-";
    }

    private void setStatus(String message) {
        statusLabel.setText(message.toUpperCase());
    }

    private String generateProductCode() throws SQLException {
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