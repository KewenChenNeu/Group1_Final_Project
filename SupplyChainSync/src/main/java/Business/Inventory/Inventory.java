/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Inventory;

import Business.Product.Product;
import Business.Material.Material;
import java.util.ArrayList;

/**
 *
 * @author chris
 */
public class Inventory {
    
    private ArrayList<InventoryItem> inventoryList;

    public Inventory() {
        this.inventoryList = new ArrayList<>();
    }

    public ArrayList<InventoryItem> getInventoryList() {
        return inventoryList;
    }
    
    // ==================== Product Inventory Methods ====================
    
    /**
     * Add a product to inventory
     */
    public InventoryItem addProduct(Product product, int quantity) {
        // Check if product already exists in inventory
        InventoryItem existing = findByProduct(product);
        if (existing != null) {
            existing.addQuantity(quantity);
            return existing;
        }
        // Create new inventory item
        InventoryItem item = new InventoryItem(product, quantity);
        inventoryList.add(item);
        return item;
    }
    
    /**
     * Add a product with full details
     */
    public InventoryItem addProduct(Product product, int quantity, int minStock, int maxStock, String location) {
        InventoryItem item = addProduct(product, quantity);
        item.setMinStockLevel(minStock);
        item.setMaxStockLevel(maxStock);
        item.setLocation(location);
        return item;
    }
    
    /**
     * Find inventory item by product
     */
    public InventoryItem findByProduct(Product product) {
        for (InventoryItem item : inventoryList) {
            if (item.getItemType() == InventoryItem.ItemType.PRODUCT 
                && item.getProduct() != null 
                && item.getProduct().getProductId() == product.getProductId()) {
                return item;
            }
        }
        return null;
    }
    
    /**
     * Find inventory item by product code
     */
    public InventoryItem findByProductCode(String productCode) {
        for (InventoryItem item : inventoryList) {
            if (item.getItemType() == InventoryItem.ItemType.PRODUCT 
                && item.getProduct() != null 
                && item.getProduct().getProductCode().equalsIgnoreCase(productCode)) {
                return item;
            }
        }
        return null;
    }
    
    /**
     * Remove quantity of a product from inventory
     * @return true if successful, false if insufficient stock
     */
    public boolean removeProduct(Product product, int quantity) {
        InventoryItem item = findByProduct(product);
        if (item != null) {
            return item.removeQuantity(quantity);
        }
        return false;
    }
    
    /**
     * Get all product inventory items
     */
    public ArrayList<InventoryItem> getProductInventory() {
        ArrayList<InventoryItem> result = new ArrayList<>();
        for (InventoryItem item : inventoryList) {
            if (item.getItemType() == InventoryItem.ItemType.PRODUCT) {
                result.add(item);
            }
        }
        return result;
    }
    
    // ==================== Material Inventory Methods ====================
    
    /**
     * Add a material to inventory
     */
    public InventoryItem addMaterial(Material material, int quantity) {
        // Check if material already exists in inventory
        InventoryItem existing = findByMaterial(material);
        if (existing != null) {
            existing.addQuantity(quantity);
            return existing;
        }
        // Create new inventory item
        InventoryItem item = new InventoryItem(material, quantity);
        inventoryList.add(item);
        return item;
    }
    
    /**
     * Add a material with full details
     */
    public InventoryItem addMaterial(Material material, int quantity, int minStock, int maxStock, String location) {
        InventoryItem item = addMaterial(material, quantity);
        item.setMinStockLevel(minStock);
        item.setMaxStockLevel(maxStock);
        item.setLocation(location);
        return item;
    }
    
    /**
     * Find inventory item by material
     */
    public InventoryItem findByMaterial(Material material) {
        for (InventoryItem item : inventoryList) {
            if (item.getItemType() == InventoryItem.ItemType.MATERIAL 
                && item.getMaterial() != null 
                && item.getMaterial().getMaterialId() == material.getMaterialId()) {
                return item;
            }
        }
        return null;
    }
    
    /**
     * Find inventory item by material code
     */
    public InventoryItem findByMaterialCode(String materialCode) {
        for (InventoryItem item : inventoryList) {
            if (item.getItemType() == InventoryItem.ItemType.MATERIAL 
                && item.getMaterial() != null 
                && item.getMaterial().getMaterialCode().equalsIgnoreCase(materialCode)) {
                return item;
            }
        }
        return null;
    }
    
    /**
     * Remove quantity of a material from inventory
     * @return true if successful, false if insufficient stock
     */
    public boolean removeMaterial(Material material, int quantity) {
        InventoryItem item = findByMaterial(material);
        if (item != null) {
            return item.removeQuantity(quantity);
        }
        return false;
    }
    
    /**
     * Get all material inventory items
     */
    public ArrayList<InventoryItem> getMaterialInventory() {
        ArrayList<InventoryItem> result = new ArrayList<>();
        for (InventoryItem item : inventoryList) {
            if (item.getItemType() == InventoryItem.ItemType.MATERIAL) {
                result.add(item);
            }
        }
        return result;
    }
    
    // ==================== General Inventory Methods ====================
    
    /**
     * Get all items with low stock
     */
    public ArrayList<InventoryItem> getLowStockItems() {
        ArrayList<InventoryItem> result = new ArrayList<>();
        for (InventoryItem item : inventoryList) {
            if (item.isLowStock()) {
                result.add(item);
            }
        }
        return result;
    }
    
    /**
     * Get inventory item by ID
     */
    public InventoryItem findById(int inventoryItemId) {
        for (InventoryItem item : inventoryList) {
            if (item.getInventoryItemId() == inventoryItemId) {
                return item;
            }
        }
        return null;
    }
    
    /**
     * Check if there's enough stock for an order
     */
    public boolean hasEnoughStock(String itemCode, int requiredQuantity) {
        InventoryItem item = findByProductCode(itemCode);
        if (item == null) {
            item = findByMaterialCode(itemCode);
        }
        return item != null && item.getQuantity() >= requiredQuantity;
    }
    
    /**
     * Get total number of inventory items
     */
    public int getTotalItemCount() {
        return inventoryList.size();
    }
    
    /**
     * Get total quantity of all items
     */
    public int getTotalQuantity() {
        int total = 0;
        for (InventoryItem item : inventoryList) {
            total += item.getQuantity();
        }
        return total;
    }
    
    /**
     * Remove an inventory item completely
     */
    public void removeInventoryItem(InventoryItem item) {
        inventoryList.remove(item);
    }
}

