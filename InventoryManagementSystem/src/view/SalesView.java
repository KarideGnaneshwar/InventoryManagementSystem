package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import controller.*;
import model.*;

public class SalesView extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private SalesController salesController;
    private ProductController productController;
    private User currentUser;

    public SalesView(User user) {
        this.currentUser = user;
        salesController = new SalesController();
        productController = new ProductController();

        setTitle("Recorded Sales");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(245, 245, 245));
        setLayout(new BorderLayout(10, 10));

        // --- Header ---
        JLabel title = new JLabel("💰 Recorded Sales", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // --- Table ---
        model = new DefaultTableModel(
                new String[]{"Sale ID", "Product Name", "Quantity Sold", "Total Amount", "Sold By", "Available Stock"}, 0);
        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setEnabled(false); // Non-editable
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Panel ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBackground(new Color(245, 245, 245));

        // Search
        bottomPanel.add(new JLabel("Search:"));
        searchField = new JTextField(15);
        bottomPanel.add(searchField);

        JButton searchBtn = new JButton("🔍 Find");
        searchBtn.setForeground(Color.BLACK);
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchBtn.addActionListener(e -> searchSales());
        bottomPanel.add(searchBtn);

        // Total Revenue (Admin only)
        if ("Admin".equalsIgnoreCase(user.getRole())) {
            JButton revenueBtn = new JButton("📊 Total Revenue");
            revenueBtn.setForeground(Color.BLACK);
            revenueBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            revenueBtn.addActionListener(e -> {
                double total = salesController.getTotalRevenue();
                JOptionPane.showMessageDialog(this, "Total Revenue: ₹" + String.format("%.2f", total));
            });
            bottomPanel.add(revenueBtn);
        } else { // Staff: show total sales made by this user
            JButton staffTotalBtn = new JButton("📊 My Total Sales");
            staffTotalBtn.setForeground(Color.BLACK);
            staffTotalBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            staffTotalBtn.addActionListener(e -> {
                double total = salesController.getSalesByUser(currentUser.getUsername())
                        .stream()
                        .mapToDouble(Sale::getTotalAmount)
                        .sum();
                JOptionPane.showMessageDialog(this, "Your Total Sales: ₹" + String.format("%.2f", total));
            });
            bottomPanel.add(staffTotalBtn);
        }

        // Back button
        JButton backBtn = new JButton("⬅️ Back");
        backBtn.setForeground(Color.BLACK);
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.addActionListener(e -> {
            dispose();
            new MainFrame(currentUser).setVisible(true);
        });
        bottomPanel.add(backBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        // --- Load Sales ---
        loadSales();
    }

    private void loadSales() {
        List<Sale> list = "Admin".equalsIgnoreCase(currentUser.getRole()) ?
                salesController.getAllSales() :
                salesController.getSalesByUser(currentUser.getUsername());
        display(list);
    }

    private void searchSales() {
        String keyword = searchField.getText().trim();
        List<Sale> list = "Admin".equalsIgnoreCase(currentUser.getRole()) ?
                salesController.searchSales(keyword) :
                salesController.searchSalesByUser(keyword, currentUser.getUsername());
        display(list);
    }

    private void display(List<Sale> list) {
        model.setRowCount(0);
        for (Sale s : list) {
            Product p = productController.getProductById(s.getProductId());
            model.addRow(new Object[]{
                    s.getId(),
                    p != null ? p.getName() : "Unknown",
                    s.getQuantitySold(),
                    s.getTotalAmount(),
                    s.getSoldBy(),
                    p != null ? p.getQuantity() : 0
            });
        }
        if (list.isEmpty()) JOptionPane.showMessageDialog(this, "No sales found!");
    }

    // --- Test Main ---
    public static void main(String[] args) {
        User staff = new User();
        staff.setUsername("staff1");
        staff.setRole("Staff");

        SwingUtilities.invokeLater(() -> new SalesView(staff).setVisible(true));
    }
}
