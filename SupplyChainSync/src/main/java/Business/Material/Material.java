/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Material;

/**
 *
 * @author chris
 */
public class Material {
    
    private static int counter = 2000;
    
    private int materialId;
    private String materialCode;     // e.g., "RM-001", "RM-002"
    private String materialName;     // e.g., "Steel Sheets", "Copper Wire"
    private String description;
    private String category;         // e.g., "Metal", "Wood", "Chemical", "Plastic"
    private double unitPrice;
    private String unit;             // e.g., "kg", "meters", "liters", "sheets"
    private boolean active;          // Is material currently available

    public Material() {
        this.materialId = counter++;
        this.active = true;
    }
    
    public Material(String materialCode, String materialName, double unitPrice, String unit) {
        this();
        this.materialCode = materialCode;
        this.materialName = materialName;
        this.unitPrice = unitPrice;
        this.unit = unit;
    }

    // Getters and Setters
    public int getMaterialId() {
        return materialId;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return materialCode + " - " + materialName;
    }
}

