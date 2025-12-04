/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.WorkQueue;

import Business.Material.Material;

/**
 *
 * @author chris
 */
public class RawMaterialRestockRequest extends WorkRequest {
    
    // Material reference (preferred)
    private Material material;
    
    // Material details (for display/backup)
    private String materialCode;
    private String materialName;
    private int quantity;
    private String unit;  // kg, pieces, liters, etc.
    private double unitPrice;
    private String urgencyLevel;  // Normal, Urgent, Critical
    
    public RawMaterialRestockRequest() {
        super();
        this.urgencyLevel = "Normal";
    }
    
    public RawMaterialRestockRequest(Material material, int quantity) {
        this();
        setMaterial(material);
        this.quantity = quantity;
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
            this.unitPrice = material.getUnitPrice();
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
    
    public double getTotalPrice() {
        return quantity * unitPrice;
    }
    
    @Override
    public String toString() {
        return "RM Restock #" + getRequestId() + " - " + materialName + " (" + quantity + " " + unit + ")";
    }
}

