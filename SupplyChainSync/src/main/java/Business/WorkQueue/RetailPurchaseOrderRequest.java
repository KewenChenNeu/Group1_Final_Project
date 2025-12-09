/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.WorkQueue;

import Business.Product.Product;

/**
 *
 * @author chris
 */
public class RetailPurchaseOrderRequest extends WorkRequest {
    
    // Product reference (preferred)
    private Product product;
    
    // Product details (for display/backup)
    private String productCode;
    private String productName;
    private int quantity;
    private String unit;
    private double unitPrice;
    
    // Store information
    private String storeName;
    private String storeAddress;
    
    // Order details
    private String urgencyLevel;  // Normal, Urgent, Critical
    private String paymentMethod;

    // Target distributor information (for approval workflow)
    private String targetDistributorName;
    
    public RetailPurchaseOrderRequest() {
        super();
        this.urgencyLevel = "Normal";
    }
    
    public RetailPurchaseOrderRequest(Product product, int quantity) {
        this();
        setProduct(product);
        this.quantity = quantity;
    }

    // ==================== Product Reference ====================
    
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        if (product != null) {
            this.productCode = product.getProductCode();
            this.productName = product.getProductName();
            this.unit = product.getUnit();
            this.unitPrice = product.getUnitPrice();
        }
    }

    // ==================== Product Details ====================

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    // ==================== Store Information ====================

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    // ==================== Order Details ====================

    public String getUrgencyLevel() {
        return urgencyLevel;
    }

    public void setUrgencyLevel(String urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
        // Set priority based on urgency
        if ("Critical".equalsIgnoreCase(urgencyLevel)) {
            setPriority(1);
        } else if ("Urgent".equalsIgnoreCase(urgencyLevel)) {
            setPriority(2);
        } else {
            setPriority(3);
        }
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    // ==================== Target Distributor ====================

    public String getTargetDistributorName() {
        return targetDistributorName;
    }

    public void setTargetDistributorName(String targetDistributorName) {
        this.targetDistributorName = targetDistributorName;
    }

    // ==================== Calculated Fields ====================
    
    public double getTotalPrice() {
        return quantity * unitPrice;
    }
    
    @Override
    public String toString() {
        return "Retail PO #" + getRequestId() + " - " + productName + " (" + quantity + " " + unit + ")";
    }
}

