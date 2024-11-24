package org.edu.usco.pw.ms_official.model;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa un producto en el sistema de inventario")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Schema(description = "Identificador único del producto", example = "1")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    @Schema(description = "Nombre del producto", example = "Laptop")
    private String name;

    @Column(name = "description", length = 500)
    @Schema(description = "Descripción del producto", example = "Laptop de última generación con 16GB de RAM y 512GB de SSD")
    private String description;

    @Column(name = "price", nullable = false)
    @Schema(description = "Precio del producto", example = "1200.50")
    private BigDecimal price;

    @Column(name = "stock", nullable = false)
    @Schema(description = "Cantidad disponible del producto en inventario", example = "150")
    private int stock;

    @Column(name = "image_url")
    @Schema(description = "URL de la imagen del producto", example = "https://example.com/images/laptop.jpg")
    private String imageUrl;

    @Column(name = "creation_date", updatable = false)
    @Schema(description = "Fecha y hora de creación del producto", example = "2024-11-01T12:30:00")
    private LocalDateTime creationDate;

    @Transient
    private boolean outOfStock;

    @Transient
    private String descriptionGeneral;

    @Transient
    private List<String> features;

    @Schema(description = "Obtiene el precio del producto formateado sin ceros a la derecha", example = "1200.5")
    public String getFormattedPrice() {
        return price.stripTrailingZeros().toPlainString();
    }


}
