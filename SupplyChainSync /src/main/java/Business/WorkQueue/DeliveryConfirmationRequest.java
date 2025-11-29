/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.WorkQueue;

import java.util.Date;

/**
 *
 * @author chris
 */
public class DeliveryConfirmationRequest extends WorkRequest {
    
    private String productName;
    private String productCode;
    private int quantityDelivered;
    private String unit;
    private String storeName;
    private Date deliveryDate;
    private String deliveredBy;  // Delivery staff name
    private String receivedBy;   // Store staff who received
    private String signatureConfirmation;
    private String deliveryNotes;
    private String conditionOnArrival;  // Good, Damaged, Partial
    private boolean confirmed;
    private ProductShippingRequest originalShippingRequest;
    
    public DeliveryConfirmationRequest() {
        super();
        this.confirmed = false;
        this.deliveryDate = new Date();
        this.conditionOnArrival = "Good";
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

    public int getQuantityDelivered() {
        return quantityDelivered;
    }

    public void setQuantityDelivered(int quantityDelivered) {
        this.quantityDelivered = quantityDelivered;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveredBy() {
        return deliveredBy;
    }

    public void setDeliveredBy(String deliveredBy) {
        this.deliveredBy = deliveredBy;
    }

    public String getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(String receivedBy) {
        this.receivedBy = receivedBy;
    }

    public String getSignatureConfirmation() {
        return signatureConfirmation;
    }

    public void setSignatureConfirmation(String signatureConfirmation) {
        this.signatureConfirmation = signatureConfirmation;
    }

    public String getDeliveryNotes() {
        return deliveryNotes;
    }

    public void setDeliveryNotes(String deliveryNotes) {
        this.deliveryNotes = deliveryNotes;
    }

    public String getConditionOnArrival() {
        return conditionOnArrival;
    }

    public void setConditionOnArrival(String conditionOnArrival) {
        this.conditionOnArrival = conditionOnArrival;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public ProductShippingRequest getOriginalShippingRequest() {
        return originalShippingRequest;
    }

    public void setOriginalShippingRequest(ProductShippingRequest originalShippingRequest) {
        this.originalShippingRequest = originalShippingRequest;
    }
    
    @Override
    public String toString() {
        return "Delivery Confirm #" + getRequestId() + " - " + productName + " to " + storeName + (confirmed ? " [Confirmed]" : " [Pending]");
    }
}

