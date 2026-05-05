package com.taller.app.service.dto;

import com.taller.app.domain.enumeration.EstadoOrden;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A DTO for the {@link com.taller.app.domain.OrdenTrabajo} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrdenTrabajoDTO implements Serializable {

    private Long id;

    @NotNull
    private LocalDate fecha;

    @NotNull
    private EstadoOrden estado;

    private String observaciones;

    private String mecanico;

    private BigDecimal valorPactado;
    private BigDecimal abono;
    private BigDecimal saldo;
    private String trabajosExtras;

    private VehiculoDTO vehiculo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public EstadoOrden getEstado() {
        return estado;
    }

    public void setEstado(EstadoOrden estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getMecanico() {
        return mecanico;
    }

    public void setMecanico(String mecanico) {
        this.mecanico = mecanico;
    }

    public BigDecimal getValorPactado() {
        return valorPactado;
    }

    public void setValorPactado(BigDecimal valorPactado) {
        this.valorPactado = valorPactado;
    }

    public BigDecimal getAbono() {
        return abono;
    }

    public void setAbono(BigDecimal abono) {
        this.abono = abono;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public String getTrabajosExtras() {
        return trabajosExtras;
    }

    public void setTrabajosExtras(String trabajosExtras) {
        this.trabajosExtras = trabajosExtras;
    }

    public VehiculoDTO getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(VehiculoDTO vehiculo) {
        this.vehiculo = vehiculo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrdenTrabajoDTO)) {
            return false;
        }

        OrdenTrabajoDTO ordenTrabajoDTO = (OrdenTrabajoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ordenTrabajoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrdenTrabajoDTO{" +
            "id=" + getId() +
            ", fecha='" + getFecha() + "'" +
            ", estado='" + getEstado() + "'" +
            ", observaciones='" + getObservaciones() + "'" +
            ", mecanico='" + getMecanico() + "'" +
            ", valorPactado=" + getValorPactado() +
            ", abono=" + getAbono() +
            ", saldo=" + getSaldo() +
            ", vehiculo=" + getVehiculo() +
            "}";
    }
}
