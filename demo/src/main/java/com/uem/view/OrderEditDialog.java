package com.uem.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.uem.model.Article;
import com.uem.model.Order;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Formulario para editar un pedido: solo se pueden cambiar la cantidad y el
 * descuento de cada articulo. showDialog() devuelve la nueva lista de
 * articulos, o null si se cancela.
 */
public class OrderEditDialog extends JDialog {

    private final Order order;
    private final DefaultTableModel tableModel;
    private final JTable table;
    private List<Article> result = null;

    public OrderEditDialog(Frame owner, Order order) {
        super(owner, "Edit order " + order.getId(), true);
        this.order = order;

        // Solo las columnas Quantity (1) y Discount % (3) son editables
        tableModel = new DefaultTableModel(
                new Object[] { "Name", "Quantity", "Unit price", "Discount %" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1 || column == 3;
            }
        };
        table = new JTable(tableModel);

        for (Article a : order.getArticulos()) {
            tableModel.addRow(new Object[] {
                    a.getNombre(), a.getCantidad(), a.getPrecio(), a.getDescuento() });
        }

        setLayout(new BorderLayout(8, 8));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setPreferredSize(new Dimension(450, 160));
        scroll.setBorder(BorderFactory.createTitledBorder("Articles (only quantity and discount can be edited)"));
        add(scroll, BorderLayout.CENTER);

        JButton save = new JButton("Save");
        save.addActionListener(e -> onSave());
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(e -> dispose());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.add(save);
        bottom.add(cancel);
        add(bottom, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(owner);
    }

    /**
     * Muestra el formulario y espera. Devuelve los articulos editados o null si se
     * cancela.
     */
    public List<Article> showDialog() {
        setVisible(true);
        return result;
    }

    private void onSave() {
        // Si se esta editando una celda, se termina la edicion antes de leer
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }

        List<Article> original = order.getArticulos();
        List<Article> edited = new ArrayList<>();

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Article a = original.get(i);
            String rowText = "Article " + (i + 1) + " (" + a.getNombre() + "): ";
            try {
                int quantity = Integer.parseInt(String.valueOf(tableModel.getValueAt(i, 1)).trim());
                double discount = Double.parseDouble(
                        String.valueOf(tableModel.getValueAt(i, 3)).trim().replace(',', '.'));

                if (quantity < 1) {
                    error(rowText + "the quantity must be at least 1.");
                    return;
                }
                if (discount < 0 || discount > 100) {
                    error(rowText + "the discount must be between 0 and 100.");
                    return;
                }
                // Nombre y precio se mantienen; solo cambian cantidad y descuento
                edited.add(new Article(a.getNombre(), quantity, a.getPrecio(), discount));
            } catch (NumberFormatException ex) {
                error(rowText + "quantity and discount must be numbers.");
                return;
            }
        }

        result = edited;
        dispose();
    }

    private void error(String message) {
        JOptionPane.showMessageDialog(this, message, "Invalid data", JOptionPane.WARNING_MESSAGE);
    }
}