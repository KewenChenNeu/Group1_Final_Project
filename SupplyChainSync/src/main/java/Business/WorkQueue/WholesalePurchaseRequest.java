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
public class WholesalePurchaseRequest extends WorkRequest {
    
    // Product reference (preferred)
    private Product product;
    
    // Product details (for display/backup)
    private String productCode;
    private String productName;
    private int quantity;
    private String unit;
    private double unitPrice;
    private double discount;  // Percentage discount
    private String paymentTerms;
    private String urgencyLevel;  // Normal, Urgent, Critical
    
    public WholesalePurchaseRequest() {
        super();
        this.urgencyLevel = "Normal";
        this.discount = 0.0;
    }
    
    public WholesalePurchaseRequest(Product product, int quantity) {
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

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

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
    
    // ==================== Calculated Fields ====================
    
    public double getSubtotal() {
        return quantity * unitPrice;
    }
    
    public double getDiscountAmount() {
        return getSubtotal() * discount / 100;
    }
    
    public double getTotalPrice() {
        return getSubtotal() - getDiscountAmount();
    }
    
    @Override
    public String toString() {
        return "Wholesale Purchase #" + getRequestId() + " - " + productName + " (" + quantity + " " + unit + ")";
    }
}

