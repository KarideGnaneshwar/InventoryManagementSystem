package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

import controller.SalesController;
import model.User;

public class StaffPerformanceView extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private SalesController salesController;
    private User currentUser;
    private JComboBox<String> periodDropdown;
    private JSpinner fromDateSpinner, toDateSpinner;
    private JButton loadBtn;

    public StaffPerformanceView(User user) {
        this.currentUser = user;
        salesController = new SalesController();

        setTitle("Staff Performance");
        setSize(750, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));

        // --- Header ---
        JLabel title = new JLabel("📈 Staff Performance", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // --- Table ---
        model = new DefaultTableModel(new String[]{"Staff Username", "Total Sales"}, 0);
        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setEnabled(false);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- Bottom Panel ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setBackground(new Color(245, 245, 245));

        // --- Filter dropdown ---
        periodDropdown = new JComboBox<>(new String[]{
                "This Day", "Last 7 Days", "This Month", "Last Month", "Custom Range"
        });
        bottomPanel.add(periodDropdown);

        // --- Custom range pickers ---
        fromDateSpinner = new JSpinner(new SpinnerDateModel());
        fromDateSpinner.setEditor(new JSpinner.DateEditor(fromDateSpinner, "yyyy-MM-dd"));
        toDateSpinner = new JSpinner(new SpinnerDateModel());
        toDateSpinner.setEditor(new JSpinner.DateEditor(toDateSpinner, "yyyy-MM-dd"));
        bottomPanel.add(new JLabel("From:"));
        bottomPanel.add(fromDateSpinner);
        bottomPanel.add(new JLabel("To:"));
        bottomPanel.add(toDateSpinner);
        fromDateSpinner.setVisible(false);
        toDateSpinner.setVisible(false);

        // --- Load Button ---
        loadBtn = new JButton("🔄 Load");
        loadBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loadBtn.addActionListener(e -> loadPerformance());
        bottomPanel.add(loadBtn);

        // --- Back Button ---
        JButton backBtn = new JButton("⬅️ Back");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.addActionListener(e -> {
            dispose();
            new MainFrame(currentUser).setVisible(true);
        });
        bottomPanel.add(backBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        // --- Handle dropdown change ---
        periodDropdown.addActionListener(e -> {
            boolean isCustom = periodDropdown.getSelectedItem().equals("Custom Range");
            fromDateSpinner.setVisible(isCustom);
            toDateSpinner.setVisible(isCustom);

            if (isCustom) {
                fromDateSpinner.setValue(new Date());
                toDateSpinner.setValue(new Date());
            } else {
                loadPerformance(); // auto-load for preset ranges
            }
        });

        loadPerformance(); // initial load
    }

    private void loadPerformance() {
        Timestamp[] range = getSelectedDateRange();
        Map<String, Double> staffSales = salesController.getStaffSales(range[0], range[1]);
        model.setRowCount(0);

        for (Map.Entry<String, Double> entry : staffSales.entrySet()) {
            model.addRow(new Object[]{entry.getKey(), entry.getValue()});
        }

        if (staffSales.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No sales found for selected period!");
        }
    }

    private Timestamp[] getSelectedDateRange() {
        String period = (String) periodDropdown.getSelectedItem();
        LocalDate now = LocalDate.now();
        LocalDate startDate;
        LocalDate endDate;

        switch (period) {
            case "This Day":
                startDate = now;
                endDate = now;
                break;
            case "Last 7 Days":
                startDate = now.minusDays(7);
                endDate = now;
                break;
            case "This Month":
                startDate = now.withDayOfMonth(1);
                endDate = now;
                break;
            case "Last Month":
                YearMonth lastMonth = YearMonth.now().minusMonths(1);
                startDate = lastMonth.atDay(1);
                endDate = lastMonth.atEndOfMonth();
                break;
            case "Custom Range":
                Date from = (Date) fromDateSpinner.getValue();
                Date to = (Date) toDateSpinner.getValue();

                LocalDate fromLD = new java.sql.Date(from.getTime()).toLocalDate();
                LocalDate toLD = new java.sql.Date(to.getTime()).toLocalDate();

                if (fromLD.isAfter(toLD)) {
                    JOptionPane.showMessageDialog(this, "Start date cannot be after end date!");
                    fromLD = now;
                    toLD = now;
                }

                return new Timestamp[]{
                        Timestamp.valueOf(fromLD.atStartOfDay()),
                        Timestamp.valueOf(toLD.atTime(23, 59, 59))
                };
            default:
                startDate = now;
                endDate = now;
        }

        return new Timestamp[]{
                Timestamp.valueOf(startDate.atStartOfDay()),
                Timestamp.valueOf(endDate.atTime(23, 59, 59))
        };
    }
}
