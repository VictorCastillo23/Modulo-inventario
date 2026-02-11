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
        <a href="ProductosController?accion=nuevo">Nuevo registro</a>
        <br/><br/>
        <a href="ProductosController?accion=salida_productos">Salida de productos</a>
        <form action="ProductosController" method="post">

            <input type="hidden" name="accion" value="guardarCambios"/>
            <button type="submit">Guardar cambios</button>
            <br/>

            <table border="1" width="80%">
                <thead>
                    <tr>
                        <th>Nombre</th>
                        <th>Cantidad Actual</th>
                        <th>Cantidad a agregar</th>
                        <th>Estatus</th>
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
                
                        <td>
                            <select name="estatus[]" class="estatus-select"
                                    data-valor-inicial="${producto.estatus}">
                                <option value="true" ${producto.estatus ? 'selected' : ''}>Activo</option>
                                <option value="false" ${!producto.estatus ? 'selected' : ''}>Inactivo</option>
                            </select>
                        </td>
                    </tr>
                </c:forEach>
                   </table>

        </form>

        <script>
            document.querySelectorAll('.cantidad-input, .estatus-select').forEach(function(input) {
            
                input.addEventListener('change', function () {
                    const fila = this.closest('tr');
                    const flag = fila.querySelector('.flag-modificado');
            
                    // Para cantidad
                    if (this.classList.contains('cantidad-input')) {
                        if (parseInt(this.value, 10) > 0) {
                            flag.value = "true";
                        }
                    }
            
                    // Para estatus
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
