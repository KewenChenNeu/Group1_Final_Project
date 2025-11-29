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
public class MaterialDeliveryConfirmationRequest extends WorkRequest {
    
    private String materialName;
    private int quantityDelivered;
    private String unit;
    private Date deliveryDate;
    private String deliveredBy;  // Delivery staff name
    private String receivedBy;   // Person who received at destination
    private String signatureConfirmation;
    private String deliveryNotes;
    private boolean confirmed;
    private MaterialShippingRequest originalShippingRequest;
    
    public MaterialDeliveryConfirmationRequest() {
        super();
        this.confirmed = false;
        this.deliveryDate = new Date();
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

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public MaterialShippingRequest getOriginalShippingRequest() {
        return originalShippingRequest;
    }

    public void setOriginalShippingRequest(MaterialShippingRequest originalShippingRequest) {
        this.originalShippingRequest = originalShippingRequest;
    }
    
    @Override
    public String toString() {
        return "Material Delivery Confirm #" + getRequestId() + " - " + materialName + (confirmed ? " [Confirmed]" : " [Pending]");
    }
}

