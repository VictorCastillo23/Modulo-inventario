package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Victor
 */
public class Conexion {

    public Connection getConexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventario_roles?serverTimezone=UTC", "root", "admin");
            return conexion;
        } catch (SQLException e) {
            System.out.println("Error en la conexion: " + e.toString());
            return null;
        } catch (ClassNotFoundException ex) {
            System.getLogger(Conexion.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            return null;
        }
    }
}
