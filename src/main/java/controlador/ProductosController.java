package controlador;

import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import modelo.HistoricoDAO;
import modelo.Productos;
import modelo.ProductosDAO;

/**
 *
 * @author Victor
 */
@WebServlet(name = "ProductosController", urlPatterns = {"/ProductosController"})
public class ProductosController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ProductosDAO productosDAO = null;
        productosDAO = new ProductosDAO();
        String accion;
        RequestDispatcher dispatcher = null;

        accion = request.getParameter("accion");
        Map<String, Boolean> permisos = obtenerPermisos(request);

        if (accion == null || accion.isEmpty()) {
            if (!tienePermiso(permisos, "ver_inventario")) {
                redirigirSinPermiso(request, response);
                return;
            }
            if ("1".equals(request.getParameter("sinPermiso"))) {
                request.setAttribute("sinPermiso", true);
            }
            dispatcher = request.getRequestDispatcher("Productos/index.jsp");
            List<Productos> listaProductos = productosDAO.listarProductos();
            request.setAttribute("lista", listaProductos);

        } else if ("nuevo".equals(accion)) {
            if (!tienePermiso(permisos, "agregar_productos")) {
                redirigirSinPermiso(request, response);
                return;
            }
            dispatcher = request.getRequestDispatcher("Productos/nuevo.jsp");
        } else if ("insertar".equals(accion)) {
            if (!tienePermiso(permisos, "agregar_productos")) {
                redirigirSinPermiso(request, response);
                return;
            }

            String nombre = request.getParameter("nombre");
            int cantidad = 0;// Integer.parseInt(request.getParameter("cantidad"));
            boolean estatus = Boolean.parseBoolean(request.getParameter("estatus"));

            Productos producto = new Productos(0, nombre, cantidad, estatus);

            int idProducto = productosDAO.insertarRetornarId(producto);
            if (idProducto > 0) {
                Integer idUsuario = obtenerIdUsuario(request);
                if (idUsuario != null) {
                    HistoricoDAO historicoDAO = new HistoricoDAO();
                    historicoDAO.insertar(idUsuario, idProducto, "Entrada", cantidad);
                }
            }
            List<Productos> listaProductos = productosDAO.listarProductos();

            request.setAttribute("lista", listaProductos);
            dispatcher = request.getRequestDispatcher("Productos/index.jsp");
        } else if ("guardarCambios".equals(request.getParameter("accion"))) {
            System.out.println("Entrando en guardr camios");
            if (!tienePermiso(permisos, "aumentar_inventario") && !tienePermiso(permisos, "baja_reactivar_producto")) {
                redirigirSinPermiso(request, response);
                            System.out.println("no tiene permisos de guardr camios");
                return;
            }

            String[] ids = request.getParameterValues("id[]");
            String[] cantidades = request.getParameterValues("cantidad[]");
            String[] estatus = request.getParameterValues("estatus[]");
            String[] modificados = request.getParameterValues("modificado[]");

            for (int i = 0; i < ids.length; i++) {

                int id = Integer.parseInt(ids[i]);
                int cantidadAgregar = Integer.parseInt(cantidades[i]);
                boolean nuevoEstatus = Boolean.parseBoolean(estatus[i]);
                boolean fueModificado = Boolean.parseBoolean(modificados[i]);

                System.out.println("ID: " + id +" | Retiro: " + cantidadAgregar +" | Estatus: " + nuevoEstatus +" | Modificado: " + fueModificado);

                if (fueModificado) {
                    if (cantidadAgregar > 0 && Boolean.TRUE.equals(permisos.get("aumentar_inventario"))) {
                        productosDAO.agregarCantidad(id, cantidadAgregar);
                        Integer idUsuario = obtenerIdUsuario(request);
                        if (idUsuario != null) {
                            HistoricoDAO historicoDAO = new HistoricoDAO();
                            historicoDAO.insertar(idUsuario, id, "Entrada", cantidadAgregar);
                        }
                    }
                    if (Boolean.TRUE.equals(permisos.get("baja_reactivar_producto"))) {
                        productosDAO.cambiarEstatus(id, nuevoEstatus);
                    }
                }
            }

            List<Productos> listaProductos = productosDAO.listarProductos();

            request.setAttribute("lista", listaProductos);
            dispatcher = request.getRequestDispatcher("Productos/index.jsp");

        } else if ("salida_productos".equals(accion)) {
            if (!tienePermiso(permisos, "ver_salida")) {
                redirigirSinPermiso(request, response);
                return;
            }
            List<Productos> listaProductos = productosDAO.listarProductosActivos();
            request.setAttribute("lista", listaProductos);
            dispatcher = request.getRequestDispatcher("Productos/modificar.jsp");
        } else if ("guardarSalidas".equals(accion)) {
            if (!tienePermiso(permisos, "sacar_inventario")) {
                redirigirSinPermiso(request, response);
                return;
            }
            String[] ids = request.getParameterValues("id[]");
            String[] cantidades = request.getParameterValues("cantidad[]");

            if (ids != null && cantidades != null) {
                for (int i = 0; i < ids.length; i++) {
                    int idProducto = Integer.parseInt(ids[i]);
                    int cantidadRetirar = Integer.parseInt(cantidades[i]);

                    //System.out.println("Producto ID: " + idProducto +" | Cantidad a retirar: " + cantidadRetirar);

                    if (cantidadRetirar > 0) {
                        productosDAO.retirarCantidad(idProducto, cantidadRetirar);
                        Integer idUsuario = obtenerIdUsuario(request);
                        if (idUsuario != null) {
                            HistoricoDAO historicoDAO = new HistoricoDAO();
                            historicoDAO.insertar(idUsuario, idProducto, "Salida", cantidadRetirar);
                        }
                    }
                }
            }
            List<Productos> listaProductos = productosDAO.listarProductosActivos();
            request.setAttribute("lista", listaProductos);
            dispatcher = request.getRequestDispatcher("Productos/modificar.jsp");
        } else if ("historial".equals(accion)) {
            if (!tienePermiso(permisos, "ver_historico")) {
                redirigirSinPermiso(request, response);
                return;
            }
            String tipo = request.getParameter("tipo");
            HistoricoDAO historicoDAO = new HistoricoDAO();
            request.setAttribute("listaHistorial", historicoDAO.listar(tipo));
            request.setAttribute("tipoFiltro", tipo != null ? tipo : "");
            dispatcher = request.getRequestDispatcher("Productos/historial.jsp");
        }

        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private Integer obtenerIdUsuario(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        Object id = session.getAttribute("idUsuario");
        return (id instanceof Integer) ? (Integer) id : null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Boolean> obtenerPermisos(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return java.util.Collections.emptyMap();
        Object p = session.getAttribute("permisos");
        return (p instanceof Map) ? (Map<String, Boolean>) p : java.util.Collections.emptyMap();
    }

    private boolean tienePermiso(Map<String, Boolean> permisos, String permiso) {
        return Boolean.TRUE.equals(permisos.get(permiso));
    }

    private void redirigirSinPermiso(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/ProductosController?sinPermiso=1");
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
