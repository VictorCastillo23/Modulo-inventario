<%--
    Generic error page (SEC-08).

    Intentionally NOT isErrorPage="true": this page must never gain access to
    the implicit `exception` object, so no stack trace / exception message /
    exception class name can ever be printed here, even by a future accidental
    edit. It shows a static, generic message only.

    Kept deliberately self-contained (no layout-top/bottom.jspf include, no
    session-scoped EL reads) so rendering this page cannot itself fail because
    of whatever broke upstream (e.g. a DB configuration failure from
    config.Conexion, a CSRF/validation rejection, or any other uncaught
    exception/error status).
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Error - Almacén</title>
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/assets/img/favicon.svg">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha256-PI8n5gCcz9cQqQXm3PEtDuPG8qx9oFsFctPg0S5zb8g=" crossorigin="anonymous">
  </head>
  <body>
    <div class="container py-5">
      <div class="row justify-content-center">
        <div class="col-md-6">
          <div class="card card-soft">
            <div class="card-body p-4 p-md-5 text-center">
              <h1 class="h4 mb-3">Ocurrió un error</h1>
              <p class="text-secondary mb-4">
                No pudimos completar tu solicitud. Intenta nuevamente o vuelve
                al inicio.
              </p>
              <a class="btn btn-primary btn-erp" href="${pageContext.request.contextPath}/ProductosController">Volver al inicio</a>
            </div>
          </div>
        </div>
      </div>
    </div>
  </body>
</html>
