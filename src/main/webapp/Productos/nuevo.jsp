<%-- 
    Document   : nuevo
    Created on : 9 feb 2026, 7:32:32 p.m.
    Author     : Victor
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Nuevo producto" scope="request"/>
<%@ include file="/WEB-INF/jspf/layout-top.jspf" %>

<div class="d-flex flex-wrap align-items-center justify-content-between gap-2 mb-3">
    <div>
        <h1 class="h4 page-title mb-1">Nuevo producto</h1>
        <div class="text-secondary small">Alta de registro</div>
    </div>
    <div class="page-actions d-flex flex-wrap gap-2">
        <a class="btn btn-outline-secondary btn-sm btn-erp" href="ProductosController">Volver</a>
    </div>
</div>

<div class="card card-soft">
    <div class="card-body">
        <form action="ProductosController?accion=insertar" method="POST" autocomplete="off" class="needs-validation" novalidate>
            <div class="row g-3">
                <div class="col-12">
                    <label for="nombre" class="form-label">Nombre</label>
                    <input id="nombre" name="nombre" type="text" class="form-control" required />
                    <div class="invalid-feedback">El nombre es obligatorio.</div>
                </div>


                <div class="col-12 col-md-6">
                    <label for="estatus" class="form-label">Estatus</label>
                    <div id="estatus" class="d-flex flex-wrap gap-3">
                        <div class="form-check">
                            <input class="form-check-input" type="radio" name="estatus" id="estatus-true" value="true" checked>
                            <label class="form-check-label" for="estatus-true">
                                Activo
                            </label>
                        </div>
                        <div class="form-check">
                            <input class="form-check-input" type="radio" name="estatus" id="estatus-false" value="false">
                            <label class="form-check-label" for="estatus-false">
                                Inactivo
                            </label>
                        </div>
                    </div>
                    <div class="invalid-feedback">El estatus es obligatorio.</div>
                </div>
            </div>

            <div class="d-flex justify-content-end gap-2 mt-4">
                <a class="btn btn-outline-secondary btn-erp" href="ProductosController">Cancelar</a>
                <button id="guardar" name="guardar" type="submit" class="btn btn-primary btn-erp">Guardar</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/jspf/layout-bottom.jspf" />
