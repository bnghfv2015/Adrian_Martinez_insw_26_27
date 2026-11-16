package com.uem.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uem.model.Article;
import com.uem.model.Order;
import com.uem.model.Searcher;
import com.uem.service.ExchangeRateService;
import com.uem.service.ExchangeRateService.ExchangeRate;
import com.uem.service.OrderRepository;
import com.uem.view.OrderEditDialog;
import com.uem.view.OrderFormDialog;
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
    // Base de datos del sistema (fichero json)
    OrderRepository repository;
    // Id del pedido que se esta mostrando ahora mismo (null si no hay ninguno)
    String currentOrderId = null;

    public OrderController(OrderView view, List<Order> orders, OrderRepository repository) {
        // Initialize attributes
        this.view = view;
        this.orders = orders;
        this.repository = repository;
        this.searcher = new Searcher();
        this.exchangeRateService = new ExchangeRateService();

        // Adding action listener to the search button, call searchOrder on click
        this.view.getSearchButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchOrder();
            }
        });

        // Boton "New order": abre el formulario
        this.view.getNewOrderButton().addActionListener(e -> createOrder());

        // Boton "Edit order": edita cantidad y descuento del pedido mostrado
        this.view.getEditButton().addActionListener(e -> editOrder());

        // Boton "Delete order": borra el pedido mostrado
        this.view.getDeleteButton().addActionListener(e -> deleteOrder());

        // Mostrar en la primera pantalla la lista de ids de pedidos
        refreshIdList();

        // Al pulsar un id de la lista, se rellena el buscador y se muestra el detalle
        this.view.getIdList().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = view.getIdList().getSelectedValue();
                if (selected != null) {
                    view.setSearchId(selected);
                    searchOrder();
                }
            }
        });
    }

    private List<String> getIds() {
        return orders.stream()
                .map(Order::getId)
                .collect(Collectors.toList());
    }

    private void refreshIdList() {
        view.setOrderIds(getIds());
    }

    /** Caso de uso: crear un pedido con formulario y guardarlo en el json. */
    private void createOrder() {
        OrderFormDialog dialog = new OrderFormDialog(view, id -> getIds().contains(id));
        Order newOrder = dialog.showDialog();

        if (newOrder == null) {
            return; // cancelado
        }

        // 1) se guarda en memoria
        orders.add(newOrder);

        // 2) se sobrescribe el fichero json con la nueva lista
        try {
            repository.save(orders);
        } catch (IOException e) {
            log.error("No se pudo guardar el pedido", e);
            orders.remove(newOrder); // deshacer: que memoria y fichero coincidan
            JOptionPane.showMessageDialog(view,
                    "The order could not be saved:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        log.debug("Order created: {}", newOrder.getId());

        // 3) se actualiza la pantalla y se selecciona el nuevo pedido
        refreshIdList();
        view.getIdList().setSelectedValue(newOrder.getId(), true);
    }

    /**
     * Caso de uso: editar cantidad y descuento de los articulos del pedido
     * mostrado.
     */
    private void editOrder() {
        if (currentOrderId == null) {
            return;
        }

        int index = getIds().indexOf(currentOrderId);
        if (index < 0) {
            return;
        }
        Order order = orders.get(index);

        OrderEditDialog dialog = new OrderEditDialog(view, order);
        List<Article> editedArticles = dialog.showDialog();

        if (editedArticles == null) {
            return; // cancelado
        }

        // 1) se cambian los articulos en memoria (guardando los antiguos por si hay que
        // deshacer)
        List<Article> oldArticles = order.getArticulos();
        order.setArticulos(editedArticles);

        // 2) se sobrescribe el fichero json con la lista actualizada
        try {
            repository.save(orders);
        } catch (IOException e) {
            log.error("No se pudo guardar tras editar el pedido", e);
            order.setArticulos(oldArticles); // deshacer
            JOptionPane.showMessageDialog(view,
                    "The order could not be saved:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        log.debug("Order edited: {}", currentOrderId);

        // 3) se vuelve a mostrar el pedido con los datos nuevos
        view.setSearchId(currentOrderId);
        searchOrder();
    }

    /** Caso de uso: borrar el pedido mostrado y guardar el json sin el. */
    private void deleteOrder() {
        if (currentOrderId == null) {
            return;
        }

        int answer = JOptionPane.showConfirmDialog(view,
                "Delete order " + currentOrderId + "?",
                "Confirm delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        // 1) se quita de memoria (recordando su posicion por si hay que deshacer)
        int index = getIds().indexOf(currentOrderId);
        if (index < 0) {
            return;
        }
        Order removed = orders.remove(index);

        // 2) se sobrescribe el fichero json con la nueva lista
        try {
            repository.save(orders);
        } catch (IOException e) {
            log.error("No se pudo guardar tras borrar el pedido", e);
            orders.add(index, removed); // deshacer: que memoria y fichero coincidan
            JOptionPane.showMessageDialog(view,
                    "The order could not be deleted:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        log.debug("Order deleted: {}", removed.getId());

        // 3) se actualiza la pantalla
        String deletedId = removed.getId();
        currentOrderId = null;
        refreshIdList();
        view.setSearchId("");
        view.displayMessage("Order " + deletedId + " deleted.");
    }

    private void searchOrder() {
        String id = view.getSearchId();
        log.debug("Searching order: {}", id);

        // Lista con los ids de todos los pedidos
        List<String> ids = getIds();

        // display order if found, else display "Order not found."
        if (searcher.searchExactPhrase(id, ids)) {
            Order order = orders.get(ids.indexOf(id));
            log.debug("Order found: {}", id);
            currentOrderId = id;

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
            currentOrderId = null;
            view.displayMessage("Order not found.");
        }
    }
}