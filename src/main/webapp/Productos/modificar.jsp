<%-- 
    Document   : modificar
    Created on : 9 feb 2026, 7:32:42 p.m.
    Author     : Victor
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Salida de productos" scope="request"/>
<%@ include file="/WEB-INF/jspf/layout-top.jspf" %>

<div class="d-flex flex-wrap align-items-center justify-content-between gap-2 mb-3">
    <div>
        <h1 class="h4 page-title mb-1">Salida de productos</h1>
        <div class="text-secondary small">Registro de retiros</div>
    </div>
    <div class="page-actions d-flex flex-wrap gap-2">
        <a class="btn btn-outline-secondary btn-sm btn-erp" href="ProductosController">Volver</a>
        <c:if test="${permisos.ver_historico}">
            <a class="btn btn-outline-secondary btn-sm btn-erp" href="ProductosController?accion=historial">Histórico</a>
        </c:if>
    </div>
</div>

<div class="card card-soft">
    <div class="card-header py-3">
        <div class="fw-semibold">Retirar inventario</div>
    </div>
    <div class="card-body">

        <c:if test="${permisos.sacar_inventario}">
            <form action="ProductosController" method="post" class="needs-validation" novalidate>
                <input type="hidden" name="accion" value="guardarSalidas"/>

                <div class="d-flex justify-content-end mb-3">
                    <button type="submit" class="btn btn-success btn-sm btn-erp">Guardar cambios</button>
                </div>

                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead>
                            <tr>
                                <th>Nombre</th>
                                <th class="text-end">Cantidad actual</th>
                                <th style="width: 240px;">Cantidad a retirar</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="producto" items="${lista}">
                                <tr>
                                    <input type="hidden" name="id[]" value="${producto.id}"/>
                                    <td class="fw-semibold"><c:out value="${producto.nombre}"/></td>
                                    <td class="text-end"><span class="badge text-bg-light"><c:out value="${producto.cantidad}"/></span></td>
                                    <td>
                                        <input
                                            type="number"
                                            name="cantidad[]"
                                            class="form-control cantidad-input"
                                            min="0"
                                            value="0"
                                            data-valor-inicial="${producto.cantidad}"
                                            required
                                        />
                                        <div class="invalid-feedback">Ingresa un retiro válido.</div>
                                        <div class="form-help mt-1">Máximo: <c:out value="${producto.cantidad}"/></div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </form>
        </c:if>

        <c:if test="${!permisos.sacar_inventario}">
            <div class="alert alert-warning" role="alert">
                <strong>Atención.</strong> No tiene permiso para sacar inventario.
            </div>
        </c:if>

        <div id="uxAlert" class="alert alert-warning d-none mt-3" role="alert"></div>
    </div>
</div>

<script>
    function showUxAlert(message) {
        var box = document.getElementById('uxAlert');
        if (!box) return;
        box.textContent = message;
        box.classList.remove('d-none');
        box.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    }

    document.querySelectorAll('.cantidad-input').forEach(function(input) {
        var valorMinimo = parseInt(input.getAttribute('data-valor-inicial'), 10) || 0;
        input.addEventListener('change', function() {
            var valorActual = parseInt(this.value, 10);
            if (isNaN(valorActual) || valorActual < 0 || valorActual > valorMinimo) {
                showUxAlert('Retiro inválido. El monto máximo es el valor actual (' + valorMinimo + ').');
                this.value = 0;
            }
        });
        input.addEventListener('input', function() {
            var valorActual = parseInt(this.value, 10);
            if (!isNaN(valorActual) && (valorActual < 0 || valorActual > valorMinimo)) {
                showUxAlert('Retiro inválido. El monto máximo es el valor actual (' + valorMinimo + ').');
                this.value = 0;
            }
        });
    });
</script>

<jsp:include page="/WEB-INF/jspf/layout-bottom.jspf" />