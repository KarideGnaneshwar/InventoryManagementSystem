package dao;

import java.sql.*;
import java.util.*;
import model.Sale;
import model.Product;
import util.DBConnection;

public class SalesDAO {

    // --- Record sale and update stock ---
    public boolean recordSale(Sale sale, String username) {
        String saleSQL = "INSERT INTO sales (product_id, quantity_sold, total_amount, sold_by) VALUES (?, ?, ?, ?)";
        String updateStockSQL = "UPDATE product SET quantity = quantity - ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(saleSQL);
                 PreparedStatement ps2 = conn.prepareStatement(updateStockSQL)) {

                ps1.setInt(1, sale.getProductId());
                ps1.setInt(2, sale.getQuantitySold());
                ps1.setDouble(3, sale.getTotalAmount());
                ps1.setString(4, username); // sold_by
                ps1.executeUpdate();

                ps2.setInt(1, sale.getQuantitySold());
                ps2.setInt(2, sale.getProductId());
                ps2.executeUpdate();

                conn.commit();
                return true;

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // --- Get all sales ---
    public List<Sale> getAllSales() {
        List<Sale> list = new ArrayList<>();
        String sql = "SELECT * FROM sales ORDER BY sale_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Sale s = new Sale(
                    rs.getInt("id"),
                    rs.getInt("product_id"),
                    rs.getInt("quantity_sold"),
                    rs.getDouble("total_amount"),
                    rs.getTimestamp("sale_date"),
                    rs.getString("sold_by")
                );
                list.add(s);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- Get sales by user ---
    public List<Sale> getSalesByUser(String username) {
        List<Sale> list = new ArrayList<>();
        String sql = "SELECT * FROM sales WHERE sold_by = ? ORDER BY sale_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Sale s = new Sale(
                        rs.getInt("id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity_sold"),
                        rs.getDouble("total_amount"),
                        rs.getTimestamp("sale_date"),
                        rs.getString("sold_by")
                    );
                    list.add(s);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- Search sales for Admin ---
    public List<Sale> searchSales(String keyword) {
        List<Sale> list = new ArrayList<>();
        String sql = "SELECT s.* FROM sales s JOIN product p ON s.product_id = p.id " +
                     "WHERE p.name LIKE ? OR s.sold_by LIKE ? ORDER BY s.sale_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Sale s = new Sale(
                        rs.getInt("id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity_sold"),
                        rs.getDouble("total_amount"),
                        rs.getTimestamp("sale_date"),
                        rs.getString("sold_by")
                    );
                    list.add(s);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- Search sales by Staff ---
    public List<Sale> searchSalesByUser(String keyword, String username) {
        List<Sale> list = new ArrayList<>();
        String sql = "SELECT s.* FROM sales s JOIN product p ON s.product_id = p.id " +
                     "WHERE s.sold_by = ? AND (p.name LIKE ? OR s.sold_by LIKE ?) ORDER BY s.sale_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String kw = "%" + keyword + "%";
            ps.setString(1, username);
            ps.setString(2, kw);
            ps.setString(3, kw);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Sale s = new Sale(
                        rs.getInt("id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity_sold"),
                        rs.getDouble("total_amount"),
                        rs.getTimestamp("sale_date"),
                        rs.getString("sold_by")
                    );
                    list.add(s);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- Get product by ID ---
    public Product getProductById(int productId) {
        Product p = null;
        String sql = "SELECT * FROM product WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    p = new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getDouble("price")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return p;
    }

    // --- Get total revenue ---
    public double getTotalRevenue() {
        String sql = "SELECT SUM(total_amount) AS total FROM sales";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble("total");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
    
 // --- Get total sales per staff within a date range ---
    public Map<String, Double> getStaffSales(Timestamp startDate, Timestamp endDate) {
        Map<String, Double> map = new LinkedHashMap<>();
        String sql = "SELECT sold_by, SUM(total_amount) AS total FROM sales " +
                     "WHERE sale_date BETWEEN ? AND ? GROUP BY sold_by";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, startDate);
            ps.setTimestamp(2, endDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString("sold_by"), rs.getDouble("total"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    // --- Get sales within a date range ---
    public List<Sale> getSalesByDateRange(Timestamp startDate, Timestamp endDate) {
        List<Sale> salesList = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM sales WHERE sale_date BETWEEN ? AND ? ORDER BY sale_date ASC")) {

            stmt.setTimestamp(1, startDate);
            stmt.setTimestamp(2, endDate);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Sale s = new Sale();
                s.setId(rs.getInt("id"));
                s.setProductId(rs.getInt("product_id"));
                s.setQuantitySold(rs.getInt("quantity_sold"));
                s.setTotalAmount(rs.getDouble("total_amount"));
                s.setSoldBy(rs.getString("sold_by"));
                s.setSaleDate(rs.getTimestamp("sale_date"));
                salesList.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return salesList;
    }


}
