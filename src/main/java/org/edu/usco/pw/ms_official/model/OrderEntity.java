package org.edu.usco.pw.ms_official.model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa una orden de compra en el sistema")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @Schema(description = "Identificador único de la orden", example = "1")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_cc", nullable = false)
    @Schema(description = "Usuario que realizó la orden", example = "123456789")
    private UserEntity user; // Relación con UserEntity

    @Column(name = "order_date")
    @Schema(description = "Fecha y hora en que se realizó la orden", example = "2024-11-17T14:30:00")
    private LocalDateTime orderDate;

    @Column(name = "name", nullable = false, length = 100)
    @Schema(description = "Nombre del cliente que realizó la orden", example = "Juan Pérez")
    private String name;

    @Column(name = "email", nullable = false, length = 100)
    @Schema(description = "Correo electrónico del cliente", example = "juan.perez@example.com")
    private String email;

    @Column(name = "phone", length = 20)
    @Schema(description = "Teléfono del cliente", example = "+57 3001234567")
    private String phone;

    @Column(name = "shipping_address")
    @Schema(description = "Dirección de envío de la orden", example = "Calle 123, Bogotá, Colombia")
    private String shippingAddress;

    @Column(name = "status", nullable = false)
    @Schema(description = "Estado de la orden", example = "PENDING", allowableValues = {"PENDING", "SHIPPED", "DELIVERED", "CANCELLED"})
    private String status = "PENDING";

    @Column(name = "total", nullable = false)
    @Schema(description = "Total de la orden", example = "1500")
    private int total;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    @Schema(description = "Lista de detalles de la orden, que incluyen productos y cantidades")
    private List<OrderDetailsEntity> orderDetails;

    @Schema(description = "Obtiene el número de cédula del usuario asociado con la orden", example = "123456789")
    public Long getUserCc() {
        return user != null ? user.getCc() : null;
    }
}
