package org.edu.usco.pw.ms_official.service;

import org.edu.usco.pw.ms_official.excepciones.ResourceNotFoundException;
import org.edu.usco.pw.ms_official.model.ProductEntity;
import org.edu.usco.pw.ms_official.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Pageable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de gestionar las operaciones relacionadas con los productos en el sistema.
 * Proporciona métodos para crear, actualizar, eliminar, buscar productos y ordenar los productos.
 */
@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private ProductRepository productRepository;

    private final String UPLOAD_DIR = "src/main/resources/static/img/";

    /**
     * Obtiene un producto por su ID.
     *
     * @param id el ID del producto a buscar.
     * @return el producto correspondiente al ID, o null si no se encuentra.
     */
    public ProductEntity getProductById(Long id) {
        Optional<ProductEntity> productOptional = productRepository.findById(id);
        return productOptional.orElse(null);
    }

    /**
     * Guarda un nuevo producto o actualiza uno existente.
     *
     * @param product el producto a guardar.
     * @return el producto guardado.
     */
    public ProductEntity save(ProductEntity product) {
        return productRepository.save(product);
    }

    /**
     * Actualiza un producto existente por su ID.
     * Si se proporciona una nueva imagen, se guarda en el directorio correspondiente.
     *
     * @param id el ID del producto a actualizar.
     * @param product el producto con los nuevos datos.
     * @param file el archivo de imagen que se desea asociar al producto (puede ser nulo).
     * @return el producto actualizado.
     * @throws IOException si ocurre un error al guardar la imagen.
     */
    public ProductEntity updateProduct(Long id, ProductEntity product, MultipartFile file) throws IOException {
        ProductEntity existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));

        if (file != null && !file.isEmpty()) {
            String filename = StringUtils.cleanPath(file.getOriginalFilename());
            Path imagePath = Paths.get(UPLOAD_DIR + filename);
            Files.copy(file.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
            product.setImageUrl("/img/" + filename);
        } else {
            product.setImageUrl(existingProduct.getImageUrl());
        }

        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setStock(product.getStock());

        return productRepository.save(existingProduct);
    }

    /**
     * Elimina un producto por su ID.
     *
     * @param id el ID del producto a eliminar.
     */
    public void deleteProduct(Long id) {
        ProductEntity product = getProductById(id);
        productRepository.delete(product);
    }

    /**
     * Obtiene todos los productos, ordenados por un campo y en un orden específico.
     *
     * @param sortBy el campo por el que ordenar los productos.
     * @param order el orden de la clasificación ("asc" para ascendente, "desc" para descendente).
     * @return una lista de productos ordenada según los parámetros proporcionados.
     */
    public List<ProductEntity> getAllProductsSorted(String sortBy, String order) {
        Sort sort = order.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return productRepository.findAll(sort);
    }

    /**
     * Busca productos por su nombre o descripción.
     *
     * @param searchTerm el término de búsqueda utilizado para buscar productos en el nombre o descripción.
     * @return una lista de productos que coinciden con el término de búsqueda.
     */
    public List<ProductEntity> searchByNameOrDescription(String searchTerm) {
        if (searchTerm != null && !searchTerm.isEmpty()) {
            return productRepository.searchByNameOrDescription(searchTerm);
        } else {
            return productRepository.findAll();
        }
    }

    /**
     * Obtiene un producto por su ID en una lista, devolviendo una lista vacía si no se encuentra.
     *
     * @param id el ID del producto a buscar.
     * @return una lista con el producto si se encuentra, o una lista vacía si no se encuentra.
     */
    public List<ProductEntity> getProductoById(Long id) {
        return productRepository.findById(id).map(List::of).orElse(List.of());
    }

}
