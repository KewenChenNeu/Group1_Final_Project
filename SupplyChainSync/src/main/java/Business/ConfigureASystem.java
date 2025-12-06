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
    
    // Java Faker instance for generating realistic test data
    private static final Faker faker = new Faker();
    private static final Random random = new Random();
    
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
        // 5. Create Sample WorkRequests (Original)
        // ============================
        createSampleWorkRequests(rmSupplier, manufacturer, distributor, shipping, retail);
        
        // ============================
        // 6. Create Enhanced Distributor Test Data with Faker
        // ============================
        createDistributorTestDataWithFaker(distributor, manufacturer, shipping, retail);
        
        return system;
    }
    
    /**
     * Configure Raw Material Supplier Enterprise
     */
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
    
    /**
     * Configure Manufacturer Enterprise
     */
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
    }
    
    /**
     * Configure Product Distributor Enterprise
     */
    private static void configureDistributorEnterprise(ProductDistributorEnterprise enterprise) {
        Organization salesOrg = enterprise.getOrganizationDirectory().getOrganizationList().get(0);
        Organization inventoryOrg = enterprise.getOrganizationDirectory().getOrganizationList().get(1);
        
        // Wholesale Sales Organization - multiple employees
        Employee salesMgr = salesOrg.getEmployeeDirectory().createEmployee("James Taylor");
        salesOrg.getUserAccountDirectory().createUserAccount("dist_sales1", "password", salesMgr, new WholesaleSalesRole());
        
        Employee salesRep1 = salesOrg.getEmployeeDirectory().createEmployee(faker.name().fullName());
        salesOrg.getUserAccountDirectory().createUserAccount("dist_sales2", "password", salesRep1, new WholesaleSalesRole());
        
        Employee salesAnalyst = salesOrg.getEmployeeDirectory().createEmployee("Jennifer Martinez");
        salesOrg.getUserAccountDirectory().createUserAccount("dist_analyst1", "password", salesAnalyst, new WholesaleAnalyticsRole());
        
        Employee salesAnalyst2 = salesOrg.getEmployeeDirectory().createEmployee(faker.name().fullName());
        salesOrg.getUserAccountDirectory().createUserAccount("dist_analyst2", "password", salesAnalyst2, new WholesaleAnalyticsRole());
        
        // Wholesale Inventory Organization - multiple employees
        Employee whInvMgr = inventoryOrg.getEmployeeDirectory().createEmployee("Michael Thomas");
        inventoryOrg.getUserAccountDirectory().createUserAccount("dist_inv1", "password", whInvMgr, new WholesaleInventoryRole());
        
        Employee whInvStaff = inventoryOrg.getEmployeeDirectory().createEmployee(faker.name().fullName());
        inventoryOrg.getUserAccountDirectory().createUserAccount("dist_inv2", "password", whInvStaff, new WholesaleInventoryRole());
        
        Employee whInvStaff2 = inventoryOrg.getEmployeeDirectory().createEmployee(faker.name().fullName());
        inventoryOrg.getUserAccountDirectory().createUserAccount("dist_inv3", "password", whInvStaff2, new WholesaleInventoryRole());
    }
    
    /**
     * Configure Shipping Enterprise
     */
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
    
    /**
     * Configure Retail Enterprise
     */
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
        
        // ============================
        // Manufacturer - Products Catalog & Inventory
        // ============================
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
        
        // ============================
        // Distributor - Products Catalog & Inventory (Enhanced)
        // ============================
        setupDistributorCatalogAndInventory(distributor);
        
        // ============================
        // Retail - Products Catalog & Inventory
        // ============================
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
    
    /**
     * Setup enhanced Distributor catalog and inventory
     */
    private static void setupDistributorCatalogAndInventory(ProductDistributorEnterprise distributor) {
        ProductCatalog catalog = distributor.getProductCatalog();
        Inventory inventory = distributor.getInventory();
        
        // Products IN inventory (available for sale)
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
        
        // Products with LOW STOCK (for alerts testing)
        Product p6 = catalog.createProduct("EC-002", "Electronic Components Set B", 22.00, "sets");
        p6.setCategory("Electronics");
        p6.setDescription("Advanced electronic components set");
        inventory.addProduct(p6, 45, 50, 500, "Distribution Center A"); // Below minStock=50
        
        // Products with OUT OF STOCK (for alerts testing)
        Product p7 = catalog.createProduct("EC-003", "Electronic Starter Kit", 12.00, "sets");
        p7.setCategory("Electronics");
        p7.setDescription("Basic electronic starter kit");
        InventoryItem outOfStockItem = inventory.addProduct(p7, 30, 20, 300, "Distribution Center A");
        outOfStockItem.setReservedQuantity(30); // All reserved = 0 available
        
        // Products with HIGH STOCK (healthy)
        Product p8 = catalog.createProduct("HA-003", "Home Appliance Kit C - Deluxe", 120.00, "units");
        p8.setCategory("Appliances");
        p8.setDescription("Deluxe home appliance kit with warranty");
        inventory.addProduct(p8, 600, 50, 800, "Distribution Center B");
        
        // Additional products in catalog (NOT in inventory - for adding)
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
    
    /**
     * Create sample WorkRequests (Original basic setup)
     */
    private static void createSampleWorkRequests(
            RawMaterialSupplierEnterprise rmSupplier,
            ManufacturerEnterprise manufacturer,
            ProductDistributorEnterprise distributor,
            ShippingEnterprise shipping,
            RetailEnterprise retail) {
        
        Organization rmProcurementOrg = rmSupplier.getOrganizationDirectory().getOrganizationList().get(0);
        Organization mfgProductionOrg = manufacturer.getOrganizationDirectory().getOrganizationList().get(0);
        Organization distSalesOrg = distributor.getOrganizationDirectory().getOrganizationList().get(0);
        Organization shipMgmtOrg = shipping.getOrganizationDirectory().getOrganizationList().get(0);
        
        // Sample RM Restock Request
        RawMaterialRestockRequest rmRequest1 = new RawMaterialRestockRequest();
        rmRequest1.setMaterialName("Steel Sheets");
        rmRequest1.setQuantity(500);
        rmRequest1.setUnit("kg");
        rmRequest1.setUnitPrice(2.50);
        rmRequest1.setUrgencyLevel("Normal");
        rmRequest1.setMessage("Monthly restock for production line A");
        rmRequest1.setStatus("Pending");
        rmProcurementOrg.getWorkQueue().addWorkRequest(rmRequest1);
        
        // Sample Wholesale Purchase Request
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
    
    /**
     * ============================================================
     * ENHANCED TEST DATA GENERATION WITH JAVA FAKER
     * For comprehensive Distributor Enterprise testing
     * ============================================================
     */
    private static void createDistributorTestDataWithFaker(
            ProductDistributorEnterprise distributor,
            ManufacturerEnterprise manufacturer,
            ShippingEnterprise shipping,
            RetailEnterprise retail) {
        
        Organization distSalesOrg = distributor.getOrganizationDirectory().getOrganizationList().get(0);
        Organization distInventoryOrg = distributor.getOrganizationDirectory().getOrganizationList().get(1);
        
        UserAccount distSalesUser = distSalesOrg.getUserAccountDirectory().getUserAccountList().get(0);
        UserAccount distInvUser = distInventoryOrg.getUserAccountDirectory().getUserAccountList().get(0);
        
        // ============================================================
        // 1. RETAIL PURCHASE ORDERS (for Wholesale Sales Role)
        // ============================================================
        createRetailPurchaseOrders(distSalesOrg, retail);
        
        // ============================================================
        // 2. PRODUCT SHIPPING REQUESTS (for Wholesale Inventory Role)
        // ============================================================
        createProductShippingRequests(distInventoryOrg, distInvUser);
        
        // ============================================================
        // 3. DELIVERY CONFIRMATIONS (for Wholesale Sales Role)
        // ============================================================
        createDeliveryConfirmations(distSalesOrg);
        
        // ============================================================
        // 4. WHOLESALE PURCHASE REQUESTS (for Analytics)
        // ============================================================
        createWholesalePurchaseRequests(distSalesOrg, manufacturer);
    }
    
    /**
     * Create varied Retail Purchase Orders for testing Sales functionality
     */
    private static void createRetailPurchaseOrders(Organization distSalesOrg, RetailEnterprise retail) {
        Organization retailStoreOrg = retail.getOrganizationDirectory().getOrganizationList().get(0);
        UserAccount retailUser = retailStoreOrg.getUserAccountDirectory().getUserAccountList().get(0);
        
        String[] storeNames = {
            "MegaMart Downtown", "MegaMart Suburbs", "MegaMart Harbor", 
            "MegaMart Plaza", "MegaMart Central", "ValueMart Express",
            "SuperStore North", "SuperStore South", "QuickMart East"
        };
        
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
        
        // Generate 15 varied orders
        for (int i = 0; i < 15; i++) {
            String[] product = products[random.nextInt(products.length)];
            String storeName = storeNames[random.nextInt(storeNames.length)];
            String status = statuses[random.nextInt(statuses.length)];
            
            RetailPurchaseOrderRequest order = new RetailPurchaseOrderRequest();
            order.setProductCode(product[0]);
            order.setProductName(product[1]);
            order.setUnitPrice(Double.parseDouble(product[2]));
            order.setUnit(product[3]);
            order.setQuantity(10 + random.nextInt(200)); // 10-210 units
            order.setStoreName(storeName);
            order.setStoreAddress(faker.address().fullAddress());
            order.setSender(retailUser);
            order.setPaymentMethod(paymentMethods[random.nextInt(paymentMethods.length)]);
            order.setUrgencyLevel(urgencies[random.nextInt(urgencies.length)]);
            order.setMessage(faker.commerce().promotionCode() + " - " + faker.company().buzzword());
            order.setStatus(status);
            
            // Set dates based on status
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -random.nextInt(30)); // Up to 30 days ago
            order.setRequestDate(cal.getTime());
            
            if (status.equals("Completed") || status.equals("Delivered")) {
                Calendar resolveCal = Calendar.getInstance();
                resolveCal.add(Calendar.DAY_OF_MONTH, -random.nextInt(7)); // Within 7 days
                order.setResolveDate(resolveCal.getTime());
            }
            
            distSalesOrg.getWorkQueue().addWorkRequest(order);
        }
    }
    
    /**
     * Create varied Product Shipping Requests for Inventory Role testing
     */
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
        
        // Various statuses for testing different views
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
            shipment.setQuantity(50 + random.nextInt(250)); // 50-300 units
            shipment.setOriginAddress("XYZ Manufacturing Inc., " + faker.address().streetAddress() + ", Worcester, MA");
            shipment.setDestinationAddress("Global Distribution LLC, 789 Warehouse Blvd, Worcester, MA");
            shipment.setDestinationStoreName("Global Distribution LLC");
            shipment.setCarrierName(carrier);
            shipment.setTrackingNumber("MFG-DIST-" + String.format("%03d", i + 10));
            shipment.setPackageWeight(50.0 + random.nextDouble() * 450); // 50-500 lbs
            shipment.setShippingStatus(statusConfig[0]);
            shipment.setStatus(statusConfig[1]);
            shipment.setMessage(faker.lorem().sentence());
            
            // Set dates
            Calendar cal = Calendar.getInstance();
            int daysAgo = random.nextInt(14);
            cal.add(Calendar.DAY_OF_MONTH, -daysAgo);
            shipment.setRequestDate(cal.getTime());
            
            // If received, set receiver and resolve date
            if (statusConfig[1].equals("Received")) {
                shipment.setReceiver(distInvUser);
                Calendar resolveCal = Calendar.getInstance();
                resolveCal.add(Calendar.DAY_OF_MONTH, -random.nextInt(3));
                shipment.setResolveDate(resolveCal.getTime());
            }
            
            // Set estimated delivery for in-transit
            if (statusConfig[0].equals(ProductShippingRequest.SHIP_STATUS_IN_TRANSIT)) {
                Calendar estCal = Calendar.getInstance();
                estCal.add(Calendar.DAY_OF_MONTH, 1 + random.nextInt(5));
                shipment.setEstimatedDeliveryDate(estCal.getTime());
            }
            
            distInventoryOrg.getWorkQueue().addWorkRequest(shipment);
        }
    }
    
    /**
     * Create varied Delivery Confirmations for Sales Role testing
     */
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
        
        // Create 8 delivery confirmations with varied statuses
        for (int i = 0; i < 8; i++) {
            String[] product = products[random.nextInt(products.length)];
            String storeName = storeNames[random.nextInt(storeNames.length)];
            String deliverer = deliverers[random.nextInt(deliverers.length)];
            String condition = conditions[random.nextInt(conditions.length)];
            int quantity = 20 + random.nextInt(100);
            
            // Create underlying shipment
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
            
            // Set delivery notes based on condition
            if (condition.equals(DeliveryConfirmationRequest.CONDITION_GOOD)) {
                confirmation.setDeliveryNotes("All items received in good condition. " + faker.lorem().sentence());
            } else if (condition.equals(DeliveryConfirmationRequest.CONDITION_PARTIAL)) {
                int delivered = quantity - random.nextInt(10) - 5;
                confirmation.setQuantityDelivered(delivered);
                confirmation.setDeliveryNotes("Partial delivery: " + delivered + " of " + quantity + " units. Remaining backordered.");
            } else {
                confirmation.setDeliveryNotes("Some items damaged during transit. " + faker.lorem().sentence());
            }
            
            // Set delivery date
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -random.nextInt(10));
            confirmation.setDeliveryDate(cal.getTime());
            
            // Half confirmed, half awaiting
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
    
    /**
     * Create Wholesale Purchase Requests for Analytics testing
     */
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
        
        // Generate historical purchase orders for analytics
        for (int i = 0; i < 10; i++) {
            String[] product = products[random.nextInt(products.length)];
            String status = statuses[random.nextInt(statuses.length)];
            
            WholesalePurchaseRequest request = new WholesalePurchaseRequest();
            request.setProductCode(product[0]);
            request.setProductName(product[1]);
            request.setUnitPrice(Double.parseDouble(product[2]));
            request.setUnit(product[3]);
            request.setQuantity(100 + random.nextInt(900)); // 100-1000 units
            request.setDiscount(random.nextDouble() * 10); // 0-10% discount
            request.setPaymentTerms(paymentTerms[random.nextInt(paymentTerms.length)]);
            request.setMessage("Wholesale order: " + faker.commerce().promotionCode());
            request.setStatus(status);
            
            // Set dates
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -random.nextInt(60)); // Up to 60 days ago
            request.setRequestDate(cal.getTime());
            
            if (status.equals("Completed") || status.equals("Shipped")) {
                Calendar resolveCal = Calendar.getInstance();
                resolveCal.add(Calendar.DAY_OF_MONTH, -random.nextInt(30));
                request.setResolveDate(resolveCal.getTime());
            }
            
            distSalesOrg.getWorkQueue().addWorkRequest(request);
        }
    }
}