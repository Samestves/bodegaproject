package com.example.bodegaproject.utils;

import com.example.bodegaproject.models.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class SearchUtils {

    public static void configureSearchField(TextField searchTextField, TableView<Product> tableView, ObservableList<Product> productList) {
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                // Filtrar la lista de productos según el texto de búsqueda
                filterProducts(tableView, productList, newValue);
            } else {
                // Restaurar la lista completa de productos cuando el campo de búsqueda está vacío
                resetTableView(tableView, productList);
            }
            tableView.refresh(); // Forzar la actualización de la TableView
        });
    }

    private static void filterProducts(TableView<Product> tableView, ObservableList<Product> productList, String searchText) {
        ObservableList<Product> filteredList = FXCollections.observableArrayList();
        for (Product product : productList) {
            if (product.getProducto().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(product);
            }
        }
        tableView.setItems(filteredList);
    }

    private static void resetTableView(TableView<Product> tableView, ObservableList<Product> productList) {
        tableView.setItems(productList); // Restaurar la lista completa de productos
    }
}
