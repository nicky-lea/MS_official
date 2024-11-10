package org.edu.usco.pw.ms_official.repository;

import org.edu.usco.pw.ms_official.model.CartEntity;
import org.edu.usco.pw.ms_official.model.ProductEntity;
import org.edu.usco.pw.ms_official.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {
    List<CartEntity> findByUserCc(Long userCc);
    List<CartEntity> findByUser(UserEntity user);
    Optional<CartEntity> findByUserAndProduct(UserEntity user, ProductEntity product);
    Optional<CartEntity> findByProductId(Integer productId);
    @Modifying
    @Query("UPDATE CartEntity c SET c.quantity = :quantity WHERE c.user.cc = :userCc AND c.product.id = :productId")
    void updateQuantityByUserAndProduct(@Param("userCc") Long userCc, @Param("productId") Long productId, @Param("quantity") int quantity);
}