package com.uem.view;

import javax.swing.*;

import com.uem.model.Article;
import com.uem.model.Order;
import com.uem.service.ExchangeRateService.ExchangeRate;

import java.awt.*;
import java.net.URL;
import java.util.List;

public class OrderView extends JFrame {
    private JTextField searchField = new JTextField(10);
    private JButton searchButton = new JButton("Search");
    private JButton newOrderButton = new JButton("New order");
    private JTextArea resultArea = new JTextArea(10, 40);

    // Lista de ids de pedidos (primera pantalla)
    private DefaultListModel<String> idListModel = new DefaultListModel<>();
    private JList<String> idList = new JList<>(idListModel);

    public OrderView() {
        setTitle("Order Management");

        // Icono de la aplicacion (src/main/resources/app.png, va dentro del .jar)
        URL iconUrl = getClass().getResource("/app.png");
        if (iconUrl != null) {
            setIconImage(new ImageIcon(iconUrl).getImage());
        }

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        // Arriba: busqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Order ID:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(newOrderButton);
        add(searchPanel, BorderLayout.NORTH);

        // Izquierda: lista de ids de pedidos
        idList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane idScroll = new JScrollPane(idList);
        idScroll.setPreferredSize(new Dimension(120, 200));
        idScroll.setBorder(BorderFactory.createTitledBorder("Orders"));
        add(idScroll, BorderLayout.WEST);

        // Centro: detalle del pedido
        resultArea.setEditable(false);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public String getSearchId() {
        return searchField.getText().trim();
    }

    public void setSearchId(String id) {
        searchField.setText(id);
    }

    public JButton getSearchButton() {
        return searchButton;
    }

    public JButton getNewOrderButton() {
        return newOrderButton;
    }

    public JList<String> getIdList() {
        return idList;
    }

    /** Rellena la lista de la izquierda con los ids de los pedidos. */
    public void setOrderIds(List<String> ids) {
        idListModel.clear();
        for (String id : ids) {
            idListModel.addElement(id);
        }
    }

    /**
     * Muestra el pedido con los totales en euros y, si se ha podido obtener
     * el tipo de cambio, tambien en dolares. Si rate es null, solo euros.
     */
    public void displayOrder(Order order, ExchangeRate rate) {
        // Display order details in resultArea
        StringBuilder sb = new StringBuilder();
        sb.append("Order ").append(order.getId()).append(":\n");

        for (Article article : order.getArticulos()) {
            sb.append(article.getNombre())
                    .append(" x").append(article.getCantidad())
                    .append(" @ ").append(eur(article.getPrecio()))
                    .append(" (discount ").append(article.getDescuento()).append("%)")
                    .append(" = ").append(eur(article.getDiscountedAmount())).append("\n");
        }

        double grossTotal = order.getGrossTotal();
        double discountedTotal = order.getDiscountedTotal();

        sb.append("\nGross total: ").append(eur(grossTotal)).append("\n");
        sb.append("Discounted total: ").append(eur(discountedTotal)).append("\n");

        if (rate != null) {
            sb.append("\nExchange rate EUR/USD: ").append(String.format("%.4f", rate.getRate()))
                    .append(" (").append(rate.getDate()).append(")\n");
            sb.append("Gross total (USD): ").append(usd(grossTotal * rate.getRate())).append("\n");
            sb.append("Discounted total (USD): ").append(usd(discountedTotal * rate.getRate())).append("\n");
        } else {
            sb.append("\nUSD not available: could not get the EUR/USD exchange rate.\n");
        }

        resultArea.setText(sb.toString());
    }

    public void displayMessage(String message) {
        resultArea.setText(message);
    }

    private String eur(double amount) {
        return String.format("%.2f €", amount);
    }

    private String usd(double amount) {
        return String.format("%.2f $", amount);
    }
}