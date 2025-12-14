package controller;

import java.util.List;
import dao.ProductDAO;
import model.Product;

public class ProductController {
    private ProductDAO productDAO;

    public ProductController() {
        productDAO = new ProductDAO();
    }

    // Add new product
    public boolean addProduct(Product product, String username) {
        return productDAO.addProduct(product, username);
    }

    // Update existing product
    public boolean updateProduct(Product product, String username) {
        return productDAO.updateProduct(product, username);
    }

    // Delete product
    public boolean deleteProduct(int id, String username) {
        return productDAO.deleteProduct(id, username);
    }

    // Get all products
    public List<Product> getAllProducts() {
        return productDAO.getAllProducts();
    }

    // Search by keyword
    public List<Product> searchProducts(String keyword) {
        return productDAO.searchProducts(keyword);
    }

    // Get product by ID
    public Product getProductById(int id) {
        return productDAO.getProductById(id);
    }

    // Get product by Name
    public Product getProductByName(String name) {
        return productDAO.getProductByName(name);
    }

    // Low stock check
    public List<Product> getLowStockProducts(int threshold) {
        return productDAO.getLowStockProducts(threshold);
    }
}
