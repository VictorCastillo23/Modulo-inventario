package controlador;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.PermisoDAO;
import modelo.Usuario;
import modelo.UsuarioDAO;
import seguridad.CsrfTokens;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Victor
 */
@WebServlet(name = "LoginController", urlPatterns = {"/LoginController"})
public class LoginController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");

        if ("salir".equals(accion)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/LoginController");
            return;
        }

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("usuario") != null) {
            response.sendRedirect(request.getContextPath() + "/ProductosController");
            return;
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String usuario = request.getParameter("usuario");
        String clave = request.getParameter("clave");

        if (usuario == null || usuario.isBlank() || clave == null || clave.isBlank()) {
            request.setAttribute("error", "Ingrese usuario y contraseña.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario u = usuarioDAO.validar(usuario.trim(), clave);

        if (u != null) {
            // SEC-05: invalidate any pre-login session and create a fresh one before
            // trusting anything in it. This is strictly stronger than
            // request.changeSessionId() — that call only rotates the session id while
            // keeping any attacker-planted pre-login attributes; invalidate+recreate
            // discards the whole session state, closing session-fixation attacks
            // where an attacker seeds a victim's browser with a known session id
            // before they authenticate.
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession session = request.getSession(true);

            session.setAttribute("usuario", u.getUsuario());
            session.setAttribute("idUsuario", u.getId());
            session.setAttribute("idRol", u.getIdRol());
            PermisoDAO permisoDAO = new PermisoDAO();
            Set<String> permisosSet = permisoDAO.listarPermisosPorRol(u.getIdRol());
            Map<String, Boolean> permisos = new HashMap<>();
            for (String p : permisosSet) {
                permisos.put(p, true);
            }
            session.setAttribute("permisos", permisos);
            // Minted into the NEW session, after invalidate+recreate above — minting
            // it before invalidation would discard the token along with the rest of
            // the old session state (see Phase 2a scope note for why this line exists
            // at all: it is the minimal prerequisite for AuthFilter's CSRF check to
            // have anything to validate against).
            session.setAttribute(CsrfTokens.SESSION_ATTRIBUTE, CsrfTokens.generate());
            response.sendRedirect(request.getContextPath() + "/ProductosController");
        } else {
            request.setAttribute("error", "Usuario o contraseña incorrectos.");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}
