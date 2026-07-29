<%--
    Document   : nuevo
    Created on : 29 jul 2026
    Author     : Victor
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Nueva categoría" scope="request"/>
<%@ include file="/WEB-INF/jspf/layout-top.jspf" %>

<div class="d-flex flex-wrap align-items-center justify-content-between gap-2 mb-3">
    <div>
        <h1 class="h4 page-title mb-1">Nueva categoría</h1>
        <div class="text-secondary small">Alta de registro</div>
    </div>
    <div class="page-actions d-flex flex-wrap gap-2">
        <a class="btn btn-outline-secondary btn-sm btn-erp" href="CategoriasController">Volver</a>
    </div>
</div>

<div class="card card-soft">
    <div class="card-body">
        <form action="CategoriasController" method="POST" class="needs-validation" novalidate data-guard>
            <input type="hidden" name="accion" value="insertar"/>
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
            <div class="row g-3">
                <div class="col-12">
                    <label for="nombre" class="form-label">Nombre</label>
                    <input id="nombre" name="nombre" type="text" class="form-control" required maxlength="50"/>
                    <div class="invalid-feedback">El nombre es obligatorio.</div>
                </div>
                <div class="col-12">
                    <label for="descripcion" class="form-label">Descripción</label>
                    <textarea id="descripcion" name="descripcion" class="form-control" maxlength="150" rows="3"></textarea>
                </div>
            </div>
            <div class="d-flex justify-content-end gap-2 mt-4">
                <a class="btn btn-outline-secondary btn-erp" href="CategoriasController">Cancelar</a>
                <button type="submit" class="btn btn-primary btn-erp">Guardar</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/jspf/layout-bottom.jspf" />
