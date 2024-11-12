package org.edu.usco.pw.ms_official.model;
import lombok.Data;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_cc", nullable = false)
    private UserEntity user; // Relación con UserEntity

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    private String name;

    private String email;

    private String phone;

    @Column(name = "shipping_address")
    private String shippingAddress;

    private String status ="PENDING";

    private int total;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetailsEntity> orderDetails;

    public Long getUserCc() {
        return user != null ? user.getCc() : null; // Asegúrate de que 'getCc()' esté definido en la entidad User
    }
}
