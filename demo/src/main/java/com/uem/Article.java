package com.uem;

import com.fasterxml.jackson.annotation.JsonAlias;

public class Article {

    private String nombre;
    private int cantidad;
    private double precio;
    private double descuento;

    // Constructor vacio para Jackson
    public Article() {
    }

    // Constructor original
    public Article(String nombre, int cantidad, double precio, double descuento) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.descuento = descuento;
    }

    public String getNombre() {
        return nombre;
    }

    @JsonAlias("name")
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    @JsonAlias("quantity")
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    @JsonAlias("unitPrice")
    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getDescuento() {
        return descuento;
    }

    @JsonAlias("discount")
    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getGrossAmount() {
        Calculator calculator = new Calculator();
        return calculator.multiply(cantidad, precio);
    }

    public double getDiscountedAmount() {
        Calculator calculator = new Calculator();
        return calculator.discount(getGrossAmount(), descuento);
    }

    @Override
    public String toString() {
        return "Article{" +
                "nombre='" + nombre + '\'' +
                ", cantidad=" + cantidad +
                ", precio=" + precio +
                ", descuento=" + descuento +
                '}';
    }
}