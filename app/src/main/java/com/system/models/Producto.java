package com.system.models;

import com.google.firebase.database.Exclude;

public class Producto {
    private String codigo;
    private String nombre;
    private String grupo;
    private String estado;
    private int total;
    private int disponible;
    private int ocupado;
    @Exclude
    private int oldOcupado;


    public Producto() {
    }

    public Producto(String codigo, String nombre, String grupo, String estado, int total, int disponible, int ocupado) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.grupo = grupo;
        this.estado = estado;
        this.total = total;
        this.disponible = disponible;
        this.ocupado = ocupado;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getDisponible() {
        return disponible;
    }

    public void setDisponible(int disponible) {
        this.disponible = disponible;
    }

    public int getOcupado() {
        return ocupado;
    }

    public void setOcupado(int ocupado) {
        this.ocupado = ocupado;
    }

    @Override
    public String toString() {
        return  nombre.toUpperCase();
    }


    @Exclude
    public int getOldOcupado() {
        return oldOcupado;
    }

    @Exclude
    public void setOldOcupado(int oldOcupado) {
        this.oldOcupado = oldOcupado;
    }
}
