/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Production;

import Business.Material.Material;
import Business.Product.Product;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author chris
 */
public class BillOfMaterials {
    
    private Product product;                    // The product this BOM is for
    private ArrayList<BOMItem> bomItems;        // List of materials needed
    private double laborHours;                  // Estimated labor hours per unit
    private double productionCostPerUnit;       // Additional production cost (labor, overhead)

    public BillOfMaterials() {
        this.bomItems = new ArrayList<>();
    }
    
    public BillOfMaterials(Product product) {
        this();
        this.product = product;
    }

    // Getters and Setters
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ArrayList<BOMItem> getBomItems() {
        return bomItems;
    }

    public double getLaborHours() {
        return laborHours;
    }

    public void setLaborHours(double laborHours) {
        this.laborHours = laborHours;
    }

    public double getProductionCostPerUnit() {
        return productionCostPerUnit;
    }

    public void setProductionCostPerUnit(double productionCostPerUnit) {
        this.productionCostPerUnit = productionCostPerUnit;
    }
    
    /**
     * Add a material requirement to this BOM
     */
    public BOMItem addMaterial(Material material, double quantity) {
        BOMItem item = new BOMItem(material, quantity);
        bomItems.add(item);
        return item;
    }
    
    /**
     * Remove a material from this BOM
     */
    public void removeMaterial(Material material) {
        bomItems.removeIf(item -> 
            item.getMaterial() != null && 
            item.getMaterial().getMaterialId() == material.getMaterialId()
        );
    }
    
    /**
     * Get the BOM item for a specific material
     */
    public BOMItem getBOMItem(Material material) {
        for (BOMItem item : bomItems) {
            if (item.getMaterial() != null && 
                item.getMaterial().getMaterialId() == material.getMaterialId()) {
                return item;
            }
        }
        return null;
    }
    
    /**
     * Calculate total material requirements for producing a given quantity
     * @return Map of Material to required quantity
     */
    public Map<Material, Double> calculateMaterialRequirements(int productQuantity) {
        Map<Material, Double> requirements = new HashMap<>();
        for (BOMItem item : bomItems) {
            if (item.getMaterial() != null) {
                double totalNeeded = item.calculateTotalMaterialNeeded(productQuantity);
                requirements.put(item.getMaterial(), totalNeeded);
            }
        }
        return requirements;
    }
    
    /**
     * Calculate total raw material cost for one unit of product
     */
    public double calculateRawMaterialCostPerUnit() {
        double totalCost = 0;
        for (BOMItem item : bomItems) {
            if (item.getMaterial() != null) {
                totalCost += item.getMaterial().getUnitPrice() * item.getQuantityRequired();
            }
        }
        return totalCost;
    }
    
    /**
     * Calculate total production cost per unit (materials + labor/overhead)
     */
    public double calculateTotalCostPerUnit() {
        return calculateRawMaterialCostPerUnit() + productionCostPerUnit;
    }
    
    /**
     * Calculate profit margin based on product selling price
     */
    public double calculateProfitMargin() {
        if (product != null && product.getUnitPrice() > 0) {
            double cost = calculateTotalCostPerUnit();
            double price = product.getUnitPrice();
            return ((price - cost) / price) * 100;
        }
        return 0;
    }

    @Override
    public String toString() {
        if (product != null) {
            return "BOM for " + product.getProductName() + " (" + bomItems.size() + " materials)";
        }
        return "BOM (no product assigned)";
    }
}
