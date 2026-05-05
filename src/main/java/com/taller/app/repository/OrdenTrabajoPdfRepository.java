package com.taller.app.repository;

import com.taller.app.domain.OrdenTrabajoPdf;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdenTrabajoPdfRepository extends JpaRepository<OrdenTrabajoPdf, Long> {
    // Proyección sin BLOB — solo metadatos
    interface PdfMeta {
        Long getId();
        String getNombreArchivo();
        Instant getFechaGeneracion();
        Long getOrdenTrabajoId();
    }

    @Query(
        "SELECT p.id AS id, p.nombreArchivo AS nombreArchivo, p.fechaGeneracion AS fechaGeneracion, p.ordenTrabajo.id AS ordenTrabajoId " +
            "FROM OrdenTrabajoPdf p ORDER BY p.fechaGeneracion DESC"
    )
    List<PdfMeta> findAllMeta();

    @Query("SELECT p FROM OrdenTrabajoPdf p WHERE p.ordenTrabajo.id = :ordenId ORDER BY p.fechaGeneracion DESC")
    List<OrdenTrabajoPdf> findByOrdenTrabajoId(Long ordenId);
}
