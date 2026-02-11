package modelo;

import config.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 
 * @author Victor
 */
public class UsuarioDAO {

    private final Connection conexion;

    public UsuarioDAO() {
        Conexion con = new Conexion();
        this.conexion = con.getConexion();
    }

    public Usuario validar(String usuario, String clave) {
        String sql = "SELECT idUsuario, correo, idRol FROM Usuarios WHERE correo = ? AND contraseña = ? AND estatus = 1";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, clave);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Usuario(
                    rs.getInt("idUsuario"),
                    rs.getString("correo"),
                    rs.getInt("idRol")
                );
            }
        } catch (Exception e) {
            System.out.println("Error al validar usuario: " + e.toString());
        }
        return null;
    }
}
