package com.example.bodegaproject.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteConnection {
    // Ruta al archivo SQLite en la carpeta src/database
    private static final String URL = "jdbc:sqlite:database/mydatabase.db";

    /**
     * Devuelve una conexión a la base de datos SQLite.
     *
     * @return Connection - Objeto de conexión a la base de datos.
     * @throws SQLException Si ocurre un error al intentar conectar.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
