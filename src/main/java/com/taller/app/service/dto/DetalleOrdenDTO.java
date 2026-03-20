package com.taller.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.taller.app.domain.DetalleOrden} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DetalleOrdenDTO implements Serializable {

    private Long id;

    @NotNull
    private String descripcion;

    @NotNull
    private Integer cantidad;

    @NotNull
    private BigDecimal precio;

    private OrdenTrabajoDTO ordenTrabajo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public OrdenTrabajoDTO getOrdenTrabajo() {
        return ordenTrabajo;
    }

    public void setOrdenTrabajo(OrdenTrabajoDTO ordenTrabajo) {
        this.ordenTrabajo = ordenTrabajo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DetalleOrdenDTO)) {
            return false;
        }

        DetalleOrdenDTO detalleOrdenDTO = (DetalleOrdenDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, detalleOrdenDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DetalleOrdenDTO{" +
            "id=" + getId() +
            ", descripcion='" + getDescripcion() + "'" +
            ", cantidad=" + getCantidad() +
            ", precio=" + getPrecio() +
            ", ordenTrabajo=" + getOrdenTrabajo() +
            "}";
    }
}
