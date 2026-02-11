<%-- 
    Document   : modificar
    Created on : 9 feb 2026, 7:32:42 p.m.
    Author     : Victor
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <h2>Salida de productos</h2>
 
        <h1>Productos</h1>

        <br/><br/>
        <form action="ProductosController" method="post">

            <input type="hidden" name="accion" value="guardarSalidas"/>
            <button type="submit">Guardar cambios</button>
            <br/>

            <table border="1" width="80%">
                <thead>
                    <tr>
                        <th>Nombre</th>
                        <th>Cantidad Actual</th>
                        <th>Cantidad a retirar</th>
                    </tr>
                </thead>
                <tbody>

                    <c:forEach var="producto" items="${lista}">
                        <tr>
                    <input type="hidden" name="id[]" value="${producto.id}"/>

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
                    </tr>
                </c:forEach>

                </tbody>
            </table>

        </form>

        <script>
            document.querySelectorAll('.cantidad-input').forEach(function(input) {
                var valorMinimo = parseInt(input.getAttribute('data-valor-inicial'), 10) || 0;
                input.addEventListener('change', function() {
                    var valorActual = parseInt(this.value, 10);
                    if (isNaN(valorActual) || valorActual < 0 || valorActual > valorMinimo) {
                        alert('Retiro invalido. El monto maximo es el valor actual (' + valorMinimo + ').');
                        this.value = 0;
                    }
                });
                input.addEventListener('input', function() {
                    var valorActual = parseInt(this.value, 10);
                    if (!isNaN(valorActual) && valorActual < 0 || valorActual > valorMinimo) {
                        alert('Retiro invalido. El monto maximo es el valor actual (' + valorMinimo + ').');
                        this.value = 0;
                    }
                });
            });
        </script>

    </body>
</html>