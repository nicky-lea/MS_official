function toggleDropdown() {
    const dropdown = document.querySelector('.language-dropdown');
    const arrow = document.querySelector('.arrow');

    dropdown.classList.toggle('show');

    // Rotar la flecha cuando el menú está abierto
    if (dropdown.classList.contains('show')) {
        arrow.style.transform = 'rotate(-135deg)';
    } else {
        arrow.style.transform = 'rotate(45deg)';
    }
}

// Cerrar el dropdown cuando se hace clic fuera
document.addEventListener('click', function(event) {
    const selector = document.querySelector('.language-selector');
    if (!selector.contains(event.target)) {
        const dropdown = document.querySelector('.language-dropdown');
        const arrow = document.querySelector('.arrow');
        dropdown.classList.remove('show');
        arrow.style.transform = 'rotate(45deg)';
    }
});