/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.WorkQueue;

/**
 *
 * @author chris
 */
public class RawMaterialRestockRequest extends WorkRequest {
    
    private String materialName;
    private int quantity;
    private String unit;  // kg, pieces, liters, etc.
    private double unitPrice;
    private String urgencyLevel;  // Normal, Urgent, Critical
    private String notes;
    
    public RawMaterialRestockRequest() {
        super();
        this.urgencyLevel = "Normal";
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

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
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
        return quantity * unitPrice;
    }
    
    @Override
    public String toString() {
        return "RM Restock #" + getRequestId() + " - " + materialName + " (" + quantity + " " + unit + ")";
    }
}

