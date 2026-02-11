package modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import config.Conexion;
import java.util.ArrayList;

/**
 *
 * @author Victor
 */
public class ProductosDAO {

    Connection conexion;

    public ProductosDAO() {
        Conexion con = new Conexion();
        this.conexion = con.getConexion();

    }

    public List<Productos> listarProductos() {

        PreparedStatement ps;
        ResultSet rs;
        List<Productos> lista = new ArrayList<>();

        try {

            ps = conexion.prepareStatement("SELECT idProducto,nombre,cantidad,estatus FROM productos");

            rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("idProducto");
                String nombre = rs.getString("nombre");
                int cantidad = rs.getInt("cantidad");
                boolean estatus = rs.getBoolean("estatus");

                Productos producto = new Productos(id, nombre, cantidad, estatus);
                lista.add(producto);
            }

            return lista;
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }

    }

    public List<Productos> listarProductosActivos() {

        PreparedStatement ps;
        ResultSet rs;
        List<Productos> lista = new ArrayList<>();

        try {

            ps = conexion.prepareStatement("SELECT idProducto,nombre,cantidad,estatus FROM productos where estatus = 1");

            rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("idProducto");
                String nombre = rs.getString("nombre");
                int cantidad = rs.getInt("cantidad");
                boolean estatus = rs.getBoolean("estatus");

                Productos producto = new Productos(id, nombre, cantidad, estatus);
                System.out.println(producto.getNombre());
                lista.add(producto);
            }

            return lista;
        } catch (Exception e) {
            System.out.println(e.toString());
            return null;
        }

    }
    public boolean insertar(Productos producto) {

        PreparedStatement ps;

        try {
            String sql = "INSERT INTO productos (nombre, cantidad, estatus) VALUES (?, ?, ?)";

            ps = conexion.prepareStatement(sql);

            ps.setString(1, producto.getNombre());
            ps.setInt(2, producto.getCantidad());
            ps.setBoolean(3, producto.isEstatus());

            ps.execute();

            return true;

        } catch (Exception e) {
            System.out.println("ERROR al insertar producto");
            System.out.println(e.toString());
            return false;
        }
    }

    public int insertarRetornarId(Productos producto) {
        String sql = "INSERT INTO productos (nombre, cantidad, estatus) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, producto.getNombre());
            ps.setInt(2, producto.getCantidad());
            ps.setBoolean(3, producto.isEstatus());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            System.out.println("ERROR al insertar producto: " + e.toString());
        }
        return 0;
    }

    public boolean cambiarEstatus(int idProducto, boolean estatus) {

        PreparedStatement ps;

        try {
            ps = conexion.prepareStatement("UPDATE productos SET estatus=? where idProducto=? ");

            ps.setBoolean(1, estatus);
            ps.setInt(2, idProducto);

            ps.execute();
            return true;
        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        }
    }

    public boolean retirarCantidad(int idProducto, int cantidadRetirar) {

        PreparedStatement ps;

        try {
            ps = conexion.prepareStatement(
                "UPDATE productos SET cantidad = cantidad - ? WHERE idProducto = ?"
            );

            ps.setInt(1, cantidadRetirar);
            ps.setInt(2, idProducto);

            int filas = ps.executeUpdate();

            System.out.println("Producto " + idProducto +
                            " | Retiro: " + cantidadRetirar +
                            " | Filas afectadas: " + filas);

            return filas > 0;

        } catch (Exception e) {
            System.out.println("Error al retirar producto " + idProducto);
            e.printStackTrace();
            return false;
        }
    }

    public boolean agregarCantidad(int idProducto, int cantidadAgregar) {

        PreparedStatement ps;

        try {
            ps = conexion.prepareStatement(
                "UPDATE productos SET cantidad = cantidad + ? WHERE idProducto = ?"
            );

            ps.setInt(1, cantidadAgregar);
            ps.setInt(2, idProducto);

            int filas = ps.executeUpdate();

            System.out.println("Producto " + idProducto +
                            " | Agregados: " + cantidadAgregar +
                            " | Filas afectadas: " + filas);

            return filas > 0;

        } catch (Exception e) {
            System.out.println("Error al retirar producto " + idProducto);
            e.printStackTrace();
            return false;
        }
    }

    public Productos obtenerPorId(int id) {

        String sql = "SELECT * FROM Productos WHERE idProducto = ?";
        Productos p = null;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                p = new Productos(
                        rs.getInt("idProducto"),
                        rs.getString("nombre"),
                        rs.getInt("cantidad"),
                        rs.getBoolean("estatus")
                );
            }

        } catch (Exception e) {
            System.out.println("Error al obtener producto por ID");
            System.out.println(e.toString());
        }

        return p;
    }

}
