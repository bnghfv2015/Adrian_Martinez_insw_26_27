package com.uem.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uem.model.Order;
import com.uem.model.Searcher;
import com.uem.service.ExchangeRateService;
import com.uem.service.ExchangeRateService.ExchangeRate;
import com.uem.view.OrderView;

public class OrderController {

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    // Atributo de clase OrderView
    OrderView view;
    // Lista de orders
    List<Order> orders;
    // Atributo de clase Searcher
    Searcher searcher;
    // Servicio externo para obtener el tipo de cambio EUR -> USD
    ExchangeRateService exchangeRateService;

    public OrderController(OrderView view, List<Order> orders) {
        // Initialize attributes
        this.view = view;
        this.orders = orders;
        this.searcher = new Searcher();
        this.exchangeRateService = new ExchangeRateService();

        // Adding action listener to the search button, call searchOrder on click
        this.view.getSearchButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchOrder();
            }
        });
    }

    private void searchOrder() {
        String id = view.getSearchId();
        log.debug("Searching order: {}", id);

        // Lista con los ids de todos los pedidos
        List<String> ids = orders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());

        // display order if found, else display "Order not found."
        if (searcher.searchExactPhrase(id, ids)) {
            Order order = orders.get(ids.indexOf(id));
            log.debug("Order found: {}", id);

            // Pedimos el tipo de cambio en el momento de mostrar el pedido
            ExchangeRate rate = null;
            try {
                rate = exchangeRateService.getEurToUsd();
            } catch (IOException e) {
                log.error("No se pudo obtener el tipo de cambio EUR/USD", e);
            }

            view.displayOrder(order, rate);
        } else {
            log.debug("Order not found: {}", id);
            view.displayMessage("Order not found.");
        }
    }
}