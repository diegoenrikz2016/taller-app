package com.taller.app.repository;

import com.taller.app.domain.OrdenTrabajo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OrdenTrabajo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OrdenTrabajoRepository extends JpaRepository<OrdenTrabajo, Long> {
    List<OrdenTrabajo> findByObservacionesContainingIgnoreCaseOrMecanicoContainingIgnoreCase(String observaciones, String mecanico);

    @Query(
        """
            select o from OrdenTrabajo o
            left join fetch o.vehiculo v
            left join fetch v.cliente
            left join fetch o.detalleses
            where o.id = :id
        """
    )
    Optional<OrdenTrabajo> findByIdWithRelationships(@Param("id") Long id);
}
