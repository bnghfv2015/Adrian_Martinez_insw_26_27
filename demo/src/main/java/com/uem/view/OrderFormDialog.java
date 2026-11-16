package com.uem.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.uem.model.Article;
import com.uem.model.Order;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Formulario para crear un pedido: id + tabla de articulos.
 * showDialog() devuelve el pedido creado, o null si se cancela.
 */
public class OrderFormDialog extends JDialog {

    private final JTextField idField = new JTextField(12);
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[] { "Name", "Quantity", "Unit price", "Discount %" }, 0);
    private final JTable table = new JTable(tableModel);
    private final Predicate<String> idExists;
    private Order result = null;

    /**
     * @param idExists devuelve true si ya existe un pedido con ese id
     */
    public OrderFormDialog(Frame owner, Predicate<String> idExists) {
        super(owner, "New order", true);
        this.idExists = idExists;

        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Order ID:"));
        top.add(idField);
        add(top, BorderLayout.NORTH);

        tableModel.addRow(new Object[] { "", "1", "0", "0" });
        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(450, 160));
        scroll.setBorder(BorderFactory.createTitledBorder("Articles"));
        add(scroll, BorderLayout.CENTER);

        JButton addRow = new JButton("Add article");
        addRow.addActionListener(e -> tableModel.addRow(new Object[] { "", "1", "0", "0" }));
        JButton removeRow = new JButton("Remove article");
        removeRow.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                tableModel.removeRow(row);
            }
        });
        JButton save = new JButton("Save");
        save.addActionListener(e -> onSave());
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dispose());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(addRow);
        bottom.add(removeRow);
        bottom.add(save);
        bottom.add(cancel);
        add(bottom, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }

    /** Muestra el formulario y espera. Devuelve el pedido o null si se cancela. */
    public Order showDialog() {
        setVisible(true);
        return result;
    }

    private void onSave() {
        // Si se esta editando una celda, se termina la edicion antes de leer
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }

        String id = idField.getText().trim();
        if (id.isEmpty()) {
            error("The order ID cannot be empty.");
            return;
        }
        if (idExists.test(id)) {
            error("An order with ID " + id + " already exists. The ID must be unique.");
            return;
        }
        if (tableModel.getRowCount() == 0) {
            error("The order needs at least one article.");
            return;
        }

        List<Article> articles = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String name = String.valueOf(tableModel.getValueAt(i, 0)).trim();
            String rowText = "Article " + (i + 1) + ": ";
            if (name.isEmpty()) {
                error(rowText + "the name cannot be empty.");
                return;
            }
            try {
                int quantity = Integer.parseInt(String.valueOf(tableModel.getValueAt(i, 1)).trim());
                double price = Double.parseDouble(
                        String.valueOf(tableModel.getValueAt(i, 2)).trim().replace(',', '.'));
                double discount = Double.parseDouble(
                        String.valueOf(tableModel.getValueAt(i, 3)).trim().replace(',', '.'));

                if (quantity < 1) {
                    error(rowText + "the quantity must be at least 1.");
                    return;
                }
                if (price < 0) {
                    error(rowText + "the price cannot be negative.");
                    return;
                }
                if (discount < 0 || discount > 100) {
                    error(rowText + "the discount must be between 0 and 100.");
                    return;
                }
                articles.add(new Article(name, quantity, price, discount));
            } catch (NumberFormatException ex) {
                error(rowText + "quantity, price and discount must be numbers.");
                return;
            }
        }

        result = new Order(id, articles);
        dispose();
    }

    private void error(String message) {
        JOptionPane.showMessageDialog(this, message, "Invalid data", JOptionPane.WARNING_MESSAGE);
    }
}