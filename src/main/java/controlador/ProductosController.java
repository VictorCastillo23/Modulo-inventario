package controlador;

import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
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

        if (accion == null || accion.isEmpty()) {
            dispatcher = request.getRequestDispatcher("Productos/index.jsp");
            List<Productos> listaProductos = productosDAO.listarProductos();
            request.setAttribute("lista", listaProductos);

        } else if ("nuevo".equals(accion)) {
            dispatcher = request.getRequestDispatcher("Productos/nuevo.jsp");
        } else if ("insertar".equals(accion)) {

            String nombre = request.getParameter("nombre");
            int cantidad = Integer.parseInt(request.getParameter("cantidad"));
            boolean estatus = Boolean.parseBoolean(request.getParameter("estatus"));

            Productos producto = new Productos(0, nombre, cantidad, estatus);

            productosDAO.insertar(producto);
            List<Productos> listaProductos = productosDAO.listarProductos();

            request.setAttribute("lista", listaProductos);
            dispatcher = request.getRequestDispatcher("Productos/index.jsp");
        /*}else if ("actualizar".equals(accion)) {

            int id = Integer.parseInt(request.getParameter("id"));
            int cantidad = Integer.parseInt(request.getParameter("cantidad"));

            Productos producto = new Productos(id, "", cantidad, false);

            productosDAO.actualizar(producto);

            List<Productos> listaProductos = productosDAO.listarProductos();

            request.setAttribute("lista", listaProductos);
            dispatcher = request.getRequestDispatcher("Productos/index.jsp");
            
        } else if ("cambiar_estatus".equals(accion)) {

            int id = Integer.parseInt(request.getParameter("id"));
            boolean estatus = Boolean.parseBoolean(request.getParameter("estatus"));

            productosDAO.cambiarEstatus(id, estatus);

            List<Productos> listaProductos = productosDAO.listarProductos();

            request.setAttribute("lista", listaProductos);
            dispatcher = request.getRequestDispatcher("Productos/index.jsp");

            */
        } else if ("guardarCambios".equals(request.getParameter("accion"))) {

            String[] ids = request.getParameterValues("id[]");
            String[] cantidades = request.getParameterValues("cantidad[]");
            String[] estatus = request.getParameterValues("estatus[]");
            String[] modificados = request.getParameterValues("modificado[]");

            for (int i = 0; i < ids.length; i++) {

                int id = Integer.parseInt(ids[i]);
                int cantidadAgregar = Integer.parseInt(cantidades[i]);
                boolean nuevoEstatus = Boolean.parseBoolean(estatus[i]);
                boolean fueModificado = Boolean.parseBoolean(modificados[i]);

                //System.out.println("ID: " + id +" | Retiro: " + cantidadAgregar +" | Estatus: " + nuevoEstatus +" | Modificado: " + fueModificado);

                if (fueModificado) {
                    if (cantidadAgregar > 0) {
                        productosDAO.agregarCantidad(id, cantidadAgregar);
                    }
                    productosDAO.cambiarEstatus(id, nuevoEstatus);
                }
            }

            List<Productos> listaProductos = productosDAO.listarProductos();

            request.setAttribute("lista", listaProductos);
            dispatcher = request.getRequestDispatcher("Productos/index.jsp");

        }else if ("salida_productos".equals(accion)) {
            List<Productos> listaProductos = productosDAO.listarProductosActivos();
            request.setAttribute("lista", listaProductos);
            dispatcher = request.getRequestDispatcher("Productos/modificar.jsp");
        }else if ("guardarSalidas".equals(accion)) {
            String[] ids = request.getParameterValues("id[]");
            String[] cantidades = request.getParameterValues("cantidad[]");

            if (ids != null && cantidades != null) {
                for (int i = 0; i < ids.length; i++) {
                    int idProducto = Integer.parseInt(ids[i]);
                    int cantidadRetirar = Integer.parseInt(cantidades[i]);

                    //System.out.println("Producto ID: " + idProducto +" | Cantidad a retirar: " + cantidadRetirar);

                    if (cantidadRetirar > 0) {
                        productosDAO.retirarCantidad(idProducto, cantidadRetirar);
                    }
                }
            }
            List<Productos> listaProductos = productosDAO.listarProductosActivos();
            request.setAttribute("lista", listaProductos);
            dispatcher = request.getRequestDispatcher("Productos/modificar.jsp");
        }

        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
