package com.system.models;

import com.google.firebase.database.Exclude;

public class Grupo {
    @Exclude
    private String codigo;
    private String descripcion;
    private String estado;

    public Grupo() {
    }

    public Grupo(String codigo, String descripcion, String estado) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    @Exclude
    public String getCodigo() {
        return codigo;
    }

    @Exclude
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return descripcion.toUpperCase();
    }
}

