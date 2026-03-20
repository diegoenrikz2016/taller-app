package com.taller.app.repository;

import com.taller.app.domain.DetalleOrden;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the DetalleOrden entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DetalleOrdenRepository extends JpaRepository<DetalleOrden, Long> {}
