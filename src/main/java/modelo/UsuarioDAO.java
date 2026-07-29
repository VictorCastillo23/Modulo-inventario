package modelo;

import config.Conexion;
import seguridad.PasswordHasher;

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
        String sql = "SELECT idUsuario, correo, idRol, contraseña FROM Usuarios WHERE correo = ? AND estatus = 1";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String storedHash = rs.getString("contraseña");
                if (PasswordHasher.verify(clave, storedHash)) {
                    return new Usuario(
                        rs.getInt("idUsuario"),
                        rs.getString("correo"),
                        rs.getInt("idRol")
                    );
                }
            }
        } catch (Exception e) {
            System.out.println("Error al validar usuario: " + e.toString());
        }
        return null;
    }
}
