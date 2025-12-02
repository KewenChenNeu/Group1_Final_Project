/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.WorkQueue;

import Business.Material.Material;
import java.util.Date;

/**
 *
 * @author chris
 */
public class MaterialShippingRequest extends WorkRequest {
    
    // Shipping status constants
    public static final String SHIP_STATUS_PENDING = "Pending";
    public static final String SHIP_STATUS_PICKED_UP = "Picked Up";
    public static final String SHIP_STATUS_IN_TRANSIT = "In Transit";
    public static final String SHIP_STATUS_DELIVERED = "Delivered";
    
    // Material reference
    private Material material;
    
    // Material details
    private String materialCode;
    private String materialName;
    private int quantity;
    private String unit;
    
    // Shipping details
    private String originAddress;
    private String destinationAddress;
    private Date estimatedDeliveryDate;
    private Date actualDeliveryDate;
    private String trackingNumber;
    private String shippingStatus;
    private String carrierName;
    private double shippingCost;
    
    // Related request
    private RawMaterialRestockRequest originalRestockRequest;
    
    public MaterialShippingRequest() {
        super();
        this.shippingStatus = SHIP_STATUS_PENDING;
    }
    
    public MaterialShippingRequest(Material material, int quantity) {
        this();
        setMaterial(material);
        this.quantity = quantity;
    }
    
    public MaterialShippingRequest(RawMaterialRestockRequest restockRequest) {
        this();
        this.originalRestockRequest = restockRequest;
        if (restockRequest != null) {
            setMaterial(restockRequest.getMaterial());
            this.quantity = restockRequest.getQuantity();
            this.materialCode = restockRequest.getMaterialCode();
            this.materialName = restockRequest.getMaterialName();
            this.unit = restockRequest.getUnit();
        }
    }

    // ==================== Material Reference ====================
    
    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
        if (material != null) {
            this.materialCode = material.getMaterialCode();
            this.materialName = material.getMaterialName();
            this.unit = material.getUnit();
        }
    }

    // ==================== Material Details ====================

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
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

    // ==================== Shipping Details ====================

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

    public Date getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public void setActualDeliveryDate(Date actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
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
        // Update main status as well
        if (SHIP_STATUS_DELIVERED.equals(shippingStatus)) {
            setStatus(STATUS_DELIVERED);
            this.actualDeliveryDate = new Date();
        } else if (SHIP_STATUS_IN_TRANSIT.equals(shippingStatus)) {
            setStatus(STATUS_IN_PROGRESS);
        }
    }

    public String getCarrierName() {
        return carrierName;
    }

    public void setCarrierName(String carrierName) {
        this.carrierName = carrierName;
    }

    public double getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(double shippingCost) {
        this.shippingCost = shippingCost;
    }

    // ==================== Related Request ====================

    public RawMaterialRestockRequest getOriginalRestockRequest() {
        return originalRestockRequest;
    }

    public void setOriginalRestockRequest(RawMaterialRestockRequest originalRestockRequest) {
        this.originalRestockRequest = originalRestockRequest;
    }
    
    // ==================== Utility Methods ====================
    
    public void markPickedUp() {
        setShippingStatus(SHIP_STATUS_PICKED_UP);
    }
    
    public void markInTransit() {
        setShippingStatus(SHIP_STATUS_IN_TRANSIT);
    }
    
    public void markDelivered() {
        setShippingStatus(SHIP_STATUS_DELIVERED);
    }
    
    public boolean isDelivered() {
        return SHIP_STATUS_DELIVERED.equals(shippingStatus);
    }
    
    @Override
    public String toString() {
        return "Material Shipping #" + getRequestId() + " - " + materialName + " [" + shippingStatus + "]";
    }
}

