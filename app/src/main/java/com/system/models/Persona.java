package com.system.models;

import android.util.Log;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Persona {
    private String codigo;
    private String nombres;
    private String apellidos;
    private boolean estado;
    private String fecha_nac;
    private String tipo_identidad;
    private String cedula;
    private String correo;
    private String telefono;
    private boolean cliente;
    private boolean trabajador;

    /*@Exclude
    private boolean select;*/

    public Persona() {
    }


    public Persona(String nombres) {
        this.nombres = nombres;
    }

    public Persona(String codigo, String nombres, String apellidos, boolean estado, String fecha_nac,String tipo_identidad, String cedula, String correo, String telefono, boolean cliente, boolean trabajador) {
        this.codigo = codigo;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.estado = estado;
        this.fecha_nac = fecha_nac;
        this.tipo_identidad=tipo_identidad;
        this.cedula = cedula;
        this.correo = correo;
        this.telefono = telefono;
        this.cliente = cliente;
        this.trabajador = trabajador;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getFecha_nac() {
        return fecha_nac;
    }

    public void setFecha_nac(String fecha_nac) {
        this.fecha_nac = fecha_nac;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public boolean isCliente() {
        return cliente;
    }

    public void setCliente(boolean cliente) {
        this.cliente = cliente;
    }

    public boolean isTrabajador() {
        return trabajador;
    }

    public void setTrabajador(boolean trabajador) {
        this.trabajador = trabajador;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTipo_identidad() {
        return tipo_identidad;
    }

    public void setTipo_identidad(String tipo_identidad) {
        this.tipo_identidad = tipo_identidad;
    }

    @Override
    public String toString() {
        return nombres+" "+apellidos;
    }
}
