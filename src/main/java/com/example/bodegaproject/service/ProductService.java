package com.example.bodegaproject.service;

import com.example.bodegaproject.database.SQLiteConnection;
import com.example.bodegaproject.models.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;
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
                int cantidad = rs.getInt("cantidad");  // Obtenemos la cantidad del producto
                String pesoVolumen = rs.getString("peso_volumen");  // Obtenemos el peso/volumen del producto

                // Crear el producto con los nuevos campos: cantidad y peso_volumen
                Product product = new Product(codigo, nombre, precio, cantidad, pesoVolumen);

                // Añadir el producto a la lista observable
                productList.add(product);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar productos desde la base de datos", e);
        }
        return productList;
    }

    public static void addProduct(Product product) {
        String query = "INSERT INTO products (codigo, producto, precio, peso_volumen, cantidad) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, product.getCodigo());
            stmt.setString(2, product.getProducto());
            stmt.setInt(3, product.getPrecio());
            stmt.setString(4, product.getPesoVolumen()); // Peso como String
            stmt.setInt(5, product.getCantidad()); // Cantidad como int
            stmt.executeUpdate();
            productList.add(product); // Actualiza la lista
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

    public static String getLastProductCode(String prefix) {
        String query = "SELECT codigo FROM products WHERE codigo LIKE ? ORDER BY codigo DESC LIMIT 1";
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, prefix + "%");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("codigo");
            }

        } catch (SQLException e) {
            Logger.getLogger(ProductService.class.getName()).log(Level.SEVERE, "Error al obtener el último código", e);
        }
        return null;
    }

    public static void restarProductos(List<String> productosVendidos) {
        String querySelect = "SELECT cantidad FROM products WHERE producto = ?";
        String queryUpdate = "UPDATE products SET cantidad = cantidad - ? WHERE producto = ?";

        try (Connection conn = SQLiteConnection.getConnection()) {
            // Preparar la consulta para verificar la cantidad en inventario
            try (PreparedStatement selectStmt = conn.prepareStatement(querySelect)) {

                for (String productoStr : productosVendidos) {
                    // Suponiendo que el formato de productoStr es "cantidad - producto"
                    String[] parts = productoStr.split("x| - ");
                    int cantidadVendida = Integer.parseInt(parts[0].trim());  // Extraemos la cantidad
                    String producto = parts[1].trim();  // Extraemos el nombre del producto

                    // Verificamos si hay suficiente cantidad en el inventario
                    selectStmt.setString(1, producto);
                    try (ResultSet rs = selectStmt.executeQuery()) {
                        if (rs.next()) {
                            int cantidadEnInventario = rs.getInt("cantidad");

                            if (cantidadEnInventario >= cantidadVendida) {
                                // Si hay suficiente cantidad, procedemos a descontar
                                try (PreparedStatement updateStmt = conn.prepareStatement(queryUpdate)) {
                                    updateStmt.setInt(1, cantidadVendida);
                                    updateStmt.setString(2, producto);
                                    updateStmt.executeUpdate();
                                }
                            } else {
                                // Si no hay suficiente cantidad, lanzamos un error
                                LOGGER.log(Level.WARNING, "No hay suficiente stock para el producto: " + producto);
                                throw new RuntimeException("No hay suficiente stock para el producto: " + producto);
                            }
                        } else {
                            // Si no se encuentra el producto, lanzamos un error
                            LOGGER.log(Level.WARNING, "Producto no encontrado en inventario: " + producto);
                            throw new RuntimeException("Producto no encontrado en inventario: " + producto);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al descontar productos del inventario", e);
            throw new RuntimeException("Error al descontar productos del inventario", e);
        }
    }
}