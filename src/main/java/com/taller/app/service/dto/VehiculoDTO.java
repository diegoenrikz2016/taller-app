package com.taller.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.taller.app.domain.Vehiculo} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VehiculoDTO implements Serializable {

    private Long id;

    @NotNull
    private String placa;

    private String marca;

    private String modelo;

    private String color;

    private ClienteDTO cliente;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public ClienteDTO getCliente() {
        return cliente;
    }

    public void setCliente(ClienteDTO cliente) {
        this.cliente = cliente;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VehiculoDTO)) {
            return false;
        }

        VehiculoDTO vehiculoDTO = (VehiculoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, vehiculoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VehiculoDTO{" +
            "id=" + getId() +
            ", placa='" + getPlaca() + "'" +
            ", marca='" + getMarca() + "'" +
            ", modelo='" + getModelo() + "'" +
            ", color='" + getColor() + "'" +
            ", cliente=" + getCliente() +
            "}";
    }
}
