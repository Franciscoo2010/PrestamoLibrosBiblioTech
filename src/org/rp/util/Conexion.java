package org.rp.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static Conexion instance;
    private Connection connection;

    private static final String URL = setting("bibliotech.db.url", "BIBLIOTECH_DB_URL",
            "jdbc:mysql://localhost:3306/bibliotech_in4cm?serverTimezone=UTC");
    private static final String USER = setting("bibliotech.db.user", "BIBLIOTECH_DB_USER", "root");
    private static final String PASSWORD = setting("bibliotech.db.password", "BIBLIOTECH_DB_PASSWORD", "");

    private Conexion() {
    }

    private static String setting(String property, String environment, String defaultValue) {
        String value = System.getProperty(property);
        if (value == null || value.trim().isEmpty()) {
            value = System.getenv(environment);
        }
        return value == null ? defaultValue : value;
    }

    public static synchronized Conexion getInstance() {
        if (instance == null) {
            instance = new Conexion();
        }
        return instance;
    }

    public synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
            return connection;
        } catch (ClassNotFoundException | SQLException e) {
            throw new IllegalStateException("No se pudo conectar con la base de datos bibliotech_in4cm.", e);
        }
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                instance = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
