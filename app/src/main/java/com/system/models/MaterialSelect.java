package com.system.models;

public class MaterialSelect {
    private String material;
    private int ocupado;

    public MaterialSelect() {
    }

    public MaterialSelect(String material, int ocupado) {
        this.material = material;
        this.ocupado = ocupado;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public int getOcupado() {
        return ocupado;
    }

    public void setOcupado(int ocupado) {
        this.ocupado = ocupado;
    }
}
