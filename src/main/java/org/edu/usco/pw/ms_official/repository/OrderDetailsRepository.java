package org.edu.usco.pw.ms_official.repository;

import org.edu.usco.pw.ms_official.model.OrderDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetailsEntity, Long> {
    // Puedes agregar métodos específicos si es necesario
}
