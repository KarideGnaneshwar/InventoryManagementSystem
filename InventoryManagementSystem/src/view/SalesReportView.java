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
import controller.ProductController;
import model.Sale;
import model.Product;
import model.User;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

public class SalesReportView extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private SalesController salesController;
    private ProductController productController;
    private User currentUser;
    private JComboBox<String> periodDropdown;
    private JSpinner fromDateSpinner, toDateSpinner;
    private JButton loadBtn;

    public SalesReportView(User user) {
        this.currentUser = user;
        salesController = new SalesController();
        productController = new ProductController();

        setTitle("Sales Report");
        setSize(950, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));

        JLabel title = new JLabel("📊 Sales Report", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"Sale ID", "Product", "Quantity Sold", "Total Amount", "Sold By", "Sale Date"}, 0);
        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setEnabled(false);
        add(new JScrollPane(table), BorderLayout.CENTER);

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
        loadBtn.addActionListener(e -> loadReport());
        bottomPanel.add(loadBtn);

        // --- Chart Button ---
        JButton chartBtn = new JButton("📈 View Chart");
        chartBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        chartBtn.addActionListener(e -> showSalesChart());
        bottomPanel.add(chartBtn);

        // --- Back Button ---
        JButton backBtn = new JButton("⬅️ Back");
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.addActionListener(e -> {
            dispose();
            new MainFrame(currentUser).setVisible(true);
        });
        bottomPanel.add(backBtn);

        add(bottomPanel, BorderLayout.SOUTH);

        // --- Handle period selection ---
        periodDropdown.addActionListener(e -> {
            boolean isCustom = periodDropdown.getSelectedItem().equals("Custom Range");
            fromDateSpinner.setVisible(isCustom);
            toDateSpinner.setVisible(isCustom);

            if (isCustom) {
                // Default both to today’s date (user can change)
                fromDateSpinner.setValue(new Date());
                toDateSpinner.setValue(new Date());
            } else {
                loadReport(); // auto-load for predefined periods
            }
        });

        loadReport(); // initial load
    }

    private void loadReport() {
        Timestamp[] range = getSelectedDateRange();
        List<Sale> list = salesController.getSalesByDateRange(range[0], range[1]);
        model.setRowCount(0);

        for (Sale s : list) {
            Product p = productController.getProductById(s.getProductId());
            model.addRow(new Object[]{
                    s.getId(),
                    p != null ? p.getName() : "Unknown",
                    s.getQuantitySold(),
                    s.getTotalAmount(),
                    s.getSoldBy(),
                    s.getSaleDate()
            });
        }

        if (list.isEmpty()) {
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

                // ✅ Normalize to full-day range
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

    private void showSalesChart() {
        Timestamp[] range = getSelectedDateRange();
        String period = (String) periodDropdown.getSelectedItem();
        List<Sale> list = salesController.getSalesByDateRange(range[0], range[1]);

        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No sales data to display in chart!");
            return;
        }

        Map<String, Double> totals = new HashMap<>();
        for (Sale s : list) {
            Product p = productController.getProductById(s.getProductId());
            String productName = p != null ? p.getName() : "Unknown";
            totals.put(productName, totals.getOrDefault(productName, 0.0) + s.getTotalAmount());
        }

        DefaultPieDataset dataset = new DefaultPieDataset();
        for (String product : totals.keySet()) {
            dataset.setValue(product, totals.get(product));
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Sales Distribution (" + period + ")",
                dataset,
                true,
                true,
                false
        );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})"));

        JFrame chartFrame = new JFrame("Sales Chart - " + period);
        chartFrame.setSize(800, 600);
        chartFrame.setLocationRelativeTo(this);
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.add(new ChartPanel(chart));
        chartFrame.setVisible(true);
    }
}
