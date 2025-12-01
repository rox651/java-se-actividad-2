package com.saborpaisa.crud.model;

import java.time.Instant;
import java.time.LocalDate;

public class Customer {
    private Integer id;
    private String nombreCompleto;
    private String documentoIdentidad;
    private String correo;
    private String telefono;
    private boolean esFrecuente;
    private LocalDate fechaInscripcion;
    private int puntosAcumulados;
    private Instant createdAt;

    public Customer() {
    }

    public Customer(Integer id,
                    String nombreCompleto,
                    String documentoIdentidad,
                    String correo,
                    String telefono,
                    boolean esFrecuente,
                    LocalDate fechaInscripcion,
                    int puntosAcumulados,
                    Instant createdAt) {
        this.id = id;
        this.nombreCompleto = nombreCompleto;
        this.documentoIdentidad = documentoIdentidad;
        this.correo = correo;
        this.telefono = telefono;
        this.esFrecuente = esFrecuente;
        this.fechaInscripcion = fechaInscripcion;
        this.puntosAcumulados = puntosAcumulados;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getDocumentoIdentidad() {
        return documentoIdentidad;
    }

    public void setDocumentoIdentidad(String documentoIdentidad) {
        this.documentoIdentidad = documentoIdentidad;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isEsFrecuente() {
        return esFrecuente;
    }

    public void setEsFrecuente(boolean esFrecuente) {
        this.esFrecuente = esFrecuente;
    }

    public LocalDate getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(LocalDate fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public int getPuntosAcumulados() {
        return puntosAcumulados;
    }

    public void setPuntosAcumulados(int puntosAcumulados) {
        this.puntosAcumulados = puntosAcumulados;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return nombreCompleto + " (" + id + ")";
    }
}

