document.addEventListener('DOMContentLoaded', function () {
    const perfil = document.getElementById('perfil');
    const perfilDropdown = document.getElementById('perfilDropdown');

    perfil.addEventListener('click', function (event) {
        perfilDropdown.classList.toggle('active');
        event.stopPropagation(); // Evita que el evento se propague y cierre el menú
    });

    document.addEventListener('click', function (event) {
        if (!perfil.contains(event.target) && !perfilDropdown.contains(event.target)) {
            perfilDropdown.classList.remove('active');
        }
    });

    perfilDropdown.addEventListener('click', function (event) {
        event.stopPropagation(); // Permite la interacción con las opciones del menú sin cerrarlo
    });

    const dropdownLinks = perfilDropdown.querySelectorAll('.perfil-link');
    dropdownLinks.forEach(link => {
        link.addEventListener('click', function (event) {
            event.stopPropagation(); // Permite la navegación sin cerrar el desplegable
            window.location.href = event.target.href; // Redirige al enlace
        });
    });
});

