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

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private ProductRepository productRepository;
    private final String UPLOAD_DIR = "src/main/resources/static/img/";

    public List<ProductEntity> getAllProducts() {
        logger.info("Obteniendo todos los usuarios");
        return productRepository.findAll();
    }

    public ProductEntity getProductById(Long id) {
        Optional<ProductEntity> productOptional = productRepository.findById(id);
        return productOptional.orElse(null); // Retorna el producto o null si no se encuentra
    }

    public ProductEntity save(ProductEntity product) {
        return productRepository.save(product);
    }
    public ProductEntity updateProduct(Long id, ProductEntity product, MultipartFile file) throws IOException {

        // Obtener el producto existente
        ProductEntity existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));

        if (file != null && !file.isEmpty()) {

            // Guardar la nueva imagen y actualizar la ruta
            String filename = StringUtils.cleanPath(file.getOriginalFilename());
            Path imagePath = Paths.get(UPLOAD_DIR + filename);

            // Asegúrate de que el directorio de carga exista
            Files.copy(file.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
            product.setImageUrl("/img/" + filename); // Establecer la nueva ruta de la imagen
        } else {
            // Si no se proporciona una nueva imagen, conservar la existente
            product.setImageUrl(existingProduct.getImageUrl());
        }

        // Actualizar otros campos del producto
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setStock(product.getStock());
        return productRepository.save(existingProduct); // Guardar los cambios
    }

    public void deleteProduct(Long id) {
        ProductEntity product = getProductById(id);
        productRepository.delete(product);
    }

    public List<ProductEntity> getAllProductsSorted(String sortBy, String order) {
        Sort sort = order.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return productRepository.findAll(sort);
    }
    // Método para buscar productos por nombre o descripción con coincidencias parciales
    public List<ProductEntity> searchByNameOrDescription(String searchTerm) {
        if (searchTerm != null && !searchTerm.isEmpty()) {
            return productRepository.searchByNameOrDescription(searchTerm);
        } else {
            return productRepository.findAll();
        }
    }

    // Método para buscar un producto por ID
    public List<ProductEntity> getProductoById(Long id) {
        return productRepository.findById(id).map(List::of).orElse(List.of());
    }

}
