package net.jcip.examples;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Using ThreadLocal to ensure thread confinement
 */
public class ConnectionDispenser {
    static String DB_URL = "jdbc:mysql://localhost/mydatabase";

    /**
     * Создаём в классе переменную, в которой будет храниться конекшн к БД.
     * В потоке в методе run передаём/создём этот объект ConnectionDispenser.
     * Каждый поток на нём будет вызывать метод getConnection();
     * Конекшн будет thread confinement.
     * Т.е. initialValue в каждом потоке создаст новый/отдельный конекшн. Как? Когда?
     */
    private ThreadLocal<Connection> connectionHolder = new ThreadLocal<Connection>() {
                public Connection initialValue() {
                    try {
                        return DriverManager.getConnection(DB_URL);
                    } catch (SQLException e) {
                        throw new RuntimeException("Unable to acquire Connection, e");
                    }
                };
            };

    public Connection getConnection() {
        return connectionHolder.get();
    }
}
