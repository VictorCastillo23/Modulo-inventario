<%-- 
    Document   : index
    Created on : 9 feb 2026, 7:32:09 p.m.
    Author     : Victor
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Productos" scope="request"/>
<%@ include file="/WEB-INF/jspf/layout-top.jspf" %>

<div class="d-flex flex-wrap align-items-center justify-content-between gap-2 mb-3">
    <div>
        <h1 class="h4 page-title mb-1">Productos</h1>
        <div class="text-secondary small">Inventario y estatus</div>
    </div>
    <div class="page-actions d-flex flex-wrap gap-2">
        <c:if test="${permisos.agregar_productos}">
            <a class="btn btn-primary btn-sm btn-erp" href="ProductosController?accion=nuevo">Nuevo producto</a>
        </c:if>
    </div>
</div>

<c:if test="${sinPermiso}">
    <div class="alert alert-warning" role="alert">
        <strong>Atención.</strong> No tiene permiso para realizar esa acción.
    </div>
</c:if>
<div id="uxAlert" class="alert alert-danger d-none mb-3" role="alert"></div>

<div class="card card-soft">
    <div class="card-header py-3">
        <div class="d-flex flex-wrap align-items-center justify-content-between gap-2">
            <div class="fw-semibold">Listado</div>
            <c:if test="${permisos.aumentar_inventario || permisos.baja_reactivar_producto}">
                <span class="text-secondary small">Edita cantidades/estatus y guarda cambios</span>
            </c:if>
        </div>
    </div>
    <div class="card-body">

        <c:if test="${permisos.aumentar_inventario || permisos.baja_reactivar_producto}">
            <form action="ProductosController" method="post" class="needs-validation" novalidate data-guard data-confirm="¿Confirma guardar los cambios? Esta acción no se puede deshacer.">
                <input type="hidden" name="accion" value="guardarCambios"/>
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>

                <div class="d-flex justify-content-end mb-3">
                    <button type="submit" class="btn btn-success btn-sm btn-erp">Guardar cambios</button>
                </div>

                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead>
                            <tr>
                                <th>Nombre</th>
                                <th class="text-end">Cantidad actual</th>
                                <c:if test="${permisos.aumentar_inventario}"><th class="col-action">Cantidad a agregar</th></c:if>
                                <c:if test="${permisos.baja_reactivar_producto}"><th class="col-action">Estatus</th></c:if>
                                </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="producto" items="${lista}">
                                <tr>
                            <td>
                                <input type="hidden" name="id[]" value="${producto.id}"/>
                                <input type="hidden" name="modificado[]" value="false" class="flag-modificado"/>
                                <div class="fw-semibold"><c:out value="${producto.nombre}"/></div>
                            </td>

                            <td class="text-end">
                                <span class="badge text-bg-light"><c:out value="${producto.cantidad}"/></span>
                            </td>

                            <c:if test="${permisos.aumentar_inventario}">
                                <td>
                                    <label class="visually-hidden" for="cantidad-${producto.id}">Cantidad a agregar para <c:out value="${producto.nombre}"/></label>
                                    <input
                                        id="cantidad-${producto.id}"
                                        type="number"
                                        name="cantidad[]"
                                        class="form-control cantidad-input"
                                        min="0"
                                        value="0"
                                        data-valor-inicial="${producto.cantidad}"
                                        required
                                        />
                                    <div class="invalid-feedback">Ingresa una cantidad válida (>= 0).</div>
                                </td>
                            </c:if>
                            <c:if test="${!permisos.aumentar_inventario}">
                                <input type="hidden" name="cantidad[]" value="0"/>
                            </c:if>

                            <c:if test="${permisos.baja_reactivar_producto}">
                                <td>
                                    <div class="d-flex align-items-center justify-content-between gap-2">

                                        <span class="status-pill">
                                            <span class="status-dot ${producto.estatus ? 'status-dot--ok' : 'status-dot--off'} status-dot"></span>
                                            <span class="status-text ${producto.estatus ? 'text-success' : 'text-danger'}">
                                                <i class="bi ${producto.estatus ? 'bi-check-circle-fill' : 'bi-x-circle-fill'} me-1" aria-hidden="true"></i><c:out value="${producto.estatus ? 'Activo' : 'Inactivo'}"/>
                                            </span>
                                        </span>

                                        <div class="form-check form-switch">
                                            <label class="visually-hidden" for="estatus-${producto.id}">Cambiar estatus de <c:out value="${producto.nombre}"/></label>
                                            <input class="form-check-input estatus-input"
                                                   id="estatus-${producto.id}"
                                                   type="checkbox"
                                                   data-index="${producto.id}"
                                                   data-valor-inicial="${producto.estatus ? 'true' : 'false'}"
                                                   ${producto.estatus ? 'checked' : ''}>
                                            <input type="hidden" name="estatus[]" value="${producto.estatus}" class="estatus-hidden">

                                        </div>
                                    </div>
                                </td>                                                             
                            </c:if>
                            <c:if test="${!permisos.baja_reactivar_producto}">
                                <input type="hidden" name="estatus[]" value="${producto.estatus}"/>
                            </c:if>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </form>
        </c:if>

        <c:if test="${!permisos.aumentar_inventario && !permisos.baja_reactivar_producto}">
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead>
                        <tr>
                            <th>Nombre</th>
                            <th class="text-end">Cantidad actual</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="producto" items="${lista}">
                            <tr>
                                <td class="fw-semibold"><c:out value="${producto.nombre}"/></td>
                                <td class="text-end"><span class="badge text-bg-light"><c:out value="${producto.cantidad}"/></span></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:if>

    </div>
</div>
<script src="${pageContext.request.contextPath}/assets/js/productos-index.js"></script>

<jsp:include page="/WEB-INF/jspf/layout-bottom.jspf" />
