package com.system.models;

import com.google.firebase.database.Exclude;

import java.util.Objects;

public class Actividad {
    private String codigo;
    private String fase;
    private String descripcion;
    private boolean estado;
    @Exclude
    public String idFase;
    public Actividad() {
    }

    public Actividad(String codigo, String fase, String descripcion, boolean estado) {
        this.codigo = codigo;
        this.fase = fase;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getFase() {
        return fase;
    }

    public void setFase(String fase) {
        this.fase = fase;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}

