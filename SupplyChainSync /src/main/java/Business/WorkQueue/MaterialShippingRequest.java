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
public class MaterialShippingRequest extends WorkRequest {
    
    private String materialName;
    private int quantity;
    private String unit;
    private String originAddress;
    private String destinationAddress;
    private Date estimatedDeliveryDate;
    private String trackingNumber;
    private String shippingStatus;  // Pending, Picked Up, In Transit, Delivered
    private RawMaterialRestockRequest originalRestockRequest;
    
    public MaterialShippingRequest() {
        super();
        this.shippingStatus = "Pending";
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
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

    public RawMaterialRestockRequest getOriginalRestockRequest() {
        return originalRestockRequest;
    }

    public void setOriginalRestockRequest(RawMaterialRestockRequest originalRestockRequest) {
        this.originalRestockRequest = originalRestockRequest;
    }
    
    @Override
    public String toString() {
        return "Material Shipping #" + getRequestId() + " - " + materialName + " [" + shippingStatus + "]";
    }
}

