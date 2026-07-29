document.addEventListener("DOMContentLoaded", function () {

    function actualizarEstadoVisual(fila, checkbox) {
        const texto = fila.querySelector('.status-text');
        const punto = fila.querySelector('.status-dot');

        if (!texto || !punto)
            return;

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

        if (!flag)
            return;

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

    document.querySelectorAll('.cantidad-input, .estatus-input').forEach(function (input, index) {
        input.addEventListener('input', function () {
            const fila = this.closest('tr');
            const productoId = fila.querySelector('input[name="id[]"]')?.value;
            if (this.classList.contains('cantidad-input')) {
                if (this.value.startsWith('-')) {
                    UxAlert.show('uxAlert', 'No puede reducir la cantidad. Solo se permite incrementar el valor actual.');
                    this.value = 0;
                }
            }
            evaluarFila(fila);
        });

        input.addEventListener('change', function () {

            const fila = this.closest('tr');
            const productoId = fila.querySelector('input[name="id[]"]')?.value;

            if (this.classList.contains('cantidad-input')) {

                let valor = Number(this.value);

                if (isNaN(valor) || valor < 0) {
                    UxAlert.show('uxAlert', 'Cantidad inválida.');
                    this.value = 0;
                }
            }

            evaluarFila(fila);
        });

    });

});
