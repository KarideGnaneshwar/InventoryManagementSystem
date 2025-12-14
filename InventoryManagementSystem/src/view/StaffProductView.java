package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import controller.ProductController;
import model.Product;
import model.User;

public class StaffProductView extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private ProductController controller;
    private User currentUser;

    public StaffProductView(User user) {
        this.currentUser = user;
        controller = new ProductController();

        setTitle("Product Catalog - Staff View");
        setSize(850, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(245, 245, 245));
        setLayout(new BorderLayout(10, 10));

        // --- Header ---
        JLabel title = new JLabel("📦 Product Catalog", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // --- Table ---
        model = new DefaultTableModel(new String[]{"ID", "Name", "Category", "Quantity", "Price"}, 0);
        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setEnabled(false); // Non-editable
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Panel for Search & Back ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBackground(new Color(245, 245, 245));

        bottomPanel.add(new JLabel("Search:"));
        searchField = new JTextField(15);
        bottomPanel.add(searchField);

        JButton searchBtn = new JButton("🔍 Search");
        searchBtn.setForeground(Color.BLACK);
        searchBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchBtn.addActionListener(e -> searchProducts());
        bottomPanel.add(searchBtn);

        JButton backBtn = new JButton("⬅️ Back");
        backBtn.setForeground(Color.BLACK);
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.addActionListener(e -> {
            dispose();
            new MainFrame(currentUser).setVisible(true);
        });
        bottomPanel.add(backBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        loadProducts();
    }

    private void loadProducts() {
        List<Product> list = controller.getAllProducts();
        display(list);
    }

    private void searchProducts() {
        String keyword = searchField.getText().trim();
        List<Product> list = controller.searchProducts(keyword);
        display(list);
    }

    private void display(List<Product> list) {
        model.setRowCount(0);
        for (Product p : list) {
            model.addRow(new Object[]{p.getId(), p.getName(), p.getCategory(), p.getQuantity(), p.getPrice()});
        }
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No products found!");
        }
    }

    // --- Test Main ---
    public static void main(String[] args) {
        User staff = new User();
        staff.setUsername("staff");
        staff.setRole("Staff");
        SwingUtilities.invokeLater(() -> new StaffProductView(staff).setVisible(true));
    }
}
