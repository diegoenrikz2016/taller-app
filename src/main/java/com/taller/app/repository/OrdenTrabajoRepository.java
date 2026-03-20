package com.taller.app.repository;

import com.taller.app.domain.OrdenTrabajo;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OrdenTrabajo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, Long> {}
