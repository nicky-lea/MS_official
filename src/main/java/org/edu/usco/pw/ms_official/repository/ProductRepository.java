package org.edu.usco.pw.ms_official.repository;

import org.edu.usco.pw.ms_official.model.ProductEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar los productos.
 * Este repositorio proporciona métodos para acceder y gestionar los productos almacenados
 * en la base de datos, incluida la búsqueda por nombre o descripción.
 */
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    /**
     * Busca productos cuyo nombre o descripción contengan el término de búsqueda dado.
     * La búsqueda no es sensible a mayúsculas o minúsculas.
     *
     * @param searchTerm El término de búsqueda que se debe buscar en el nombre o descripción de los productos.
     * @return Una lista de productos que coinciden con el término de búsqueda en su nombre o descripción.
     */
    @Query("SELECT p FROM ProductEntity p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<ProductEntity> searchByNameOrDescription(@Param("searchTerm") String searchTerm);

    /**
     * Busca un producto por su identificador único.
     *
     * @param id El identificador único del producto.
     * @return Un producto opcional que puede estar presente o no en la base de datos.
     */
    Optional<ProductEntity> findById(Long id);
}