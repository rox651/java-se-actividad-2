package com.saborpaisa.crud.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

/**
 * Lee las propiedades de conexión y expone un método para obtener conexiones JDBC.
 */
public final class DatabaseConfig {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (Objects.isNull(input)) {
                throw new IllegalStateException("No se encontró el archivo db.properties en el classpath");
            }
            PROPERTIES.load(input);
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Error cargando propiedades de base de datos: " + e.getMessage());
        }
    }

    private DatabaseConfig() {
        // Utility class
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(
                PROPERTIES.getProperty("db.url"),
                PROPERTIES.getProperty("db.user"),
                PROPERTIES.getProperty("db.password")
        );
        String schema = PROPERTIES.getProperty("db.schema");
        if (schema != null && !schema.isBlank()) {
            connection.setSchema(schema);
        }
        return connection;
    }
}

