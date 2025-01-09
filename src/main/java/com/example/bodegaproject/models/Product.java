package com.example.bodegaproject.models;

import javafx.beans.property.*;

public class Product {

    private final StringProperty codigo;
    private final StringProperty producto;
    private final IntegerProperty precio;

    // Constructor
    public Product(String codigo, String producto, int precio) {
        this.codigo = new SimpleStringProperty(codigo);
        this.producto = new SimpleStringProperty(producto);
        this.precio = new SimpleIntegerProperty(precio);
    }

    // Métodos Getter y Setter para 'codigo'
    public String getCodigo() {
        return codigo.get();
    }

    public StringProperty codigoProperty() {
        return codigo;
    }

    // Métodos Getter y Setter para 'nombre'
    public String getProducto() {
        return producto.get();
    }

    public StringProperty productoProperty() {
        return producto;
    }

    // Métodos Getter y Setter para 'precio'
    public int getPrecio() {
        return precio.get();
    }

    public void setPrecio(int precio) {
        this.precio.set(precio);
    }

    public IntegerProperty precioProperty() {
        return precio;
    }

    @Override
    public String toString() {
        return "Product{" +
                "codigo='" + getCodigo() + '\'' +
                ", nombre='" + getProducto() + '\'' +
                ", precio=" + getPrecio() +
                '}';
    }
}