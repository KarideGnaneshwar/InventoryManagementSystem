package controller;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import dao.SalesDAO;
import model.Sale;
import model.Product;

public class SalesController {
    private SalesDAO salesDAO;

    public SalesController() {
        salesDAO = new SalesDAO();
    }

    public boolean recordSale(Sale sale, String username) {
        Product product = salesDAO.getProductById(sale.getProductId());
        if (product == null) return false;
        if (sale.getQuantitySold() > product.getQuantity()) return false;
        return salesDAO.recordSale(sale, username);
    }

    public Map<String, Double> getStaffSales(Timestamp startDate, Timestamp endDate) {
        return salesDAO.getStaffSales(startDate, endDate);
    }

    public List<Sale> getSalesByDateRange(Timestamp startDate, Timestamp endDate) {
        // ✅ make sure this method exists in DAO
        return salesDAO.getSalesByDateRange(startDate, endDate);
    }

    public List<Sale> getAllSales() { return salesDAO.getAllSales(); }
    public List<Sale> getSalesByUser(String username) { return salesDAO.getSalesByUser(username); }
    public List<Sale> searchSales(String keyword) { return salesDAO.searchSales(keyword); }
    public List<Sale> searchSalesByUser(String keyword, String username) {
        return salesDAO.searchSalesByUser(keyword, username);
    }
    public double getTotalRevenue() { return salesDAO.getTotalRevenue(); }
}
