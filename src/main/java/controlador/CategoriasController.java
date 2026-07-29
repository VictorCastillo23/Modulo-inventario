package controlador;

import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import modelo.Categoria;
import modelo.CategoriaDAO;
import seguridad.Permisos;

/**
 *
 * @author Victor
 */
@WebServlet(name = "CategoriasController", urlPatterns = {"/CategoriasController"})
public class CategoriasController extends HttpServlet {

    /**
     * Serves read-only actions only ({@code ""}, {@code nuevo},
     * {@code editar}). Mutating actions are handled exclusively by
     * {@link #doPost}; an unrecognized action here returns {@code 405}
     * instead of silently falling through — mirrors the split documented in
     * {@code ProductosController}.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        CategoriaDAO categoriaDAO = new CategoriaDAO();
        String accion = request.getParameter("accion");
        RequestDispatcher dispatcher;
        Map<String, Boolean> permisos = obtenerPermisos(request);

        if (accion == null || accion.isEmpty()) {
            if (!tienePermiso(permisos, Permisos.VER_CATEGORIAS)) {
                redirigirSinPermiso(request, response);
                return;
            }
            List<Categoria> lista = categoriaDAO.listar();
            request.setAttribute("lista", lista);
            dispatcher = request.getRequestDispatcher("Categorias/index.jsp");

        } else if ("nuevo".equals(accion)) {
            if (!tienePermiso(permisos, Permisos.GESTIONAR_CATEGORIAS)) {
                redirigirSinPermiso(request, response);
                return;
            }
            dispatcher = request.getRequestDispatcher("Categorias/nuevo.jsp");

        } else if ("editar".equals(accion)) {
            if (!tienePermiso(permisos, Permisos.GESTIONAR_CATEGORIAS)) {
                redirigirSinPermiso(request, response);
                return;
            }
            int id = parseIdOrZero(request.getParameter("id"));
            Categoria categoria = id > 0 ? categoriaDAO.obtenerPorId(id) : null;
            if (categoria == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Categoría no encontrada.");
                return;
            }
            request.setAttribute("categoria", categoria);
            dispatcher = request.getRequestDispatcher("Categorias/editar.jsp");

        } else {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    "Acción no soportada para GET: " + accion);
            return;
        }

        dispatcher.forward(request, response);
    }

    /**
     * Serves mutating actions only ({@code insertar}, {@code actualizar},
     * {@code cambiarEstatus}). Does NOT delegate to {@link #doGet} — an
     * unrecognized action returns {@code 405}. CSRF token validation for
     * every authenticated POST happens upstream in {@code AuthFilter}.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        CategoriaDAO categoriaDAO = new CategoriaDAO();
        String accion = request.getParameter("accion");
        RequestDispatcher dispatcher;
        Map<String, Boolean> permisos = obtenerPermisos(request);

        if (!tienePermiso(permisos, Permisos.GESTIONAR_CATEGORIAS)) {
            redirigirSinPermiso(request, response);
            return;
        }

        if ("insertar".equals(accion)) {
            String nombre = request.getParameter("nombre");
            String descripcion = request.getParameter("descripcion");

            if (nombre == null || nombre.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "El nombre es obligatorio.");
                return;
            }
            Categoria categoria = new Categoria(0, nombre.trim(), descripcion, true);
            categoriaDAO.insertarRetornarId(categoria);

        } else if ("actualizar".equals(accion)) {
            int id = parseIdOrZero(request.getParameter("id"));
            String nombre = request.getParameter("nombre");
            String descripcion = request.getParameter("descripcion");

            if (id <= 0 || nombre == null || nombre.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Datos de categoría inválidos.");
                return;
            }
            Categoria categoria = categoriaDAO.obtenerPorId(id);
            if (categoria == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Categoría no encontrada.");
                return;
            }
            categoria.setNombre(nombre.trim());
            categoria.setDescripcion(descripcion);
            categoriaDAO.actualizar(categoria);

        } else if ("cambiarEstatus".equals(accion)) {
            int id = parseIdOrZero(request.getParameter("id"));
            boolean estatus = Boolean.parseBoolean(request.getParameter("estatus"));

            if (id <= 0) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Id de categoría inválido.");
                return;
            }
            categoriaDAO.cambiarEstatus(id, estatus);

        } else {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                    "Acción no soportada para POST: " + accion);
            return;
        }

        List<Categoria> lista = categoriaDAO.listar();
        request.setAttribute("lista", lista);
        dispatcher = request.getRequestDispatcher("Categorias/index.jsp");
        dispatcher.forward(request, response);
    }

    private int parseIdOrZero(String raw) {
        try {
            return raw != null ? Integer.parseInt(raw.trim()) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
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
}
