/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Product;

/**
 *
 * @author chris
 */
public class Product {
    
    private static int counter = 1000;
    
    private int productId;
    private String productCode;      // e.g., "EC-001", "HA-002"
    private String productName;      // e.g., "Electronic Components Set A"
    private String description;
    private String category;         // e.g., "Electronics", "Appliances", "Furniture"
    private double unitPrice;
    private String unit;             // e.g., "sets", "units", "pieces"
    private boolean active;          // Is product currently available

    public Product() {
        this.productId = counter++;
        this.active = true;
    }
    
    public Product(String productCode, String productName, double unitPrice, String unit) {
        this();
        this.productCode = productCode;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.unit = unit;
    }

    // Getters and Setters
    public int getProductId() {
        return productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return productCode + " - " + productName;
    }
}
