package Business;

import Business.Employee.Employee;
import Business.Enterprise.*;
import Business.Inventory.Inventory;
import Business.Material.Material;
import Business.Material.MaterialCatalog;
import Business.Network.Network;
import Business.Organization.Organization;
import Business.Product.Product;
import Business.Product.ProductCatalog;
import Business.Role.*;
import Business.Role.Distributor.WholesaleAnalyticsRole;
import Business.Role.Distributor.WholesaleInventoryRole;
import Business.Role.Distributor.WholesaleSalesRole;
import Business.Role.Manufacturer.InventoryManagerRole;
import Business.Role.Manufacturer.ProductionManagerRole;
import Business.Role.RawMaterialSupplier.RMInventoryManagerRole;
import Business.Role.RawMaterialSupplier.RMProcurementRole;
import Business.Role.Retail.OrderClerkRole;
import Business.Role.Retail.RetailAnalyticsRole;
import Business.Role.Retail.StoreManagerRole;
import Business.Role.Shipping.DeliveryStaffRole;
import Business.Role.Shipping.ShippingManagerRole;
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
        // 4. Setup Products, Materials, and Inventory
        // ============================
        setupCatalogsAndInventory(rmSupplier, manufacturer, distributor, retail);
        
        // ============================
        // 5. Create Sample WorkRequests
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
        
        Employee orderClerk = storeOrg.getEmployeeDirectory().createEmployee("Steven Walker");
        storeOrg.getUserAccountDirectory().createUserAccount("retail_clerk1", "password", orderClerk, new OrderClerkRole());
        
        Employee orderClerk2 = storeOrg.getEmployeeDirectory().createEmployee("Michelle Hall");
        storeOrg.getUserAccountDirectory().createUserAccount("retail_clerk2", "password", orderClerk2, new OrderClerkRole());
        
        // Create employees for Retail Inventory Organization
        Employee retailAnalyst = inventoryOrg.getEmployeeDirectory().createEmployee("George Allen");
        inventoryOrg.getUserAccountDirectory().createUserAccount("retail_analyst1", "password", retailAnalyst, new RetailAnalyticsRole());
        
        Employee retailAnalyst2 = inventoryOrg.getEmployeeDirectory().createEmployee("Betty Young");
        inventoryOrg.getUserAccountDirectory().createUserAccount("retail_analyst2", "password", retailAnalyst2, new RetailAnalyticsRole());
    }
    
    /**
     * Setup Products, Materials, and Inventory for all enterprises
     */
    private static void setupCatalogsAndInventory(
            RawMaterialSupplierEnterprise rmSupplier,
            ManufacturerEnterprise manufacturer,
            ProductDistributorEnterprise distributor,
            RetailEnterprise retail) {
        
        // ============================
        // Raw Material Supplier - Materials Catalog & Inventory
        // ============================
        MaterialCatalog rmMaterialCatalog = rmSupplier.getMaterialCatalog();
        Inventory rmInventory = rmSupplier.getInventory();
        
        // Create materials that RM Supplier sells
        Material steelSheets = rmMaterialCatalog.createMaterial("RM-001", "Steel Sheets", 2.50, "kg");
        steelSheets.setCategory("Metal");
        steelSheets.setDescription("High-grade steel sheets for manufacturing");
        
        Material copperWire = rmMaterialCatalog.createMaterial("RM-002", "Copper Wire", 5.00, "meters");
        copperWire.setCategory("Metal");
        copperWire.setDescription("Pure copper wire for electronics");
        
        Material aluminumBars = rmMaterialCatalog.createMaterial("RM-003", "Aluminum Bars", 3.00, "kg");
        aluminumBars.setCategory("Metal");
        aluminumBars.setDescription("Lightweight aluminum bars");
        
        Material plasticPellets = rmMaterialCatalog.createMaterial("RM-004", "Plastic Pellets", 1.50, "kg");
        plasticPellets.setCategory("Plastic");
        plasticPellets.setDescription("Industrial grade plastic pellets");
        
        Material rubberSheets = rmMaterialCatalog.createMaterial("RM-005", "Rubber Sheets", 4.00, "sheets");
        rubberSheets.setCategory("Rubber");
        rubberSheets.setDescription("Natural rubber sheets");
        
        Material glassPanel = rmMaterialCatalog.createMaterial("RM-006", "Glass Panels", 8.00, "panels");
        glassPanel.setCategory("Glass");
        glassPanel.setDescription("Tempered glass panels");
        
        // Add inventory for RM Supplier
        rmInventory.addMaterial(steelSheets, 10000, 1000, 20000, "Warehouse A-1");
        rmInventory.addMaterial(copperWire, 5000, 500, 10000, "Warehouse A-2");
        rmInventory.addMaterial(aluminumBars, 8000, 800, 15000, "Warehouse A-3");
        rmInventory.addMaterial(plasticPellets, 15000, 2000, 30000, "Warehouse B-1");
        rmInventory.addMaterial(rubberSheets, 3000, 300, 6000, "Warehouse B-2");
        rmInventory.addMaterial(glassPanel, 2000, 200, 4000, "Warehouse C-1");
        
        // ============================
        // Manufacturer - Products Catalog & Inventory
        // ============================
        ProductCatalog mfgProductCatalog = manufacturer.getProductCatalog();
        MaterialCatalog mfgMaterialCatalog = manufacturer.getMaterialCatalog();
        Inventory mfgInventory = manufacturer.getInventory();
        
        // Add materials that manufacturer needs (reference from RM Supplier)
        mfgMaterialCatalog.addMaterial(steelSheets);
        mfgMaterialCatalog.addMaterial(copperWire);
        mfgMaterialCatalog.addMaterial(aluminumBars);
        mfgMaterialCatalog.addMaterial(plasticPellets);
        
        // Create products that Manufacturer produces
        Product electronicSet = mfgProductCatalog.createProduct("EC-001", "Electronic Components Set A", 15.00, "sets");
        electronicSet.setCategory("Electronics");
        electronicSet.setDescription("Complete set of electronic components for assembly");
        
        Product homeAppliance = mfgProductCatalog.createProduct("HA-001", "Home Appliance Kit A", 50.00, "units");
        homeAppliance.setCategory("Appliances");
        homeAppliance.setDescription("Basic home appliance assembly kit");
        
        Product homeApplianceB = mfgProductCatalog.createProduct("HA-002", "Home Appliance Kit B", 75.00, "units");
        homeApplianceB.setCategory("Appliances");
        homeApplianceB.setDescription("Premium home appliance assembly kit");
        
        Product officeSupply = mfgProductCatalog.createProduct("OS-001", "Office Supplies Pack", 12.00, "packs");
        officeSupply.setCategory("Office");
        officeSupply.setDescription("Standard office supplies pack");
        
        Product industrialTool = mfgProductCatalog.createProduct("IT-001", "Industrial Tool Set", 120.00, "sets");
        industrialTool.setCategory("Industrial");
        industrialTool.setDescription("Professional industrial tool set");
        
        // Add raw material inventory for Manufacturer
        mfgInventory.addMaterial(steelSheets, 2000, 500, 5000, "Raw Materials Section A");
        mfgInventory.addMaterial(copperWire, 1000, 200, 3000, "Raw Materials Section B");
        mfgInventory.addMaterial(aluminumBars, 1500, 300, 4000, "Raw Materials Section C");
        mfgInventory.addMaterial(plasticPellets, 3000, 500, 8000, "Raw Materials Section D");
        
        // Add finished product inventory for Manufacturer
        mfgInventory.addProduct(electronicSet, 5000, 500, 10000, "Finished Goods A");
        mfgInventory.addProduct(homeAppliance, 2000, 200, 5000, "Finished Goods B");
        mfgInventory.addProduct(homeApplianceB, 1500, 150, 4000, "Finished Goods B");
        mfgInventory.addProduct(officeSupply, 8000, 1000, 15000, "Finished Goods C");
        mfgInventory.addProduct(industrialTool, 500, 50, 1000, "Finished Goods D");
        
        // ============================
        // Setup BOM (Bill of Materials) for each product
        // ============================
        Business.Production.ProductionManager prodManager = manufacturer.getProductionManager();
        
        // BOM for Electronic Components Set A (EC-001)
        // Requires: 0.5kg steel, 2m copper wire, 0.1kg plastic
        Business.Production.BillOfMaterials bomElectronic = prodManager.createBOM(electronicSet);
        bomElectronic.addMaterial(steelSheets, 0.5);      // 0.5 kg per set
        bomElectronic.addMaterial(copperWire, 2.0);       // 2 meters per set
        bomElectronic.addMaterial(plasticPellets, 0.1);   // 0.1 kg per set
        bomElectronic.setLaborHours(0.5);
        bomElectronic.setProductionCostPerUnit(3.00);     // Labor + overhead
        
        // BOM for Home Appliance Kit A (HA-001)
        // Requires: 2kg steel, 1m copper, 0.5kg plastic, 0.3kg aluminum
        Business.Production.BillOfMaterials bomApplianceA = prodManager.createBOM(homeAppliance);
        bomApplianceA.addMaterial(steelSheets, 2.0);      // 2 kg per unit
        bomApplianceA.addMaterial(copperWire, 1.0);       // 1 meter per unit
        bomApplianceA.addMaterial(plasticPellets, 0.5);   // 0.5 kg per unit
        bomApplianceA.addMaterial(aluminumBars, 0.3);     // 0.3 kg per unit
        bomApplianceA.setLaborHours(1.5);
        bomApplianceA.setProductionCostPerUnit(10.00);
        
        // BOM for Home Appliance Kit B (HA-002) - Premium version
        // Requires: 3kg steel, 2m copper, 1kg plastic, 0.5kg aluminum
        Business.Production.BillOfMaterials bomApplianceB = prodManager.createBOM(homeApplianceB);
        bomApplianceB.addMaterial(steelSheets, 3.0);      // 3 kg per unit
        bomApplianceB.addMaterial(copperWire, 2.0);       // 2 meters per unit
        bomApplianceB.addMaterial(plasticPellets, 1.0);   // 1 kg per unit
        bomApplianceB.addMaterial(aluminumBars, 0.5);     // 0.5 kg per unit
        bomApplianceB.setLaborHours(2.0);
        bomApplianceB.setProductionCostPerUnit(15.00);
        
        // BOM for Office Supplies Pack (OS-001)
        // Requires: 0.2kg plastic, 0.1kg aluminum
        Business.Production.BillOfMaterials bomOffice = prodManager.createBOM(officeSupply);
        bomOffice.addMaterial(plasticPellets, 0.2);       // 0.2 kg per pack
        bomOffice.addMaterial(aluminumBars, 0.1);         // 0.1 kg per pack
        bomOffice.setLaborHours(0.25);
        bomOffice.setProductionCostPerUnit(2.00);
        
        // BOM for Industrial Tool Set (IT-001)
        // Requires: 5kg steel, 3m copper, 2kg plastic, 1kg aluminum
        Business.Production.BillOfMaterials bomTool = prodManager.createBOM(industrialTool);
        bomTool.addMaterial(steelSheets, 5.0);            // 5 kg per set
        bomTool.addMaterial(copperWire, 3.0);             // 3 meters per set
        bomTool.addMaterial(plasticPellets, 2.0);         // 2 kg per set
        bomTool.addMaterial(aluminumBars, 1.0);           // 1 kg per set
        bomTool.setLaborHours(3.0);
        bomTool.setProductionCostPerUnit(25.00);
        
        // ============================
        // Distributor - Products Catalog & Inventory
        // ============================
        ProductCatalog distProductCatalog = distributor.getProductCatalog();
        Inventory distInventory = distributor.getInventory();
        
        // Distributor sells products from Manufacturer
        distProductCatalog.addProduct(electronicSet);
        distProductCatalog.addProduct(homeAppliance);
        distProductCatalog.addProduct(homeApplianceB);
        distProductCatalog.addProduct(officeSupply);
        distProductCatalog.addProduct(industrialTool);
        
        // Add inventory for Distributor (wholesale quantities)
        distInventory.addProduct(electronicSet, 3000, 500, 8000, "Distribution Center A");
        distInventory.addProduct(homeAppliance, 1000, 200, 3000, "Distribution Center A");
        distInventory.addProduct(homeApplianceB, 800, 100, 2000, "Distribution Center B");
        distInventory.addProduct(officeSupply, 5000, 1000, 12000, "Distribution Center C");
        distInventory.addProduct(industrialTool, 200, 30, 500, "Distribution Center D");
        
        // ============================
        // Retail - Products Catalog & Inventory
        // ============================
        ProductCatalog retailProductCatalog = retail.getProductCatalog();
        Inventory retailInventory = retail.getInventory();
        
        // Retail sells products from Distributor (same products, retail pricing)
        Product retailElectronic = retailProductCatalog.createProduct("EC-001", "Electronic Components Set A", 25.00, "sets");
        retailElectronic.setCategory("Electronics");
        retailElectronic.setDescription("Complete set of electronic components");
        
        Product retailAppliance = retailProductCatalog.createProduct("HA-001", "Home Appliance Kit A", 89.99, "units");
        retailAppliance.setCategory("Appliances");
        retailAppliance.setDescription("Basic home appliance kit");
        
        Product retailApplianceB = retailProductCatalog.createProduct("HA-002", "Home Appliance Kit B", 129.99, "units");
        retailApplianceB.setCategory("Appliances");
        retailApplianceB.setDescription("Premium home appliance kit");
        
        Product retailOffice = retailProductCatalog.createProduct("OS-001", "Office Supplies Pack", 19.99, "packs");
        retailOffice.setCategory("Office");
        retailOffice.setDescription("Standard office supplies pack");
        
        Product retailTool = retailProductCatalog.createProduct("IT-001", "Industrial Tool Set", 199.99, "sets");
        retailTool.setCategory("Industrial");
        retailTool.setDescription("Professional industrial tool set");
        
        // Add inventory for Retail (store quantities)
        retailInventory.addProduct(retailElectronic, 150, 20, 300, "Store Shelf A1");
        retailInventory.addProduct(retailAppliance, 50, 10, 100, "Store Shelf B1");
        retailInventory.addProduct(retailApplianceB, 30, 5, 60, "Store Shelf B2");
        retailInventory.addProduct(retailOffice, 200, 30, 400, "Store Shelf C1");
        retailInventory.addProduct(retailTool, 15, 3, 30, "Store Shelf D1");
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
        UserAccount retailUser = retailStoreOrg.getUserAccountDirectory().getUserAccountList().get(0);

        
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
        retailPO1.setSender(retailUser);
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
        retailPO2.setSender(retailUser);
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
        delConfirm1.setProductCode("OS-001");
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





