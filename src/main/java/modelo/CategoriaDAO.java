package modelo;

import config.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Victor
 */
public class CategoriaDAO {

    private final Connection conexion;

    public CategoriaDAO() {
        Conexion con = new Conexion();
        this.conexion = con.getConexion();
    }

    public List<Categoria> listar() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT idCategoria, nombre, descripcion, estatus FROM Categorias ORDER BY nombre";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Categoria(
                        rs.getInt("idCategoria"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getBoolean("estatus")
                ));
            }
        } catch (Exception e) {
            System.out.println("Error al listar categorias: " + e.toString());
        }
        return lista;
    }

    public List<Categoria> listarActivas() {
        List<Categoria> lista = new ArrayList<>();
        String sql = "SELECT idCategoria, nombre, descripcion, estatus FROM Categorias WHERE estatus = 1 ORDER BY nombre";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Categoria(
                        rs.getInt("idCategoria"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getBoolean("estatus")
                ));
            }
        } catch (Exception e) {
            System.out.println("Error al listar categorias activas: " + e.toString());
        }
        return lista;
    }

    public int insertarRetornarId(Categoria categoria) {
        String sql = "INSERT INTO Categorias (nombre, descripcion, estatus) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());
            ps.setBoolean(3, categoria.isEstatus());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("Error al insertar categoria: " + e.toString());
        }
        return 0;
    }

    public boolean actualizar(Categoria categoria) {
        String sql = "UPDATE Categorias SET nombre = ?, descripcion = ? WHERE idCategoria = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, categoria.getNombre());
            ps.setString(2, categoria.getDescripcion());
            ps.setInt(3, categoria.getId());
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            System.out.println("Error al actualizar categoria: " + e.toString());
            return false;
        }
    }

    public boolean cambiarEstatus(int idCategoria, boolean estatus) {
        String sql = "UPDATE Categorias SET estatus = ? WHERE idCategoria = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setBoolean(1, estatus);
            ps.setInt(2, idCategoria);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            System.out.println("Error al cambiar estatus de categoria: " + e.toString());
            return false;
        }
    }

    public Categoria obtenerPorId(int id) {
        String sql = "SELECT idCategoria, nombre, descripcion, estatus FROM Categorias WHERE idCategoria = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Categoria(
                        rs.getInt("idCategoria"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getBoolean("estatus")
                );
            }
        } catch (Exception e) {
            System.out.println("Error al obtener categoria por id: " + e.toString());
        }
        return null;
    }
}
