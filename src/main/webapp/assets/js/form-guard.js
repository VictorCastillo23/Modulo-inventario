(function () {
    'use strict';

    document.querySelectorAll('form[data-guard]').forEach(function (form) {
        form.addEventListener('submit', function (event) {
            if (!form.checkValidity()) {
                return;
            }

            var confirmMessage = form.getAttribute('data-confirm');
            if (confirmMessage && !window.confirm(confirmMessage)) {
                event.preventDefault();
                event.stopPropagation();
                return;
            }

            var submitBtn = form.querySelector('button[type="submit"]');
            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.textContent = 'Guardando...';
            }
        });
    });
})();
