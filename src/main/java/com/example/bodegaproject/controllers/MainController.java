package com.example.bodegaproject.controllers;

import com.example.bodegaproject.database.SQLiteConnection;
import com.example.bodegaproject.models.Product;
import com.example.bodegaproject.service.ProductService;
import com.example.bodegaproject.utils.AlertHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;

import static com.example.bodegaproject.database.CreateTable.createProductsTable;

/**
 * Controlador para la pantalla principal de la aplicación.
 * Gestiona la navegación, la búsqueda de productos y la visualización en la tabla.
 */
public class MainController {

    @FXML private TableView<Product> tableView;
    @FXML private TableColumn<Product, String> codigoColumn;
    @FXML private TableColumn<Product, String> productoColumn;
    @FXML private TableColumn<Product, Integer> precioColumn;
    @FXML private TextField searchTextField;
    private ObservableList<Product> masterProductList; // Lista principal de productos

    @FXML
    public void initialize() {
        // Primero, asegurarse de que el directorio y base de datos estén listos
        try {
            // Asegurarse de que el directorio de la base de datos esté listo
            SQLiteConnection.initializeDatabaseDirectory();

            // Establecer conexión
            SQLiteConnection.getConnection();
            System.out.println("✅ Conexión establecida correctamente.");

            // Crear tabla si no existe
            createProductsTable();

            // Configurar la tabla y cargar datos
            configureTableView();
            loadProductData();
            configureSearchField();
        } catch (SQLException e) {
            System.out.println("Error al establecer la conexión con la base de datos: " + e.getMessage());
        }
    }

    private void configureTableView() {
        codigoColumn.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        productoColumn.setCellValueFactory(cellData -> cellData.getValue().productoProperty());
        precioColumn.setCellValueFactory(cellData -> cellData.getValue().precioProperty().asObject());

        // Configurar el placeholder
        Label placeholder = new Label("No hay productos disponibles");
        tableView.setPlaceholder(placeholder);

        // Configurar eventos de la TableView para desactivar el foco y mantener la selección
        configureTableViewFocus(tableView);
    }

    private void configureTableViewFocus(TableView<?> tableView) {
        // Evento para clics en la tabla (mousePressed y mouseClicked)
        EventHandler<MouseEvent> focusHandler = event -> {
            if (tableView.isFocused()) {
                removeFocusFromTable(tableView);
            }
            maintainCellSelection(tableView);
            event.consume();
        };

        tableView.setOnMousePressed(focusHandler);
        tableView.setOnMouseClicked(focusHandler);
    }

    // Método para desactivar el foco en la TableView
    private void removeFocusFromTable(TableView<?> tableView) {
        if (tableView.getParent() != null) {
            tableView.getParent().requestFocus(); // Quitar el foco de la tabla
        }
    }

    // Método para mantener la selección de la celda sin alterar el foco
    private void maintainCellSelection(TableView<?> tableView) {
        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            tableView.getSelectionModel().select(selectedIndex);
        }
    }

    /**
     * Configura el campo de texto para realizar búsquedas en tiempo real.
     */
    private void configureSearchField() {
        searchTextField.textProperty().addListener((observable, oldvalue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                filterProducts(newValue);
            } else {
                resetTableView();
            }
        });
    }

    /**
     * Filtra los productos según el texto ingresado en el campo de búsqueda.
     */
    private void filterProducts(String searchText) {
        ObservableList<Product> filteredList = FXCollections.observableArrayList();

        for (Product product : masterProductList) {
            if (product.getProducto().toLowerCase().contains(searchText.toLowerCase()) ||
                    product.getCodigo().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(product);
            }
        }

        tableView.setItems(filteredList);
    }

    /**
     * Restablece la tabla a la lista completa de productos.
     */
    private void resetTableView() {
        tableView.setItems(masterProductList);
    }

    /**
     * Carga los datos de productos desde la base de datos y actualiza la lista principal.
     */
    private void loadProductData() {
        masterProductList = FXCollections.observableArrayList(ProductService.loadProducts());
        resetTableView();
    }

    @FXML
    private void openRegisterProductView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/register-product-view.fxml"));
            Stage stage = new Stage();

            // Establecer el redimensionamiento
            stage.setResizable(false);

            // Establecer un icono vacío
            Image icon = new Image(Objects.requireNonNull(getClass().getResource("/images/edit.png")).toExternalForm());
            stage.getIcons().add(icon);

            stage.setTitle("Registrar Producto");
            stage.setScene(new Scene(loader.load()));
            stage.show();

            // Escuchar el cierre de la ventana para actualizar la tabla
            stage.setOnHiding(event -> loadProductData());
        } catch (IOException e) {
            AlertHelper.showErrorAlert("Error al abrir la ventana de registro", e.getMessage());
        }
    }

    @FXML
    private void handleUpdateProduct() {
        Product selectedProduct = tableView.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            TextInputDialog dialog = new TextInputDialog(String.valueOf(selectedProduct.getPrecio()));

            dialog.setTitle("Actualizar precio");
            dialog.setHeaderText("Actualizar precio del producto");
            dialog.setContentText("Ingrese el nuevo precio:");

            // Cambiar el icono de la barra de título del TextInputDialog
            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();  // Obtiene el Stage
            Image titleIcon = new Image(Objects.requireNonNull(getClass().getResource("/images/updatePrice.png")).toExternalForm());  // Cargar el icono
            stage.getIcons().add(titleIcon);  // Establecer el icono en la barra de título

            // Establecer un icono personalizado en el TextInputDialog
            Image icon = new Image(Objects.requireNonNull(getClass().getResource("/images/edit.png")).toExternalForm());
            ImageView iconView = new ImageView(icon);
            iconView.setFitWidth(40);  // Ajusta el tamaño del icono
            iconView.setFitHeight(40);
            dialog.getDialogPane().setGraphic(iconView);  // Asignar el gráfico al dialog

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(newPrice -> {
                try {
                    int updatedPrice = Integer.parseInt(newPrice);
                    ProductService.updateProductPrice(selectedProduct, updatedPrice);
                    loadProductData(); // Actualiza la tabla después de modificar
                    AlertHelper.showInfoAlert("Actualización exitosa", "El precio se actualizó correctamente.");
                } catch (NumberFormatException e) {
                    AlertHelper.showErrorAlert("Entrada inválida", "El precio debe ser un número válido.");
                }
            });
        } else {
            AlertHelper.showErrorAlert("Selección requerida", "Por favor selecciona un producto para actualizar su precio.");
        }
    }

    @FXML
    private void handleDeleteProduct() {
        Product selectedProduct = tableView.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            ProductService.deleteProduct(selectedProduct);
            loadProductData(); // Actualiza la tabla después de eliminar
        } else {
            AlertHelper.showErrorAlert("Selección requerida", "Por favor selecciona un producto para eliminar.");
        }
    }
}