package com.uem;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // Configurar los logs para mostrar mensajes DEBUG
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "debug");

        Logger log = LoggerFactory.getLogger(Main.class);

        // Codigo de la actividad anterior
        System.out.println("Hello world!");

        Calculator calculator = new Calculator();
        int result = calculator.multiply(2, 3);

        System.out.println("Result of multiplication: " + result);

        // Crear el objeto para leer JSON
        ObjectMapper mapper = new ObjectMapper();

        // Leer el archivo orders.json
        try (InputStream input = Main.class.getResourceAsStream("/orders.json")) {

            if (input == null) {
                log.error("No se encuentra el archivo orders.json");
                return;
            }

            // Convertir el JSON en una lista de pedidos
            List<Order> orders = mapper.readValue(
                    input,
                    new TypeReference<List<Order>>() {
                    });

            // Mostrar cada pedido en el sistema de logs
            for (Order order : orders) {
                log.debug("Loaded order: {}", order.getId());
            }

        } catch (IOException e) {
            log.error("Error al leer el archivo orders.json", e);
        }
    }
}