package org.edu.usco.pw.ms_official.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Entity
@Table(name = "cart")
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Entidad que representa un carrito de compras")
public class CartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID del carrito de compras", example = "1")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_cc", nullable = false)
    @Schema(description = "Usuario asociado al carrito", example = "1234567890")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @Schema(description = "Producto agregado al carrito", example = "5")
    private ProductEntity product;

    @Schema(description = "Cantidad del producto en el carrito", example = "2")
    private Integer quantity;

    @Column(name = "added_date")
    @Schema(description = "Fecha en que el producto fue agregado al carrito", example = "2024-11-17T15:30:00")
    private LocalDateTime addedDate;

    @Column(name = "details", nullable = false)
    @Schema(description = "Detalles adicionales sobre el producto", example = "Producto con descuento")
    private String details = "Detalles";
}