package com.taller.app.service.dto;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class OrdenTrabajoPdfMetaDTO {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneId.of("UTC"));

    private Long id;
    private String nombreArchivo;
    private String fechaGeneracion;
    private Long ordenTrabajoId;

    public OrdenTrabajoPdfMetaDTO(Long id, String nombreArchivo, Instant fechaGeneracion, Long ordenTrabajoId) {
        this.id = id;
        this.nombreArchivo = nombreArchivo;
        this.fechaGeneracion = fechaGeneracion != null ? FMT.format(fechaGeneracion) : null;
        this.ordenTrabajoId = ordenTrabajoId;
    }

    public Long getId() {
        return id;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public String getFechaGeneracion() {
        return fechaGeneracion;
    }

    public Long getOrdenTrabajoId() {
        return ordenTrabajoId;
    }
}
