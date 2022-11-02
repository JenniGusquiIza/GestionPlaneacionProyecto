package com.system.models;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.Map;

public class Proyecto implements Parcelable {
    private String codigo;
    private String fechaRegistro;
    private String nombre;
    private String numero;
    private Persona cliente;
    private double monto;
    private String fechaInicio;
    private String fechaFin;
    private String duracion;
    private String archivo;
    private String ubicacion;
    private String descripcion;
    private boolean estado;

    private Map<String,Fase> fases;
    private Map<String,Persona> trabajadores;
    private Map<String,MaterialSelect> materiales;
    private Map<String,String> observaciones;
    public Proyecto() {
    }

    public Proyecto(String codigo, String fechaRegistro, String nombre, String numero, Persona cliente, double monto, String fechaInicio, String fechaFin, String duracion, String archivo, String ubicacion, String descripcion, boolean estado, Map<String, Fase> fases,Map<String, String> observaciones) {
        this.codigo = codigo;
        this.fechaRegistro = fechaRegistro;
        this.nombre = nombre;
        this.numero = numero;
        this.cliente = cliente;
        this.monto = monto;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.duracion = duracion;
        this.archivo = archivo;
        this.ubicacion = ubicacion;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fases = fases;
        this.observaciones=observaciones;
    }


    protected Proyecto(Parcel in) {
        codigo = in.readString();
        fechaRegistro = in.readString();
        nombre = in.readString();
        numero = in.readString();
        monto = in.readDouble();
        fechaInicio = in.readString();
        fechaFin = in.readString();
        duracion = in.readString();
        archivo = in.readString();
        ubicacion = in.readString();
        descripcion = in.readString();
        estado = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(codigo);
        dest.writeString(fechaRegistro);
        dest.writeString(nombre);
        dest.writeString(numero);
        dest.writeDouble(monto);
        dest.writeString(fechaInicio);
        dest.writeString(fechaFin);
        dest.writeString(duracion);
        dest.writeString(archivo);
        dest.writeString(ubicacion);
        dest.writeString(descripcion);
        dest.writeByte((byte) (estado ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Proyecto> CREATOR = new Creator<Proyecto>() {
        @Override
        public Proyecto createFromParcel(Parcel in) {
            return new Proyecto(in);
        }

        @Override
        public Proyecto[] newArray(int size) {
            return new Proyecto[size];
        }
    };

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Persona getCliente() {
        return cliente;
    }

    public void setCliente(Persona cliente) {
        this.cliente = cliente;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public String getArchivo() {
        return archivo;
    }

    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
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

    public Map<String, Fase> getFases() {
        return fases;
    }

    public void setFases(Map<String, Fase> fases) {
        this.fases = fases;
    }

    public Map<String, Persona> getTrabajadores() {
        return trabajadores;
    }

    public void setTrabajadores(Map<String, Persona> trabajadores) {
        this.trabajadores = trabajadores;
    }

    public Map<String, MaterialSelect> getMateriales() {
        return materiales;
    }

    public void setMateriales(Map<String, MaterialSelect> materiales) {
        this.materiales = materiales;
    }

    public Map<String, String> getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(Map<String, String> observaciones) {
        this.observaciones = observaciones;
    }

    @Override
    public String toString() {
        return nombre+" " +"\""+cliente+"\"";
    }


}
