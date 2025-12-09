package Business;

import Business.Employee.Employee;
import Business.Enterprise.*;
import Business.Inventory.Inventory;
import Business.Inventory.InventoryItem;
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
import com.github.javafaker.Faker;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Enhanced System Configuration with Java Faker
 * Generates comprehensive test data for all enterprises, 
 * especially for Distributor Enterprise testing
 * 
 * @author chris
 */
public class ConfigureASystem {
    
    private static final Faker faker = new Faker();
    private static final Random random = new Random();
    
    public static EcoSystem configure() {
        
        EcoSystem system = EcoSystem.getInstance();
        
        // 1. Create System Admin
        Employee sysAdminEmployee = system.getEmployeeDirectory().createEmployee("System Admin");
        system.getUserAccountDirectory().createUserAccount("sysadmin", "sysadmin", sysAdminEmployee, new SystemAdminRole());
        
        // 2. Create Network
        Network supplyChainNetwork = system.createAndAddNetwork("Supply Chain Network");
        
        // 3. Create Enterprises
        RawMaterialSupplierEnterprise rmSupplier = new RawMaterialSupplierEnterprise("ABC Raw Materials Co.");
        supplyChainNetwork.getEnterpriseDirectory().addEnterprise(rmSupplier);
        configureRMSupplierEnterprise(rmSupplier);
        
        ManufacturerEnterprise manufacturer = new ManufacturerEnterprise("XYZ Manufacturing Inc.");
        supplyChainNetwork.getEnterpriseDirectory().addEnterprise(manufacturer);
        configureManufacturerEnterprise(manufacturer);
        
        ProductDistributorEnterprise distributor = new ProductDistributorEnterprise("Global Distribution LLC");
        supplyChainNetwork.getEnterpriseDirectory().addEnterprise(distributor);
        configureDistributorEnterprise(distributor);
        
        ShippingEnterprise shipping = new ShippingEnterprise("FastShip Logistics");
        supplyChainNetwork.getEnterpriseDirectory().addEnterprise(shipping);
        configureShippingEnterprise(shipping);
        
        RetailEnterprise retail = new RetailEnterprise("MegaMart Retail");
        supplyChainNetwork.getEnterpriseDirectory().addEnterprise(retail);
        configureRetailEnterprise(retail);
        
        // SECOND Distributor Enterprise
        ProductDistributorEnterprise distributor2 = new ProductDistributorEnterprise("Pacific Wholesale Partners");
        supplyChainNetwork.getEnterpriseDirectory().addEnterprise(distributor2);
        configureSecondDistributorEnterprise(distributor2);
        
        // 4. Setup Catalogs and Inventory
        setupCatalogsAndInventory(rmSupplier, manufacturer, distributor, retail);
        
        // 5. Create Sample WorkRequests
        createSampleWorkRequests(rmSupplier, manufacturer, distributor, shipping, retail);
        
        // 6. Create Enhanced Test Data
        createDistributorTestDataWithFaker(distributor, manufacturer, shipping, retail);
        createManufacturerTestDataWithFaker(manufacturer, rmSupplier, shipping, retail);
        
        // 7. Setup Second Distributor
        setupSecondDistributorCatalogAndInventory(distributor2);
        createSecondDistributorTestData(distributor2, manufacturer, shipping);
        
        return system;
    }
    
    private static void configureRMSupplierEnterprise(RawMaterialSupplierEnterprise enterprise) {
        Organization rmProcurementOrg = enterprise.getOrganizationDirectory().getOrganizationList().get(0);
        Organization rmInventoryOrg = enterprise.getOrganizationDirectory().getOrganizationList().get(1);
        
        Employee procurementMgr = rmProcurementOrg.getEmployeeDirectory().createEmployee(faker.name().fullName());
        rmProcurementOrg.getUserAccountDirectory().createUserAccount("rm_proc1", "password", procurementMgr, new RMProcurementRole());
        
        Employee procurementStaff = rmProcurementOrg.getEmployeeDirectory().createEmployee(faker.name().fullName());
        rmProcurementOrg.getUserAccountDirectory().createUserAccount("rm_proc2", "password", procurementStaff, new RMProcurementRole());
        
        Employee inventoryMgr = rmInventoryOrg.getEmployeeDirectory().createEmployee(faker.name().fullName());
        rmInventoryOrg.getUserAccountDirectory().createUserAccount("rm_inv1", "password", inventoryMgr, new RMInventoryManagerRole());
        
        Employee inventoryStaff = rmInventoryOrg.getEmployeeDirectory().createEmployee(faker.name().fullName());
        rmInventoryOrg.getUserAccountDirectory().createUserAccount("rm_inv2", "password", inventoryStaff, new RMInventoryManagerRole());
    }
    
    private static void configureManufacturerEnterprise(ManufacturerEnterprise enterprise) {
        Organization productionOrg = enterprise.getOrganizationDirectory().getOrganizationList().get(0);
        Organization inventoryOrg = enterprise.getOrganizationDirectory().getOrganizationList().get(1);
        
        Employee productionMgr = productionOrg.getEmployeeDirectory().createEmployee(faker.name().fullName());
        productionOrg.getUserAccountDirectory().createUserAccount("mfg_prod1", "password", productionMgr, new ProductionManagerRole());
        
        Employee productionStaff = productionOrg.getEmployeeDirectory().createEmployee(faker.name().fullName());
        productionOrg.getUserAccountDirectory().createUserAccount("mfg_prod2", "password", productionStaff, new ProductionManagerRole());
        
        Employee invMgr = inventoryOrg.getEmployeeDirectory().createEmployee(faker.name().fullName());
        inventoryOrg.getUserAccountDirectory().createUserAccount("mfg_inv1", "password", invMgr, new InventoryManagerRole());
        
        Employee invStaff = inventoryOrg.getEmployeeDirectory().createEmployee(faker.name().fullName());
        inventoryOrg.getUserAccountDirectory().createUserAccount("mfg_inv2", "password", invStaff, new InventoryManagerRole());
        
        Employee productionStaff2 = productionOrg.getEmployeeDirectory().createEmployee(faker.name().fullName());
        productionOrg.getUserAccountDirectory().createUserAccount("mp", "", productionStaff2, new ProductionManagerRole());
        
        Employee invStaff2 = inventoryOrg.getEmployeeDirectory().createEmployee("Lisa Wilson");
        inventoryOrg.getUserAccountDirectory().createUserAccount("mi", "", invStaff2, new InventoryManagerRole());
    }
    
    private static void configureDistributorEnterprise(ProductDistributorEnterprise enterprise) {
        Organization salesOrg = enterprise.getOrganizationDirectory().getOrganizationList().get(0);
        Organization inventoryOrg = enterprise.getOrganizationDirectory().getOrganizationList().get(1);
        
        Employee salesMgr = salesOrg.getEmployeeDirectory().createEmployee("James Taylor");
        salesOrg.getUserAccountDirectory().createUserAccount("dist_sales1", "password", salesMgr, new WholesaleSalesRole());
        
        Employee salesRep1 = salesOrg.getEmployeeDirectory().createEmployee(faker.name().fullName());
        salesOrg.getUserAccountDirectory().createUserAccount("dist_sales2", "password", salesRep1, new WholesaleSalesRole());
        
        Employee salesAnalyst = salesOrg.getEmployeeDirectory().createEmployee("Jennifer Martinez");
        salesOrg.getUserAccountDirectory().createUserAccount("dist_analyst1", "password", salesAnalyst, new WholesaleAnalyticsRole());
        
        Employee salesAnalyst2 = salesOrg.getEmployeeDirectory().createEmployee(faker.name().fullName());
        salesOrg.getUserAccountDirectory().createUserAccount("dist_analyst2", "password", salesAnalyst2, new WholesaleAnalyticsRole());
        
        Employee whInvMgr = inventoryOrg.getEmployeeDirectory().createEmployee("Michael Thomas");
        inventoryOrg.getUserAccountDirectory().createUserAccount("dist_inv1", "password", whInvMgr, new WholesaleInventoryRole());
        
        Employee whInvStaff = inventoryOrg.getEmployeeDirectory().createEmployee(faker.name().fullName());
        inventoryOrg.getUserAccountDirectory().createUserAccount("dist_inv2", "password", whInvStaff, new WholesaleInventoryRole());
        
        Employee whInvStaff2 = inventoryOrg.getEmployeeDirectory().createEmployee(faker.name().fullName());
        inventoryOrg.getUserAccountDirectory().createUserAccount("dist_inv3", "password", whInvStaff2, new WholesaleInventoryRole());
    }
    
    private static void configureSecondDistributorEnterprise(ProductDistributorEnterprise enterprise) {
        Organization salesOrg = enterprise.getOrganizationDirectory().getOrganizationList().get(0);
        Organization inventoryOrg = enterprise.getOrganizationDirectory().getOrganizationList().get(1);
        
        Employee salesMgr = salesOrg.getEmployeeDirectory().createEmployee("Robert Chen");
        salesOrg.getUserAccountDirectory().createUserAccount("pac_sales1", "password", salesMgr, new WholesaleSalesRole());
        
        Employee salesRep1 = salesOrg.getEmployeeDirectory().createEmployee("Michelle Wong");
        salesOrg.getUserAccountDirectory().createUserAccount("pac_sales2", "password", salesRep1, new WholesaleSalesRole());
        
        Employee salesRep2 = salesOrg.getEmployeeDirectory().createEmployee(faker.name().fullName());
        salesOrg.getUserAccountDirectory().createUserAccount("pac_sales3", "password", salesRep2, new WholesaleSalesRole());
        
        Employee salesAnalyst = salesOrg.getEmployeeDirectory().createEmployee("David Kim");
        salesOrg.getUserAccountDirectory().createUserAccount("pac_analyst1", "password", salesAnalyst, new WholesaleAnalyticsRole());
        
        Employee salesAnalyst2 = salesOrg.getEmployeeDirectory().createEmployee(faker.name().fullName());
        salesOrg.getUserAccountDirectory().createUserAccount("pac_analyst2", "password", salesAnalyst2, new WholesaleAnalyticsRole());
        
        Employee whInvMgr = inventoryOrg.getEmployeeDirectory().createEmployee("Sarah Park");
        inventoryOrg.getUserAccountDirectory().createUserAccount("pac_inv1", "password", whInvMgr, new WholesaleInventoryRole());
        
        Employee whInvStaff = inventoryOrg.getEmployeeDirectory().createEmployee("Kevin Nguyen");
        inventoryOrg.getUserAccountDirectory().createUserAccount("pac_inv2", "password", whInvStaff, new WholesaleInventoryRole());
        
        Employee whInvStaff2 = inventoryOrg.getEmployeeDirectory().createEmployee(faker.name().fullName());
        inventoryOrg.getUserAccountDirectory().createUserAccount("pac_inv3", "password", whInvStaff2, new WholesaleInventoryRole());
        
        Employee quickSales = salesOrg.getEmployeeDirectory().createEmployee("Quick Sales User");
        salesOrg.getUserAccountDirectory().createUserAccount("ps", "", quickSales, new WholesaleSalesRole());
        
        Employee quickAnalyst = salesOrg.getEmployeeDirectory().createEmployee("Quick Analyst User");
        salesOrg.getUserAccountDirectory().createUserAccount("pa", "", quickAnalyst, new WholesaleAnalyticsRole());
        
        Employee quickInv = inventoryOrg.getEmployeeDirectory().createEmployee("Quick Inventory User");
        inventoryOrg.getUserAccountDirectory().createUserAccount("pi", "", quickInv, new WholesaleInventoryRole());
    }
    
    private static void configureShippingEnterprise(ShippingEnterprise enterprise) {
        Organization shipMgmtOrg = enterprise.getOrganizationDirectory().getOrganizationList().get(0);
        Organization shipOpOrg = enterprise.getOrganizationDirectory().getOrganizationList().get(1);
        
        Employee shipMgr = shipMgmtOrg.getEmployeeDirectory().createEmployee(faker.name().fullName());
        shipMgmtOrg.getUserAccountDirectory().createUserAccount("ship_mgr1", "password", shipMgr, new ShippingManagerRole());
        
        Employee deliveryStaff1 = shipOpOrg.getEmployeeDirectory().createEmployee("Daniel Harris");
        shipOpOrg.getUserAccountDirectory().createUserAccount("ship_del1", "password", deliveryStaff1, new DeliveryStaffRole());
        
        Employee deliveryStaff2 = shipOpOrg.getEmployeeDirectory().createEmployee("Kevin Garcia");
        shipOpOrg.getUserAccountDirectory().createUserAccount("ship_del2", "password", deliveryStaff2, new DeliveryStaffRole());
    }
    
    private static void configureRetailEnterprise(RetailEnterprise enterprise) {
        Organization storeOrg = enterprise.getOrganizationDirectory().getOrganizationList().get(0);
        Organization retailInvOrg = enterprise.getOrganizationDirectory().getOrganizationList().get(1);
        
        Employee storeMgr = storeOrg.getEmployeeDirectory().createEmployee(faker.name().fullName());
        storeOrg.getUserAccountDirectory().createUserAccount("retail_mgr1", "password", storeMgr, new StoreManagerRole());
        
        Employee orderClerk = storeOrg.getEmployeeDirectory().createEmployee(faker.name().fullName());
        storeOrg.getUserAccountDirectory().createUserAccount("retail_clerk1", "password", orderClerk, new OrderClerkRole());
        
        Employee retailAnalyst = retailInvOrg.getEmployeeDirectory().createEmployee(faker.name().fullName());
        retailInvOrg.getUserAccountDirectory().createUserAccount("retail_analyst1", "password", retailAnalyst, new RetailAnalyticsRole());
    }
    
    private static void setupCatalogsAndInventory(
            RawMaterialSupplierEnterprise rmSupplier,
            ManufacturerEnterprise manufacturer,
            ProductDistributorEnterprise distributor,
            RetailEnterprise retail) {
        
        // Raw Material Supplier
        MaterialCatalog rmMaterialCatalog = rmSupplier.getMaterialCatalog();
        Inventory rmInventory = rmSupplier.getInventory();
        
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
        
        rmInventory.addMaterial(steelSheets, 10000, 1000, 20000, "Warehouse A");
        rmInventory.addMaterial(copperWire, 5000, 500, 10000, "Warehouse B");
        rmInventory.addMaterial(plasticPellets, 8000, 800, 15000, "Warehouse A");
        rmInventory.addMaterial(aluminumBars, 6000, 600, 12000, "Warehouse C");
        
        // Manufacturer
        ProductCatalog mfgProductCatalog = manufacturer.getProductCatalog();
        Inventory mfgInventory = manufacturer.getInventory();
        
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
        
        mfgInventory.addProduct(electronicSet, 2000, 200, 5000, "Production Line A");
        mfgInventory.addProduct(applianceKitA, 500, 50, 1000, "Production Line B");
        mfgInventory.addProduct(applianceKitB, 300, 30, 600, "Production Line B");
        mfgInventory.addProduct(officeSupply, 3000, 300, 6000, "Production Line C");
        mfgInventory.addProduct(industrialTool, 150, 20, 300, "Production Line D");
        
        MaterialCatalog mfgMaterialCatalog = manufacturer.getMaterialCatalog();
        
        Material mfgSteelSheets = mfgMaterialCatalog.createMaterial("RM-001", "Steel Sheets", 2.50, "kg");
        mfgSteelSheets.setCategory("Metals");
        mfgSteelSheets.setDescription("High-quality steel sheets for manufacturing");
        
        Material mfgCopperWire = mfgMaterialCatalog.createMaterial("RM-002", "Copper Wire", 5.00, "meters");
        mfgCopperWire.setCategory("Metals");
        mfgCopperWire.setDescription("Pure copper wire for electrical components");
        
        Material mfgPlasticPellets = mfgMaterialCatalog.createMaterial("RM-003", "Plastic Pellets", 1.20, "kg");
        mfgPlasticPellets.setCategory("Plastics");
        mfgPlasticPellets.setDescription("Industrial grade plastic pellets");
        
        Material mfgAluminumBars = mfgMaterialCatalog.createMaterial("RM-004", "Aluminum Bars", 3.75, "kg");
        mfgAluminumBars.setCategory("Metals");
        mfgAluminumBars.setDescription("Lightweight aluminum bars");
        
        mfgInventory.addMaterial(mfgSteelSheets, 10000, 1000, 20000, "Warehouse A");
        mfgInventory.addMaterial(mfgCopperWire, 5000, 500, 10000, "Warehouse B");
        mfgInventory.addMaterial(mfgPlasticPellets, 8000, 800, 15000, "Warehouse A");
        mfgInventory.addMaterial(mfgAluminumBars, 6000, 600, 12000, "Warehouse C");
        
        // Distributor
        setupDistributorCatalogAndInventory(distributor);
        
        // Retail
        ProductCatalog retailProductCatalog = retail.getProductCatalog();
        Inventory retailInventory = retail.getInventory();
        
        Product retailElectronic = retailProductCatalog.createProduct("EC-001", "Electronic Components Set A", 25.00, "sets");
        retailElectronic.setCategory("Electronics");
        
        Product retailAppliance = retailProductCatalog.createProduct("HA-001", "Home Appliance Kit A", 89.99, "units");
        retailAppliance.setCategory("Appliances");
        
        Product retailApplianceB = retailProductCatalog.createProduct("HA-002", "Home Appliance Kit B", 129.99, "units");
        retailApplianceB.setCategory("Appliances");
        
        Product retailOffice = retailProductCatalog.createProduct("OS-001", "Office Supplies Pack", 19.99, "packs");
        retailOffice.setCategory("Office");
        
        Product retailTool = retailProductCatalog.createProduct("IT-001", "Industrial Tool Set", 199.99, "sets");
        retailTool.setCategory("Industrial");
        
        retailInventory.addProduct(retailElectronic, 150, 20, 300, "Store Shelf A1");
        retailInventory.addProduct(retailAppliance, 50, 10, 100, "Store Shelf B1");
        retailInventory.addProduct(retailApplianceB, 30, 5, 60, "Store Shelf B2");
        retailInventory.addProduct(retailOffice, 200, 30, 400, "Store Shelf C1");
        retailInventory.addProduct(retailTool, 15, 3, 30, "Store Shelf D1");
    }
    
    private static void setupDistributorCatalogAndInventory(ProductDistributorEnterprise distributor) {
        ProductCatalog catalog = distributor.getProductCatalog();
        Inventory inventory = distributor.getInventory();
        
        Product p1 = catalog.createProduct("EC-001", "Electronic Components Set A", 18.00, "sets");
        p1.setCategory("Electronics");
        p1.setDescription("Complete set of electronic components");
        inventory.addProduct(p1, 3000, 500, 8000, "Distribution Center A");
        
        Product p2 = catalog.createProduct("HA-001", "Home Appliance Kit A", 55.00, "units");
        p2.setCategory("Appliances");
        p2.setDescription("Basic home appliance kit");
        inventory.addProduct(p2, 800, 100, 2000, "Distribution Center B");
        
        Product p3 = catalog.createProduct("HA-002", "Home Appliance Kit B", 85.00, "units");
        p3.setCategory("Appliances");
        p3.setDescription("Premium home appliance kit");
        inventory.addProduct(p3, 400, 50, 1000, "Distribution Center B");
        
        Product p4 = catalog.createProduct("OS-001", "Office Supplies Pack", 12.00, "packs");
        p4.setCategory("Office");
        p4.setDescription("Standard office supplies pack");
        inventory.addProduct(p4, 5000, 1000, 12000, "Distribution Center C");
        
        Product p5 = catalog.createProduct("IT-001", "Industrial Tool Set", 150.00, "sets");
        p5.setCategory("Industrial");
        p5.setDescription("Professional industrial tool set");
        inventory.addProduct(p5, 200, 30, 500, "Distribution Center D");
        
        Product p6 = catalog.createProduct("EC-002", "Electronic Components Set B", 22.00, "sets");
        p6.setCategory("Electronics");
        p6.setDescription("Advanced electronic components set");
        inventory.addProduct(p6, 45, 50, 500, "Distribution Center A");
        
        Product p7 = catalog.createProduct("EC-003", "Electronic Starter Kit", 12.00, "sets");
        p7.setCategory("Electronics");
        p7.setDescription("Basic electronic starter kit");
        InventoryItem outOfStockItem = inventory.addProduct(p7, 30, 20, 300, "Distribution Center A");
        outOfStockItem.setReservedQuantity(30);
        
        Product p8 = catalog.createProduct("HA-003", "Home Appliance Kit C - Deluxe", 120.00, "units");
        p8.setCategory("Appliances");
        p8.setDescription("Deluxe home appliance kit with warranty");
        inventory.addProduct(p8, 600, 50, 800, "Distribution Center B");
        
        Product p9 = catalog.createProduct("OS-002", "Office Supplies Pack - Premium", 18.00, "packs");
        p9.setCategory("Office");
        p9.setDescription("Premium office supplies pack");
        
        Product p10 = catalog.createProduct("FN-001", "Office Furniture Set A", 250.00, "sets");
        p10.setCategory("Furniture");
        p10.setDescription("Complete office furniture set");
        
        Product p11 = catalog.createProduct("FN-002", "Office Furniture Set B - Executive", 450.00, "sets");
        p11.setCategory("Furniture");
        p11.setDescription("Executive office furniture set");
        
        Product p12 = catalog.createProduct("SF-001", "Safety Equipment Kit", 65.00, "kits");
        p12.setCategory("Safety");
        p12.setDescription("Workplace safety equipment kit");
        
        Product p13 = catalog.createProduct("CL-001", "Industrial Cleaning Supplies", 35.00, "sets");
        p13.setCategory("Cleaning");
        p13.setDescription("Industrial cleaning supplies set");
    }
    
    private static void setupSecondDistributorCatalogAndInventory(ProductDistributorEnterprise distributor2) {
        ProductCatalog catalog = distributor2.getProductCatalog();
        Inventory inventory = distributor2.getInventory();
        
        // Tech & Gadgets
        Product tech1 = catalog.createProduct("TG-001", "Smart Home Hub", 89.00, "units");
        tech1.setCategory("Tech & Gadgets");
        tech1.setDescription("Central smart home control hub with voice assistant");
        inventory.addProduct(tech1, 500, 50, 1000, "Pacific Warehouse A");
        
        Product tech2 = catalog.createProduct("TG-002", "Wireless Security Camera Set", 149.00, "sets");
        tech2.setCategory("Tech & Gadgets");
        tech2.setDescription("4-camera wireless security system with night vision");
        inventory.addProduct(tech2, 300, 30, 600, "Pacific Warehouse A");
        
        Product tech3 = catalog.createProduct("TG-003", "Smart Thermostat Pro", 75.00, "units");
        tech3.setCategory("Tech & Gadgets");
        tech3.setDescription("Wi-Fi enabled programmable thermostat");
        inventory.addProduct(tech3, 400, 40, 800, "Pacific Warehouse A");
        
        Product tech4 = catalog.createProduct("TG-004", "Robot Vacuum Cleaner", 299.00, "units");
        tech4.setCategory("Tech & Gadgets");
        tech4.setDescription("AI-powered robot vacuum with mapping");
        inventory.addProduct(tech4, 25, 30, 200, "Pacific Warehouse A");
        
        // Outdoor & Garden
        Product outdoor1 = catalog.createProduct("OG-001", "Solar Garden Light Set", 45.00, "sets");
        outdoor1.setCategory("Outdoor & Garden");
        outdoor1.setDescription("10-piece solar powered garden lights");
        inventory.addProduct(outdoor1, 800, 100, 1500, "Pacific Warehouse B");
        
        Product outdoor2 = catalog.createProduct("OG-002", "Patio Furniture Set - Premium", 599.00, "sets");
        outdoor2.setCategory("Outdoor & Garden");
        outdoor2.setDescription("5-piece weather resistant patio set");
        inventory.addProduct(outdoor2, 150, 20, 300, "Pacific Warehouse B");
        
        Product outdoor3 = catalog.createProduct("OG-003", "Electric Lawn Mower", 349.00, "units");
        outdoor3.setCategory("Outdoor & Garden");
        outdoor3.setDescription("Cordless electric lawn mower with battery");
        inventory.addProduct(outdoor3, 200, 25, 400, "Pacific Warehouse B");
        
        Product outdoor4 = catalog.createProduct("OG-004", "BBQ Grill - Professional", 899.00, "units");
        outdoor4.setCategory("Outdoor & Garden");
        outdoor4.setDescription("Stainless steel professional BBQ grill");
        InventoryItem outOfStock = inventory.addProduct(outdoor4, 50, 10, 100, "Pacific Warehouse B");
        outOfStock.setReservedQuantity(50);
        
        // Fitness & Wellness
        Product fitness1 = catalog.createProduct("FW-001", "Home Gym Set - Basic", 299.00, "sets");
        fitness1.setCategory("Fitness & Wellness");
        fitness1.setDescription("Dumbbells, resistance bands, yoga mat combo");
        inventory.addProduct(fitness1, 350, 40, 700, "Pacific Warehouse C");
        
        Product fitness2 = catalog.createProduct("FW-002", "Treadmill - Smart", 799.00, "units");
        fitness2.setCategory("Fitness & Wellness");
        fitness2.setDescription("Foldable smart treadmill with display");
        inventory.addProduct(fitness2, 100, 15, 200, "Pacific Warehouse C");
        
        Product fitness3 = catalog.createProduct("FW-003", "Exercise Bike - Pro", 549.00, "units");
        fitness3.setCategory("Fitness & Wellness");
        fitness3.setDescription("Indoor cycling bike with resistance control");
        inventory.addProduct(fitness3, 180, 20, 350, "Pacific Warehouse C");
        
        // Kitchen & Dining
        Product kitchen1 = catalog.createProduct("KD-001", "Air Fryer - XL", 129.00, "units");
        kitchen1.setCategory("Kitchen & Dining");
        kitchen1.setDescription("5.8 quart digital air fryer");
        inventory.addProduct(kitchen1, 600, 75, 1200, "Pacific Warehouse D");
        
        Product kitchen2 = catalog.createProduct("KD-002", "Coffee Machine - Espresso", 249.00, "units");
        kitchen2.setCategory("Kitchen & Dining");
        kitchen2.setDescription("Semi-automatic espresso machine with grinder");
        inventory.addProduct(kitchen2, 250, 30, 500, "Pacific Warehouse D");
        
        Product kitchen3 = catalog.createProduct("KD-003", "Cookware Set - 15 Piece", 189.00, "sets");
        kitchen3.setCategory("Kitchen & Dining");
        kitchen3.setDescription("Non-stick ceramic cookware set");
        inventory.addProduct(kitchen3, 400, 50, 800, "Pacific Warehouse D");
        
        Product kitchen4 = catalog.createProduct("KD-004", "Stand Mixer - Professional", 399.00, "units");
        kitchen4.setCategory("Kitchen & Dining");
        kitchen4.setDescription("5-speed stand mixer with attachments");
        inventory.addProduct(kitchen4, 35, 40, 200, "Pacific Warehouse D");
        
        // Catalog Only
        Product catOnly1 = catalog.createProduct("TG-005", "Smart Doorbell Pro", 179.00, "units");
        catOnly1.setCategory("Tech & Gadgets");
        catOnly1.setDescription("Video doorbell with 2-way audio");
        
        Product catOnly2 = catalog.createProduct("OG-005", "Outdoor Heater - Patio", 229.00, "units");
        catOnly2.setCategory("Outdoor & Garden");
        catOnly2.setDescription("Propane patio heater");
        
        Product catOnly3 = catalog.createProduct("FW-004", "Massage Gun - Deep Tissue", 149.00, "units");
        catOnly3.setCategory("Fitness & Wellness");
        catOnly3.setDescription("Percussion massage gun with 6 heads");
        
        Product catOnly4 = catalog.createProduct("KD-005", "Blender - High Power", 199.00, "units");
        catOnly4.setCategory("Kitchen & Dining");
        catOnly4.setDescription("1200W professional blender");
    }
    
    private static void createSampleWorkRequests(
            RawMaterialSupplierEnterprise rmSupplier,
            ManufacturerEnterprise manufacturer,
            ProductDistributorEnterprise distributor,
            ShippingEnterprise shipping,
            RetailEnterprise retail) {
        
        Organization rmProcurementOrg = rmSupplier.getOrganizationDirectory().getOrganizationList().get(0);
        Organization mfgProductionOrg = manufacturer.getOrganizationDirectory().getOrganizationList().get(0);
        
        RawMaterialRestockRequest rmRequest1 = new RawMaterialRestockRequest();
        rmRequest1.setMaterialName("Steel Sheets");
        rmRequest1.setQuantity(500);
        rmRequest1.setUnit("kg");
        rmRequest1.setUnitPrice(2.50);
        rmRequest1.setUrgencyLevel("Normal");
        rmRequest1.setMessage("Monthly restock for production line A");
        rmRequest1.setStatus("Pending");
        rmProcurementOrg.getWorkQueue().addWorkRequest(rmRequest1);
        
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
    }
    
    private static void createDistributorTestDataWithFaker(
            ProductDistributorEnterprise distributor,
            ManufacturerEnterprise manufacturer,
            ShippingEnterprise shipping,
            RetailEnterprise retail) {
        
        Organization distSalesOrg = distributor.getOrganizationDirectory().getOrganizationList().get(0);
        Organization distInventoryOrg = distributor.getOrganizationDirectory().getOrganizationList().get(1);
        UserAccount distInvUser = distInventoryOrg.getUserAccountDirectory().getUserAccountList().get(0);
        
        createRetailPurchaseOrders(distSalesOrg, retail);
        createProductShippingRequests(distInventoryOrg, distInvUser);
        createDeliveryConfirmations(distSalesOrg);
        createWholesalePurchaseRequests(distSalesOrg, manufacturer);
    }
    
    private static void createSecondDistributorTestData(
            ProductDistributorEnterprise distributor2,
            ManufacturerEnterprise manufacturer,
            ShippingEnterprise shipping) {
        
        Organization salesOrg = distributor2.getOrganizationDirectory().getOrganizationList().get(0);
        Organization inventoryOrg = distributor2.getOrganizationDirectory().getOrganizationList().get(1);
        UserAccount invUser = inventoryOrg.getUserAccountDirectory().getUserAccountList().get(0);
        
        createPacificRetailOrders(salesOrg);
        createPacificShippingRequests(inventoryOrg, invUser);
        createPacificDeliveryConfirmations(salesOrg);
        createPacificWholesaleRequests(salesOrg);
    }
    
    private static void createRetailPurchaseOrders(Organization distSalesOrg, RetailEnterprise retail) {
        Organization retailStoreOrg = retail.getOrganizationDirectory().getOrganizationList().get(0);
        UserAccount retailUser = retailStoreOrg.getUserAccountDirectory().getUserAccountList().get(0);
        
        String[] storeNames = {"MegaMart Downtown", "MegaMart Suburbs", "MegaMart Harbor", 
            "MegaMart Plaza", "MegaMart Central", "ValueMart Express",
            "SuperStore North", "SuperStore South", "QuickMart East"};
        
        String[][] products = {
            {"EC-001", "Electronic Components Set A", "18.00", "sets"},
            {"HA-001", "Home Appliance Kit A", "55.00", "units"},
            {"HA-002", "Home Appliance Kit B", "85.00", "units"},
            {"OS-001", "Office Supplies Pack", "12.00", "packs"},
            {"IT-001", "Industrial Tool Set", "150.00", "sets"},
            {"EC-002", "Electronic Components Set B", "22.00", "sets"},
            {"HA-003", "Home Appliance Kit C - Deluxe", "120.00", "units"}
        };
        
        String[] statuses = {"Pending", "Pending", "Pending", "Approved", "Approved", 
                            "Processing", "Shipped", "Shipped", "Delivered", "Completed"};
        String[] urgencies = {"Normal", "Normal", "Normal", "Urgent", "Critical"};
        String[] paymentMethods = {"Credit", "Invoice", "Cash", "Wire Transfer"};
        
        for (int i = 0; i < 15; i++) {
            String[] product = products[random.nextInt(products.length)];
            String storeName = storeNames[random.nextInt(storeNames.length)];
            String status = statuses[random.nextInt(statuses.length)];
            
            RetailPurchaseOrderRequest order = new RetailPurchaseOrderRequest();
            order.setProductCode(product[0]);
            order.setProductName(product[1]);
            order.setUnitPrice(Double.parseDouble(product[2]));
            order.setUnit(product[3]);
            order.setQuantity(10 + random.nextInt(200));
            order.setStoreName(storeName);
            order.setStoreAddress(faker.address().fullAddress());
            order.setSender(retailUser);
            order.setPaymentMethod(paymentMethods[random.nextInt(paymentMethods.length)]);
            order.setUrgencyLevel(urgencies[random.nextInt(urgencies.length)]);
            order.setMessage(faker.commerce().promotionCode() + " - " + faker.company().buzzword());
            order.setStatus(status);
            
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -random.nextInt(30));
            order.setRequestDate(cal.getTime());
            
            if (status.equals("Completed") || status.equals("Delivered")) {
                Calendar resolveCal = Calendar.getInstance();
                resolveCal.add(Calendar.DAY_OF_MONTH, -random.nextInt(7));
                order.setResolveDate(resolveCal.getTime());
            }
            
            distSalesOrg.getWorkQueue().addWorkRequest(order);
        }
    }
    
    private static void createProductShippingRequests(Organization distInventoryOrg, UserAccount distInvUser) {
        String[][] products = {
            {"EC-001", "Electronic Components Set A", "sets"},
            {"HA-001", "Home Appliance Kit A", "units"},
            {"HA-002", "Home Appliance Kit B", "units"},
            {"OS-001", "Office Supplies Pack", "packs"},
            {"IT-001", "Industrial Tool Set", "sets"},
            {"EC-002", "Electronic Components Set B", "sets"},
            {"HA-003", "Home Appliance Kit C - Deluxe", "units"}
        };
        
        String[] carriers = {"FastShip Logistics", "QuickDeliver Express", "SafeTransport Co.", "PrimeFreight"};
        
        String[][] statusConfigs = {
            {ProductShippingRequest.SHIP_STATUS_PENDING, "Pending"},
            {ProductShippingRequest.SHIP_STATUS_PENDING, "Pending"},
            {ProductShippingRequest.SHIP_STATUS_IN_TRANSIT, "In Transit"},
            {ProductShippingRequest.SHIP_STATUS_IN_TRANSIT, "In Transit"},
            {ProductShippingRequest.SHIP_STATUS_OUT_FOR_DELIVERY, "Out for Delivery"},
            {ProductShippingRequest.SHIP_STATUS_OUT_FOR_DELIVERY, "Out for Delivery"},
            {ProductShippingRequest.SHIP_STATUS_DELIVERED, "Delivered"},
            {ProductShippingRequest.SHIP_STATUS_DELIVERED, "Delivered"},
            {ProductShippingRequest.SHIP_STATUS_DELIVERED, "Received"},
            {ProductShippingRequest.SHIP_STATUS_DELIVERED, "Received"}
        };
        
        for (int i = 0; i < 12; i++) {
            String[] product = products[random.nextInt(products.length)];
            String[] statusConfig = statusConfigs[i % statusConfigs.length];
            String carrier = carriers[random.nextInt(carriers.length)];
            
            ProductShippingRequest shipment = new ProductShippingRequest();
            shipment.setProductCode(product[0]);
            shipment.setProductName(product[1]);
            shipment.setUnit(product[2]);
            shipment.setQuantity(50 + random.nextInt(250));
            shipment.setOriginAddress("XYZ Manufacturing Inc., " + faker.address().streetAddress() + ", Worcester, MA");
            shipment.setDestinationAddress("Global Distribution LLC, 789 Warehouse Blvd, Worcester, MA");
            shipment.setDestinationStoreName("Global Distribution LLC");
            shipment.setCarrierName(carrier);
            shipment.setTrackingNumber("MFG-DIST-" + String.format("%03d", i + 10));
            shipment.setPackageWeight(50.0 + random.nextDouble() * 450);
            shipment.setShippingStatus(statusConfig[0]);
            shipment.setStatus(statusConfig[1]);
            shipment.setMessage(faker.lorem().sentence());
            
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -random.nextInt(14));
            shipment.setRequestDate(cal.getTime());
            
            if (statusConfig[1].equals("Received")) {
                shipment.setReceiver(distInvUser);
                Calendar resolveCal = Calendar.getInstance();
                resolveCal.add(Calendar.DAY_OF_MONTH, -random.nextInt(3));
                shipment.setResolveDate(resolveCal.getTime());
            }
            
            if (statusConfig[0].equals(ProductShippingRequest.SHIP_STATUS_IN_TRANSIT)) {
                Calendar estCal = Calendar.getInstance();
                estCal.add(Calendar.DAY_OF_MONTH, 1 + random.nextInt(5));
                shipment.setEstimatedDeliveryDate(estCal.getTime());
            }
            
            distInventoryOrg.getWorkQueue().addWorkRequest(shipment);
        }
    }
    
    private static void createDeliveryConfirmations(Organization distSalesOrg) {
        String[] storeNames = {"MegaMart Downtown", "MegaMart Suburbs", "MegaMart Harbor", 
                              "ValueMart Express", "SuperStore North"};
        String[] deliverers = {"Daniel Harris", "Kevin Garcia", "Alex Thompson", "Maria Santos"};
        String[] conditions = {
            DeliveryConfirmationRequest.CONDITION_GOOD,
            DeliveryConfirmationRequest.CONDITION_GOOD,
            DeliveryConfirmationRequest.CONDITION_GOOD,
            DeliveryConfirmationRequest.CONDITION_PARTIAL,
            DeliveryConfirmationRequest.CONDITION_DAMAGED
        };
        
        String[][] products = {
            {"EC-001", "Electronic Components Set A", "sets"},
            {"HA-001", "Home Appliance Kit A", "units"},
            {"OS-001", "Office Supplies Pack", "packs"},
            {"IT-001", "Industrial Tool Set", "sets"}
        };
        
        for (int i = 0; i < 8; i++) {
            String[] product = products[random.nextInt(products.length)];
            String storeName = storeNames[random.nextInt(storeNames.length)];
            String deliverer = deliverers[random.nextInt(deliverers.length)];
            String condition = conditions[random.nextInt(conditions.length)];
            int quantity = 20 + random.nextInt(100);
            
            ProductShippingRequest shipment = new ProductShippingRequest();
            shipment.setProductCode(product[0]);
            shipment.setProductName(product[1]);
            shipment.setQuantity(quantity);
            shipment.setUnit(product[2]);
            shipment.setDestinationStoreName(storeName);
            shipment.setDestinationAddress(faker.address().fullAddress());
            shipment.setCarrierName("FastShip Logistics");
            shipment.setTrackingNumber("TRK" + faker.number().digits(9));
            shipment.setShippingStatus(ProductShippingRequest.SHIP_STATUS_DELIVERED);
            shipment.setStatus("Delivered");
            
            DeliveryConfirmationRequest confirmation = new DeliveryConfirmationRequest(shipment);
            confirmation.setDeliveredBy(deliverer);
            confirmation.setConditionOnArrival(condition);
            
            if (condition.equals(DeliveryConfirmationRequest.CONDITION_GOOD)) {
                confirmation.setDeliveryNotes("All items received in good condition. " + faker.lorem().sentence());
            } else if (condition.equals(DeliveryConfirmationRequest.CONDITION_PARTIAL)) {
                int delivered = quantity - random.nextInt(10) - 5;
                confirmation.setQuantityDelivered(delivered);
                confirmation.setDeliveryNotes("Partial delivery: " + delivered + " of " + quantity + " units.");
            } else {
                confirmation.setDeliveryNotes("Some items damaged during transit. " + faker.lorem().sentence());
            }
            
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -random.nextInt(10));
            confirmation.setDeliveryDate(cal.getTime());
            
            if (i < 4) {
                confirmation.setConfirmed(false);
                confirmation.setStatus("Awaiting Confirmation");
                confirmation.setMessage("Please confirm receipt of " + product[1]);
            } else {
                confirmation.confirmDelivery("James Taylor");
                confirmation.setStatus("Completed");
                confirmation.setMessage("Delivery confirmed");
            }
            
            distSalesOrg.getWorkQueue().addWorkRequest(confirmation);
        }
    }
    
    private static void createWholesalePurchaseRequests(Organization distSalesOrg, ManufacturerEnterprise manufacturer) {
        String[][] products = {
            {"EC-001", "Electronic Components Set A", "15.00", "sets"},
            {"HA-001", "Home Appliance Kit A", "45.00", "units"},
            {"HA-002", "Home Appliance Kit B", "75.00", "units"},
            {"OS-001", "Office Supplies Pack", "8.50", "packs"},
            {"IT-001", "Industrial Tool Set", "120.00", "sets"}
        };
        
        String[] statuses = {"Pending", "Approved", "Processing", "Shipped", "Completed"};
        String[] paymentTerms = {"Net 15", "Net 30", "Net 45", "COD"};
        
        for (int i = 0; i < 10; i++) {
            String[] product = products[random.nextInt(products.length)];
            String status = statuses[random.nextInt(statuses.length)];
            
            WholesalePurchaseRequest request = new WholesalePurchaseRequest();
            request.setProductCode(product[0]);
            request.setProductName(product[1]);
            request.setUnitPrice(Double.parseDouble(product[2]));
            request.setUnit(product[3]);
            request.setQuantity(100 + random.nextInt(900));
            request.setDiscount(random.nextDouble() * 10);
            request.setPaymentTerms(paymentTerms[random.nextInt(paymentTerms.length)]);
            request.setMessage("Wholesale order: " + faker.commerce().promotionCode());
            request.setStatus(status);
            
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -random.nextInt(60));
            request.setRequestDate(cal.getTime());
            
            if (status.equals("Completed") || status.equals("Shipped")) {
                Calendar resolveCal = Calendar.getInstance();
                resolveCal.add(Calendar.DAY_OF_MONTH, -random.nextInt(30));
                request.setResolveDate(resolveCal.getTime());
            }
            
            distSalesOrg.getWorkQueue().addWorkRequest(request);
        }
    }
    
    private static void createPacificRetailOrders(Organization salesOrg) {
        String[] storeNames = {"TechZone Central", "HomeStyle Plus", "Fitness First Store",
            "Kitchen Essentials", "Garden World", "SmartLife Shop",
            "Outdoor Living Co.", "Wellness Hub", "Modern Home Depot"};
        
        String[][] products = {
            {"TG-001", "Smart Home Hub", "89.00", "units"},
            {"TG-002", "Wireless Security Camera Set", "149.00", "sets"},
            {"TG-003", "Smart Thermostat Pro", "75.00", "units"},
            {"OG-001", "Solar Garden Light Set", "45.00", "sets"},
            {"OG-002", "Patio Furniture Set - Premium", "599.00", "sets"},
            {"FW-001", "Home Gym Set - Basic", "299.00", "sets"},
            {"FW-002", "Treadmill - Smart", "799.00", "units"},
            {"KD-001", "Air Fryer - XL", "129.00", "units"},
            {"KD-002", "Coffee Machine - Espresso", "249.00", "units"}
        };
        
        String[] statuses = {"Pending", "Pending", "Pending", "Approved", "Approved", 
                            "Processing", "Shipped", "Delivered", "Completed", "Completed"};
        String[] urgencies = {"Normal", "Normal", "Urgent", "Critical"};
        String[] paymentMethods = {"Credit", "Invoice", "Wire Transfer", "Net 30"};
        
        for (int i = 0; i < 20; i++) {
            String[] product = products[random.nextInt(products.length)];
            String storeName = storeNames[random.nextInt(storeNames.length)];
            String status = statuses[random.nextInt(statuses.length)];
            
            RetailPurchaseOrderRequest order = new RetailPurchaseOrderRequest();
            order.setProductCode(product[0]);
            order.setProductName(product[1]);
            order.setUnitPrice(Double.parseDouble(product[2]));
            order.setUnit(product[3]);
            order.setQuantity(5 + random.nextInt(50));
            order.setStoreName(storeName);
            order.setStoreAddress(faker.address().fullAddress());
            order.setPaymentMethod(paymentMethods[random.nextInt(paymentMethods.length)]);
            order.setUrgencyLevel(urgencies[random.nextInt(urgencies.length)]);
            order.setMessage("Order #PAC-" + String.format("%04d", i + 1) + " - " + faker.company().buzzword());
            order.setStatus(status);
            
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -random.nextInt(45));
            order.setRequestDate(cal.getTime());
            
            if (status.equals("Completed") || status.equals("Delivered")) {
                Calendar resolveCal = Calendar.getInstance();
                resolveCal.add(Calendar.DAY_OF_MONTH, -random.nextInt(10));
                order.setResolveDate(resolveCal.getTime());
            }
            
            salesOrg.getWorkQueue().addWorkRequest(order);
        }
    }
    
    private static void createPacificShippingRequests(Organization inventoryOrg, UserAccount invUser) {
        String[][] products = {
            {"TG-001", "Smart Home Hub", "units"},
            {"TG-002", "Wireless Security Camera Set", "sets"},
            {"OG-001", "Solar Garden Light Set", "sets"},
            {"OG-003", "Electric Lawn Mower", "units"},
            {"FW-001", "Home Gym Set - Basic", "sets"},
            {"FW-003", "Exercise Bike - Pro", "units"},
            {"KD-001", "Air Fryer - XL", "units"},
            {"KD-003", "Cookware Set - 15 Piece", "sets"}
        };
        
        String[] carriers = {"Pacific Express", "WestCoast Freight", "Swift Logistics", "Prime Delivery Co."};
        
        String[][] statusConfigs = {
            {ProductShippingRequest.SHIP_STATUS_PENDING, "Pending"},
            {ProductShippingRequest.SHIP_STATUS_PENDING, "Pending"},
            {ProductShippingRequest.SHIP_STATUS_IN_TRANSIT, "In Transit"},
            {ProductShippingRequest.SHIP_STATUS_IN_TRANSIT, "In Transit"},
            {ProductShippingRequest.SHIP_STATUS_OUT_FOR_DELIVERY, "Out for Delivery"},
            {ProductShippingRequest.SHIP_STATUS_OUT_FOR_DELIVERY, "Out for Delivery"},
            {ProductShippingRequest.SHIP_STATUS_DELIVERED, "Delivered"},
            {ProductShippingRequest.SHIP_STATUS_DELIVERED, "Delivered"},
            {ProductShippingRequest.SHIP_STATUS_DELIVERED, "Received"},
            {ProductShippingRequest.SHIP_STATUS_DELIVERED, "Received"},
            {ProductShippingRequest.SHIP_STATUS_DELIVERED, "Received"}
        };
        
        for (int i = 0; i < 15; i++) {
            String[] product = products[random.nextInt(products.length)];
            String[] statusConfig = statusConfigs[i % statusConfigs.length];
            String carrier = carriers[random.nextInt(carriers.length)];
            
            ProductShippingRequest shipment = new ProductShippingRequest();
            shipment.setProductCode(product[0]);
            shipment.setProductName(product[1]);
            shipment.setUnit(product[2]);
            shipment.setQuantity(20 + random.nextInt(100));
            shipment.setOriginAddress("XYZ Manufacturing Inc., " + faker.address().streetAddress());
            shipment.setDestinationAddress("Pacific Wholesale Partners, 500 Harbor Blvd, San Francisco, CA");
            shipment.setDestinationStoreName("Pacific Wholesale Partners");
            shipment.setCarrierName(carrier);
            shipment.setTrackingNumber("PAC-SHIP-" + String.format("%04d", i + 1));
            shipment.setPackageWeight(20.0 + random.nextDouble() * 200);
            shipment.setShippingStatus(statusConfig[0]);
            shipment.setStatus(statusConfig[1]);
            shipment.setMessage("Shipment for Pacific Warehouse - " + faker.lorem().sentence());
            
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -random.nextInt(20));
            shipment.setRequestDate(cal.getTime());
            
            if (statusConfig[1].equals("Received")) {
                shipment.setReceiver(invUser);
                Calendar resolveCal = Calendar.getInstance();
                resolveCal.add(Calendar.DAY_OF_MONTH, -random.nextInt(5));
                shipment.setResolveDate(resolveCal.getTime());
            }
            
            if (statusConfig[0].equals(ProductShippingRequest.SHIP_STATUS_IN_TRANSIT)) {
                Calendar estCal = Calendar.getInstance();
                estCal.add(Calendar.DAY_OF_MONTH, 1 + random.nextInt(4));
                shipment.setEstimatedDeliveryDate(estCal.getTime());
            }
            
            inventoryOrg.getWorkQueue().addWorkRequest(shipment);
        }
    }
    
    private static void createPacificDeliveryConfirmations(Organization salesOrg) {
        String[] storeNames = {"TechZone Central", "HomeStyle Plus", "Fitness First Store",
                              "Kitchen Essentials", "Garden World"};
        String[] deliverers = {"Alex Chen", "Maria Rodriguez", "James Kim", "Linda Park"};
        String[] conditions = {
            DeliveryConfirmationRequest.CONDITION_GOOD,
            DeliveryConfirmationRequest.CONDITION_GOOD,
            DeliveryConfirmationRequest.CONDITION_GOOD,
            DeliveryConfirmationRequest.CONDITION_PARTIAL,
            DeliveryConfirmationRequest.CONDITION_DAMAGED
        };
        
        String[][] products = {
            {"TG-001", "Smart Home Hub", "units"},
            {"OG-001", "Solar Garden Light Set", "sets"},
            {"FW-001", "Home Gym Set - Basic", "sets"},
            {"KD-001", "Air Fryer - XL", "units"}
        };
        
        for (int i = 0; i < 10; i++) {
            String[] product = products[random.nextInt(products.length)];
            String storeName = storeNames[random.nextInt(storeNames.length)];
            String deliverer = deliverers[random.nextInt(deliverers.length)];
            String condition = conditions[random.nextInt(conditions.length)];
            int quantity = 10 + random.nextInt(40);
            
            ProductShippingRequest shipment = new ProductShippingRequest();
            shipment.setProductCode(product[0]);
            shipment.setProductName(product[1]);
            shipment.setQuantity(quantity);
            shipment.setUnit(product[2]);
            shipment.setDestinationStoreName(storeName);
            shipment.setDestinationAddress(faker.address().fullAddress());
            shipment.setCarrierName("Pacific Express");
            shipment.setTrackingNumber("PAC-DEL-" + faker.number().digits(8));
            shipment.setShippingStatus(ProductShippingRequest.SHIP_STATUS_DELIVERED);
            shipment.setStatus("Delivered");
            
            DeliveryConfirmationRequest confirmation = new DeliveryConfirmationRequest(shipment);
            confirmation.setDeliveredBy(deliverer);
            confirmation.setConditionOnArrival(condition);
            
            if (condition.equals(DeliveryConfirmationRequest.CONDITION_GOOD)) {
                confirmation.setDeliveryNotes("All items in perfect condition. " + faker.lorem().sentence());
            } else if (condition.equals(DeliveryConfirmationRequest.CONDITION_PARTIAL)) {
                int delivered = quantity - random.nextInt(8) - 2;
                confirmation.setQuantityDelivered(delivered);
                confirmation.setDeliveryNotes("Partial: " + delivered + "/" + quantity + " delivered.");
            } else {
                confirmation.setDeliveryNotes("Minor damage to packaging. Contents OK.");
            }
            
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -random.nextInt(14));
            confirmation.setDeliveryDate(cal.getTime());
            
            if (i < 5) {
                confirmation.setConfirmed(false);
                confirmation.setStatus("Awaiting Confirmation");
                confirmation.setMessage("Please confirm receipt of " + product[1]);
            } else {
                confirmation.confirmDelivery("Robert Chen");
                confirmation.setStatus("Completed");
                confirmation.setMessage("Delivery confirmed by Pacific team");
            }
            
            salesOrg.getWorkQueue().addWorkRequest(confirmation);
        }
    }
    
    private static void createPacificWholesaleRequests(Organization salesOrg) {
        String[][] products = {
            {"TG-001", "Smart Home Hub", "75.00", "units"},
            {"TG-002", "Wireless Security Camera Set", "125.00", "sets"},
            {"OG-002", "Patio Furniture Set - Premium", "499.00", "sets"},
            {"FW-002", "Treadmill - Smart", "650.00", "units"},
            {"KD-002", "Coffee Machine - Espresso", "199.00", "units"}
        };
        
        String[] statuses = {"Pending", "Approved", "Processing", "Shipped", "Completed"};
        String[] paymentTerms = {"Net 15", "Net 30", "Net 45", "COD"};
        
        for (int i = 0; i < 12; i++) {
            String[] product = products[random.nextInt(products.length)];
            String status = statuses[random.nextInt(statuses.length)];
            
            WholesalePurchaseRequest request = new WholesalePurchaseRequest();
            request.setProductCode(product[0]);
            request.setProductName(product[1]);
            request.setUnitPrice(Double.parseDouble(product[2]));
            request.setUnit(product[3]);
            request.setQuantity(50 + random.nextInt(200));
            request.setDiscount(2.0 + random.nextDouble() * 8);
            request.setPaymentTerms(paymentTerms[random.nextInt(paymentTerms.length)]);
            request.setMessage("Pacific WO-" + String.format("%04d", i + 1) + ": " + faker.commerce().promotionCode());
            request.setStatus(status);
            
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -random.nextInt(90));
            request.setRequestDate(cal.getTime());
            
            if (status.equals("Completed") || status.equals("Shipped")) {
                Calendar resolveCal = Calendar.getInstance();
                resolveCal.add(Calendar.DAY_OF_MONTH, -random.nextInt(45));
                request.setResolveDate(resolveCal.getTime());
            }
            
            salesOrg.getWorkQueue().addWorkRequest(request);
        }
    }

    private static void createManufacturerTestDataWithFaker(ManufacturerEnterprise manufacturer, 
            RawMaterialSupplierEnterprise rmSupplier, ShippingEnterprise shipping, RetailEnterprise retail) {
        Organization inventoryOrg = manufacturer.getOrganizationDirectory().getOrganizationList().get(1);
        UserAccount distInvUser1 = inventoryOrg.getUserAccountDirectory().getUserAccountList().get(1);
        createMaterialShippingRequests(inventoryOrg, distInvUser1);
    }
    
    private static void createMaterialShippingRequests(Organization manufactureInventoryOrg, UserAccount receiverUser) {
        String[][] materials = {
            {"RM-001", "Steel Rods", "kg"},
            {"RM-002", "Aluminum Sheets", "sheets"},
            {"RM-003", "Copper Wire Coils", "rolls"},
            {"RM-004", "Plastic Pellets", "bags"},
            {"RM-005", "Rubber Components", "boxes"},
            {"RM-006", "Glass Panels", "panels"},
            {"RM-007", "Paint Chemicals", "drums"}
        };

        String[] carriers = {"Global Freight Co.", "SkyLine Logistics", "RapidTransport", "EcoFreight Shipping"};

        String[][] statusConfigs = {
            {MaterialShippingRequest.SHIP_STATUS_PENDING, WorkRequest.STATUS_PENDING},
            {MaterialShippingRequest.SHIP_STATUS_PENDING, WorkRequest.STATUS_PENDING},
            {MaterialShippingRequest.SHIP_STATUS_PICKED_UP, WorkRequest.STATUS_IN_PROGRESS},
            {MaterialShippingRequest.SHIP_STATUS_IN_TRANSIT, WorkRequest.STATUS_IN_PROGRESS},
            {MaterialShippingRequest.SHIP_STATUS_IN_TRANSIT, WorkRequest.STATUS_IN_PROGRESS},
            {MaterialShippingRequest.SHIP_STATUS_DELIVERED, WorkRequest.STATUS_DELIVERED},
            {MaterialShippingRequest.SHIP_STATUS_DELIVERED, WorkRequest.STATUS_DELIVERED},
            {MaterialShippingRequest.SHIP_STATUS_DELIVERED, WorkRequest.STATUS_DELIVERED}
        };

        for (int i = 0; i < 15; i++) {
            String[] material = materials[random.nextInt(materials.length)];
            String[] statusConfig = statusConfigs[i % statusConfigs.length];
            String carrier = carriers[random.nextInt(carriers.length)];

            MaterialShippingRequest request = new MaterialShippingRequest();
            request.setMaterialCode(material[0]);
            request.setMaterialName(material[1]);
            request.setUnit(material[2]);
            request.setQuantity(100 + random.nextInt(900));
            request.setOriginAddress("Supplier Warehouse, " + faker.address().streetAddress());
            request.setDestinationAddress("Manufacturing Plant, " + faker.address().streetAddress());
            request.setCarrierName(carrier);
            request.setTrackingNumber("SHIP-" + String.format("%04d", i + 1));
            request.setShippingCost(500 + random.nextDouble() * 4500);
            request.setShippingStatus(statusConfig[0]);
            request.setStatus(statusConfig[1]);
            request.setMessage(faker.lorem().sentence());

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -random.nextInt(10));
            request.setRequestDate(cal.getTime());

            if (statusConfig[0].equals(MaterialShippingRequest.SHIP_STATUS_IN_TRANSIT)) {
                Calendar est = Calendar.getInstance();
                est.add(Calendar.DAY_OF_MONTH, 1 + random.nextInt(5));
                request.setEstimatedDeliveryDate(est.getTime());
            }

            if (statusConfig[0].equals(MaterialShippingRequest.SHIP_STATUS_DELIVERED)) {
                request.setReceiver(receiverUser);
                Calendar delivered = Calendar.getInstance();
                delivered.add(Calendar.DAY_OF_MONTH, -random.nextInt(3));
                request.setActualDeliveryDate(delivered.getTime());
            }

            manufactureInventoryOrg.getWorkQueue().addWorkRequest(request);
        }
    }
}








