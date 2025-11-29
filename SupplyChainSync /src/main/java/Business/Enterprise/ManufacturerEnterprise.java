/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Enterprise;

import Business.Organization.Manufacturer.ProductionManagementOrganization;
import Business.Organization.Manufacturer.InventoryOrganization;
import Business.Role.Role;
import Business.Role.Manufacturer.ProductionManagerRole;
import Business.Role.Manufacturer.InventoryManagerRole;
import java.util.ArrayList;

/**
 *
 * @author zhaojinkun
 */
public class ManufacturerEnterprise extends Enterprise {

    public ManufacturerEnterprise(String name) {
        super(name, EnterpriseType.Manufacturer);
        initializeOrganizations();
    }

    @Override
    public void initializeOrganizations() {
        // Create and add Production Management Organization
        ProductionManagementOrganization productionOrg = new ProductionManagementOrganization();
        getOrganizationDirectory().addOrganization(productionOrg);
        
        // Create and add Inventory Organization
        InventoryOrganization inventoryOrg = new InventoryOrganization();
        getOrganizationDirectory().addOrganization(inventoryOrg);
    }

    @Override
    public ArrayList<Role> getSupportedRole() {
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(new ProductionManagerRole());
        roles.add(new InventoryManagerRole());
        return roles;
    }
}
