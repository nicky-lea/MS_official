package org.edu.usco.pw.ms_official.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Entity
@Table(name = "order_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa los detalles de una orden de compra (productos y cantidades)")
public class OrderDetailsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Schema(description = "Identificador único de los detalles de la orden", example = "1")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @Schema(description = "Orden a la que pertenecen los detalles", example = "1")
    private OrderEntity order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @Schema(description = "Producto asociado con los detalles de la orden", example = "1")
    private ProductEntity product;

    @Column(name = "quantity", nullable = false)
    @Schema(description = "Cantidad del producto en la orden", example = "3")
    private Integer quantity;

    @Column(name = "unit_price", nullable = false)
    @Schema(description = "Precio unitario del producto en la orden", example = "15.99")
    private BigDecimal unitPrice;

    @Column(name = "details")
    @Schema(description = "Detalles adicionales sobre el producto en la orden", example = "Producto en buen estado")
    private String details;
}
