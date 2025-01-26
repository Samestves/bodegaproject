package com.example.bodegaproject.controllers;

import com.example.bodegaproject.models.Product;
import com.example.bodegaproject.service.ProductService;
import com.example.bodegaproject.utils.AlertHelper;
import com.example.bodegaproject.utils.ProductServiceException;
import com.example.bodegaproject.utils.SearchUtils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

import static com.example.bodegaproject.utils.IconUtils.createIcon;

public class InventoryController {

    @FXML private TableView<Product> tableView;
    @FXML private TableColumn<Product, String> codigoColumn;
    @FXML private TableColumn<Product, String> productoColumn;
    @FXML private TableColumn<Product, Integer> precioColumn;
    @FXML private TableColumn<Product, String> pesoColumn;
    @FXML private TableColumn<Product, Integer> cantidadColumn;
    @FXML private TextField searchTextField;
    private final ObservableList<Product> productList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configureTableView();
        loadProductData();
        configureSearchField();
    }

    private void configureTableView() {
        configureTableColumns();
        configureTableBehavior();
        tableView.setPlaceholder(new Label("No hay productos disponibles"));
    }

    private void configureTableColumns() {
        codigoColumn.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        productoColumn.setCellValueFactory(cellData -> cellData.getValue().productoProperty());
        precioColumn.setCellValueFactory(cellData -> cellData.getValue().precioProperty().asObject());
        pesoColumn.setCellValueFactory(cellData -> cellData.getValue().pesoProperty());
        cantidadColumn.setCellValueFactory(cellData -> cellData.getValue().cantidadProperty().asObject());
    }

    private void configureTableBehavior() {
        tableView.setOnMousePressed(this::handleTableClick);
        tableView.setOnMouseClicked(this::handleTableClick);
    }

    private void handleTableClick(MouseEvent event) {
        if (tableView.isFocused()) {
            removeFocusFromTable();
        }
        maintainCellSelection();
        event.consume();
    }

    private void removeFocusFromTable() {
        if (tableView.getParent() != null) {
            tableView.getParent().requestFocus();
        }
    }

    private void maintainCellSelection() {
        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            tableView.getSelectionModel().select(selectedIndex);
        }
    }

    private void loadProductData() {
        productList.setAll(ProductService.loadProducts()); // Usa setAll para actualizar la lista existente
        tableView.setItems(productList); // Asegúrate de que la tabla use la lista actualizada
    }

    private void configureSearchField() {
        SearchUtils.configureSearchField(searchTextField, tableView, productList);
    }

    @FXML
    private void openRegisterProductView() {
        try {
            Stage stage = createRegisterProductStage();
            stage.show();
            stage.setOnHiding(event -> {
                System.out.println("La ventana se está cerrando. Actualizando la tabla...");
                Platform.runLater(this::loadProductData);
            });
        } catch (IOException e) {
            AlertHelper.showErrorAlert("Error al abrir la ventana de registro", e.getMessage());
        }
    }

    private Stage createRegisterProductStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/register-product-view.fxml"));
        Stage stage = new Stage();

        stage.setResizable(false);
        stage.getIcons().add(createIcon("/images/edit.png").getImage());
        stage.setTitle("Registrar Producto");
        stage.setScene(new Scene(loader.load()));

        return stage;
    }

    @FXML
    private void handleUpdateProduct() {
        Product selectedProduct = tableView.getSelectionModel().getSelectedItem();
        if (selectedProduct == null || selectedProduct.getCodigo() == null) {
            AlertHelper.showErrorAlert("Selección inválida", "Por favor selecciona un producto válido.");
            return;
        }

        Optional<String> result = showUpdatePriceDialog(selectedProduct);
        result.ifPresent(newPrice -> updateProductPrice(selectedProduct, newPrice));
    }

    private Optional<String> showUpdatePriceDialog(Product product) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(product.getPrecio()));

        dialog.setTitle("Actualizar precio");
        dialog.setHeaderText("Actualizar precio del producto");
        dialog.setContentText("Ingrese el nuevo precio:");

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(createIcon("/images/updatePrice.png").getImage());
        dialog.getDialogPane().setGraphic(createIcon("/images/edit.png"));

        return dialog.showAndWait();
    }

    private void updateProductPrice(Product product, String newPrice) {
        try {
            int updatedPrice = Integer.parseInt(newPrice);
            ProductService.updateProductPrice(product, updatedPrice);
            loadProductData();
            AlertHelper.showInfoAlert("Actualización exitosa", "El precio se actualizó correctamente.");
        } catch (NumberFormatException | ProductServiceException e) {
            AlertHelper.showErrorAlert("Entrada inválida", "El precio debe ser un número válido.");
        }
    }

    @FXML
    private void handleDeleteProduct() throws ProductServiceException {
        Product selectedProduct = tableView.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            AlertHelper.showErrorAlert("Selección requerida", "Por favor selecciona un producto para eliminar.");
            return;
        }

        ProductService.deleteProduct(selectedProduct);
        loadProductData();
    }
}