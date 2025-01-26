package com.example.bodegaproject.service;

import com.example.bodegaproject.database.SQLiteConnection;
import com.example.bodegaproject.models.Product;
import com.example.bodegaproject.utils.ProductServiceException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductService {

    private static final Logger LOGGER = Logger.getLogger(ProductService.class.getName());

    // Consultas SQL
    private static final String QUERY_LOAD_PRODUCTS = "SELECT * FROM products";
    private static final String QUERY_ADD_PRODUCT = "INSERT INTO products (codigo, producto, precio, peso_volumen, cantidad) VALUES (?, ?, ?, ?, ?)";
    private static final String QUERY_UPDATE_PRICE = "UPDATE products SET precio = ? WHERE codigo = ?";
    private static final String QUERY_DELETE_PRODUCT = "DELETE FROM products WHERE codigo = ?";
    private static final String QUERY_FIND_PRODUCT_BY_NAME = "SELECT COUNT(*) FROM products WHERE UPPER(producto) = ?";
    private static final String QUERY_GET_LAST_PRODUCT_CODE = "SELECT codigo FROM products WHERE codigo LIKE ? ORDER BY codigo DESC LIMIT 1";
    private static final String QUERY_RESTAR_PRODUCTOS_SELECT = "SELECT cantidad FROM products WHERE codigo = ?";
    private static final String QUERY_RESTAR_PRODUCTOS_UPDATE = "UPDATE products SET cantidad = cantidad - ? WHERE codigo = ?";

    // Inicialización de la base de datos
    static {
        try {
            SQLiteConnection.initializeDatabaseDirectory();
            SQLiteConnection.getConnection();
            createProductsTable();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al inicializar la base de datos", e);
        }
    }

    private static void createProductsTable() throws SQLException {
        String query = "CREATE TABLE IF NOT EXISTS products (" +
                "codigo TEXT PRIMARY KEY, " +
                "producto TEXT NOT NULL, " +
                "precio INTEGER NOT NULL, " +
                "peso_volumen TEXT, " +
                "cantidad INTEGER NOT NULL)";

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.execute();
        }
    }

    public static ObservableList<Product> loadProducts() {
        ObservableList<Product> productList = FXCollections.observableArrayList();

        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(QUERY_LOAD_PRODUCTS);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("producto");
                int precio = rs.getInt("precio");
                int cantidad = rs.getInt("cantidad");
                String pesoVolumen = rs.getString("peso_volumen");

                Product product = new Product(codigo, nombre, precio, cantidad, pesoVolumen);
                productList.add(product);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar productos desde la base de datos", e);
        }

        return productList;
    }

    public static void addProduct(Product product) throws ProductServiceException {
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(QUERY_ADD_PRODUCT)) {
            stmt.setString(1, product.getCodigo());
            stmt.setString(2, product.getProducto());
            stmt.setInt(3, product.getPrecio());
            stmt.setString(4, product.getPesoVolumen());
            stmt.setInt(5, product.getCantidad());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ProductServiceException("Error al agregar el producto", e);
        }
    }

    public static void updateProductPrice(Product product, int newPrice) throws ProductServiceException {
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(QUERY_UPDATE_PRICE)) {
            stmt.setInt(1, newPrice);
            stmt.setString(2, product.getCodigo());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ProductServiceException("Error al actualizar el producto", e);
        }
    }

    public static void deleteProduct(Product product) throws ProductServiceException {
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(QUERY_DELETE_PRODUCT)) {
            stmt.setString(1, product.getCodigo());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new ProductServiceException("Error al eliminar el producto", e);
        }
    }

    public static boolean findProductByName(String name) throws ProductServiceException {
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(QUERY_FIND_PRODUCT_BY_NAME)) {
            stmt.setString(1, name.toUpperCase());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new ProductServiceException("Error al buscar el producto por nombre", e);
        }
    }

    public static String getLastProductCode(String prefix) throws SQLException {
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(QUERY_GET_LAST_PRODUCT_CODE)) {
            stmt.setString(1, prefix + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getString("codigo") : null;
            }
        }
    }

    public static void restarProductos(List<Pair<String, Integer>> productosConCantidad) throws SQLException {
        try (Connection conn = SQLiteConnection.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(QUERY_RESTAR_PRODUCTOS_SELECT);
             PreparedStatement updateStmt = conn.prepareStatement(QUERY_RESTAR_PRODUCTOS_UPDATE)) {

            for (Pair<String, Integer> producto : productosConCantidad) {
                String codigo = producto.getKey();
                int cantidadVendida = producto.getValue();

                // Verificar si hay suficiente cantidad en inventario
                selectStmt.setString(1, codigo);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        int cantidadEnInventario = rs.getInt("cantidad");

                        if (cantidadEnInventario < cantidadVendida) {
                            throw new SQLException("Stock insuficiente para el producto con código: " + codigo);
                        }

                        // Actualizar la cantidad en inventario
                        updateStmt.setInt(1, cantidadVendida);
                        updateStmt.setString(2, codigo);
                        updateStmt.executeUpdate();
                    } else {
                        throw new SQLException("Producto no encontrado con código: " + codigo);
                    }
                }
            }
        }
    }
}