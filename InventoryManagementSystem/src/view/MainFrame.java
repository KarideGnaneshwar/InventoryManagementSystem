package view;

import javax.swing.*;
import java.awt.*;
import model.User;

public class MainFrame extends JFrame {

    private User loggedInUser;

    public MainFrame(User user) {
        this.loggedInUser = user;
        setTitle("Inventory Management System - Dashboard");
        setSize(550, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 242, 245));

        // --- Header ---
        JLabel titleLabel = new JLabel(" Inventory Management System", JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(25, 25, 28));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // --- Welcome Info ---
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        userPanel.setBackground(new Color(240, 242, 245));
        JLabel welcomeLabel = new JLabel("Logged in as: " + user.getUsername() + " (" + user.getRole() + ")");
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        welcomeLabel.setForeground(new Color(60, 60, 60));
        userPanel.add(welcomeLabel);
        add(userPanel, BorderLayout.SOUTH);

        // --- Center Buttons ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 1, 20, 20)); // Vertical stack
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 120, 30, 120));
        buttonPanel.setBackground(Color.WHITE);

        // --- Buttons ---
        JButton manageProductsBtn = createStyledButton("Products", new Color(173, 216, 230)); // Light blue
        JButton recordSalesBtn = createStyledButton("Record Sale", new Color(144, 238, 144)); // Light green
        JButton viewSalesBtn = createStyledButton("View Sales", new Color(255, 223, 186));     // Light orange
        JButton salesReportBtn = createStyledButton("Sales Report", new Color(135, 206, 250)); // Sky blue
        JButton staffPerfBtn = createStyledButton("Staff Performance", new Color(255, 250, 205)); // Lemon chiffon
        JButton logoutBtn = createStyledButton("Logout", new Color(255, 182, 193));           // Light pink

        // --- Button Actions ---
        manageProductsBtn.addActionListener(e -> {
            dispose();
            if ("Admin".equalsIgnoreCase(user.getRole())) {
                new ProductForm(user).setVisible(true); // Full CRUD
            } else {
                new StaffProductView(user).setVisible(true); // View/search only
            }
        });

        recordSalesBtn.addActionListener(e -> {
            dispose();
            new SaleForm(user).setVisible(true);
        });

        viewSalesBtn.addActionListener(e -> {
            dispose();
            new SalesView(user).setVisible(true);
        });

        salesReportBtn.addActionListener(e -> {
            dispose();
            new SalesReportView(user).setVisible(true);
        });

        staffPerfBtn.addActionListener(e -> {
            dispose();
            new StaffPerformanceView(user).setVisible(true);
        });

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });

        // --- Add Buttons (Admin vs Staff) ---
        buttonPanel.add(manageProductsBtn);
        buttonPanel.add(recordSalesBtn);
        buttonPanel.add(viewSalesBtn);

        if ("Admin".equalsIgnoreCase(user.getRole())) {
            buttonPanel.add(salesReportBtn);
            buttonPanel.add(staffPerfBtn);
        }

        buttonPanel.add(logoutBtn);

        add(buttonPanel, BorderLayout.CENTER);
    }

    // --- Helper: Styled Button ---
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // --- Test Main ---
    public static void main(String[] args) {
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setRole("Admin");

        User staffUser = new User();
        staffUser.setUsername("staff");
        staffUser.setRole("Staff");

        SwingUtilities.invokeLater(() -> new MainFrame(adminUser).setVisible(true));
        //SwingUtilities.invokeLater(() -> new MainFrame(staffUser).setVisible(true));
    }
}
