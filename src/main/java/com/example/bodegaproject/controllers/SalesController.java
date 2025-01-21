package com.example.bodegaproject.controllers;

import com.example.bodegaproject.models.Product;
import com.example.bodegaproject.service.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalesController {

    @FXML public ListView<String> listItems;
    @FXML public TableColumn<Product, Integer> qtyColumn; // Definimos la columna de cantidad como Integer
    @FXML private TableView<Product> salesTable;
    @FXML private TableColumn<Product, String> codigoColumn;
    @FXML private TableColumn<Product, String> productColumn;
    @FXML private TableColumn<Product, Integer> precioColumn;
    @FXML private Label totalLabel;
    private double totalVenta; // Total de la venta actual
    private ObservableList<Product> ProductList;
    private final Map<Product, Integer> selectedQuantities = new HashMap<>();


    @FXML
    public void initialize(){
        configureTableView();
        loadProductData();
    }

    private void configureTableView() {
        // Configuración de las columnas normales
        codigoColumn.setCellValueFactory(cellData -> cellData.getValue().codigoProperty());
        productColumn.setCellValueFactory(cellData -> cellData.getValue().productoProperty());
        precioColumn.setCellValueFactory(cellData -> cellData.getValue().precioProperty().asObject());

        qtyColumn.setCellFactory(column -> new TableCell<>() {
            private final ComboBox<Integer> comboBox = new ComboBox<>();

            {
                comboBox.getItems().addAll(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
                comboBox.setOnAction(event -> {
                    Product product = getTableView().getItems().get(getIndex());
                    selectedQuantities.put(product, comboBox.getValue());
                });
            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(comboBox);
                    Product product = getTableView().getItems().get(getIndex());
                    comboBox.setValue(selectedQuantities.getOrDefault(product, 1)); // Valor por defecto
                }
            }
        });
    }

    private void loadProductData() {
        ProductList = FXCollections.observableArrayList(ProductService.loadProducts());
        updateTableView();
    }

    private void updateTableView() {
        salesTable.setItems(ProductList);
    }

    @FXML
    private void addProductToList() {
        Product selectedProduct = salesTable.getSelectionModel().getSelectedItem();

        if (selectedProduct != null) {
            // Obtén la cantidad seleccionada del mapa
            Integer cantidadSeleccionada = selectedQuantities.getOrDefault(selectedProduct, 1);

            if (cantidadSeleccionada > 0) {
                // Calcula el precio total por la cantidad seleccionada
                double totalProducto = cantidadSeleccionada * selectedProduct.getPrecio();
                totalVenta += totalProducto;

                // Formatear la cantidad y nombre a la izquierda
                String cantidadStr = String.format("%-5d", cantidadSeleccionada);  // 5 caracteres para la cantidad
                String productoStr = String.format("%-20s", selectedProduct.getProducto());  // 20 caracteres para el nombre

                // Formatear el monto a la derecha
                String totalStr = String.format("%10.2f", totalProducto);  // 10 caracteres para el total con dos decimales

                // Concatenamos el formato deseado
                String formattedProduct = cantidadStr + productoStr + totalStr + " BS";
                listItems.getItems().add(formattedProduct);

                // Actualiza el total en la UI
                totalLabel.setText(totalVenta + " BS");
            }
        }
    }

    @FXML
    private void realizarVenta() {
        if (listItems.getItems().isEmpty()) {
            // Si no hay productos en el carrito, muestra un mensaje de advertencia
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Carrito vacío");
            alert.setHeaderText("No hay productos en el carrito");
            alert.setContentText("Por favor, agrega productos antes de realizar la venta.");
            alert.showAndWait();
            return;
        }

        // Aquí puedes obtener la lista de los productos vendidos (lo que está en el carrito)
        List<String> productosVendidos = new ArrayList<>(listItems.getItems());

        try {
            // Descontar productos del inventario (o base de datos)
            ProductService.restarProductos(productosVendidos);

            // Limpia el carrito y reinicia el total
            listItems.getItems().clear();
            totalVenta = 0.0;
            totalLabel.setText("0.0 BS");

            // Muestra un mensaje de confirmación
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Venta realizada");
            alert.setHeaderText("¡Éxito!");
            alert.setContentText("La venta se realizó correctamente.");
            alert.showAndWait();

        } catch (RuntimeException e) {
            // Si ocurre un error (por ejemplo, no hay suficiente stock), mostrar el mensaje de error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error en la venta");
            alert.setHeaderText("No se pudo realizar la venta");
            alert.setContentText(e.getMessage());  // Muestra el mensaje de error
            listItems.getItems().clear();
            totalVenta = 0.0;
            totalLabel.setText("0.0 BS");
            alert.showAndWait();
        }
    }
}