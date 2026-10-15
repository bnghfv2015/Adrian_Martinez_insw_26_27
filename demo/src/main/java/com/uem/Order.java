package com.uem;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.ArrayList;
import java.util.List;

public class Order {

    private String id;
    private List<Article> articulos;

    // Constructor vacio para Jackson
    public Order() {
    }

    // Constructor original
    public Order(String id, List<Article> articulos) {
        this.id = id;
        this.articulos = articulos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Article> getArticulos() {
        return articulos;
    }

    @JsonAlias("articles")
    public void setArticulos(List<Article> articulos) {
        this.articulos = articulos;
    }

    public double getGrossTotal() {
        Calculator calculator = new Calculator();
        List<Double> cantidades = new ArrayList<>();

        for (Article articulo : articulos) {
            cantidades.add(articulo.getGrossAmount());
        }

        return calculator.calculateTotal(cantidades);
    }

    public double getDiscountedTotal() {
        Calculator calculator = new Calculator();
        List<Double> cantidades = new ArrayList<>();

        for (Article articulo : articulos) {
            cantidades.add(articulo.getDiscountedAmount());
        }

        return calculator.calculateTotal(cantidades);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", articulos=" + articulos +
                '}';
    }
}