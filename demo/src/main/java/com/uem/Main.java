package com.uem;

import com.uem.controller.OrderController;
import com.uem.model.Calculator;
import com.uem.model.Order;
import com.uem.service.OrderRepository;
import com.uem.view.OrderView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.util.List;

public class Main {

    // El tipo de cambio EUR -> USD ya no es una constante: se obtiene en tiempo
    // real
    // con ExchangeRateService (ver com.uem.service.ExchangeRateService y README.md)

    public static void main(String[] args) {

        // Configurar los logs para mostrar mensajes DEBUG
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");

        Logger log = LoggerFactory.getLogger(Main.class);

        // Codigo de la actividad anterior
        System.out.println("Hello world!");

        Calculator calculator = new Calculator();
        int result = calculator.multiply(2, 3);

        System.out.println("Result of multiplication: " + result);

        // Base de datos del sistema: fichero orders.json en disco
        // (la primera vez se crea a partir del orders.json inicial del .jar)
        OrderRepository repository = new OrderRepository();

        try {
            List<Order> orders = repository.load();
            log.debug("Pedidos cargados desde {}", repository.getFile());

            // Mostrar cada pedido en el sistema de logs
            for (Order order : orders) {
                log.debug("Loaded order: {}", order.getId());
            }

            // Initialize MVC (solo si hay pantalla, para que los tests no fallen)
            if (!GraphicsEnvironment.isHeadless()) {
                OrderView view = new OrderView();
                new OrderController(view, orders, repository);
            }

        } catch (IOException e) {
            log.error("Error al leer el archivo orders.json", e);
        }
    }
}