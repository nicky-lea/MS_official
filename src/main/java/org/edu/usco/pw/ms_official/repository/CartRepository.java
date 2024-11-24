package org.edu.usco.pw.ms_official.repository;

import org.edu.usco.pw.ms_official.model.CartEntity;
import org.edu.usco.pw.ms_official.model.ProductEntity;
import org.edu.usco.pw.ms_official.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar los carritos de compra de los usuarios.
 * Este repositorio proporciona métodos para acceder a los carritos de compra almacenados
 * en la base de datos.
 */
@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {

    /**
     * Encuentra todos los elementos del carrito de un usuario dado su número de cédula.
     *
     * @param userCc El número de cédula del usuario cuyo carrito se va a obtener.
     * @return Una lista de entidades de carrito relacionadas con el usuario.
     */
    List<CartEntity> findByUserCc(Long userCc);

    /**
     * Encuentra todos los elementos del carrito de un usuario dado el objeto de usuario.
     *
     * @param user El usuario cuyo carrito se va a obtener.
     * @return Una lista de entidades de carrito relacionadas con el usuario.
     */
    List<CartEntity> findByUser(UserEntity user);

    /**
     * Encuentra un elemento en el carrito de un usuario para un producto específico.
     *
     * @param user El usuario cuyo carrito se va a consultar.
     * @param product El producto en cuestión para el que se quiere verificar si está en el carrito.
     * @return Un {@link Optional} que contiene el carrito si existe o está vacío si no lo está.
     */
    Optional<CartEntity> findByUserAndProduct(UserEntity user, ProductEntity product);
}