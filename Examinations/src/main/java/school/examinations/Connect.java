package school.examinations;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Handles the MySQL database connection for the Examinations app.
 *
 * Configuration is loaded from src/main/resources/school/examinations/db.properties
 * so that credentials are never hardcoded in source.
 *
 * Expected properties:
 *   db.url      = jdbc:mysql://localhost:3306/examinations
 *   db.user     = root
 *   db.password = your_password
 */
public class Connect {

    private static final String CONFIG_FILE = "db.properties";

    // Defaults (used only if db.properties is missing)
    private static String url      = "jdbc:mysql://localhost:3306/examinations?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static String user     = "root";
    private static String password = "";

    static {
        // Load the MySQL JDBC driver explicitly (needed when using Java modules / jlink)
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found on classpath: " + e.getMessage());
        }

        // Load credentials from db.properties (if available)
        try (InputStream in = Connect.class.getResourceAsStream(CONFIG_FILE)) {
            if (in != null) {
                Properties props = new Properties();
                props.load(in);
                url      = props.getProperty("db.url",      url);
                user     = props.getProperty("db.user",     user);
                password = props.getProperty("db.password", password);
            } else {
                System.err.println("Warning: " + CONFIG_FILE
                        + " not found on the classpath. Falling back to default values.");
            }
        } catch (IOException e) {
            System.err.println("Could not load " + CONFIG_FILE + ": " + e.getMessage());
        }
    }

    /**
     * Opens a new connection to the MySQL database.
     *
     * @return a live Connection, or null if the connection fails.
     */
    public static Connection connection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            return null;
        }
    }
}
