<%--
    Document   : index
    Created on : 29 jul 2026
    Author     : Victor
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="pageTitle" value="Categorías" scope="request"/>
<%@ include file="/WEB-INF/jspf/layout-top.jspf" %>

<div class="d-flex flex-wrap align-items-center justify-content-between gap-2 mb-3">
    <div>
        <h1 class="h4 page-title mb-1">Categorías</h1>
        <div class="text-secondary small">Categorías de producto</div>
    </div>
    <div class="page-actions d-flex flex-wrap gap-2">
        <a class="btn btn-outline-secondary btn-sm btn-erp" href="ProductosController">Volver</a>
        <c:if test="${permisos.gestionar_categorias}">
            <a class="btn btn-primary btn-sm btn-erp" href="CategoriasController?accion=nuevo"><i class="bi bi-plus-circle me-1" aria-hidden="true"></i>Nueva categoría</a>
        </c:if>
    </div>
</div>

<div class="card card-soft">
    <div class="card-header py-3">
        <div class="fw-semibold">Listado</div>
    </div>
    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead>
                    <tr>
                        <th>Nombre</th>
                        <th>Descripción</th>
                        <th>Estatus</th>
                        <c:if test="${permisos.gestionar_categorias}"><th class="col-action">Acciones</th></c:if>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty lista}">
                            <tr>
                                <td colspan="4" class="text-center text-secondary py-4">No hay categorías registradas.</td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="categoria" items="${lista}">
                                <tr>
                                    <td class="fw-semibold"><c:out value="${categoria.nombre}"/></td>
                                    <td><c:out value="${categoria.descripcion}"/></td>
                                    <td>
                                        <span class="status-pill">
                                            <span class="status-dot ${categoria.estatus ? 'status-dot--ok' : 'status-dot--off'}"></span>
                                            <span class="status-text ${categoria.estatus ? 'text-success' : 'text-danger'}">
                                                <i class="bi ${categoria.estatus ? 'bi-check-circle-fill' : 'bi-x-circle-fill'} me-1" aria-hidden="true"></i><c:out value="${categoria.estatus ? 'Activa' : 'Inactiva'}"/>
                                            </span>
                                        </span>
                                    </td>
                                    <c:if test="${permisos.gestionar_categorias}">
                                        <td>
                                            <div class="d-flex flex-wrap gap-2">
                                                <a class="btn btn-outline-secondary btn-sm btn-erp" href="CategoriasController?accion=editar&id=${categoria.id}">Editar</a>
                                                <form action="CategoriasController" method="post" class="d-inline" data-guard data-confirm="¿Confirma ${categoria.estatus ? 'desactivar' : 'activar'} esta categoría?">
                                                    <input type="hidden" name="accion" value="cambiarEstatus"/>
                                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}"/>
                                                    <input type="hidden" name="id" value="${categoria.id}"/>
                                                    <input type="hidden" name="estatus" value="${!categoria.estatus}"/>
                                                    <button type="submit" class="btn btn-outline-danger btn-sm btn-erp"><c:out value="${categoria.estatus ? 'Desactivar' : 'Activar'}"/></button>
                                                </form>
                                            </div>
                                        </td>
                                    </c:if>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/jspf/layout-bottom.jspf" />
