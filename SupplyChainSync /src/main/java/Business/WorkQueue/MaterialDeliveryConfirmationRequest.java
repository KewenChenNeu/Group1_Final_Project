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
public class MaterialDeliveryConfirmationRequest extends WorkRequest {
    
    // Condition constants
    public static final String CONDITION_GOOD = "Good";
    public static final String CONDITION_DAMAGED = "Damaged";
    public static final String CONDITION_PARTIAL = "Partial";
    
    // Material reference
    private Material material;
    
    // Material details
    private String materialCode;
    private String materialName;
    private int quantityDelivered;
    private String unit;
    
    // Delivery details
    private Date deliveryDate;
    private String deliveredBy;  // Delivery staff name
    private String receivedBy;   // Person who received at destination
    private String signatureConfirmation;
    private String deliveryNotes;
    private String conditionOnArrival;
    
    // Confirmation status
    private boolean confirmed;
    private Date confirmationDate;
    
    // Related request
    private MaterialShippingRequest originalShippingRequest;
    
    public MaterialDeliveryConfirmationRequest() {
        super();
        this.confirmed = false;
        this.deliveryDate = new Date();
        this.conditionOnArrival = CONDITION_GOOD;
    }
    
    public MaterialDeliveryConfirmationRequest(Material material, int quantity) {
        this();
        setMaterial(material);
        this.quantityDelivered = quantity;
    }
    
    public MaterialDeliveryConfirmationRequest(MaterialShippingRequest shippingRequest) {
        this();
        this.originalShippingRequest = shippingRequest;
        if (shippingRequest != null) {
            setMaterial(shippingRequest.getMaterial());
            this.quantityDelivered = shippingRequest.getQuantity();
            this.materialCode = shippingRequest.getMaterialCode();
            this.materialName = shippingRequest.getMaterialName();
            this.unit = shippingRequest.getUnit();
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

    public MaterialShippingRequest getOriginalShippingRequest() {
        return originalShippingRequest;
    }

    public void setOriginalShippingRequest(MaterialShippingRequest originalShippingRequest) {
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
        return "Material Delivery Confirm #" + getRequestId() + " - " + materialName + (confirmed ? " [Confirmed]" : " [Pending]");
    }
}
