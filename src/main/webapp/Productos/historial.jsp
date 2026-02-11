<%--
    Historial de movimientos (Entrada/Salida) de productos.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Historial de movimientos</title>
    </head>
    <body>
        <a href="LoginController?accion=salir">Cerrar sesión</a> |
        <a href="ProductosController">Productos</a>
        <c:if test="${permisos.agregar_productos}">
            | <a href="ProductosController?accion=nuevo">Nuevo registro</a>
        </c:if>
        <c:if test="${permisos.ver_salida}">
            | <a href="ProductosController?accion=salida_productos">Salida de productos</a>
        </c:if>
        <h1>Historial de movimientos</h1>

        <form action="ProductosController" method="get">
            <input type="hidden" name="accion" value="historial"/>
            <label for="tipo">Tipo de movimiento:</label>
            <select name="tipo" id="tipo" onchange="this.form.submit()">
                <option value="" ${empty tipoFiltro ? 'selected' : ''}>Todos</option>
                <option value="Entrada" ${tipoFiltro == 'Entrada' ? 'selected' : ''}>Entrada</option>
                <option value="Salida" ${tipoFiltro == 'Salida' ? 'selected' : ''}>Salida</option>
            </select>
        </form>

        <br/>
        <table border="1" width="90%">
            <thead>
                <tr>
                    <th>Fecha y hora</th>
                    <th>Movimiento</th>
                    <th>Producto</th>
                    <th>Cantidad</th>
                    <th>Realizado por</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty listaHistorial}">
                        <tr><td colspan="5">No hay movimientos registrados.</td></tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="r" items="${listaHistorial}">
                            <tr>
                                <td><c:out value="${r.fechaFormateada}"/></td>
                                <td><c:out value="${r.movimiento}"/></td>
                                <td><c:out value="${r.nombreProducto}"/></td>
                                <td><c:out value="${r.cantidad}"/></td>
                                <td><c:out value="${r.nombreUsuario}"/></td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </body>
</html>
