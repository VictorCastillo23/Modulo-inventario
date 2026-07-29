package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Opens JDBC connections to the {@code inventario_roles} database using
 * credentials sourced exclusively from environment variables (or system
 * properties as a fallback, e.g. for {@code -DDB_URL=...} in a test/IDE run
 * configuration) — never hardcoded (SEC-01).
 *
 * <p>Any missing or blank {@code DB_URL}/{@code DB_USER}/{@code DB_PASSWORD}
 * fails loudly with {@link IllegalStateException} instead of silently
 * returning {@code null}, which used to defer the failure into a
 * {@code NullPointerException} deep inside a DAO. {@link StartupConfigListener}
 * calls {@link #requireConfig()} once at deployment time so a misconfigured
 * environment fails the deploy immediately rather than the first request.
 *
 * @author Victor
 */
public class Conexion {

    private static final String DB_URL_VAR = "DB_URL";
    private static final String DB_USER_VAR = "DB_USER";
    private static final String DB_PASSWORD_VAR = "DB_PASSWORD";

    public Connection getConexion() {
        String url = requireValue(DB_URL_VAR);
        String user = requireValue(DB_USER_VAR);
        String password = requireValue(DB_PASSWORD_VAR);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException | ClassNotFoundException ex) {
            throw new IllegalStateException("Failed to open database connection", ex);
        }
    }

    /**
     * Verifies that {@code DB_URL}, {@code DB_USER}, and {@code DB_PASSWORD}
     * are all present and non-blank. Intended to be called once at
     * application startup (see {@link StartupConfigListener}).
     *
     * @throws IllegalStateException naming the first missing/blank variable
     *         found (never the value itself)
     */
    public static void requireConfig() {
        requireValue(DB_URL_VAR);
        requireValue(DB_USER_VAR);
        requireValue(DB_PASSWORD_VAR);
    }

    private static String requireValue(String name) {
        String value = resolve(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required configuration: " + name);
        }
        return value;
    }

    private static String resolve(String name) {
        String value = System.getenv(name);
        if (value == null) {
            value = System.getProperty(name);
        }
        return value;
    }
}
