package config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

/**
 * Fails the deployment immediately if {@code DB_URL}, {@code DB_USER}, or
 * {@code DB_PASSWORD} are not configured in the environment, instead of
 * letting a misconfiguration surface later as a {@code NullPointerException}
 * deep inside a DAO (SEC-01).
 *
 * @author Victor
 */
@WebListener
public class StartupConfigListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Conexion.requireConfig();
    }
}
