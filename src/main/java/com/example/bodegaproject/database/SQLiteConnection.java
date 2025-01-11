package com.example.bodegaproject.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;

public class SQLiteConnection {
    private static final String DOCUMENTS_DIR = System.getProperty("user.home") + File.separator + "Documents";
    private static final String DB_DIRECTORY = DOCUMENTS_DIR + File.separator + "BodegaDB" + File.separator + "database";
    private static final String DB_PATH = DB_DIRECTORY + File.separator + "mydatabase.db";

    private static boolean directoryInitialized = false; // Indicador para evitar mensajes repetidos

    /**
     * Inicializa el directorio de la base de datos.
     */
    public static void initializeDatabaseDirectory() {
        if (!directoryInitialized) {
            File dbFolder = new File(DB_DIRECTORY);
            if (!dbFolder.exists()) {
                if (dbFolder.mkdirs()) {
                    System.out.println("✅ Carpeta 'database' creada correctamente en Documentos.");
                } else {
                    System.out.println("⚠️ No se pudo crear la carpeta 'database'. Verifica los permisos.");
                }
            } else {
                System.out.println("📂 La carpeta 'database' ya existe en Documentos.");
            }
            directoryInitialized = true; // Evitar inicializar múltiples veces
        }
    }

    /**
     * Devuelve una conexión a la base de datos SQLite.
     *
     * @return Connection - Objeto de conexión a la base de datos.
     * @throws SQLException Si ocurre un error al intentar conectar.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
    }
}