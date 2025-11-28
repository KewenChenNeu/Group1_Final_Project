/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Business.Role;

import Business.EcoSystem;
import Business.Enterprise.Enterprise;
import Business.Organization.Organization;
import Business.UserAccount.UserAccount;
import javax.swing.JPanel;

/**
 *
 * @author chris
 */
public abstract class Role {
    
    public enum RoleType {
        // System Admin
        SystemAdmin("System Admin"),
        
        // Raw Material Supplier Enterprise Roles
        RMProcurement("RM Procurement"),
        RMInventoryManager("RM Inventory Manager"),
        
        // Manufacturer Enterprise Roles
        ProductionManager("Production Manager"),
        InventoryManager("Inventory Manager"),
        
        // Product Distributor Enterprise Roles
        WholesaleSales("Wholesale Sales"),
        WholesaleInventory("Wholesale Inventory"),
        WholesaleAnalytics("Wholesale Analytics"),
        
        // Shipping Enterprise Roles
        ShippingManager("Shipping Manager"),
        DeliveryStaff("Delivery Staff"),
        
        // Retail Enterprise Roles
        StoreManager("Store Manager"),
        OrderClerk("Order Clerk"),
        RetailAnalytics("Retail Analytics");
        
        private String value;
        
        private RoleType(String value) {
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
    
    public abstract JPanel createWorkArea(JPanel userProcessContainer, 
            UserAccount account, 
            Organization organization, 
            Enterprise enterprise, 
            EcoSystem business);

    @Override
    public String toString() {
        return this.getClass().getSimpleName().replace("Role", "");
    }
    
    
}