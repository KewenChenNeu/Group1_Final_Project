/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Inventory;

import Business.Product.Product;
import Business.Material.Material;
import java.util.Date;

/**
 *
 * @author chris
 */
public class InventoryItem {
    
    private static int counter = 3000;
    
    public enum ItemType {
        PRODUCT,
        MATERIAL
    }
    
    private int inventoryItemId;
    private ItemType itemType;
    private Product product;         // Set if itemType is PRODUCT
    private Material material;       // Set if itemType is MATERIAL
    private int quantity;
    private int minStockLevel;       // Low stock alert threshold
    private int maxStockLevel;       // Maximum capacity
    private String location;         // Warehouse/shelf location
    private Date lastUpdated;

    public InventoryItem() {
        this.inventoryItemId = counter++;
        this.lastUpdated = new Date();
    }
    
    /**
     * Create inventory item for a Product
     */
    public InventoryItem(Product product, int quantity) {
        this();
        this.itemType = ItemType.PRODUCT;
        this.product = product;
        this.quantity = quantity;
    }
    
    /**
     * Create inventory item for a Material
     */
    public InventoryItem(Material material, int quantity) {
        this();
        this.itemType = ItemType.MATERIAL;
        this.material = material;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getInventoryItemId() {
        return inventoryItemId;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.itemType = ItemType.PRODUCT;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
        this.itemType = ItemType.MATERIAL;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.lastUpdated = new Date();
    }
    
    /**
     * Add quantity to inventory
     */
    public void addQuantity(int amount) {
        this.quantity += amount;
        this.lastUpdated = new Date();
    }
    
    /**
     * Remove quantity from inventory
     * @return true if successful, false if insufficient stock
     */
    public boolean removeQuantity(int amount) {
        if (this.quantity >= amount) {
            this.quantity -= amount;
            this.lastUpdated = new Date();
            return true;
        }
        return false;
    }

    public int getMinStockLevel() {
        return minStockLevel;
    }

    public void setMinStockLevel(int minStockLevel) {
        this.minStockLevel = minStockLevel;
    }

    public int getMaxStockLevel() {
        return maxStockLevel;
    }

    public void setMaxStockLevel(int maxStockLevel) {
        this.maxStockLevel = maxStockLevel;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    /**
     * Check if stock is below minimum level
     */
    public boolean isLowStock() {
        return quantity <= minStockLevel;
    }
    
    /**
     * Check if stock is at or above maximum level
     */
    public boolean isFullStock() {
        return quantity >= maxStockLevel;
    }
    
    /**
     * Get the name of the item (product or material)
     */
    public String getItemName() {
        if (itemType == ItemType.PRODUCT && product != null) {
            return product.getProductName();
        } else if (itemType == ItemType.MATERIAL && material != null) {
            return material.getMaterialName();
        }
        return "Unknown";
    }
    
    /**
     * Get the code of the item (product or material)
     */
    public String getItemCode() {
        if (itemType == ItemType.PRODUCT && product != null) {
            return product.getProductCode();
        } else if (itemType == ItemType.MATERIAL && material != null) {
            return material.getMaterialCode();
        }
        return "Unknown";
    }
    
    /**
     * Get the unit of the item
     */
    public String getUnit() {
        if (itemType == ItemType.PRODUCT && product != null) {
            return product.getUnit();
        } else if (itemType == ItemType.MATERIAL && material != null) {
            return material.getUnit();
        }
        return "units";
    }

    @Override
    public String toString() {
        return getItemCode() + " - " + getItemName() + " (Qty: " + quantity + ")";
    }
}

