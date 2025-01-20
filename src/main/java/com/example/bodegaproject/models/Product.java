package com.example.bodegaproject.models;

import javafx.beans.property.*;

public class Product {

    private final StringProperty codigo;
    private final StringProperty producto;
    private final IntegerProperty precio;
    private final IntegerProperty cantidad;
    private final StringProperty pesoVolumen; // Cambiado a StringProperty

    // Constructor
    public Product(String codigo, String producto, int precio, int cantidad, String pesoVolumen) {
        this.codigo = new SimpleStringProperty(codigo);
        this.producto = new SimpleStringProperty(producto);
        this.precio = new SimpleIntegerProperty(precio);
        this.cantidad = new SimpleIntegerProperty(cantidad);
        this.pesoVolumen = new SimpleStringProperty(pesoVolumen); // Inicializado con la unidad
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

    // Métodos Getter y Setter para 'cantidad'
    public int getCantidad() {
        return cantidad.get();
    }

    public IntegerProperty cantidadProperty() {
        return cantidad;
    }

    // Métodos Getter y Setter para 'pesoVolumen'
    public String getPesoVolumen() {
        return pesoVolumen.get();
    }

    public StringProperty pesoProperty() {
        return pesoVolumen;
    }

    @Override
    public String toString() {
        return "Product{" +
                "codigo='" + getCodigo() + '\'' +
                ", producto='" + getProducto() + '\'' +
                ", precio=" + getPrecio() +
                ", cantidad=" + getCantidad() +
                ", peso_volumen='" + getPesoVolumen() + '\'' +
                '}';
    }
}
