/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Production;

import Business.Product.Product;
import Business.UserAccount.UserAccount;
import java.util.Date;

/**
 *
 * @author chris
 */
public class ProductionOrder {
    
    private static int counter = 4000;
    
    public enum ProductionStatus {
        PLANNED("Planned"),           // Order created, not started
        MATERIALS_RESERVED("Materials Reserved"), // Materials allocated
        IN_PROGRESS("In Progress"),   // Production ongoing
        COMPLETED("Completed"),       // Production finished
        CANCELLED("Cancelled");       // Order cancelled
        
        private String value;
        
        private ProductionStatus(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        @Override
        public String toString() {
            return value;
        }
    }
    
    private int orderId;
    private Product product;
    private BillOfMaterials bom;
    private int quantityOrdered;
    private int quantityProduced;
    private ProductionStatus status;
    private Date orderDate;
    private Date plannedStartDate;
    private Date actualStartDate;
    private Date completionDate;
    private UserAccount createdBy;
    private String notes;
    private int priority;  // 1 = Highest, 5 = Lowest

    public ProductionOrder() {
        this.orderId = counter++;
        this.orderDate = new Date();
        this.status = ProductionStatus.PLANNED;
        this.quantityProduced = 0;
        this.priority = 3; // Default medium priority
    }
    
    public ProductionOrder(Product product, int quantity) {
        this();
        this.product = product;
        this.quantityOrdered = quantity;
    }
    
    public ProductionOrder(Product product, BillOfMaterials bom, int quantity) {
        this(product, quantity);
        this.bom = bom;
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public BillOfMaterials getBom() {
        return bom;
    }

    public void setBom(BillOfMaterials bom) {
        this.bom = bom;
    }

    public int getQuantityOrdered() {
        return quantityOrdered;
    }

    public void setQuantityOrdered(int quantityOrdered) {
        this.quantityOrdered = quantityOrdered;
    }

    public int getQuantityProduced() {
        return quantityProduced;
    }

    public void setQuantityProduced(int quantityProduced) {
        this.quantityProduced = quantityProduced;
    }
    
    /**
     * Add to quantity produced (during production)
     */
    public void addProducedQuantity(int quantity) {
        this.quantityProduced += quantity;
        if (this.quantityProduced >= this.quantityOrdered) {
            this.status = ProductionStatus.COMPLETED;
            this.completionDate = new Date();
        }
    }

    public ProductionStatus getStatus() {
        return status;
    }

    public void setStatus(ProductionStatus status) {
        this.status = status;
        if (status == ProductionStatus.IN_PROGRESS && actualStartDate == null) {
            this.actualStartDate = new Date();
        } else if (status == ProductionStatus.COMPLETED && completionDate == null) {
            this.completionDate = new Date();
        }
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getPlannedStartDate() {
        return plannedStartDate;
    }

    public void setPlannedStartDate(Date plannedStartDate) {
        this.plannedStartDate = plannedStartDate;
    }

    public Date getActualStartDate() {
        return actualStartDate;
    }

    public void setActualStartDate(Date actualStartDate) {
        this.actualStartDate = actualStartDate;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public UserAccount getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserAccount createdBy) {
        this.createdBy = createdBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    /**
     * Get remaining quantity to produce
     */
    public int getRemainingQuantity() {
        return quantityOrdered - quantityProduced;
    }
    
    /**
     * Get completion percentage
     */
    public double getCompletionPercentage() {
        if (quantityOrdered == 0) return 0;
        return ((double) quantityProduced / quantityOrdered) * 100;
    }
    
    /**
     * Check if production is complete
     */
    public boolean isComplete() {
        return status == ProductionStatus.COMPLETED || quantityProduced >= quantityOrdered;
    }
    
    /**
     * Start production
     */
    public void startProduction() {
        if (status == ProductionStatus.PLANNED || status == ProductionStatus.MATERIALS_RESERVED) {
            this.status = ProductionStatus.IN_PROGRESS;
            this.actualStartDate = new Date();
        }
    }
    
    /**
     * Complete production
     */
    public void completeProduction() {
        this.quantityProduced = this.quantityOrdered;
        this.status = ProductionStatus.COMPLETED;
        this.completionDate = new Date();
    }
    
    /**
     * Cancel production order
     */
    public void cancelOrder() {
        this.status = ProductionStatus.CANCELLED;
    }

    @Override
    public String toString() {
        if (product != null) {
            return "PO-" + orderId + ": " + product.getProductName() + " x " + quantityOrdered;
        }
        return "PO-" + orderId;
    }
}

