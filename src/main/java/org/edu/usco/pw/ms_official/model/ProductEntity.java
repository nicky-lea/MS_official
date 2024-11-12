package org.edu.usco.pw.ms_official.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private BigDecimal price;
    private int stock;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creationDate;

    public String getFormattedPrice() {
        return price.stripTrailingZeros().toPlainString();
    }

}
