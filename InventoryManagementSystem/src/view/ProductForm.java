package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import controller.ProductController;
import model.Product;
import model.User;

public class ProductForm extends JFrame {

    private JTextField nameField, categoryField, qtyField, priceField, searchField;
    private JTable table;
    private ProductController controller;
    private DefaultTableModel model;
    private User currentUser;

    public ProductForm(User user) {
        this.currentUser = user;
        controller = new ProductController();

        setTitle("Inventory Management - Product Management");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));

        // --- Title ---
        JLabel titleLabel = new JLabel("📦 Manage Products", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // --- Input Form ---
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Category:"));
        categoryField = new JTextField();
        formPanel.add(categoryField);

        formPanel.add(new JLabel("Quantity:"));
        qtyField = new JTextField();
        formPanel.add(qtyField);

        formPanel.add(new JLabel("Price:"));
        priceField = new JTextField();
        formPanel.add(priceField);

        // --- Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttonPanel.setBackground(Color.WHITE);

        JButton addBtn = new JButton("➕ Add");
        JButton updateBtn = new JButton("✏️ Update");
        JButton deleteBtn = new JButton("🗑️ Delete");

        addBtn.setBackground(new Color(0, 123, 255));
        addBtn.setForeground(Color.BLACK);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        updateBtn.setBackground(new Color(255, 193, 7));
        updateBtn.setForeground(Color.BLACK);
        updateBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        deleteBtn.setBackground(new Color(220, 53, 69));
        deleteBtn.setForeground(Color.BLACK);
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Disable buttons for Staff
        if ("Staff".equalsIgnoreCase(currentUser.getRole())) {
            addBtn.setEnabled(false);
            updateBtn.setEnabled(false);
            deleteBtn.setEnabled(false);
            addBtn.setToolTipText("Admins only");
            updateBtn.setToolTipText("Admins only");
            deleteBtn.setToolTipText("Admins only");
        }

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // --- Table Section ---
        model = new DefaultTableModel(new String[]{"ID", "Name", "Category", "Quantity", "Price"}, 0);
        table = new JTable(model);
        table.setRowHeight(22);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // --- Search Panel with Back Button ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        searchPanel.setBackground(new Color(245, 245, 245));
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(15);
        searchPanel.add(searchField);

        JButton searchBtn = new JButton("🔍 Find");
        searchBtn.setBackground(new Color(23, 162, 184));
        searchBtn.setForeground(Color.BLACK);
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchBtn.addActionListener(e -> searchProducts());
        searchPanel.add(searchBtn);

        JButton backBtn = new JButton("⬅️ Back");
        backBtn.setBackground(new Color(108, 117, 125));
        backBtn.setForeground(Color.BLACK);
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.addActionListener(e -> goBack());
        searchPanel.add(backBtn);

        add(searchPanel, BorderLayout.SOUTH);

        // --- Load data ---
        loadProducts();

        // --- Button Actions ---
        addBtn.addActionListener(e -> addProduct());
        updateBtn.addActionListener(e -> updateProduct());
        deleteBtn.addActionListener(e -> deleteProduct());
    }

    // --- CRUD & Utility Methods ---
    private void addProduct() {
        try {
            String name = nameField.getText().trim();
            String cat = categoryField.getText().trim();
            int qty = Integer.parseInt(qtyField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            Product p = new Product(name, cat, qty, price);

            if (controller.addProduct(p, currentUser.getUsername())) {
                JOptionPane.showMessageDialog(this, "✅ Product added successfully!");
                clearFields();
                loadProducts();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "⚠️ Invalid input: " + ex.getMessage());
        }
    }

    private void updateProduct() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to update.");
            return;
        }

        try {
            int id = (int) model.getValueAt(row, 0);
            String name = nameField.getText().trim();
            String cat = categoryField.getText().trim();
            int qty = Integer.parseInt(qtyField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            Product p = new Product(id, name, cat, qty, price);

            if (controller.updateProduct(p, currentUser.getUsername())) {
                JOptionPane.showMessageDialog(this, "✅ Product updated successfully!");
                clearFields();
                loadProducts();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "⚠️ Error: " + ex.getMessage());
        }
    }

    private void deleteProduct() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this product?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.deleteProduct(id, currentUser.getUsername())) {
                JOptionPane.showMessageDialog(this, "✅ Product deleted successfully!");
                loadProducts();
            }
        }
    }

    private void searchProducts() {
        String keyword = searchField.getText().trim();
        List<Product> list = controller.searchProducts(keyword);
        display(list);
    }

    private void loadProducts() {
        display(controller.getAllProducts());
    }

    private void display(List<Product> list) {
        model.setRowCount(0);
        for (Product p : list) {
            model.addRow(new Object[]{p.getId(), p.getName(), p.getCategory(), p.getQuantity(), p.getPrice()});
        }
    }

    private void clearFields() {
        nameField.setText("");
        categoryField.setText("");
        qtyField.setText("");
        priceField.setText("");
    }

    private void goBack() {
        dispose();
        new MainFrame(currentUser).setVisible(true);
    }
}
