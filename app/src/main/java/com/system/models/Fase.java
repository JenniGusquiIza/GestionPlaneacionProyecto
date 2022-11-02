package com.system.models;

import com.google.firebase.database.Exclude;

import java.util.List;
import java.util.Map;

public class Fase{

    private String codigo;
    private String descripcion;
    private boolean estado;
    private Map<String,Actividad> actividades;

    public Fase() {
    }

    public Fase(String codigo, String descripcion, boolean estado) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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

    public Map<String, Actividad> getActividades() {
        return actividades;
    }

    public void setActividades(Map<String, Actividad> actividades) {
        this.actividades = actividades;
    }

    @Override
    public String toString() {
        return descripcion ;
    }
}
