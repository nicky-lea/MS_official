// loadMenu.js
document.addEventListener('DOMContentLoaded', function () {
    const menuContainer = document.getElementById('menu-container');
    fetch('menu_user.html')
        .then(response => response.text())
        .then(data => {
            menuContainer.innerHTML = data;

            // Re-aplicar el script de menú y perfil
            const script = document.createElement('script');
            script.src = '../../js/menu_toggle.js';
            script.defer = true;
            document.body.appendChild(script);

            const scriptPerfil = document.createElement('script');
            scriptPerfil.src = '../../js/perfil_dropdown.js';
            scriptPerfil.defer = true;
            document.body.appendChild(scriptPerfil);
        });
});
