package com.uem.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    @JsonProperty("name")
    public String getNombre() {
        return nombre;
    }

    @JsonAlias("name")
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @JsonProperty("quantity")
    public int getCantidad() {
        return cantidad;
    }

    @JsonAlias("quantity")
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @JsonProperty("unitPrice")
    public double getPrecio() {
        return precio;
    }

    @JsonAlias("unitPrice")
    public void setPrecio(double precio) {
        this.precio = precio;
    }

    @JsonProperty("discount")
    public double getDescuento() {
        return descuento;
    }

    @JsonAlias("discount")
    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    @JsonIgnore
    public double getGrossAmount() {
        Calculator calculator = new Calculator();
        return calculator.multiply(cantidad, precio);
    }

    @JsonIgnore
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