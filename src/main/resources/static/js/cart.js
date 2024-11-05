let cart = [];

function addToCart(product) {
    // Verificar si el producto ya está en el carrito
    const existingProduct = cart.find(item => item.id === product.id);
    if (existingProduct) {
        existingProduct.quantity++;
    } else {
        product.quantity = 1;
        cart.push(product);
    }
    updateCartDisplay();
}

function updateCartDisplay() {
    const cartItemsContainer = document.getElementById('cartItems');
    cartItemsContainer.innerHTML = '';

    let total = 0;

    cart.forEach(item => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${item.name}</td>
            <td>${item.quantity}</td>
            <td>${(item.price * item.quantity).toFixed(2)}</td>
            <td>
                <button class="btn btn-danger" onclick="removeFromCart(${item.id})">Eliminar</button>
            </td>
        `;
        cartItemsContainer.appendChild(row);
        total += item.price * item.quantity;
    });

    document.getElementById('totalPrice').innerText = `$${total.toFixed(2)}`;
}

function removeFromCart(productId) {
    cart = cart.filter(item => item.id !== productId);
    updateCartDisplay();
}

// Muestra el modal
function showCart() {
    $('#cartModal').modal('show');
}

// Manejo del botón de finalizar compra
document.getElementById('checkoutButton').addEventListener('click', () => {
    // Implementar la lógica para finalizar la compra
    alert('Finalizando la compra...');
});
