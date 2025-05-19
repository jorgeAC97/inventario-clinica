package com.clinica.data;

public class ProductoFarmacia {
    private String codigo;
    private String nombre;
    private String dimension;
    private int unidades;
    private String viaAdmin;

    public ProductoFarmacia(String codigo, String nombre, String dimension, int unidades, String viaAdmin) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.dimension = dimension;
        this.unidades = unidades;
        this.viaAdmin = viaAdmin;
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

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public int getUnidades() {
        return unidades;
    }

    public void setUnidades(int unidades) {
        this.unidades = unidades;
    }

    public String getViaAdmin() {
        return viaAdmin;
    }

    public void setViaAdmin(String viaAdmin) {
        this.viaAdmin = viaAdmin;
    }
} 