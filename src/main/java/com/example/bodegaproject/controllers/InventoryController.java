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
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import static com.example.bodegaproject.database.CreateTable.createProductsTable;
import static com.example.bodegaproject.utils.IconUtils.createIcon;

public class InventoryController {



    @FXML private TableView<Product> tableView;
    @FXML private TableColumn<Product, String> codigoColumn;
    @FXML private TableColumn<Product, String> productoColumn;
    @FXML private TableColumn<Product, Integer> precioColumn;
    @FXML private TableColumn<Product, String> pesoColumn;
    @FXML private TableColumn<Product, Integer> cantidadColumn;
    @FXML private TextField searchTextField;
    private ObservableList<Product> masterProductList;

    @FXML
    public void initialize() {
        try {
            initializeDatabase();
            configureTableView();
            loadProductData();
            configureSearchField();
        } catch (SQLException e) {
            System.out.println("Error al establecer la conexión con la base de datos: " + e.getMessage());
        }
    }

    private void initializeDatabase() throws SQLException {
        SQLiteConnection.initializeDatabaseDirectory();
        SQLiteConnection.getConnection();
        System.out.println("✅ Conexión establecida correctamente.");
        createProductsTable();
    }

    private void configureTableView() {
        codigoColumn.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        productoColumn.setCellValueFactory(cellData -> cellData.getValue().productoProperty());
        precioColumn.setCellValueFactory(cellData -> cellData.getValue().precioProperty().asObject());
        pesoColumn.setCellValueFactory(cellData -> cellData.getValue().pesoProperty());
        cantidadColumn.setCellValueFactory(cellData -> cellData.getValue().cantidadProperty().asObject());
        tableView.setPlaceholder(new Label("No hay productos disponibles"));
        configureTableViewFocus(tableView);
    }

    private void configureTableViewFocus(TableView<?> tableView) {
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

    private void removeFocusFromTable(TableView<?> tableView) {
        if (tableView.getParent() != null) {
            tableView.getParent().requestFocus();
        }
    }

    private void maintainCellSelection(TableView<?> tableView) {
        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            tableView.getSelectionModel().select(selectedIndex);
        }
    }

    private void configureSearchField() {
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                filterProducts(newValue);
            } else {
                resetTableView();
            }
        });
    }

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

    private void resetTableView() {
        tableView.setItems(masterProductList);
    }

    private void loadProductData() {
        masterProductList = FXCollections.observableArrayList(ProductService.loadProducts());
        resetTableView();
    }

    @FXML
    private void openRegisterProductView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/register-product-view.fxml"));
            Stage stage = new Stage();

            stage.setResizable(false);
            stage.getIcons().add(createIcon("/images/edit.png").getImage());

            stage.setTitle("Registrar Producto");
            stage.setScene(new Scene(loader.load()));
            stage.show();

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

            // Cambiar el icono de la barra de título
            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            stage.getIcons().add(createIcon("/images/updatePrice.png").getImage());

            // Establecer un icono personalizado en el TextInputDialog
            dialog.getDialogPane().setGraphic(createIcon("/images/edit.png"));

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(newPrice -> {
                try {
                    int updatedPrice = Integer.parseInt(newPrice);
                    ProductService.updateProductPrice(selectedProduct, updatedPrice);
                    loadProductData();
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
            loadProductData();
        } else {
            AlertHelper.showErrorAlert("Selección requerida", "Por favor selecciona un producto para eliminar.");
        }
    }
}