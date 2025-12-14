package model;

import java.sql.Timestamp;

public class Sale {
	private int id;
	private int productId;
	private int quantitySold;
	private double totalAmount;
	private String soldBy;
	private Timestamp saleDate;

    public Sale() {}

    // Constructor for inserting new sale
    public Sale(int productId, int quantitySold, double totalAmount) {
        this.productId = productId;
        this.quantitySold = quantitySold;
        this.totalAmount = totalAmount;
    }

    // Constructor for retrieving sale from DB
    public Sale(int id, int productId, int quantitySold, double totalAmount, Timestamp saleDate, String soldBy) {
        this.id = id;
        this.productId = productId;
        this.quantitySold = quantitySold;
        this.totalAmount = totalAmount;
        this.saleDate = saleDate;
        this.soldBy = soldBy;
    }

    // --- Getters & Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantitySold() { return quantitySold; }
    public void setQuantitySold(int quantitySold) { this.quantitySold = quantitySold; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public Timestamp getSaleDate() { return saleDate; }
    public void setSaleDate(Timestamp saleDate) { this.saleDate = saleDate; }

    public String getSoldBy() { return soldBy; }
    public void setSoldBy(String soldBy) { this.soldBy = soldBy; }

    @Override
    public String toString() {
        return "Sale [id=" + id + ", productId=" + productId + ", quantitySold=" + quantitySold +
               ", totalAmount=" + totalAmount + ", soldBy=" + soldBy + "]";
    }
}
