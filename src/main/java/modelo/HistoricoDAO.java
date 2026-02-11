package modelo;

import config.Conexion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Victor
 */
public class HistoricoDAO {

    private final Connection conexion;

    public HistoricoDAO() {
        Conexion con = new Conexion();
        this.conexion = con.getConexion();
    }

    public boolean insertar(int idUsuario, int idProducto, String movimiento, int cantidad) {
        String sql = "INSERT INTO historico (idUsuario, idProducto, movimiento, cantidad, fecha) VALUES (?, ?, ?, ?, NOW())";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idProducto);
            ps.setString(3, movimiento);
            ps.setInt(4, cantidad);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println("Error al insertar en historico: " + e.toString());
            return false;
        }
    }

    public List<HistorialRegistro> listar(String tipoMovimiento) {
        List<HistorialRegistro> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT h.idHistorico, h.movimiento, h.cantidad, h.fecha, " +
            "COALESCE(u.correo, CONCAT('Usuario ', h.idUsuario)) AS nombreUsuario, " +
            "COALESCE(p.nombre, CONCAT('Producto ', h.idProducto)) AS nombreProducto " +
            "FROM historico h " +
            "LEFT JOIN Usuarios u ON h.idUsuario = u.idUsuario " +
            "LEFT JOIN productos p ON h.idProducto = p.idProducto " +
            "WHERE 1=1"
        );
        if (tipoMovimiento != null && !tipoMovimiento.trim().isEmpty()) {
            sql.append(" AND h.movimiento = ?");
        }
        sql.append(" ORDER BY h.fecha DESC");

        try (PreparedStatement ps = conexion.prepareStatement(sql.toString())) {
            if (tipoMovimiento != null && !tipoMovimiento.trim().isEmpty()) {
                ps.setString(1, tipoMovimiento.trim());
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("fecha");
                LocalDateTime fecha = ts != null ? ts.toLocalDateTime() : null;
                lista.add(new HistorialRegistro(
                    rs.getInt("idHistorico"),
                    rs.getString("nombreUsuario"),
                    rs.getString("nombreProducto"),
                    rs.getString("movimiento"),
                    rs.getInt("cantidad"),
                    fecha
                ));
            }
        } catch (Exception e) {
            System.out.println("Error al listar historico: " + e.toString());
        }
        return lista;
    }
}
