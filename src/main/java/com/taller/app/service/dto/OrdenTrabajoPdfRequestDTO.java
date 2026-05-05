package com.taller.app.service.dto;

import java.util.List;

/**
 * DTO con los datos adicionales que el técnico llena antes de imprimir la orden.
 */
public class OrdenTrabajoPdfRequestDTO {

    // ── Trabajo a realizar (checks E=Enderezado, P=Pintura) ──
    private List<String> trabajoEnderezado; // partes marcadas en E
    private List<String> trabajoPintura; // partes marcadas en P

    // ── Inventario ──
    private List<String> inventario; // ítems marcados

    // ── Nivel de combustible (0-8 para representar E a F) ──
    private Integer nivelCombustible;

    // ── Kilometraje ──
    private String kilometraje;

    // ── Ingreso en grúa ──
    private Boolean ingresoGrua;

    // ── Valores ──
    private String valorPactado;
    private String abono;
    private String saldo;

    // ── Trabajos extras ──
    private String trabajosExtras;

    // ── Daños preexistentes (texto libre por vista) ──
    private String danosDerecho;
    private String danosFrente;
    private String danosDetras;
    private String danosIzquierdo;

    // getters y setters

    public List<String> getTrabajoEnderezado() {
        return trabajoEnderezado;
    }

    public void setTrabajoEnderezado(List<String> trabajoEnderezado) {
        this.trabajoEnderezado = trabajoEnderezado;
    }

    public List<String> getTrabajoPintura() {
        return trabajoPintura;
    }

    public void setTrabajoPintura(List<String> trabajoPintura) {
        this.trabajoPintura = trabajoPintura;
    }

    public List<String> getInventario() {
        return inventario;
    }

    public void setInventario(List<String> inventario) {
        this.inventario = inventario;
    }

    public Integer getNivelCombustible() {
        return nivelCombustible;
    }

    public void setNivelCombustible(Integer nivelCombustible) {
        this.nivelCombustible = nivelCombustible;
    }

    public String getKilometraje() {
        return kilometraje;
    }

    public void setKilometraje(String kilometraje) {
        this.kilometraje = kilometraje;
    }

    public Boolean getIngresoGrua() {
        return ingresoGrua;
    }

    public void setIngresoGrua(Boolean ingresoGrua) {
        this.ingresoGrua = ingresoGrua;
    }

    public String getValorPactado() {
        return valorPactado;
    }

    public void setValorPactado(String valorPactado) {
        this.valorPactado = valorPactado;
    }

    public String getAbono() {
        return abono;
    }

    public void setAbono(String abono) {
        this.abono = abono;
    }

    public String getSaldo() {
        return saldo;
    }

    public void setSaldo(String saldo) {
        this.saldo = saldo;
    }

    public String getTrabajosExtras() {
        return trabajosExtras;
    }

    public void setTrabajosExtras(String trabajosExtras) {
        this.trabajosExtras = trabajosExtras;
    }

    public String getDanosDerecho() {
        return danosDerecho;
    }

    public void setDanosDerecho(String danosDerecho) {
        this.danosDerecho = danosDerecho;
    }

    public String getDanosFrente() {
        return danosFrente;
    }

    public void setDanosFrente(String danosFrente) {
        this.danosFrente = danosFrente;
    }

    public String getDanosDetras() {
        return danosDetras;
    }

    public void setDanosDetras(String danosDetras) {
        this.danosDetras = danosDetras;
    }

    public String getDanosIzquierdo() {
        return danosIzquierdo;
    }

    public void setDanosIzquierdo(String danosIzquierdo) {
        this.danosIzquierdo = danosIzquierdo;
    }
}
