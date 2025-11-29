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
public class WholesalesShippingRequest extends WorkRequest {
    
    private String productName;
    private String productCode;
    private int quantity;
    private String unit;
    private String originAddress;
    private String destinationAddress;
    private Date estimatedDeliveryDate;
    private String trackingNumber;
    private String shippingStatus;  // Pending, Picked Up, In Transit, Delivered
    private double shippingCost;
    private WholesalePurchaseRequest originalPurchaseRequest;
    
    public WholesalesShippingRequest() {
        super();
        this.shippingStatus = "Pending";
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

    public String getOriginAddress() {
        return originAddress;
    }

    public void setOriginAddress(String originAddress) {
        this.originAddress = originAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public Date getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(Date estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getShippingStatus() {
        return shippingStatus;
    }

    public void setShippingStatus(String shippingStatus) {
        this.shippingStatus = shippingStatus;
    }

    public double getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(double shippingCost) {
        this.shippingCost = shippingCost;
    }

    public WholesalePurchaseRequest getOriginalPurchaseRequest() {
        return originalPurchaseRequest;
    }

    public void setOriginalPurchaseRequest(WholesalePurchaseRequest originalPurchaseRequest) {
        this.originalPurchaseRequest = originalPurchaseRequest;
    }
    
    @Override
    public String toString() {
        return "Wholesale Shipping #" + getRequestId() + " - " + productName + " [" + shippingStatus + "]";
    }
}

