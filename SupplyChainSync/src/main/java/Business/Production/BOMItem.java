/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Production;

import Business.Material.Material;

/**
 *
 * @author chris
 */
public class BOMItem {
    
    private Material material;          // The raw material needed
    private double quantityRequired;    // How much of this material is needed
    private String unit;                // Unit of measurement (should match material's unit)

    public BOMItem() {
    }
    
    public BOMItem(Material material, double quantityRequired) {
        this.material = material;
        this.quantityRequired = quantityRequired;
        this.unit = material != null ? material.getUnit() : "units";
    }

    // Getters and Setters
    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
        if (material != null && this.unit == null) {
            this.unit = material.getUnit();
        }
    }

    public double getQuantityRequired() {
        return quantityRequired;
    }

    public void setQuantityRequired(double quantityRequired) {
        this.quantityRequired = quantityRequired;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    /**
     * Calculate total material needed for producing a given quantity of product
     */
    public double calculateTotalMaterialNeeded(int productQuantity) {
        return quantityRequired * productQuantity;
    }

    @Override
    public String toString() {
        if (material != null) {
            return material.getMaterialName() + " x " + quantityRequired + " " + unit;
        }
        return "Unknown material x " + quantityRequired;
    }
}
