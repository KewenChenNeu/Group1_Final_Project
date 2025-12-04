/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.WorkQueue;

import Business.Product.Product;
import java.util.Date;

/**
 *
 * @author chris
 */
public class DeliveryConfirmationRequest extends WorkRequest {
    
    // Condition constants
    public static final String CONDITION_GOOD = "Good";
    public static final String CONDITION_DAMAGED = "Damaged";
    public static final String CONDITION_PARTIAL = "Partial";
    
    // Product reference
    private Product product;
    
    // Product details
    private String productCode;
    private String productName;
    private int quantityDelivered;
    private String unit;
    
    // Store information
    private String storeName;
    
    // Delivery details
    private Date deliveryDate;
    private String deliveredBy;  // Delivery staff name
    private String receivedBy;   // Store staff who received
    private String signatureConfirmation;
    private String deliveryNotes;
    private String conditionOnArrival;
    
    // Confirmation status
    private boolean confirmed;
    private Date confirmationDate;
    
    // Related request
    private ProductShippingRequest originalShippingRequest;
    
    public DeliveryConfirmationRequest() {
        super();
        this.confirmed = false;
        this.deliveryDate = new Date();
        this.conditionOnArrival = CONDITION_GOOD;
    }
    
    public DeliveryConfirmationRequest(Product product, int quantity) {
        this();
        setProduct(product);
        this.quantityDelivered = quantity;
    }
    
    public DeliveryConfirmationRequest(ProductShippingRequest shippingRequest) {
        this();
        this.originalShippingRequest = shippingRequest;
        if (shippingRequest != null) {
            setProduct(shippingRequest.getProduct());
            this.quantityDelivered = shippingRequest.getQuantity();
            this.productCode = shippingRequest.getProductCode();
            this.productName = shippingRequest.getProductName();
            this.unit = shippingRequest.getUnit();
            this.storeName = shippingRequest.getDestinationStoreName();
        }
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

    // ==================== Store Information ====================

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    // ==================== Delivery Details ====================

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

    // ==================== Confirmation Status ====================

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
        if (confirmed) {
            this.confirmationDate = new Date();
            setStatus(STATUS_COMPLETED);
        }
    }

    public Date getConfirmationDate() {
        return confirmationDate;
    }

    public void setConfirmationDate(Date confirmationDate) {
        this.confirmationDate = confirmationDate;
    }
    
    /**
     * Confirm the delivery
     */
    public void confirmDelivery(String receivedBy) {
        this.receivedBy = receivedBy;
        setConfirmed(true);
    }

    // ==================== Related Request ====================

    public ProductShippingRequest getOriginalShippingRequest() {
        return originalShippingRequest;
    }

    public void setOriginalShippingRequest(ProductShippingRequest originalShippingRequest) {
        this.originalShippingRequest = originalShippingRequest;
    }
    
    // ==================== Utility Methods ====================
    
    public boolean isDamaged() {
        return CONDITION_DAMAGED.equals(conditionOnArrival);
    }
    
    public boolean isPartial() {
        return CONDITION_PARTIAL.equals(conditionOnArrival);
    }
    
    @Override
    public String toString() {
        return "Delivery Confirm #" + getRequestId() + " - " + productName + " to " + storeName + (confirmed ? " [Confirmed]" : " [Pending]");
    }
}
