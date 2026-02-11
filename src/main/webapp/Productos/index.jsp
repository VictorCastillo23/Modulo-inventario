<%-- 
    Document   : index
    Created on : 9 feb 2026, 7:32:09 p.m.
    Author     : Victor
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
    <head>
        <title>Almacén</title>
        <meta charset="UTF-8">
    </head>
    <body>
        <h1>Productos</h1>
        <c:if test="${sinPermiso}">
            <p style="color: red;">No tiene permiso para realizar esa acción.</p>
        </c:if>
        <a href="LoginController?accion=salir">Cerrar sesión</a> |
        <c:if test="${permisos.agregar_productos}">
            <a href="ProductosController?accion=nuevo">Nuevo registro</a> |
        </c:if>
        <c:if test="${permisos.ver_salida}">
            <a href="ProductosController?accion=salida_productos">Salida de productos</a> |
        </c:if>
        <c:if test="${permisos.ver_historico}">
            <a href="ProductosController?accion=historial">Historial de movimientos</a>
        </c:if>
        <br/><br/>
        <c:if test="${permisos.aumentar_inventario || permisos.baja_reactivar_producto}">
        <form action="ProductosController" method="post">

            <input type="hidden" name="accion" value="guardarCambios"/>
            <button type="submit">Guardar cambios</button>
            <br/>

            <table border="1" width="80%">
                <thead>
                    <tr>
                        <th>Nombre</th>
                        <th>Cantidad Actual</th>
                        <c:if test="${permisos.aumentar_inventario}"><th>Cantidad a agregar</th></c:if>
                        <c:if test="${permisos.baja_reactivar_producto}"><th>Estatus</th></c:if>
                    </tr>
                </thead>
                <c:forEach var="producto" items="${lista}">
                    <tr>
                        <input type="hidden" name="id[]" value="${producto.id}"/>
                        <input type="hidden" name="modificado[]" value="false" class="flag-modificado"/>
                
                        <td>
                            <c:out value="${producto.nombre}"/>
                        </td>
                
                        <td>
                            <c:out value="${producto.cantidad}"/>
                        </td>
                
                        <c:if test="${permisos.aumentar_inventario}">
                        <td>
                            <input
                                type="number"
                                name="cantidad[]"
                                class="cantidad-input"
                                min="0"
                                value="0"
                                data-valor-inicial="${producto.cantidad}"
                                required
                            />
                        </td>
                        </c:if>
                        <c:if test="${!permisos.aumentar_inventario}">
                            <input type="hidden" name="cantidad[]" value="0"/>
                        </c:if>
                
                        <c:if test="${permisos.baja_reactivar_producto}">
                        <td>
                            <select name="estatus[]" class="estatus-select"
                                    data-valor-inicial="${producto.estatus}">
                                <option value="true" ${producto.estatus ? 'selected' : ''}>Activo</option>
                                <option value="false" ${!producto.estatus ? 'selected' : ''}>Inactivo</option>
                            </select>
                        </td>
                        </c:if>
                        <c:if test="${!permisos.baja_reactivar_producto}">
                            <input type="hidden" name="estatus[]" value="${producto.estatus}"/>
                        </c:if>
                    </tr>
                </c:forEach>
                   </table>

        </form>
        </c:if>
        <c:if test="${!permisos.aumentar_inventario && !permisos.baja_reactivar_producto}">
            <table border="1" width="80%">
                <thead>
                    <tr>
                        <th>Nombre</th>
                        <th>Cantidad Actual</th>
                    </tr>
                </thead>
                <c:forEach var="producto" items="${lista}">
                    <tr>
                        <td><c:out value="${producto.nombre}"/></td>
                        <td><c:out value="${producto.cantidad}"/></td>
                    </tr>
                </c:forEach>
            </table>
        </c:if>

        <script>
            document.querySelectorAll('.cantidad-input, .estatus-select').forEach(function(input) {
            
                input.addEventListener('change', function () {
                    const fila = this.closest('tr');
                    const flag = fila.querySelector('.flag-modificado');
            
                    if (this.classList.contains('cantidad-input')) {
                        if (parseInt(this.value, 10) > 0) {
                            flag.value = "true";
                        }
                    }
            
                    if (this.classList.contains('estatus-select')) {
                        const inicial = this.getAttribute('data-valor-inicial');
                        if (this.value !== inicial) {
                            flag.value = "true";
                        }
                    }
                });
            
            });
                document.querySelectorAll('.cantidad-input').forEach(function(input) {
                var valorMinimo = parseInt(input.getAttribute('data-valor-inicial'), 10) || 0;
                input.addEventListener('change', function() {
                    var valorActual = parseInt(this.value, 10);
                    if (isNaN(valorActual) || valorActual < 0) {
                        alert('No puede reducir la cantidad. Solo se permite incrementar el valor actual.');
                        this.value = 0;
                    }
                });
                input.addEventListener('input', function() {
                    var valorActual = parseInt(this.value, 10);
                    if (!isNaN(valorActual) && valorActual < 0) {
                        alert('No puede reducir la cantidad. Solo se permite incrementar el valor actual.');
                        this.value = 0;
                    }
                });
            });
        </script>

    </body>
</html>
