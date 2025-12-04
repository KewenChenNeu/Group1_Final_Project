/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Production;

import Business.Inventory.Inventory;
import Business.Inventory.InventoryItem;
import Business.Material.Material;
import Business.Product.Product;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author chris
 */
public class ProductionManager {
    
    private ArrayList<BillOfMaterials> bomList;
    private ArrayList<ProductionOrder> productionOrders;

    public ProductionManager() {
        this.bomList = new ArrayList<>();
        this.productionOrders = new ArrayList<>();
    }

    public ArrayList<BillOfMaterials> getBomList() {
        return bomList;
    }

    public ArrayList<ProductionOrder> getProductionOrders() {
        return productionOrders;
    }
    
    // ==================== BOM Management ====================
    
    /**
     * Create a new BOM for a product
     */
    public BillOfMaterials createBOM(Product product) {
        // Check if BOM already exists
        BillOfMaterials existing = getBOMByProduct(product);
        if (existing != null) {
            return existing;
        }
        BillOfMaterials bom = new BillOfMaterials(product);
        bomList.add(bom);
        return bom;
    }
    
    /**
     * Get BOM for a specific product
     */
    public BillOfMaterials getBOMByProduct(Product product) {
        for (BillOfMaterials bom : bomList) {
            if (bom.getProduct() != null && 
                bom.getProduct().getProductId() == product.getProductId()) {
                return bom;
            }
        }
        return null;
    }
    
    /**
     * Get BOM by product code
     */
    public BillOfMaterials getBOMByProductCode(String productCode) {
        for (BillOfMaterials bom : bomList) {
            if (bom.getProduct() != null && 
                bom.getProduct().getProductCode().equalsIgnoreCase(productCode)) {
                return bom;
            }
        }
        return null;
    }
    
    /**
     * Remove a BOM
     */
    public void removeBOM(BillOfMaterials bom) {
        bomList.remove(bom);
    }
    
    // ==================== Production Order Management ====================
    
    /**
     * Create a new production order
     */
    public ProductionOrder createProductionOrder(Product product, int quantity) {
        BillOfMaterials bom = getBOMByProduct(product);
        ProductionOrder order = new ProductionOrder(product, bom, quantity);
        productionOrders.add(order);
        return order;
    }
    
    /**
     * Get production order by ID
     */
    public ProductionOrder getOrderById(int orderId) {
        for (ProductionOrder order : productionOrders) {
            if (order.getOrderId() == orderId) {
                return order;
            }
        }
        return null;
    }
    
    /**
     * Get all orders with a specific status
     */
    public ArrayList<ProductionOrder> getOrdersByStatus(ProductionOrder.ProductionStatus status) {
        ArrayList<ProductionOrder> result = new ArrayList<>();
        for (ProductionOrder order : productionOrders) {
            if (order.getStatus() == status) {
                result.add(order);
            }
        }
        return result;
    }
    
    /**
     * Get all active orders (not completed or cancelled)
     */
    public ArrayList<ProductionOrder> getActiveOrders() {
        ArrayList<ProductionOrder> result = new ArrayList<>();
        for (ProductionOrder order : productionOrders) {
            if (order.getStatus() != ProductionOrder.ProductionStatus.COMPLETED &&
                order.getStatus() != ProductionOrder.ProductionStatus.CANCELLED) {
                result.add(order);
            }
        }
        return result;
    }
    
    // ==================== Material Availability Check ====================
    
    /**
     * Check if there are enough materials to produce a given quantity
     * @return true if all materials are available
     */
    public boolean checkMaterialAvailability(Product product, int quantity, Inventory inventory) {
        BillOfMaterials bom = getBOMByProduct(product);
        if (bom == null) {
            return false; // No BOM defined
        }
        
        Map<Material, Double> requirements = bom.calculateMaterialRequirements(quantity);
        
        for (Map.Entry<Material, Double> entry : requirements.entrySet()) {
            Material material = entry.getKey();
            double needed = entry.getValue();
            
            InventoryItem invItem = inventory.findByMaterial(material);
            if (invItem == null || invItem.getQuantity() < needed) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Get material shortages for producing a given quantity
     * @return Map of Material to shortage amount (positive = shortage, zero or negative = sufficient)
     */
    public Map<Material, Double> getMaterialShortages(Product product, int quantity, Inventory inventory) {
        BillOfMaterials bom = getBOMByProduct(product);
        Map<Material, Double> shortages = new java.util.HashMap<>();
        
        if (bom == null) {
            return shortages;
        }
        
        Map<Material, Double> requirements = bom.calculateMaterialRequirements(quantity);
        
        for (Map.Entry<Material, Double> entry : requirements.entrySet()) {
            Material material = entry.getKey();
            double needed = entry.getValue();
            
            InventoryItem invItem = inventory.findByMaterial(material);
            double available = (invItem != null) ? invItem.getQuantity() : 0;
            double shortage = needed - available;
            
            if (shortage > 0) {
                shortages.put(material, shortage);
            }
        }
        return shortages;
    }
    
    /**
     * Calculate maximum producible quantity based on current inventory
     */
    public int calculateMaxProducibleQuantity(Product product, Inventory inventory) {
        BillOfMaterials bom = getBOMByProduct(product);
        if (bom == null || bom.getBomItems().isEmpty()) {
            return 0;
        }
        
        int maxQuantity = Integer.MAX_VALUE;
        
        for (BOMItem bomItem : bom.getBomItems()) {
            Material material = bomItem.getMaterial();
            double perUnit = bomItem.getQuantityRequired();
            
            if (perUnit <= 0) continue;
            
            InventoryItem invItem = inventory.findByMaterial(material);
            double available = (invItem != null) ? invItem.getQuantity() : 0;
            
            int canProduce = (int) (available / perUnit);
            maxQuantity = Math.min(maxQuantity, canProduce);
        }
        
        return (maxQuantity == Integer.MAX_VALUE) ? 0 : maxQuantity;
    }
    
    // ==================== Production Execution ====================
    
    /**
     * Execute production: consume materials and produce finished goods
     * @return true if production was successful
     */
    public boolean executeProduction(ProductionOrder order, Inventory inventory) {
        if (order == null || order.getProduct() == null) {
            return false;
        }
        
        BillOfMaterials bom = order.getBom();
        if (bom == null) {
            bom = getBOMByProduct(order.getProduct());
        }
        
        if (bom == null) {
            return false; // No BOM defined
        }
        
        int quantityToProduce = order.getRemainingQuantity();
        
        // Check material availability
        if (!checkMaterialAvailability(order.getProduct(), quantityToProduce, inventory)) {
            return false;
        }
        
        // Consume materials
        Map<Material, Double> requirements = bom.calculateMaterialRequirements(quantityToProduce);
        for (Map.Entry<Material, Double> entry : requirements.entrySet()) {
            Material material = entry.getKey();
            int needed = (int) Math.ceil(entry.getValue());
            inventory.removeMaterial(material, needed);
        }
        
        // Add finished products to inventory
        inventory.addProduct(order.getProduct(), quantityToProduce);
        
        // Update order status
        order.completeProduction();
        
        return true;
    }
    
    /**
     * Partially execute production (produce only what materials allow)
     * @return quantity actually produced
     */
    public int executePartialProduction(ProductionOrder order, Inventory inventory) {
        if (order == null || order.getProduct() == null) {
            return 0;
        }
        
        int maxCanProduce = calculateMaxProducibleQuantity(order.getProduct(), inventory);
        int wantToProduce = order.getRemainingQuantity();
        int actualProduction = Math.min(maxCanProduce, wantToProduce);
        
        if (actualProduction <= 0) {
            return 0;
        }
        
        BillOfMaterials bom = order.getBom();
        if (bom == null) {
            bom = getBOMByProduct(order.getProduct());
        }
        
        if (bom == null) {
            return 0;
        }
        
        // Consume materials
        Map<Material, Double> requirements = bom.calculateMaterialRequirements(actualProduction);
        for (Map.Entry<Material, Double> entry : requirements.entrySet()) {
            Material material = entry.getKey();
            int needed = (int) Math.ceil(entry.getValue());
            inventory.removeMaterial(material, needed);
        }
        
        // Add finished products
        inventory.addProduct(order.getProduct(), actualProduction);
        
        // Update order
        order.addProducedQuantity(actualProduction);
        if (order.getStatus() == ProductionOrder.ProductionStatus.PLANNED) {
            order.setStatus(ProductionOrder.ProductionStatus.IN_PROGRESS);
        }
        
        return actualProduction;
    }
}

