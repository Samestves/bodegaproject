package com.example.bodegaproject.controllers;

import com.example.bodegaproject.models.Product;
import com.example.bodegaproject.service.ProductService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterProductController {

    private static final Logger LOGGER = Logger.getLogger(RegisterProductController.class.getName());

    @FXML
    private Label productCodeLabel;  // Ahora es un Label en lugar de un TextField

    @FXML
    private TextField productNameField;

    @FXML
    private TextField productPriceField;

    @FXML
    private Label statusLabel;

    private static final int MAX_CODE = 100; // Límite máximo para el código
    private static final int RETRY_LIMIT = 10; // Número máximo de intentos para generar un código único

    @FXML
    private void initialize() {
        try {
            int uniqueCode = generateUniqueCode();
            productCodeLabel.setText(String.valueOf(uniqueCode));
        } catch (RuntimeException e) {
            statusLabel.setText("Error al generar un código único. Intente nuevamente.");
            LOGGER.log(Level.SEVERE, "Error generando código único", e);
        }

        // Evitar que cualquier campo tenga el enfoque al abrir la vista
        productNameField.setFocusTraversable(false);
        productPriceField.setFocusTraversable(false);
    }

    /**
     * Genera un código único verificando que no exista en la base de datos.
     *
     * @return Un código único.
     * @throws RuntimeException Si no se puede generar un código único después de varios intentos.
     */
    private int generateUniqueCode() {
        for (int i = 0; i < RETRY_LIMIT; i++) {
            int generatedCode = (int) (Math.random() * MAX_CODE) + 1; // Genera un código entre 1 y MAX_CODE
            if (!ProductService.isCodeExists(String.valueOf(generatedCode))) {
                return generatedCode;
            }
        }
        throw new RuntimeException("No se pudo generar un código único después de " + RETRY_LIMIT + " intentos.");
    }

    @FXML
    private void handleRegister() {
        String code = productCodeLabel.getText();
        String name = productNameField.getText();
        String priceText = productPriceField.getText();

        // Validar que todos los campos están llenos
        if (code.isEmpty() || name.isEmpty() || priceText.isEmpty()) {
            statusLabel.setText("TODOS LOS CAMPOS SON OBLIGATORIOS.");
            return;
        }

        // Validar que el nombre solo contenga letras y espacios
        if (!name.matches("[a-zA-Z ]+")) {
            statusLabel.setText("EL NOMBRE SOLO PUEDE CONTENER LETRAS.");
            return;
        }

        try {
            // Validar que el precio sea un número
            double price = Double.parseDouble(priceText);

            // Verificar que el precio sea positivo
            if (price <= 0) {
                statusLabel.setText("EL PRECIO DEBE SER UN NÚMERO POSITIVO.");
                return;
            }

            // Convertir el precio a entero
            int priceInt = (int) price;

            // Convertir el nombre a mayúsculas
            name = name.toUpperCase();

            // Verificar si el producto ya existe
            if (ProductService.findProductByName(name)) {
                statusLabel.setText("EL PRODUCTO YA EXISTE. INTENTE CON OTRO NOMBRE.");
                return;
            }

            // Crear y registrar el producto
            Product newProduct = new Product(code, name, priceInt);
            ProductService.addProduct(newProduct);
            clearFields();

            // Mostrar mensaje de éxito
            statusLabel.setText("✅ PRODUCTO REGISTRADO CORRECTAMENTE.");

            // Cerrar la ventana
            productCodeLabel.getScene().getWindow().hide();
        } catch (NumberFormatException e) {
            statusLabel.setText("EL PRECIO DEBE SER UN NÚMERO VÁLIDO.");
        } catch (RuntimeException e) {
            statusLabel.setText("ERROR AL REGISTRAR EL PRODUCTO.");
            LOGGER.log(Level.SEVERE, "Error al cargar productos desde la base de datos", e);
        }

        // Asegurarse de que el mensaje de estado esté en mayúsculas
        statusLabel.setText(statusLabel.getText().toUpperCase());
    }

    private void clearFields() {
        productNameField.clear();
        productPriceField.clear();
    }
}