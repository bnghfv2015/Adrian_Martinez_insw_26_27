package com.uem.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.uem.model.Order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Base de datos del sistema: un fichero orders.json REAL en disco.
 *
 * El orders.json de src/main/resources va dentro del .jar y es de solo lectura,
 * asi que se usa solo como datos iniciales: la primera vez que se ejecuta la
 * app se copia a la carpeta del usuario (~/OrderManagement/orders.json) y a
 * partir de ahi se lee y se sobrescribe ese fichero.
 */
public class OrderRepository {

    private static final Logger log = LoggerFactory.getLogger(OrderRepository.class);

    private final Path file;
    private final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    /** Usa ~/OrderManagement/orders.json */
    public OrderRepository() {
        this(Paths.get(System.getProperty("user.home"), "OrderManagement", "orders.json"));
    }

    public OrderRepository(Path file) {
        this.file = file;
    }

    public Path getFile() {
        return file;
    }

    /**
     * Lee todos los pedidos. Si el fichero no existe, lo crea con los datos
     * iniciales.
     */
    public List<Order> load() throws IOException {
        if (!Files.exists(file)) {
            createFromInitialData();
        }
        List<Order> orders = mapper.readValue(file.toFile(), new TypeReference<List<Order>>() {
        });
        return new ArrayList<>(orders);
    }

    /** Sobrescribe el fichero json con la lista completa de pedidos. */
    public void save(List<Order> orders) throws IOException {
        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }
        mapper.writeValue(file.toFile(), orders);
        log.debug("Guardados {} pedidos en {}", orders.size(), file);
    }

    private void createFromInitialData() throws IOException {
        if (file.getParent() != null) {
            Files.createDirectories(file.getParent());
        }
        try (InputStream input = OrderRepository.class.getResourceAsStream("/orders.json")) {
            if (input == null) {
                throw new IOException("No se encuentra el orders.json inicial dentro de la aplicacion");
            }
            Files.copy(input, file, StandardCopyOption.REPLACE_EXISTING);
        }
        log.debug("Creado {} a partir de los datos iniciales", file);
    }
}