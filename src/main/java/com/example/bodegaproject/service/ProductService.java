package com.example.bodegaproject.service;

import com.example.bodegaproject.database.SQLiteConnection;
import com.example.bodegaproject.models.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductService {

    private static final ObservableList<Product> productList = FXCollections.observableArrayList();
    private static final Logger LOGGER = Logger.getLogger(ProductService.class.getName());

    public static ObservableList<Product> loadProducts() {
        productList.clear();
        String query = "SELECT * FROM products";  // Consulta para obtener todos los productos

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("producto");
                int precio = rs.getInt("precio");

                Product product = new Product(codigo, nombre, precio);  // Crear el producto
                productList.add(product);  // Añadirlo a la lista observable
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar productos desde la base de datos", e);
        }
        return productList;
    }

    public static void addProduct(Product product) {
        String query = "INSERT INTO products (codigo, producto, precio) VALUES (?, ?, ?)";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, product.getCodigo());
            stmt.setString(2, product.getProducto());
            stmt.setInt(3, product.getPrecio());
            stmt.executeUpdate();
            productList.add(product);  // Actualiza la lista
        } catch (SQLException e) {
            throw new RuntimeException("Error al agregar el producto", e);
        }
    }

    public static void updateProductPrice(Product product, int newPrice) {
        String query = "UPDATE products SET precio = ? WHERE codigo = ?";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, newPrice);
            stmt.setString(2, product.getCodigo());
            stmt.executeUpdate();

            // Actualiza el precio en la lista observable
            product.setPrecio(newPrice);
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el precio del producto", e);
        }
    }

    public static void deleteProduct(Product product) {
        String query = "DELETE FROM products WHERE codigo = ?";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, product.getCodigo());
            stmt.executeUpdate();
            productList.remove(product);  // Elimina el producto de la lista
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el producto", e);
        }
    }

    public static boolean findProductByName(String name) {
        try (Connection conn = SQLiteConnection.getConnection()) {
            String query = "SELECT COUNT(*) FROM products WHERE UPPER(producto) = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, name.toUpperCase());
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al buscar producto en la base de datos", e);
        }
        return false;
    }


    public static boolean isCodeExists(String code) {
        String query = "SELECT COUNT(*) FROM products WHERE codigo = ?";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Si COUNT(*) > 0, el código ya existe
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error verificando existencia del código", e);
        }
        return false;
    }
}