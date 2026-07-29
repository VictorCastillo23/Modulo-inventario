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
import modelo.CategoriaDAO;
import modelo.HistoricoDAO;
import modelo.Productos;
import modelo.ProductosDAO;
import seguridad.InventoryRequestValidator;
import seguridad.Permisos;
import seguridad.ValidationResult;
import seguridad.WithdrawalOutcome;

/**
 *
 * @author Victor
 */
@WebServlet(name = "ProductosController", urlPatterns = {"/ProductosController"})
public class ProductosController extends HttpServlet {

    /**
     * Serves read-only actions only ({@code ""}, {@code nuevo},
     * {@code salida_productos}, {@code historial}). Mutating actions are
     * handled exclusively by {@link #doPost}; an unrecognized action here
     * returns {@code 405} instead of silently falling through.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ProductosDAO productosDAO = new ProductosDAO();
        String accion = request.getParameter("accion");
        RequestDispatcher dispatcher;
        Map<String, Boolean> permisos = obtenerPermisos(request);

        if (accion == null || accion.isEmpty()) {
            if (!tienePermiso(permisos, Permisos.VER_INVENTARIO)) {
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
            if (!tienePermiso(permisos, Permisos.AGREGAR_PRODUCTOS)) {
                redirigirSinPermiso(request, response);
                return;
            }
            request.setAttribute("listaCategorias", new CategoriaDAO().listarActivas());
            dispatcher = request.getRequestDispatcher("Productos/nuevo.jsp");
        } else if ("salida_productos".equals(accion)) {
            if (!tienePermiso(permisos, Permisos.VER_SALIDA)) {
                redirigirSinPermiso(request, response);
                return;
            }
            List<Productos> listaProductos = productosDAO.listarProductosActivos();
            request.setAttribute("lista", listaProductos);
            dispatcher = request.getRequestDispatcher("Productos/salida.jsp");
        } else if ("historial".equals(accion)) {
            if (!tienePermiso(permisos, Permisos.VER_HISTORICO)) {
                redirigirSinPermiso(request, response);
                return;
            }
            String tipo = request.getParameter("tipo");
            HistoricoDAO historicoDAO = new HistoricoDAO();
            request.setAttribute("listaHistorial", historicoDAO.listar(tipo));
            request.setAttribute("tipoFiltro", tipo != null ? tipo : "");
            dispatcher = request.getRequestDispatcher("Productos/historial.jsp");
        } else {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    "Acción no soportada para GET: " + accion);
            return;
        }

        dispatcher.forward(request, response);
    }

    /**
     * Serves mutating actions only ({@code insertar}, {@code guardarCambios},
     * {@code guardarSalidas}). Does NOT delegate to {@link #doGet} — an
     * unrecognized action returns {@code 405}. CSRF token validation for
     * every authenticated POST happens upstream in {@code AuthFilter}, before
     * this method (and any DAO call) ever runs.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ProductosDAO productosDAO = new ProductosDAO();
        String accion = request.getParameter("accion");
        RequestDispatcher dispatcher;
        Map<String, Boolean> permisos = obtenerPermisos(request);

        if ("insertar".equals(accion)) {
            if (!tienePermiso(permisos, Permisos.AGREGAR_PRODUCTOS)) {
                redirigirSinPermiso(request, response);
                return;
            }

            String nombre = request.getParameter("nombre");
            int cantidad = 0;// Integer.parseInt(request.getParameter("cantidad"));
            boolean estatus = Boolean.parseBoolean(request.getParameter("estatus"));
            Integer idCategoria = parseNullableInt(request.getParameter("idCategoria"));

            Productos producto = new Productos(0, nombre, cantidad, estatus, idCategoria);

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
        } else if ("guardarCambios".equals(accion)) {
            if (!tienePermiso(permisos, Permisos.AUMENTAR_INVENTARIO) && !tienePermiso(permisos, Permisos.BAJA_REACTIVAR_PRODUCTO)) {
                redirigirSinPermiso(request, response);
                return;
            }

            String[] ids = request.getParameterValues("id[]");
            String[] cantidades = request.getParameterValues("cantidad[]");
            String[] estatus = request.getParameterValues("estatus[]");
            String[] modificados = request.getParameterValues("modificado[]");

            ValidationResult validation = InventoryRequestValidator.validateEdit(ids, cantidades, estatus, modificados);
            if (!validation.isValid()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Datos de edición inválidos: " + validation.error());
                return;
            }

            for (int i = 0; i < ids.length; i++) {

                int id = Integer.parseInt(ids[i]);
                int cantidadAgregar = Integer.parseInt(cantidades[i]);
                boolean nuevoEstatus = Boolean.parseBoolean(estatus[i]);
                boolean fueModificado = Boolean.parseBoolean(modificados[i]);

                if (fueModificado) {
                    if (cantidadAgregar > 0 && Boolean.TRUE.equals(permisos.get(Permisos.AUMENTAR_INVENTARIO))) {
                        productosDAO.agregarCantidad(id, cantidadAgregar);
                        Integer idUsuario = obtenerIdUsuario(request);
                        if (idUsuario != null) {
                            HistoricoDAO historicoDAO = new HistoricoDAO();
                            historicoDAO.insertar(idUsuario, id, "Entrada", cantidadAgregar);
                        }
                    }
                    if (Boolean.TRUE.equals(permisos.get(Permisos.BAJA_REACTIVAR_PRODUCTO))) {
                        productosDAO.cambiarEstatus(id, nuevoEstatus);
                    }
                }
            }

            List<Productos> listaProductos = productosDAO.listarProductos();

            request.setAttribute("lista", listaProductos);
            dispatcher = request.getRequestDispatcher("Productos/index.jsp");

        } else if ("guardarSalidas".equals(accion)) {
            if (!tienePermiso(permisos, Permisos.SACAR_INVENTARIO)) {
                redirigirSinPermiso(request, response);
                return;
            }
            String[] ids = request.getParameterValues("id[]");
            String[] cantidades = request.getParameterValues("cantidad[]");

            ValidationResult validation = InventoryRequestValidator.validateWithdrawal(ids, cantidades);
            if (!validation.isValid()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Datos de salida inválidos: " + validation.error());
                return;
            }

            WithdrawalOutcome outcome = new WithdrawalOutcome();
            for (int i = 0; i < ids.length; i++) {
                int idProducto = Integer.parseInt(ids[i]);
                int cantidadRetirar = Integer.parseInt(cantidades[i]);

                if (cantidadRetirar > 0) {
                    boolean aplicado = productosDAO.retirarCantidad(idProducto, cantidadRetirar);
                    if (aplicado) {
                        outcome.applied(idProducto);
                        // SEC-04 fix: only log a Historico "Salida" row when the DAO
                        // actually updated exactly one row. Previously this write fired
                        // unconditionally, logging a phantom movement even on a no-op
                        // (rejected) update.
                        Integer idUsuario = obtenerIdUsuario(request);
                        if (idUsuario != null) {
                            HistoricoDAO historicoDAO = new HistoricoDAO();
                            historicoDAO.insertar(idUsuario, idProducto, "Salida", cantidadRetirar);
                        }
                    } else {
                        outcome.rejected(idProducto);
                    }
                }
            }
            List<Productos> listaProductos = productosDAO.listarProductosActivos();
            request.setAttribute("lista", listaProductos);
            request.setAttribute("rechazados", outcome.rejectedIds());
            dispatcher = request.getRequestDispatcher("Productos/salida.jsp");
        } else {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    "Acción no soportada para POST: " + accion);
            return;
        }

        dispatcher.forward(request, response);
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

    private Integer parseNullableInt(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(raw.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
