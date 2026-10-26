package com.uem.view;

import javax.swing.*;

import com.uem.model.Article;
import com.uem.model.Order;

import java.awt.*;

public class OrderView extends JFrame {
    private JTextField searchField = new JTextField(10);
    private JButton searchButton = new JButton("Search");
    private JTextArea resultArea = new JTextArea(10, 40);

    public OrderView() {
        setTitle("Order Management");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // add new JLabel("Order ID:")
        add(new JLabel("Order ID:"));
        // add to view the search field
        add(searchField);
        // add to view the search button
        add(searchButton);
        // add to view the result area inside a JScrollPane
        resultArea.setEditable(false);
        add(new JScrollPane(resultArea));

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public String getSearchId() {
        return searchField.getText().trim();
    }

    public JButton getSearchButton() {
        return searchButton;
    }

    public void displayOrder(Order order, double rate) {
        // Display order details in resultArea
        StringBuilder sb = new StringBuilder();
        sb.append("Order ").append(order.getId()).append(":\n");

        for (Article article : order.getArticulos()) {
            sb.append(article.getNombre())
                    .append(" x").append(article.getCantidad())
                    .append(" @ ").append(article.getPrecio()).append("$")
                    .append(" (discount ").append(article.getDescuento()).append("%)")
                    .append(" = ").append(article.getDiscountedAmount()).append("$\n");
        }

        double grossTotal = order.getGrossTotal();
        double discountedTotal = order.getDiscountedTotal();

        sb.append("Gross total: ").append(grossTotal).append("$\n");
        sb.append("Discounted total: ").append(discountedTotal).append("$\n");
        sb.append("Gross total (USD): ").append(grossTotal * rate).append("$\n");
        sb.append("Discounted total (USD): ").append(discountedTotal * rate).append("$\n");

        resultArea.setText(sb.toString());
    }

    public void displayMessage(String message) {
        resultArea.setText(message);
    }
}