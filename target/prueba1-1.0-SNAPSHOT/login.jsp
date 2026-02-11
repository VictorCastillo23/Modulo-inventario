<%--
    Página de inicio de sesión
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Iniciar sesión - Almacén</title>
        <meta charset="UTF-8">
        <style>
            body { font-family: sans-serif; max-width: 320px; margin: 60px auto; padding: 20px; }
            h1 { font-size: 1.3em; margin-bottom: 1em; }
            label { display: block; margin-bottom: 4px; }
            input[type="text"], input[type="password"] { width: 100%; padding: 8px; margin-bottom: 12px; box-sizing: border-box; }
            button { padding: 10px 20px; background: #333; color: #fff; border: none; cursor: pointer; width: 100%; }
            button:hover { background: #555; }
            .error { color: #c00; margin-bottom: 12px; font-size: 0.9em; }
        </style>
    </head>
    <body>
        <h1>Iniciar sesión</h1>
        <c:if test="${not empty error}">
            <p class="error"><c:out value="${error}"/></p>
        </c:if>
        <form action="LoginController" method="post">
            <label for="usuario">Usuario</label>
            <input type="text" id="usuario" name="usuario" required autofocus/>
            <label for="clave">Contraseña</label>
            <input type="password" id="clave" name="clave" required/>
            <button type="submit">Entrar</button>
        </form>
    </body>
</html>
