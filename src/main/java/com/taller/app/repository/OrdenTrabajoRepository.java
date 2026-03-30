package com.taller.app.repository;

import com.taller.app.domain.OrdenTrabajo;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OrdenTrabajo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, Long> {
    List<OrdenTrabajo> findByObservacionesContainingIgnoreCaseOrMecanicoContainingIgnoreCase(String observaciones, String mecanico);
}
