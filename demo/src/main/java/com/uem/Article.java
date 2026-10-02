package com.uem;

public class Article {

    private String nombre;
    private int cantidad;
    private double precio;
    private double descuento;

    public Article(String nombre, int cantidad, double precio, double descuento) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.descuento = descuento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getDescuento() {
        return descuento;
    }

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