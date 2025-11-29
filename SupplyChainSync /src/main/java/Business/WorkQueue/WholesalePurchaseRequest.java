/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.WorkQueue;

/**
 *
 * @author chris
 */
public class WholesalePurchaseRequest extends WorkRequest {
    
    private String productName;
    private String productCode;
    private int quantity;
    private String unit;
    private double unitPrice;
    private double discount;  // Percentage discount
    private String paymentTerms;
    private String urgencyLevel;  // Normal, Urgent, Critical
    private String notes;
    
    public WholesalePurchaseRequest() {
        super();
        this.urgencyLevel = "Normal";
        this.discount = 0.0;
    }

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
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public double getTotalPrice() {
        double subtotal = quantity * unitPrice;
        return subtotal - (subtotal * discount / 100);
    }
    
    @Override
    public String toString() {
        return "Wholesale Purchase #" + getRequestId() + " - " + productName + " (" + quantity + " " + unit + ")";
    }
}

