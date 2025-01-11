package com.example.bodegaproject.database;

import java.sql.Connection;
import java.sql.Statement;

public class CreateTable {
    /**
     * Crea la tabla `products` si no existe en la base de datos.
     */
    public static void createProductsTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS products (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                codigo TEXT NOT NULL UNIQUE,
                producto TEXT NOT NULL,
                precio REAL NOT NULL
            );
        """;

        try (Connection conn = SQLiteConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("✅ Tabla 'products' creada o ya existe.");
        } catch (Exception e) {
            System.out.println("Error al crear la tabla: " + e.getMessage());
        }
    }
}