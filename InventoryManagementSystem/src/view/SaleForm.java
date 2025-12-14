package view;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import controller.*;
import model.*;

public class SaleForm extends JFrame {
    private JComboBox<Product> productDropdown;
    private JTextField qtyField;
    private JLabel totalLabel;
    private SalesController salesController;
    private ProductController productController;
    private User currentUser;

    public SaleForm(User user) {
        this.currentUser = user;
        salesController = new SalesController();
        productController = new ProductController();

        // --- Frame settings ---
        setTitle("Record Sale");
        setSize(500, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(245, 245, 245));
        setLayout(new BorderLayout(10, 10));

        // --- Header ---
        JLabel header = new JLabel("💰 Record Sale", JLabel.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(header, BorderLayout.NORTH);

        // --- Form Panel ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // All input fields will expand horizontally
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Product
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Product:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        productDropdown = new JComboBox<>();
        loadProducts();
        formPanel.add(productDropdown, gbc);

        // Quantity
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(new JLabel("Quantity:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        qtyField = new JTextField();
        formPanel.add(qtyField, gbc);

        // Calculate total button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        JButton calcBtn = new JButton("💵 Calculate Total");
        calcBtn.setPreferredSize(new Dimension(200, 30)); // fixed size button
        calcBtn.setForeground(Color.BLACK);
        calcBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        calcBtn.addActionListener(e -> calculateTotal());
        formPanel.add(calcBtn, gbc);

        // Total Label
        gbc.gridy = 3;
        totalLabel = new JLabel("Total: ₹0.00", JLabel.CENTER);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(totalLabel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // --- Bottom Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));

        JButton recordBtn = new JButton("✅ Record Sale");
        recordBtn.setPreferredSize(new Dimension(200, 30));
        recordBtn.setForeground(Color.BLACK);
        recordBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        recordBtn.addActionListener(e -> recordSale());
        buttonPanel.add(recordBtn);

        JButton backBtn = new JButton("⬅ Back");
        backBtn.setPreferredSize(new Dimension(200, 30));
        backBtn.setForeground(Color.BLACK);
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.addActionListener(e -> {
            dispose();
            new MainFrame(currentUser).setVisible(true);
        });
        buttonPanel.add(backBtn);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadProducts() {
        productDropdown.removeAllItems();
        List<Product> list = productController.getAllProducts();
        for (Product p : list) productDropdown.addItem(p);
    }

    private void calculateTotal() {
        try {
            Product p = (Product) productDropdown.getSelectedItem();
            int qty = Integer.parseInt(qtyField.getText());
            totalLabel.setText("Total: ₹" + String.format("%.2f", p.getPrice() * qty));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Enter valid quantity!");
        }
    }

    private void recordSale() {
        try {
            Product p = (Product) productDropdown.getSelectedItem();
            int qty = Integer.parseInt(qtyField.getText());
            if (qty > p.getQuantity()) {
                JOptionPane.showMessageDialog(this, "❌ Insufficient stock!");
                return;
            }
            double total = p.getPrice() * qty;
            Sale sale = new Sale(p.getId(), qty, total);
            if (salesController.recordSale(sale, currentUser.getUsername())) {
                JOptionPane.showMessageDialog(this, "✅ Sale recorded successfully!");
                qtyField.setText("");
                totalLabel.setText("Total: ₹0.00");
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Failed to record sale!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Enter valid details!");
        }
    }

    public static void main(String[] args) {
        User admin = new User();
        admin.setUsername("admin");
        admin.setRole("Admin");
        SwingUtilities.invokeLater(() -> new SaleForm(admin).setVisible(true));
    }
}
