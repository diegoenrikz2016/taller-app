package com.taller.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.taller.app.domain.enumeration.EstadoOrden;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A OrdenTrabajo.
 */
@Entity
@Table(name = "orden_trabajo")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class OrdenTrabajo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoOrden estado;

    @Column(name = "observaciones")
    private String observaciones;

    @Column(name = "mecanico")
    private String mecanico;

    @Column(name = "mano_obra", precision = 21, scale = 2)
    private BigDecimal manoObra;

    @Column(name = "subtotal", precision = 21, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "total", precision = 21, scale = 2)
    private BigDecimal total;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ordenTrabajo")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "ordenTrabajo" }, allowSetters = true)
    private Set<DetalleOrden> detalleses = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "cliente" }, allowSetters = true)
    private Vehiculo vehiculo;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public OrdenTrabajo id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return this.fecha;
    }

    public OrdenTrabajo fecha(LocalDate fecha) {
        this.setFecha(fecha);
        return this;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public EstadoOrden getEstado() {
        return this.estado;
    }

    public OrdenTrabajo estado(EstadoOrden estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(EstadoOrden estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return this.observaciones;
    }

    public OrdenTrabajo observaciones(String observaciones) {
        this.setObservaciones(observaciones);
        return this;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getMecanico() {
        return this.mecanico;
    }

    public OrdenTrabajo mecanico(String mecanico) {
        this.setMecanico(mecanico);
        return this;
    }

    public void setMecanico(String mecanico) {
        this.mecanico = mecanico;
    }

    public BigDecimal getManoObra() {
        return this.manoObra;
    }

    public OrdenTrabajo manoObra(BigDecimal manoObra) {
        this.setManoObra(manoObra);
        return this;
    }

    public void setManoObra(BigDecimal manoObra) {
        this.manoObra = manoObra;
    }

    public BigDecimal getSubtotal() {
        return this.subtotal;
    }

    public OrdenTrabajo subtotal(BigDecimal subtotal) {
        this.setSubtotal(subtotal);
        return this;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTotal() {
        return this.total;
    }

    public OrdenTrabajo total(BigDecimal total) {
        this.setTotal(total);
        return this;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Set<DetalleOrden> getDetalleses() {
        return this.detalleses;
    }

    public void setDetalleses(Set<DetalleOrden> detalleOrdens) {
        if (this.detalleses != null) {
            this.detalleses.forEach(i -> i.setOrdenTrabajo(null));
        }
        if (detalleOrdens != null) {
            detalleOrdens.forEach(i -> i.setOrdenTrabajo(this));
        }
        this.detalleses = detalleOrdens;
    }

    public OrdenTrabajo detalleses(Set<DetalleOrden> detalleOrdens) {
        this.setDetalleses(detalleOrdens);
        return this;
    }

    public OrdenTrabajo addDetalles(DetalleOrden detalleOrden) {
        this.detalleses.add(detalleOrden);
        detalleOrden.setOrdenTrabajo(this);
        return this;
    }

    public OrdenTrabajo removeDetalles(DetalleOrden detalleOrden) {
        this.detalleses.remove(detalleOrden);
        detalleOrden.setOrdenTrabajo(null);
        return this;
    }

    public Vehiculo getVehiculo() {
        return this.vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
    }

    public OrdenTrabajo vehiculo(Vehiculo vehiculo) {
        this.setVehiculo(vehiculo);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrdenTrabajo)) {
            return false;
        }
        return getId() != null && getId().equals(((OrdenTrabajo) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "OrdenTrabajo{" +
            "id=" + getId() +
            ", fecha='" + getFecha() + "'" +
            ", estado='" + getEstado() + "'" +
            ", observaciones='" + getObservaciones() + "'" +
            ", mecanico='" + getMecanico() + "'" +
            ", manoObra=" + getManoObra() +
            ", subtotal=" + getSubtotal() +
            ", total=" + getTotal() +
            "}";
    }
}
