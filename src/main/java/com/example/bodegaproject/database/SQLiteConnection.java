package com.example.bodegaproject.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;

public class SQLiteConnection {
    // Ruta al directorio de Documentos del usuario
    private static final String DOCUMENTS_DIR = System.getProperty("user.home") + File.separator + "Documents";
    private static final String DB_DIRECTORY = DOCUMENTS_DIR + File.separator + "BodegaProject" + File.separator + "database";
    private static final String DB_PATH = DB_DIRECTORY + File.separator + "mydatabase.db";

    /**
     * Devuelve una conexión a la base de datos SQLite.
     * Si la base de datos no existe, se creará automáticamente.
     *
     * @return Connection - Objeto de conexión a la base de datos.
     * @throws SQLException Si ocurre un error al intentar conectar.
     */
    public static Connection getConnection() throws SQLException {
        File dbFolder = new File(DB_DIRECTORY);
        if (!dbFolder.exists()) {
            boolean created = dbFolder.mkdirs();  // Usamos mkdirs() para crear directorios intermedios si es necesario
            if (created) {
                System.out.println("Carpeta 'database' creada correctamente en Documentos.");
            } else {
                System.out.println("La carpeta 'database' ya existe o no se pudo crear.");
                System.out.println("Verifica los permisos de escritura en el directorio: " + DB_DIRECTORY);
            }
        } else {
            System.out.println("La carpeta 'database' ya existe en Documentos.");
        }

        // Establecer la conexión con la base de datos SQLite
        return DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
    }
}