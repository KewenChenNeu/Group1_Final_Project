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
import java.util.Calendar;

/**
 *
 * @author chris
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
        Organization shipMgmtOrg = enterprise.getOrganizationDirectory().getOrganizationList().get(0);
        Organization shipOpOrg = enterprise.getOrganizationDirectory().getOrganizationList().get(1);
        
        // Create employees for Shipping Management Organization
        Employee shipMgr = shipMgmtOrg.getEmployeeDirectory().createEmployee("Christopher Lee");
        shipMgmtOrg.getUserAccountDirectory().createUserAccount("ship_mgr1", "password", shipMgr, new ShippingManagerRole());
        
        // Create employees for Shipping Operation Organization
        Employee deliveryStaff1 = shipOpOrg.getEmployeeDirectory().createEmployee("Daniel Harris");
        shipOpOrg.getUserAccountDirectory().createUserAccount("ship_del1", "password", deliveryStaff1, new DeliveryStaffRole());
        
        Employee deliveryStaff2 = shipOpOrg.getEmployeeDirectory().createEmployee("Kevin Garcia");
        shipOpOrg.getUserAccountDirectory().createUserAccount("ship_del2", "password", deliveryStaff2, new DeliveryStaffRole());
    }
    
    /**
     * Configure Retail Enterprise
     */
    private static void configureRetailEnterprise(RetailEnterprise enterprise) {
        // Get organizations
        Organization storeOrg = enterprise.getOrganizationDirectory().getOrganizationList().get(0);
        Organization retailInvOrg = enterprise.getOrganizationDirectory().getOrganizationList().get(1);
        
        // Create employees for Store Management Organization
        Employee storeMgr = storeOrg.getEmployeeDirectory().createEmployee("Patricia White");
        storeOrg.getUserAccountDirectory().createUserAccount("retail_mgr1", "password", storeMgr, new StoreManagerRole());
        
        Employee orderClerk = storeOrg.getEmployeeDirectory().createEmployee("Steven Clark");
        storeOrg.getUserAccountDirectory().createUserAccount("retail_clerk1", "password", orderClerk, new OrderClerkRole());
        
        // Create employees for Retail Inventory Organization
        Employee retailAnalyst = retailInvOrg.getEmployeeDirectory().createEmployee("Nancy Rodriguez");
        retailInvOrg.getUserAccountDirectory().createUserAccount("retail_analyst1", "password", retailAnalyst, new RetailAnalyticsRole());
    }
    
    /**
     * Setup product catalogs and inventory for all enterprises
     */
    private static void setupCatalogsAndInventory(
            RawMaterialSupplierEnterprise rmSupplier,
            ManufacturerEnterprise manufacturer,
            ProductDistributorEnterprise distributor,
            RetailEnterprise retail) {
        
        // ============================
        // Raw Material Supplier - Material Catalog & Inventory
        // ============================
        MaterialCatalog rmMaterialCatalog = rmSupplier.getMaterialCatalog();
        Inventory rmInventory = rmSupplier.getInventory();
        
        // Create materials
        Material steelSheets = rmMaterialCatalog.createMaterial("RM-001", "Steel Sheets", 2.50, "kg");
        steelSheets.setCategory("Metals");
        steelSheets.setDescription("High-quality steel sheets for manufacturing");
        
        Material copperWire = rmMaterialCatalog.createMaterial("RM-002", "Copper Wire", 5.00, "meters");
        copperWire.setCategory("Metals");
        copperWire.setDescription("Pure copper wire for electrical components");
        
        Material plasticPellets = rmMaterialCatalog.createMaterial("RM-003", "Plastic Pellets", 1.20, "kg");
        plasticPellets.setCategory("Plastics");
        plasticPellets.setDescription("Industrial grade plastic pellets");
        
        Material aluminumBars = rmMaterialCatalog.createMaterial("RM-004", "Aluminum Bars", 3.75, "kg");
        aluminumBars.setCategory("Metals");
        aluminumBars.setDescription("Lightweight aluminum bars");
        
        // Add inventory for materials
        rmInventory.addMaterial(steelSheets, 10000, 1000, 20000, "Warehouse A");
        rmInventory.addMaterial(copperWire, 5000, 500, 10000, "Warehouse B");
        rmInventory.addMaterial(plasticPellets, 8000, 800, 15000, "Warehouse A");
        rmInventory.addMaterial(aluminumBars, 6000, 600, 12000, "Warehouse C");
        
        // ============================
        // Manufacturer - Products Catalog & Inventory
        // ============================
        ProductCatalog mfgProductCatalog = manufacturer.getProductCatalog();
        Inventory mfgInventory = manufacturer.getInventory();
        
        // Create products that manufacturer produces
        Product electronicSet = mfgProductCatalog.createProduct("EC-001", "Electronic Components Set A", 15.00, "sets");
        electronicSet.setCategory("Electronics");
        electronicSet.setDescription("Complete set of electronic components");
        
        Product applianceKitA = mfgProductCatalog.createProduct("HA-001", "Home Appliance Kit A", 45.00, "units");
        applianceKitA.setCategory("Appliances");
        applianceKitA.setDescription("Basic home appliance kit");
        
        Product applianceKitB = mfgProductCatalog.createProduct("HA-002", "Home Appliance Kit B", 75.00, "units");
        applianceKitB.setCategory("Appliances");
        applianceKitB.setDescription("Premium home appliance kit");
        
        Product officeSupply = mfgProductCatalog.createProduct("OS-001", "Office Supplies Pack", 8.50, "packs");
        officeSupply.setCategory("Office");
        officeSupply.setDescription("Standard office supplies pack");
        
        Product industrialTool = mfgProductCatalog.createProduct("IT-001", "Industrial Tool Set", 120.00, "sets");
        industrialTool.setCategory("Industrial");
        industrialTool.setDescription("Professional industrial tool set");
        
        // Add inventory for manufactured products
        mfgInventory.addProduct(electronicSet, 2000, 200, 5000, "Production Line A");
        mfgInventory.addProduct(applianceKitA, 500, 50, 1000, "Production Line B");
        mfgInventory.addProduct(applianceKitB, 300, 30, 600, "Production Line B");
        mfgInventory.addProduct(officeSupply, 3000, 300, 6000, "Production Line C");
        mfgInventory.addProduct(industrialTool, 150, 20, 300, "Production Line D");
        
        // ============================
        // Distributor - Products Catalog & Inventory
        // ============================
        ProductCatalog distProductCatalog = distributor.getProductCatalog();
        Inventory distInventory = distributor.getInventory();
        
        // Distributor sells same products from manufacturer (wholesale pricing)
        Product distElectronic = distProductCatalog.createProduct("EC-001", "Electronic Components Set A", 18.00, "sets");
        distElectronic.setCategory("Electronics");
        distElectronic.setDescription("Complete set of electronic components");
        
        Product distApplianceA = distProductCatalog.createProduct("HA-001", "Home Appliance Kit A", 55.00, "units");
        distApplianceA.setCategory("Appliances");
        distApplianceA.setDescription("Basic home appliance kit");
        
        Product distApplianceB = distProductCatalog.createProduct("HA-002", "Home Appliance Kit B", 85.00, "units");
        distApplianceB.setCategory("Appliances");
        distApplianceB.setDescription("Premium home appliance kit");
        
        Product distOfficeSupply = distProductCatalog.createProduct("OS-001", "Office Supplies Pack", 12.00, "packs");
        distOfficeSupply.setCategory("Office");
        distOfficeSupply.setDescription("Standard office supplies pack");
        
        Product distIndustrialTool = distProductCatalog.createProduct("IT-001", "Industrial Tool Set", 150.00, "sets");
        distIndustrialTool.setCategory("Industrial");
        distIndustrialTool.setDescription("Professional industrial tool set");
        
        // Add inventory for distributor (wholesale quantities)
        distInventory.addProduct(distElectronic, 3000, 500, 8000, "Distribution Center A");
        distInventory.addProduct(distApplianceA, 800, 100, 2000, "Distribution Center B");
        distInventory.addProduct(distApplianceB, 400, 50, 1000, "Distribution Center B");
        distInventory.addProduct(distOfficeSupply, 5000, 1000, 12000, "Distribution Center C");
        distInventory.addProduct(distIndustrialTool, 200, 30, 500, "Distribution Center D");
        
        // ============================================================
        // Additional Products in Catalog (NOT yet in inventory)
        // These can be added via "Add New Product" button
        // ============================================================
        Product distElectronicB = distProductCatalog.createProduct("EC-002", "Electronic Components Set B", 22.00, "sets");
        distElectronicB.setCategory("Electronics");
        distElectronicB.setDescription("Advanced electronic components set with premium parts");
        
        Product distElectronicC = distProductCatalog.createProduct("EC-003", "Electronic Starter Kit", 12.00, "sets");
        distElectronicC.setCategory("Electronics");
        distElectronicC.setDescription("Basic electronic starter kit for beginners");
        
        Product distApplianceC = distProductCatalog.createProduct("HA-003", "Home Appliance Kit C - Deluxe", 120.00, "units");
        distApplianceC.setCategory("Appliances");
        distApplianceC.setDescription("Deluxe home appliance kit with extended warranty");
        
        Product distOfficeSupplyB = distProductCatalog.createProduct("OS-002", "Office Supplies Pack - Premium", 18.00, "packs");
        distOfficeSupplyB.setCategory("Office");
        distOfficeSupplyB.setDescription("Premium office supplies pack with ergonomic items");
        
        Product distFurnitureA = distProductCatalog.createProduct("FN-001", "Office Furniture Set A", 250.00, "sets");
        distFurnitureA.setCategory("Furniture");
        distFurnitureA.setDescription("Complete office furniture set including desk and chair");
        
        Product distFurnitureB = distProductCatalog.createProduct("FN-002", "Office Furniture Set B - Executive", 450.00, "sets");
        distFurnitureB.setCategory("Furniture");
        distFurnitureB.setDescription("Executive office furniture set with premium materials");
        
        Product distSafetyKit = distProductCatalog.createProduct("SF-001", "Safety Equipment Kit", 65.00, "kits");
        distSafetyKit.setCategory("Safety");
        distSafetyKit.setDescription("Comprehensive workplace safety equipment kit");
        
        Product distCleaningSupply = distProductCatalog.createProduct("CL-001", "Industrial Cleaning Supplies", 35.00, "sets");
        distCleaningSupply.setCategory("Cleaning");
        distCleaningSupply.setDescription("Industrial grade cleaning supplies set");
        
        // Add inventory for distributor (wholesale quantities) - ONLY original 5 products
        distInventory.addProduct(distElectronic, 3000, 500, 8000, "Distribution Center A");
        distInventory.addProduct(distApplianceA, 800, 100, 2000, "Distribution Center B");
        distInventory.addProduct(distApplianceB, 400, 50, 1000, "Distribution Center B");
        distInventory.addProduct(distOfficeSupply, 5000, 1000, 12000, "Distribution Center C");
        distInventory.addProduct(distIndustrialTool, 200, 30, 500, "Distribution Center D");
        
        
        
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
        retailPO1.setProductName("Electronic Components Set A");
        retailPO1.setProductCode("EC-001");
        retailPO1.setQuantity(50);
        retailPO1.setUnit("sets");
        retailPO1.setUnitPrice(25.00);
        retailPO1.setStoreName("MegaMart Downtown");
        retailPO1.setSender(retailUser);
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
        retailPO2.setSender(retailUser);
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
        
        // ============================================================
        // Sample 5: DeliveryConfirmationRequest for Distributor Sales
        // (These go to distSalesOrg - WholesaleSalesOrganization)
        // ============================================================
        
        // Create a completed shipping request to link with delivery confirmation
        ProductShippingRequest completedShipment1 = new ProductShippingRequest();
        completedShipment1.setProductName("Office Supplies Pack");
        completedShipment1.setProductCode("OS-001");
        completedShipment1.setQuantity(100);
        completedShipment1.setUnit("packs");
        completedShipment1.setOriginAddress("Global Distribution LLC Warehouse");
        completedShipment1.setDestinationAddress("123 Main Street, Boston, MA");
        completedShipment1.setDestinationStoreName("MegaMart Downtown");
        completedShipment1.setShippingStatus(ProductShippingRequest.SHIP_STATUS_DELIVERED);
        completedShipment1.setCarrierName("FastShip Logistics");
        completedShipment1.setTrackingNumber("TRK123456789");
        completedShipment1.setStatus("Delivered");
        
        // Delivery Confirmation 1 - Good condition, pending confirmation
        DeliveryConfirmationRequest delConfirm1 = new DeliveryConfirmationRequest(completedShipment1);
        delConfirm1.setDeliveredBy("Daniel Harris");
        delConfirm1.setConditionOnArrival(DeliveryConfirmationRequest.CONDITION_GOOD);
        delConfirm1.setDeliveryNotes("Delivered to back entrance, signed by store manager");
        delConfirm1.setConfirmed(false);
        delConfirm1.setMessage("Please confirm receipt of Office Supplies Pack");
        delConfirm1.setStatus("Awaiting Confirmation");
        // Set delivery date to yesterday
        Calendar cal1 = Calendar.getInstance();
        cal1.add(Calendar.DAY_OF_MONTH, -1);
        delConfirm1.setDeliveryDate(cal1.getTime());
        // Add to Distributor Sales Organization (WholesaleSalesOrganization)
        distSalesOrg.getWorkQueue().addWorkRequest(delConfirm1);
        
        // Create another completed shipping request
        ProductShippingRequest completedShipment2 = new ProductShippingRequest();
        completedShipment2.setProductName("Home Appliance Kit A");
        completedShipment2.setProductCode("HA-001");
        completedShipment2.setQuantity(25);
        completedShipment2.setUnit("units");
        completedShipment2.setOriginAddress("Global Distribution LLC Warehouse");
        completedShipment2.setDestinationAddress("456 Oak Avenue, Cambridge, MA");
        completedShipment2.setDestinationStoreName("MegaMart Suburbs");
        completedShipment2.setShippingStatus(ProductShippingRequest.SHIP_STATUS_DELIVERED);
        completedShipment2.setCarrierName("FastShip Logistics");
        completedShipment2.setTrackingNumber("TRK987654321");
        completedShipment2.setStatus("Delivered");
        
        // Delivery Confirmation 2 - Partial delivery, pending confirmation
        DeliveryConfirmationRequest delConfirm2 = new DeliveryConfirmationRequest(completedShipment2);
        delConfirm2.setDeliveredBy("Kevin Garcia");
        delConfirm2.setConditionOnArrival(DeliveryConfirmationRequest.CONDITION_PARTIAL);
        delConfirm2.setDeliveryNotes("Only 20 out of 25 units delivered. 5 units backordered.");
        delConfirm2.setQuantityDelivered(20);  // Override to show partial
        delConfirm2.setConfirmed(false);
        delConfirm2.setMessage("Partial delivery - please confirm receipt");
        delConfirm2.setStatus("Awaiting Confirmation");
        // Set delivery date to 2 hours ago
        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.HOUR_OF_DAY, -2);
        delConfirm2.setDeliveryDate(cal2.getTime());
        // Add to Distributor Sales Organization
        distSalesOrg.getWorkQueue().addWorkRequest(delConfirm2);
        
        // ============================================================
        // Sample 6: Already confirmed delivery (for history table)
        // ============================================================
        ProductShippingRequest completedShipment3 = new ProductShippingRequest();
        completedShipment3.setProductName("Electronic Components Set A");
        completedShipment3.setProductCode("EC-001");
        completedShipment3.setQuantity(75);
        completedShipment3.setUnit("sets");
        completedShipment3.setOriginAddress("Global Distribution LLC Warehouse");
        completedShipment3.setDestinationAddress("789 Harbor View, Boston, MA");
        completedShipment3.setDestinationStoreName("MegaMart Harbor");
        completedShipment3.setShippingStatus(ProductShippingRequest.SHIP_STATUS_DELIVERED);
        completedShipment3.setCarrierName("FastShip Logistics");
        completedShipment3.setTrackingNumber("TRK111222333");
        completedShipment3.setStatus("Delivered");
        
        DeliveryConfirmationRequest delConfirm3 = new DeliveryConfirmationRequest(completedShipment3);
        delConfirm3.setDeliveredBy("Daniel Harris");
        delConfirm3.setConditionOnArrival(DeliveryConfirmationRequest.CONDITION_GOOD);
        delConfirm3.setDeliveryNotes("All items received in good condition");
        // Set delivery date to 3 days ago
        Calendar cal3 = Calendar.getInstance();
        cal3.add(Calendar.DAY_OF_MONTH, -3);
        delConfirm3.setDeliveryDate(cal3.getTime());
        // Confirm this one (for history display)
        delConfirm3.confirmDelivery("James Taylor");
        delConfirm3.setMessage("Delivery confirmed");
        delConfirm3.setStatus("Completed");
        // Add to Distributor Sales Organization
        distSalesOrg.getWorkQueue().addWorkRequest(delConfirm3);
        
        // ============================================================
        // Sample 7: Delivery confirmation for Retail (optional - as per original)
        // ============================================================
        DeliveryConfirmationRequest delConfirmRetail = new DeliveryConfirmationRequest();
        delConfirmRetail.setProductName("Industrial Tool Set");
        delConfirmRetail.setProductCode("IT-001");
        delConfirmRetail.setQuantityDelivered(10);
        delConfirmRetail.setUnit("sets");
        delConfirmRetail.setStoreName("MegaMart Downtown");
        delConfirmRetail.setDeliveredBy("Daniel Harris");
        delConfirmRetail.setConditionOnArrival(DeliveryConfirmationRequest.CONDITION_GOOD);
        delConfirmRetail.setDeliveryNotes("Delivered to warehouse area");
        delConfirmRetail.setConfirmed(false);
        delConfirmRetail.setMessage("Please confirm receipt");
        delConfirmRetail.setStatus("Awaiting Confirmation");
        retailStoreOrg.getWorkQueue().addWorkRequest(delConfirmRetail);
        
        // ============================================================
        // Sample 8: Product Shipping Requests TO Distributor (for Receive Shipment testing)
        // These are shipments FROM Manufacturer TO Distributor
        // ============================================================
        Organization distInventoryOrg = distributor.getOrganizationDirectory().getOrganizationList().get(1); // WholesaleInventoryOrganization
        UserAccount distInvUser = distInventoryOrg.getUserAccountDirectory().getUserAccountList().get(0);
        
        // 8.1 Shipment - Delivered, ready to receive
        ProductShippingRequest toDistShip1 = new ProductShippingRequest();
        toDistShip1.setProductName("Electronic Components Set A");
        toDistShip1.setProductCode("EC-001");
        toDistShip1.setQuantity(200);
        toDistShip1.setUnit("sets");
        toDistShip1.setOriginAddress("XYZ Manufacturing Inc., 100 Factory Lane, Worcester, MA");
        toDistShip1.setDestinationAddress("Global Distribution LLC, 789 Warehouse Blvd, Worcester, MA");
        toDistShip1.setDestinationStoreName("Global Distribution LLC");
        toDistShip1.setShippingStatus(ProductShippingRequest.SHIP_STATUS_DELIVERED);
        toDistShip1.setCarrierName("FastShip Logistics");
        toDistShip1.setTrackingNumber("MFG-DIST-001");
        toDistShip1.setPackageWeight(150.0);
        toDistShip1.setMessage("Monthly supply of electronic components");
        toDistShip1.setStatus("Delivered");
        Calendar ship1Date = Calendar.getInstance();
        ship1Date.add(Calendar.DAY_OF_MONTH, -2);
        toDistShip1.setRequestDate(ship1Date.getTime());
        distInventoryOrg.getWorkQueue().addWorkRequest(toDistShip1);
        
        // 8.2 Shipment - Out for Delivery, ready to receive
        ProductShippingRequest toDistShip2 = new ProductShippingRequest();
        toDistShip2.setProductName("Home Appliance Kit A");
        toDistShip2.setProductCode("HA-001");
        toDistShip2.setQuantity(100);
        toDistShip2.setUnit("units");
        toDistShip2.setOriginAddress("XYZ Manufacturing Inc., 100 Factory Lane, Worcester, MA");
        toDistShip2.setDestinationAddress("Global Distribution LLC, 789 Warehouse Blvd, Worcester, MA");
        toDistShip2.setDestinationStoreName("Global Distribution LLC");
        toDistShip2.setShippingStatus(ProductShippingRequest.SHIP_STATUS_OUT_FOR_DELIVERY);
        toDistShip2.setCarrierName("FastShip Logistics");
        toDistShip2.setTrackingNumber("MFG-DIST-002");
        toDistShip2.setPackageWeight(500.0);
        toDistShip2.setMessage("Urgent appliance shipment");
        toDistShip2.setStatus("Out for Delivery");
        Calendar ship2Date = Calendar.getInstance();
        ship2Date.add(Calendar.DAY_OF_MONTH, -1);
        toDistShip2.setRequestDate(ship2Date.getTime());
        distInventoryOrg.getWorkQueue().addWorkRequest(toDistShip2);
        
        // 8.3 Shipment - In Transit
        ProductShippingRequest toDistShip3 = new ProductShippingRequest();
        toDistShip3.setProductName("Home Appliance Kit B");
        toDistShip3.setProductCode("HA-002");
        toDistShip3.setQuantity(75);
        toDistShip3.setUnit("units");
        toDistShip3.setOriginAddress("XYZ Manufacturing Inc., 100 Factory Lane, Worcester, MA");
        toDistShip3.setDestinationAddress("Global Distribution LLC, 789 Warehouse Blvd, Worcester, MA");
        toDistShip3.setDestinationStoreName("Global Distribution LLC");
        toDistShip3.setShippingStatus(ProductShippingRequest.SHIP_STATUS_IN_TRANSIT);
        toDistShip3.setCarrierName("QuickDeliver Express");
        toDistShip3.setTrackingNumber("MFG-DIST-003");
        toDistShip3.setPackageWeight(350.0);
        toDistShip3.setMessage("Standard shipment - appliances");
        toDistShip3.setStatus("In Transit");
        Calendar ship3Date = Calendar.getInstance();
        ship3Date.add(Calendar.DAY_OF_MONTH, -3);
        toDistShip3.setRequestDate(ship3Date.getTime());
        // Set estimated delivery date
        Calendar estDel3 = Calendar.getInstance();
        estDel3.add(Calendar.DAY_OF_MONTH, 2);
        toDistShip3.setEstimatedDeliveryDate(estDel3.getTime());
        distInventoryOrg.getWorkQueue().addWorkRequest(toDistShip3);
        
        // 8.4 Shipment - Pending (just created)
        ProductShippingRequest toDistShip4 = new ProductShippingRequest();
        toDistShip4.setProductName("Office Supplies Pack");
        toDistShip4.setProductCode("OS-001");
        toDistShip4.setQuantity(300);
        toDistShip4.setUnit("packs");
        toDistShip4.setOriginAddress("XYZ Manufacturing Inc., 100 Factory Lane, Worcester, MA");
        toDistShip4.setDestinationAddress("Global Distribution LLC, 789 Warehouse Blvd, Worcester, MA");
        toDistShip4.setDestinationStoreName("Global Distribution LLC");
        toDistShip4.setShippingStatus(ProductShippingRequest.SHIP_STATUS_PENDING);
        toDistShip4.setCarrierName("FastShip Logistics");
        toDistShip4.setTrackingNumber("MFG-DIST-004");
        toDistShip4.setPackageWeight(120.0);
        toDistShip4.setMessage("Office supplies restock");
        toDistShip4.setStatus("Pending");
        distInventoryOrg.getWorkQueue().addWorkRequest(toDistShip4);
        
        // 8.5 Shipment - Already Received (for history table)
        ProductShippingRequest toDistShip5 = new ProductShippingRequest();
        toDistShip5.setProductName("Industrial Tool Set");
        toDistShip5.setProductCode("IT-001");
        toDistShip5.setQuantity(50);
        toDistShip5.setUnit("sets");
        toDistShip5.setOriginAddress("XYZ Manufacturing Inc., 100 Factory Lane, Worcester, MA");
        toDistShip5.setDestinationAddress("Global Distribution LLC, 789 Warehouse Blvd, Worcester, MA");
        toDistShip5.setDestinationStoreName("Global Distribution LLC");
        toDistShip5.setShippingStatus(ProductShippingRequest.SHIP_STATUS_DELIVERED);
        toDistShip5.setCarrierName("FastShip Logistics");
        toDistShip5.setTrackingNumber("MFG-DIST-005");
        toDistShip5.setPackageWeight(200.0);
        toDistShip5.setMessage("Tool set shipment");
        toDistShip5.setStatus("Received");
        toDistShip5.setReceiver(distInvUser);
        Calendar ship5Date = Calendar.getInstance();
        ship5Date.add(Calendar.DAY_OF_MONTH, -5);
        toDistShip5.setRequestDate(ship5Date.getTime());
        Calendar recv5Date = Calendar.getInstance();
        recv5Date.add(Calendar.DAY_OF_MONTH, -4);
        toDistShip5.setResolveDate(recv5Date.getTime());
        distInventoryOrg.getWorkQueue().addWorkRequest(toDistShip5);
        
        // 8.6 Another Already Received (for history table)
        ProductShippingRequest toDistShip6 = new ProductShippingRequest();
        toDistShip6.setProductName("Electronic Components Set A");
        toDistShip6.setProductCode("EC-001");
        toDistShip6.setQuantity(150);
        toDistShip6.setUnit("sets");
        toDistShip6.setOriginAddress("XYZ Manufacturing Inc., 100 Factory Lane, Worcester, MA");
        toDistShip6.setDestinationAddress("Global Distribution LLC, 789 Warehouse Blvd, Worcester, MA");
        toDistShip6.setDestinationStoreName("Global Distribution LLC");
        toDistShip6.setShippingStatus(ProductShippingRequest.SHIP_STATUS_DELIVERED);
        toDistShip6.setCarrierName("QuickDeliver Express");
        toDistShip6.setTrackingNumber("MFG-DIST-006");
        toDistShip6.setPackageWeight(110.0);
        toDistShip6.setMessage("Electronic components restock");
        toDistShip6.setStatus("Received");
        toDistShip6.setReceiver(distInvUser);
        Calendar ship6Date = Calendar.getInstance();
        ship6Date.add(Calendar.DAY_OF_MONTH, -7);
        toDistShip6.setRequestDate(ship6Date.getTime());
        Calendar recv6Date = Calendar.getInstance();
        recv6Date.add(Calendar.DAY_OF_MONTH, -6);
        toDistShip6.setResolveDate(recv6Date.getTime());
        distInventoryOrg.getWorkQueue().addWorkRequest(toDistShip6);
        
        // 8.7 Shipment - Delivered yesterday (another ready to receive)
        ProductShippingRequest toDistShip7 = new ProductShippingRequest();
        toDistShip7.setProductName("Electronic Components Set B");
        toDistShip7.setProductCode("EC-002");
        toDistShip7.setQuantity(80);
        toDistShip7.setUnit("sets");
        toDistShip7.setOriginAddress("XYZ Manufacturing Inc., 100 Factory Lane, Worcester, MA");
        toDistShip7.setDestinationAddress("Global Distribution LLC, 789 Warehouse Blvd, Worcester, MA");
        toDistShip7.setDestinationStoreName("Global Distribution LLC");
        toDistShip7.setShippingStatus(ProductShippingRequest.SHIP_STATUS_DELIVERED);
        toDistShip7.setCarrierName("FastShip Logistics");
        toDistShip7.setTrackingNumber("MFG-DIST-007");
        toDistShip7.setPackageWeight(60.0);
        toDistShip7.setMessage("Electronic set B - new product line");
        toDistShip7.setStatus("Delivered");
        Calendar ship7Date = Calendar.getInstance();
        ship7Date.add(Calendar.DAY_OF_MONTH, -1);
        toDistShip7.setRequestDate(ship7Date.getTime());
        distInventoryOrg.getWorkQueue().addWorkRequest(toDistShip7);
    }
}