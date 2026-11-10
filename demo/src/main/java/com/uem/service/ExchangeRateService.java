package com.uem.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Obtiene el tipo de cambio EUR -> USD llamando a un servicio externo.
 *
 * Solucion elegida:
 * - Servicio: API publica de Frankfurter (https://frankfurter.dev). Es
 * gratuita,
 * no necesita API key y publica los tipos de referencia del Banco Central
 * Europeo.
 * - Endpoint: GET https://api.frankfurter.dev/v1/latest?base=EUR&symbols=USD
 * Respuesta de ejemplo:
 * {"amount":1.0,"base":"EUR","date":"2026-11-06","rates":{"USD":1.16}}
 * - Cliente HTTP: java.net.http.HttpClient (incluido en Java 11+, no hace falta
 * ninguna dependencia nueva). El JSON se lee con Jackson, que ya usa el
 * proyecto.
 * - El tipo de cambio se pide cada vez que se muestra un pedido, asi siempre
 * esta
 * actualizado. Si no hay conexion o el servicio falla, se lanza IOException y
 * la
 * aplicacion muestra solo los importes en euros.
 */
public class ExchangeRateService {

    private static final Logger log = LoggerFactory.getLogger(ExchangeRateService.class);

    public static final String DEFAULT_URL = "https://api.frankfurter.dev/v1/latest?base=EUR&symbols=USD";

    private final HttpClient client;
    private final String url;
    private final ObjectMapper mapper = new ObjectMapper();

    public ExchangeRateService() {
        this(DEFAULT_URL);
    }

    public ExchangeRateService(String url) {
        this.url = url;
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    /**
     * Llama al servicio externo y devuelve el tipo de cambio EUR -> USD actual.
     *
     * @throws IOException si no hay conexion o la respuesta no es valida
     */
    public ExchangeRate getEurToUsd() throws IOException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .header("Accept", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IOException("El servicio de tipo de cambio respondio con codigo " + response.statusCode());
            }

            ExchangeRate rate = parse(response.body());
            log.debug("Tipo de cambio EUR/USD obtenido: {} (fecha {})", rate.getRate(), rate.getDate());
            return rate;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Peticion del tipo de cambio interrumpida", e);
        }
    }

    /**
     * Lee la respuesta JSON de Frankfurter y extrae el tipo EUR -> USD.
     * Es publico para poder probarlo en los tests sin conexion a internet.
     */
    public ExchangeRate parse(String json) throws IOException {
        JsonNode root = mapper.readTree(json);
        JsonNode usd = root.path("rates").path("USD");

        if (!usd.isNumber()) {
            throw new IOException("La respuesta no contiene el tipo de cambio USD");
        }

        return new ExchangeRate(usd.asDouble(), root.path("date").asText(""));
    }

    /** Tipo de cambio y fecha a la que corresponde. */
    public static class ExchangeRate {
        private final double rate;
        private final String date;

        public ExchangeRate(double rate, String date) {
            this.rate = rate;
            this.date = date;
        }

        public double getRate() {
            return rate;
        }

        public String getDate() {
            return date;
        }
    }
}