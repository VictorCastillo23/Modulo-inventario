package filtro;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import seguridad.CsrfTokens;

/**
 * Authentication gate for {@code /ProductosController} and
 * {@code /Productos/*}. Also enforces CSRF synchronizer-token validation on
 * every authenticated POST, based purely on {@code request.getMethod()} —
 * this filter has no knowledge of individual {@code accion} values; the
 * GET/POST split in {@code ProductosController} is what guarantees POST
 * always means "mutating" (see design D3).
 *
 * @author Victor
 */
@WebFilter(urlPatterns = {"/ProductosController", "/Productos/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        boolean autenticado = session != null && session.getAttribute("usuario") != null;

        if (!autenticado) {
            resp.sendRedirect(req.getContextPath() + "/LoginController");
            return;
        }

        if ("POST".equalsIgnoreCase(req.getMethod())) {
            String tokenEnSesion = (String) session.getAttribute(CsrfTokens.SESSION_ATTRIBUTE);
            String tokenEnRequest = req.getParameter(CsrfTokens.PARAMETER_NAME);
            if (!CsrfTokens.matches(tokenEnSesion, tokenEnRequest)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Token CSRF inválido o ausente.");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
