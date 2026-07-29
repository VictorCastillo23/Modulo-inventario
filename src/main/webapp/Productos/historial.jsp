<%--
    Historial de movimientos (Entrada/Salida) de productos.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Historial" scope="request"/>
<%@ include file="/WEB-INF/jspf/layout-top.jspf" %>

<div class="d-flex flex-wrap align-items-center justify-content-between gap-2 mb-3">
    <div>
        <h1 class="h4 page-title mb-1">Historial de movimientos</h1>
        <div class="text-secondary small">Entradas y salidas registradas</div>
    </div>
    <div class="page-actions d-flex flex-wrap gap-2">
        <a class="btn btn-outline-secondary btn-sm btn-erp" href="ProductosController">Volver</a>
    </div>
</div>

<div class="card card-soft">
    <div class="card-header py-3">
        <form action="ProductosController" method="get" class="row g-2 align-items-end">
            <input type="hidden" name="accion" value="historial"/>
            <div class="col-12 col-md-4">
                <label for="tipo" class="form-label mb-1">Tipo de movimiento</label>
                <select name="tipo" id="tipo" class="form-select">
                    <option value="" ${empty tipoFiltro ? 'selected' : ''}>Todos</option>
                    <option value="Entrada" ${tipoFiltro == 'Entrada' ? 'selected' : ''}>Entrada</option>
                    <option value="Salida" ${tipoFiltro == 'Salida' ? 'selected' : ''}>Salida</option>
                </select>
                <div id="filtroEstado" class="visually-hidden" aria-live="polite"></div>
            </div>
            <div class="col-12 col-md-8 text-md-end">
                <div class="form-help">El filtro se aplica automáticamente al cambiar el valor.</div>
            </div>
        </form>
    </div>

    <div class="card-body">
        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead>
                    <tr>
                        <th>Fecha y hora</th>
                        <th>Movimiento</th>
                        <th>Producto</th>
                        <th class="text-end">Cantidad</th>
                        <th>Realizado por</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty listaHistorial}">
                            <tr>
                                <td colspan="5" class="text-center text-secondary py-4">
                                    No hay movimientos registrados.
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="r" items="${listaHistorial}">
                                <tr>
                                    <td><c:out value="${r.fechaFormateada}"/></td>
                                    <td>
                                        <span class="badge ${r.movimiento == 'Entrada' ? 'text-bg-success' : 'text-bg-primary'}">
                                            <c:out value="${r.movimiento}"/>
                                        </span>
                                    </td>
                                    <td class="fw-semibold"><c:out value="${r.nombreProducto}"/></td>
                                    <td class="text-end"><span class="badge text-bg-light"><c:out value="${r.cantidad}"/></span></td>
                                    <td><c:out value="${r.nombreUsuario}"/></td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script>
    document.getElementById('tipo').addEventListener('change', function () {
        var estado = document.getElementById('filtroEstado');
        if (estado) {
            estado.textContent = 'Filtrando resultados…';
        }
        this.disabled = true;
        this.form.submit();
    });
</script>

<jsp:include page="/WEB-INF/jspf/layout-bottom.jspf" />
