document.querySelectorAll('.cantidad-input').forEach(function(input) {
    var valorMinimo = parseInt(input.getAttribute('data-valor-inicial'), 10) || 0;
    input.addEventListener('change', function() {
        var valorActual = parseInt(this.value, 10);
        if (isNaN(valorActual) || valorActual < 0 || valorActual > valorMinimo) {
            UxAlert.show('uxAlert', 'Retiro inválido. El monto máximo es el valor actual (' + valorMinimo + ').');
            this.value = 0;
        }
    });
    input.addEventListener('input', function() {
        var valorActual = parseInt(this.value, 10);
        if (!isNaN(valorActual) && (valorActual < 0 || valorActual > valorMinimo)) {
            UxAlert.show('uxAlert', 'Retiro inválido. El monto máximo es el valor actual (' + valorMinimo + ').');
            this.value = 0;
        }
    });
});
