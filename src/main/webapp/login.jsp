<%--
    Página de inicio de sesión
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Iniciar sesión" scope="request"/>
<%@ include file="/WEB-INF/jspf/layout-top.jspf" %>

<c:if test="${not empty sessionScope.usuario}">
    <a class="btn btn-outline-secondary btn-sm btn-erp" href="LoginController?accion=salir">Cerrar sesión</a>
  </c:if>
  
<div class="auth-wrap">
    <div class="card card-soft auth-card">
        <div class="card-body p-4 p-md-5">
            <div class="d-flex align-items-center gap-2 mb-3">
                <div>
                    <div class="fw-semibold">Almacén</div>
                    <div class="text-secondary small">Acceso al sistema</div>
                </div>
            </div>

            <h1 class="h4 page-title mb-3">Iniciar sesión</h1>

            <c:if test="${not empty error}">
                <div class="alert alert-danger" role="alert">
                    <strong>Error.</strong> <c:out value="${error}"/>
                </div>
            </c:if>

            <form action="LoginController" method="post" class="needs-validation" novalidate>
                <div class="mb-3">
                    <label for="usuario" class="form-label">Usuario</label>
                    <input type="text" id="usuario" name="usuario" class="form-control" required autofocus />
                    <div class="invalid-feedback">Ingresa tu usuario.</div>
                </div>

                <div class="mb-3">
                    <label for="clave" class="form-label">Contraseña</label>
                    <input type="password" id="clave" name="clave" class="form-control" required />
                    <div class="invalid-feedback">Ingresa tu contraseña.</div>
                </div>

                <button type="submit" class="btn btn-primary w-100 btn-erp">Entrar</button>

            </form>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jspf/layout-bottom.jspf" />
