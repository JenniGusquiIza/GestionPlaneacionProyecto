package com.system.models;

import com.google.firebase.database.Exclude;

import java.util.List;

public class Usuario {
    @Exclude
    private String codigo;
    private String password;
    private String correo;
    private String estado;
    private boolean actualizacion;
    private List<String> roles;

    public Usuario() {
    }


    public Usuario(String codigo, String password, String correo, String estado, boolean actualizacion, List<String> roles) {
        this.codigo = codigo;
        this.password = password;
        this.correo = correo;
        this.estado = estado;
        this.actualizacion = actualizacion;
        this.roles = roles;
    }

    @Exclude
    public String getCodigo() {
        return codigo;
    }

    @Exclude
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isActualizacion() {
        return actualizacion;
    }

    public void setActualizacion(boolean actualizacion) {
        this.actualizacion = actualizacion;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
