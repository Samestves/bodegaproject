package com.example.bodegaproject.controllers;

import com.example.bodegaproject.models.Product;
import com.example.bodegaproject.service.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalesController {

    @FXML public ListView<String> listItems;
    @FXML public TableColumn<Product, Integer> qtyColumn; // Definimos la columna de cantidad como Integer
    @FXML private TableView<Product> salesTable;
    @FXML private TableColumn<Product, String> packColumn;
    @FXML private TableColumn<Product, String> productColumn;
    @FXML private TableColumn<Product, Integer> precioColumn;
    @FXML private Label totalLabel;
    private double totalVenta; // Total de la venta actual
    private ObservableList<Product> ProductList;
    private final Map<Product, Integer> selectedQuantities = new HashMap<>();
    private final Map<String, String> productCodes = new HashMap<>();


    @FXML
    public void initialize(){
        configureTableView();
        loadProductData();
    }

    private void configureTableView() {
        // Configuración de las columnas normales
        packColumn.setCellValueFactory(cellData -> cellData.getValue().pesoProperty());
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
            // Obtén la cantidad seleccionada o usa 1 como predeterminado
            Integer cantidadSeleccionada = selectedQuantities.getOrDefault(selectedProduct, 1);

            if (cantidadSeleccionada > 0) {
                // Calcula el total del producto
                double totalProducto = cantidadSeleccionada * selectedProduct.getPrecio();
                totalVenta += totalProducto;

                // Construye una línea formateada sin mostrar el código
                String formattedProduct = formatProductLine(cantidadSeleccionada, selectedProduct.getProducto(), totalProducto);

                // Agrega la línea al carrito y almacena el código internamente
                listItems.getItems().add(formattedProduct);
                productCodes.put(formattedProduct, selectedProduct.getCodigo()); // Asocia la línea al código del producto

                // Actualiza el total en la UI
                totalLabel.setText(String.format("%.2f BS", totalVenta));
            }
        }
    }

    /**
     * Formatea una línea para mostrar en la lista.
     * @param cantidad La cantidad seleccionada del producto.
     * @param nombreProducto El nombre del producto.
     * @param total El total calculado del producto.
     * @return Una línea formateada.
     */
    private String formatProductLine(int cantidad, String nombreProducto, double total) {
        int nombreProductoWidth = 17; // Ajusta el ancho para la columna del producto
        String cantidadStr = String.format("%-3d", cantidad); // 3 caracteres para la cantidad
        String productoStr;

        // Recortar y formatear el nombre del producto si es necesario
        if (nombreProducto.length() > nombreProductoWidth) {
            productoStr = String.format("%-" + nombreProductoWidth + "s", nombreProducto.substring(0, nombreProductoWidth - 3) + "...");
        } else {
            productoStr = String.format("%-" + nombreProductoWidth + "s", nombreProducto);
        }

        String totalStr = String.format("%11.2f BS", total); // 10 caracteres para el total + "BS"
        return cantidadStr + productoStr + totalStr;
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
            // Preparar lista de códigos y cantidades para el método restarProductos
            List<Pair<String, Integer>> productosConCantidad = new ArrayList<>();
            for (String producto : productosVendidos) {
                String codigo = productCodes.get(producto); // Obtiene el código asociado
                int cantidadVendida = Integer.parseInt(producto.split("\\s+")[0]); // Extrae la cantidad desde el formato
                productosConCantidad.add(new Pair<>(codigo, cantidadVendida));
            }

            // Descontar productos del inventario (o base de datos)
            ProductService.restarProductos(productosConCantidad);

            // Limpia el carrito y reinicia el total
            listItems.getItems().clear();
            productCodes.clear(); // Limpia los códigos asociados
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
            productCodes.clear(); // Limpia los códigos asociados
            totalVenta = 0.0;
            totalLabel.setText("0.0 BS");
            alert.showAndWait();
        }
    }
}