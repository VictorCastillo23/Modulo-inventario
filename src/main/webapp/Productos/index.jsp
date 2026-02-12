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
        <c:if test="${permisos.ver_salida}">
            <a class="btn btn-outline-primary btn-sm btn-erp" href="ProductosController?accion=salida_productos">Salidas</a>
        </c:if>
        <c:if test="${permisos.ver_historico}">
            <a class="btn btn-outline-secondary btn-sm btn-erp" href="ProductosController?accion=historial">Histórico</a>
        </c:if>
    </div>
</div>

<c:if test="${sinPermiso}">
    <div class="alert alert-warning" role="alert">
        <strong>Atención.</strong> No tiene permiso para realizar esa acción.
    </div>
</c:if>

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
            <form action="ProductosController" method="post" class="needs-validation" novalidate>
                <input type="hidden" name="accion" value="guardarCambios"/>

                <div class="d-flex justify-content-end mb-3">
                    <button type="submit" class="btn btn-success btn-sm btn-erp">Guardar cambios</button>
                </div>

                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead>
                            <tr>
                                <th>Nombre</th>
                                <th class="text-end">Cantidad actual</th>
                                <c:if test="${permisos.aumentar_inventario}"><th style="width: 220px;">Cantidad a agregar</th></c:if>
                                <c:if test="${permisos.baja_reactivar_producto}"><th style="width: 220px;">Estatus</th></c:if>
                                </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="producto" items="${lista}">
                                <tr>
                            <input type="hidden" name="id[]" value="${producto.id}"/>
                            <input type="hidden" name="modificado[]" value="false" class="flag-modificado"/>

                            <td>
                                <div class="fw-semibold"><c:out value="${producto.nombre}"/></div>
                            </td>

                            <td class="text-end">
                                <span class="badge text-bg-light"><c:out value="${producto.cantidad}"/></span>
                            </td>

                            <c:if test="${permisos.aumentar_inventario}">
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
                                                <c:out value="${producto.estatus ? 'Activo' : 'Inactivo'}"/>
                                            </span>
                                        </span>

                                        <div class="form-check form-switch">
                                            <input class="form-check-input estatus-input"
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

        <div id="uxAlert" class="alert alert-warning d-none mt-3" role="alert"></div>
    </div>
</div>
<script>
    document.addEventListener("DOMContentLoaded", function () {
    
        function showUxAlert(message) {
            const box = document.getElementById('uxAlert');
            if (!box) return;
    
            box.textContent = message;
            box.classList.remove('d-none');
            box.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
        }
    
        function actualizarEstadoVisual(fila, checkbox) {
            const texto = fila.querySelector('.status-text');
            const punto = fila.querySelector('.status-dot');
    
            if (!texto || !punto) return;
    
            if (checkbox.checked) {
                texto.textContent = "Activo";
                texto.classList.remove("text-danger");
                texto.classList.add("text-success");
    
                punto.classList.remove("status-dot--off");
                punto.classList.add("status-dot--ok");
            } else {
                texto.textContent = "Inactivo";
                texto.classList.remove("text-success");
                texto.classList.add("text-danger");
    
                punto.classList.remove("status-dot--ok");
                punto.classList.add("status-dot--off");
            }
        }
    
        function evaluarFila(fila) {
    
            const cantidadInput = fila.querySelector('.cantidad-input');
            const estatusInput = fila.querySelector('.estatus-input');
            const estatusHidden = fila.querySelector('.estatus-hidden');
            const flag = fila.querySelector('.flag-modificado');
    
            if (!flag) return;
    
            let huboCambio = false;
    
            if (cantidadInput) {
                const valor = parseInt(cantidadInput.value, 10) || 0;
                if (valor > 0) {
                    huboCambio = true;
                }
            }
            if (estatusInput) {

                const valorInicial = String(estatusInput.dataset.valorInicial).trim().toLowerCase() === "true";

                const valorActual = estatusInput.checked;
    

                if (estatusHidden) {
                    estatusHidden.value = valorActual;
                }
    
                if (valorActual !== valorInicial) {
                    huboCambio = true;
                }
    
                actualizarEstadoVisual(fila, estatusInput);
            }
    
            flag.value = huboCambio ? "true" : "false";
            fila.classList.toggle("table-warning", huboCambio);
        }
    
        document.querySelectorAll('.cantidad-input, .estatus-input').forEach(function (input) {
    
            input.addEventListener('input', function () {
    
                const fila = this.closest('tr');
    
                if (this.classList.contains('cantidad-input')) {
                    if (this.value.startsWith('-')) {
                        showUxAlert('No puede reducir la cantidad. Solo se permite incrementar el valor actual.');
                        this.value = 0;
                    }
                }
    
                evaluarFila(fila);
            });
    
            input.addEventListener('change', function () {
    
                const fila = this.closest('tr');
    
                if (this.classList.contains('cantidad-input')) {
                    let valor = Number(this.value);
                    if (isNaN(valor) || valor < 0) {
                        showUxAlert('Cantidad inválida.');
                        this.value = 0;
                    }
                }
    
                evaluarFila(fila);
            });
        });
    
    });
    </script>
    
<jsp:include page="/WEB-INF/jspf/layout-bottom.jspf" />
