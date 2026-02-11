package modelo;

import config.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Victor
 */
public class PermisoDAO {

    private final Connection conexion;

    public PermisoDAO() {
        Conexion con = new Conexion();
        this.conexion = con.getConexion();
    }

    public Set<String> listarPermisosPorRol(int idRol) {
        Set<String> permisos = new HashSet<>();
        String sql = "SELECT p.nombre FROM permisos p "
                + "INNER JOIN roles_Permisos rp ON p.idPermiso = rp.idPermisos "
                + "WHERE rp.idRol = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idRol);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                permisos.add(rs.getString("nombre"));
            }
        } catch (Exception e) {
            System.out.println("Error al listar permisos por rol: " + e.toString());
        }
        return permisos;
    }
}
