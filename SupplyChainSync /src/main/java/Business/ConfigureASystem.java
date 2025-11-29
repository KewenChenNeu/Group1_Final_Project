package Business;

import Business.Role.Retail.OrderClerkRole;
import Business.Role.Retail.StoreManagerRole;
import Business.Role.Retail.RetailAnalyticsRole;
import Business.Role.Manufacturer.InventoryManagerRole;
import Business.Role.Manufacturer.ProductionManagerRole;
import Business.Role.Shipping.ShippingManagerRole;
import Business.Role.Shipping.DeliveryStaffRole;
import Business.Role.RawMaterialSupplier.RMProcurementRole;
import Business.Role.RawMaterialSupplier.RMInventoryManagerRole;
import Business.Role.Distributor.WholesaleAnalyticsRole;
import Business.Role.Distributor.WholesaleInventoryRole;
import Business.Role.Distributor.WholesaleSalesRole;
import Business.Employee.Employee;
import Business.Enterprise.*;
import Business.Network.Network;
import Business.Organization.Organization;
import Business.Role.*;
import Business.UserAccount.UserAccount;
import Business.WorkQueue.*;

/**
 *
 * @author rrheg
 */
public class ConfigureASystem {
    
    public static EcoSystem configure() {
        
        EcoSystem system = EcoSystem.getInstance();
        
        // ============================
        // 1. Create System Admin
        // ============================
        Employee sysAdminEmployee = system.getEmployeeDirectory().createEmployee("System Admin");
        system.getUserAccountDirectory().createUserAccount("sysadmin", "sysadmin", sysAdminEmployee, new SystemAdminRole());
        
        // ============================
        // 2. Create Network
        // ============================
        Network supplyChainNetwork = system.createAndAddNetwork("Supply Chain Network");
        
        // ============================
        // 3. Create Enterprises
        // ============================
        
        // 3.1 Raw Material Supplier Enterprise
        RawMaterialSupplierEnterprise rmSupplier = new RawMaterialSupplierEnterprise("ABC Raw Materials Co.");
        supplyChainNetwork.getEnterpriseDirectory().addEnterprise(rmSupplier);
        configureRMSupplierEnterprise(rmSupplier);
        
        // 3.2 Manufacturer Enterprise
        ManufacturerEnterprise manufacturer = new ManufacturerEnterprise("XYZ Manufacturing Inc.");
        supplyChainNetwork.getEnterpriseDirectory().addEnterprise(manufacturer);
        configureManufacturerEnterprise(manufacturer);
        
        // 3.3 Product Distributor Enterprise
        ProductDistributorEnterprise distributor = new ProductDistributorEnterprise("Global Distribution LLC");
        supplyChainNetwork.getEnterpriseDirectory().addEnterprise(distributor);
        configureDistributorEnterprise(distributor);
        
        // 3.4 Shipping Enterprise
        ShippingEnterprise shipping = new ShippingEnterprise("FastShip Logistics");
        supplyChainNetwork.getEnterpriseDirectory().addEnterprise(shipping);
        configureShippingEnterprise(shipping);
        
        // 3.5 Retail Enterprise
        RetailEnterprise retail = new RetailEnterprise("MegaMart Retail");
        supplyChainNetwork.getEnterpriseDirectory().addEnterprise(retail);
        configureRetailEnterprise(retail);
        
        // ============================
        // 4. Create Sample WorkRequests
        // ============================
        createSampleWorkRequests(rmSupplier, manufacturer, distributor, shipping, retail);
        
        return system;
    }
    
    /**
     * Configure Raw Material Supplier Enterprise
     */
    private static void configureRMSupplierEnterprise(RawMaterialSupplierEnterprise enterprise) {
        // Get organizations
        Organization rmProcurementOrg = enterprise.getOrganizationDirectory().getOrganizationList().get(0);
        Organization rmInventoryOrg = enterprise.getOrganizationDirectory().getOrganizationList().get(1);
        
        // Create employees for RM Procurement Organization
        Employee procurementMgr = rmProcurementOrg.getEmployeeDirectory().createEmployee("John Smith");
        rmProcurementOrg.getUserAccountDirectory().createUserAccount("rm_proc1", "password", procurementMgr, new RMProcurementRole());
        
        Employee procurementStaff = rmProcurementOrg.getEmployeeDirectory().createEmployee("Jane Doe");
        rmProcurementOrg.getUserAccountDirectory().createUserAccount("rm_proc2", "password", procurementStaff, new RMProcurementRole());
        
        // Create employees for RM Inventory Organization
        Employee inventoryMgr = rmInventoryOrg.getEmployeeDirectory().createEmployee("Mike Johnson");
        rmInventoryOrg.getUserAccountDirectory().createUserAccount("rm_inv1", "password", inventoryMgr, new RMInventoryManagerRole());
        
        Employee inventoryStaff = rmInventoryOrg.getEmployeeDirectory().createEmployee("Sarah Williams");
        rmInventoryOrg.getUserAccountDirectory().createUserAccount("rm_inv2", "password", inventoryStaff, new RMInventoryManagerRole());
    }
    
    /**
     * Configure Manufacturer Enterprise
     */
    private static void configureManufacturerEnterprise(ManufacturerEnterprise enterprise) {
        // Get organizations
        Organization productionOrg = enterprise.getOrganizationDirectory().getOrganizationList().get(0);
        Organization inventoryOrg = enterprise.getOrganizationDirectory().getOrganizationList().get(1);
        
        // Create employees for Production Management Organization
        Employee productionMgr = productionOrg.getEmployeeDirectory().createEmployee("Robert Brown");
        productionOrg.getUserAccountDirectory().createUserAccount("mfg_prod1", "password", productionMgr, new ProductionManagerRole());
        
        Employee productionStaff = productionOrg.getEmployeeDirectory().createEmployee("Emily Davis");
        productionOrg.getUserAccountDirectory().createUserAccount("mfg_prod2", "password", productionStaff, new ProductionManagerRole());
        
        // Create employees for Inventory Organization
        Employee invMgr = inventoryOrg.getEmployeeDirectory().createEmployee("David Wilson");
        inventoryOrg.getUserAccountDirectory().createUserAccount("mfg_inv1", "password", invMgr, new InventoryManagerRole());
        
        Employee invStaff = inventoryOrg.getEmployeeDirectory().createEmployee("Lisa Anderson");
        inventoryOrg.getUserAccountDirectory().createUserAccount("mfg_inv2", "password", invStaff, new InventoryManagerRole());
    }
    
    /**
     * Configure Product Distributor Enterprise
     */
    private static void configureDistributorEnterprise(ProductDistributorEnterprise enterprise) {
        // Get organizations
        Organization salesOrg = enterprise.getOrganizationDirectory().getOrganizationList().get(0);
        Organization inventoryOrg = enterprise.getOrganizationDirectory().getOrganizationList().get(1);
        
        // Create employees for Wholesale Sales Organization
        Employee salesMgr = salesOrg.getEmployeeDirectory().createEmployee("James Taylor");
        salesOrg.getUserAccountDirectory().createUserAccount("dist_sales1", "password", salesMgr, new WholesaleSalesRole());
        
        Employee salesAnalyst = salesOrg.getEmployeeDirectory().createEmployee("Jennifer Martinez");
        salesOrg.getUserAccountDirectory().createUserAccount("dist_analyst1", "password", salesAnalyst, new WholesaleAnalyticsRole());
        
        // Create employees for Wholesale Inventory Organization
        Employee whInvMgr = inventoryOrg.getEmployeeDirectory().createEmployee("Michael Thomas");
        inventoryOrg.getUserAccountDirectory().createUserAccount("dist_inv1", "password", whInvMgr, new WholesaleInventoryRole());
        
        Employee whInvStaff = inventoryOrg.getEmployeeDirectory().createEmployee("Amanda Jackson");
        inventoryOrg.getUserAccountDirectory().createUserAccount("dist_inv2", "password", whInvStaff, new WholesaleInventoryRole());
    }
    
    /**
     * Configure Shipping Enterprise
     */
    private static void configureShippingEnterprise(ShippingEnterprise enterprise) {
        // Get organizations
        Organization managementOrg = enterprise.getOrganizationDirectory().getOrganizationList().get(0);
        Organization operationOrg = enterprise.getOrganizationDirectory().getOrganizationList().get(1);
        
        // Create employees for Shipping Management Organization
        Employee shipMgr = managementOrg.getEmployeeDirectory().createEmployee("Christopher Lee");
        managementOrg.getUserAccountDirectory().createUserAccount("ship_mgr1", "password", shipMgr, new ShippingManagerRole());
        
        Employee shipCoord = managementOrg.getEmployeeDirectory().createEmployee("Patricia White");
        managementOrg.getUserAccountDirectory().createUserAccount("ship_mgr2", "password", shipCoord, new ShippingManagerRole());
        
        // Create employees for Shipping Operation Organization
        Employee driver1 = operationOrg.getEmployeeDirectory().createEmployee("Daniel Harris");
        operationOrg.getUserAccountDirectory().createUserAccount("ship_staff1", "password", driver1, new DeliveryStaffRole());
        
        Employee driver2 = operationOrg.getEmployeeDirectory().createEmployee("Kevin Clark");
        operationOrg.getUserAccountDirectory().createUserAccount("ship_staff2", "password", driver2, new DeliveryStaffRole());
        
        Employee driver3 = operationOrg.getEmployeeDirectory().createEmployee("Brian Lewis");
        operationOrg.getUserAccountDirectory().createUserAccount("ship_staff3", "password", driver3, new DeliveryStaffRole());
    }
    
    /**
     * Configure Retail Enterprise
     */
    private static void configureRetailEnterprise(RetailEnterprise enterprise) {
        // Get organizations
        Organization storeOrg = enterprise.getOrganizationDirectory().getOrganizationList().get(0);
        Organization inventoryOrg = enterprise.getOrganizationDirectory().getOrganizationList().get(1);
        
        // Create employees for Store Management Organization
        Employee storeMgr = storeOrg.getEmployeeDirectory().createEmployee("Nancy Robinson");
        storeOrg.getUserAccountDirectory().createUserAccount("retail_mgr1", "password", storeMgr, new StoreManagerRole());
        
        Employee orderClerk = inventoryOrg.getEmployeeDirectory().createEmployee("Steven Walker");
        inventoryOrg.getUserAccountDirectory().createUserAccount("retail_clerk1", "password", orderClerk, new OrderClerkRole());
        
        Employee orderClerk2 = inventoryOrg.getEmployeeDirectory().createEmployee("Michelle Hall");
        inventoryOrg.getUserAccountDirectory().createUserAccount("retail_clerk2", "password", orderClerk2, new OrderClerkRole());
        
        // Create employees for Retail Inventory Organization
        Employee retailAnalyst = inventoryOrg.getEmployeeDirectory().createEmployee("George Allen");
        inventoryOrg.getUserAccountDirectory().createUserAccount("retail_analyst1", "password", retailAnalyst, new RetailAnalyticsRole());
        
        Employee retailAnalyst2 = inventoryOrg.getEmployeeDirectory().createEmployee("Betty Young");
        inventoryOrg.getUserAccountDirectory().createUserAccount("retail_analyst2", "password", retailAnalyst2, new RetailAnalyticsRole());
    }
    
    /**
     * Create sample WorkRequests for testing
     */
    private static void createSampleWorkRequests(
            RawMaterialSupplierEnterprise rmSupplier,
            ManufacturerEnterprise manufacturer,
            ProductDistributorEnterprise distributor,
            ShippingEnterprise shipping,
            RetailEnterprise retail) {
        
        // Get organizations for work queues
        Organization rmProcurementOrg = rmSupplier.getOrganizationDirectory().getOrganizationList().get(0);
        Organization mfgProductionOrg = manufacturer.getOrganizationDirectory().getOrganizationList().get(0);
        Organization distSalesOrg = distributor.getOrganizationDirectory().getOrganizationList().get(0);
        Organization shipMgmtOrg = shipping.getOrganizationDirectory().getOrganizationList().get(0);
        Organization retailStoreOrg = retail.getOrganizationDirectory().getOrganizationList().get(0);
        
        // Sample 1: Raw Material Restock Request (Manufacturer → RM Supplier)
        RawMaterialRestockRequest rmRequest1 = new RawMaterialRestockRequest();
        rmRequest1.setMaterialName("Steel Sheets");
        rmRequest1.setQuantity(500);
        rmRequest1.setUnit("kg");
        rmRequest1.setUnitPrice(2.50);
        rmRequest1.setUrgencyLevel("Normal");
        rmRequest1.setMessage("Monthly restock for production line A");
        rmRequest1.setStatus("Pending");
        rmProcurementOrg.getWorkQueue().addWorkRequest(rmRequest1);
        
        RawMaterialRestockRequest rmRequest2 = new RawMaterialRestockRequest();
        rmRequest2.setMaterialName("Copper Wire");
        rmRequest2.setQuantity(200);
        rmRequest2.setUnit("meters");
        rmRequest2.setUnitPrice(5.00);
        rmRequest2.setUrgencyLevel("Urgent");
        rmRequest2.setMessage("Urgent need for electronics assembly");
        rmRequest2.setStatus("Pending");
        rmProcurementOrg.getWorkQueue().addWorkRequest(rmRequest2);
        
        // Sample 2: Wholesale Purchase Request (Distributor → Manufacturer)
        WholesalePurchaseRequest whRequest1 = new WholesalePurchaseRequest();
        whRequest1.setProductName("Electronic Components Set A");
        whRequest1.setProductCode("EC-001");
        whRequest1.setQuantity(1000);
        whRequest1.setUnit("sets");
        whRequest1.setUnitPrice(15.00);
        whRequest1.setDiscount(5.0);
        whRequest1.setPaymentTerms("Net 30");
        whRequest1.setMessage("Quarterly order for retail distribution");
        whRequest1.setStatus("Pending");
        mfgProductionOrg.getWorkQueue().addWorkRequest(whRequest1);
        
        // Sample 3: Retail Purchase Order (Retail → Distributor)
        RetailPurchaseOrderRequest retailPO1 = new RetailPurchaseOrderRequest();
        retailPO1.setProductName("Electronic Components Set A");
        retailPO1.setProductCode("EC-001");
        retailPO1.setQuantity(50);
        retailPO1.setUnit("sets");
        retailPO1.setUnitPrice(25.00);
        retailPO1.setStoreName("MegaMart Downtown");
        retailPO1.setStoreAddress("123 Main Street, Boston, MA");
        retailPO1.setPaymentMethod("Credit");
        retailPO1.setMessage("Restock for weekend sale");
        retailPO1.setStatus("Pending");
        distSalesOrg.getWorkQueue().addWorkRequest(retailPO1);
        
        RetailPurchaseOrderRequest retailPO2 = new RetailPurchaseOrderRequest();
        retailPO2.setProductName("Home Appliance Kit B");
        retailPO2.setProductCode("HA-002");
        retailPO2.setQuantity(30);
        retailPO2.setUnit("units");
        retailPO2.setUnitPrice(75.00);
        retailPO2.setStoreName("MegaMart Suburbs");
        retailPO2.setStoreAddress("456 Oak Avenue, Cambridge, MA");
        retailPO2.setPaymentMethod("Invoice");
        retailPO2.setUrgencyLevel("Urgent");
        retailPO2.setMessage("Low stock alert - need ASAP");
        retailPO2.setStatus("Pending");
        distSalesOrg.getWorkQueue().addWorkRequest(retailPO2);
        
        // Sample 4: Product Shipping Request (Distributor → Shipping)
        ProductShippingRequest shipRequest1 = new ProductShippingRequest();
        shipRequest1.setProductName("Electronic Components Set A");
        shipRequest1.setProductCode("EC-001");
        shipRequest1.setQuantity(50);
        shipRequest1.setUnit("sets");
        shipRequest1.setOriginAddress("789 Warehouse Blvd, Worcester, MA");
        shipRequest1.setDestinationAddress("123 Main Street, Boston, MA");
        shipRequest1.setDestinationStoreName("MegaMart Downtown");
        shipRequest1.setShippingStatus("Pending");
        shipRequest1.setPackageWeight(25.5);
        shipRequest1.setMessage("Standard delivery");
        shipRequest1.setStatus("Pending");
        shipMgmtOrg.getWorkQueue().addWorkRequest(shipRequest1);
        
        // Sample 5: Delivery Confirmation (for testing completed workflow)
        DeliveryConfirmationRequest delConfirm1 = new DeliveryConfirmationRequest();
        delConfirm1.setProductName("Office Supplies Pack");
        delConfirm1.setProductCode("OS-003");
        delConfirm1.setQuantityDelivered(100);
        delConfirm1.setUnit("packs");
        delConfirm1.setStoreName("MegaMart Downtown");
        delConfirm1.setDeliveredBy("Daniel Harris");
        delConfirm1.setConditionOnArrival("Good");
        delConfirm1.setDeliveryNotes("Delivered to back entrance");
        delConfirm1.setConfirmed(false);
        delConfirm1.setMessage("Please confirm receipt");
        delConfirm1.setStatus("Awaiting Confirmation");
        retailStoreOrg.getWorkQueue().addWorkRequest(delConfirm1);
    }
}





