<%-- 
    Document   : nuevo
    Created on : 9 feb 2026, 7:32:32 p.m.
    Author     : Victor
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Almacén - Nuevo registro</title>
    </head>
    <body>
        <a href="LoginController?accion=salir">Cerrar sesión</a> |
        <a href="ProductosController">Productos</a>
        <c:if test="${permisos.ver_salida}">
            | <a href="ProductosController?accion=salida_productos">Salida de productos</a>
        </c:if>
        <c:if test="${permisos.ver_historico}">
            | <a href="ProductosController?accion=historial">Historial</a>
        </c:if>
        <h2>Nuevo Registro</h2>
        <br/>
        <form action="ProductosController?accion=insertar" method="POST" autocomplete="off">
            <p>
                Nombre:
                <input id="nombre" name="nombre" type="text" />
            </p>
            <p>
                cantidad:
                <input id="cantidad" name="cantidad" type="text" />
            </p>
            <p>
                Estatus:
                <input id="estatus" name="estatus" type="text" />
            </p>
            
            <button id="guardar" name="guardar" type="submit">Guardar<button/>
            
        </form>
    </body>
</html>
