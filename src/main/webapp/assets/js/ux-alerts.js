(function () {
    'use strict';

    var timeouts = {};

    function show(elementId, message, duration) {
        var box = document.getElementById(elementId);
        if (!box) return;

        box.textContent = message;
        box.classList.remove('d-none');
        box.scrollIntoView({ behavior: 'smooth', block: 'nearest' });

        if (timeouts[elementId]) {
            clearTimeout(timeouts[elementId]);
        }
        timeouts[elementId] = setTimeout(function () {
            box.classList.add('d-none');
        }, duration || 2000);
    }

    window.UxAlert = { show: show };
})();
