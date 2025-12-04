/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Enterprise;

import Business.Organization.Distributor.WholesaleSalesOrganization;
import Business.Organization.Distributor.WholesaleInventoryOrganization;
import Business.Role.Role;
import Business.Role.Distributor.WholesaleSalesRole;
import Business.Role.Distributor.WholesaleInventoryRole;
import Business.Role.Distributor.WholesaleAnalyticsRole;
import java.util.ArrayList;

/**
 *
 * @author chris
 */
public class ProductDistributorEnterprise extends Enterprise {

    public ProductDistributorEnterprise(String name) {
        super(name, EnterpriseType.ProductDistributor);
        initializeOrganizations();
    }

    @Override
    public void initializeOrganizations() {
        // Create and add Wholesale Sales Organization
        WholesaleSalesOrganization salesOrg = new WholesaleSalesOrganization();
        getOrganizationDirectory().addOrganization(salesOrg);
        
        // Create and add Wholesale Inventory Organization
        WholesaleInventoryOrganization inventoryOrg = new WholesaleInventoryOrganization();
        getOrganizationDirectory().addOrganization(inventoryOrg);
    }

    @Override
    public ArrayList<Role> getSupportedRole() {
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(new WholesaleSalesRole());
        roles.add(new WholesaleInventoryRole());
        roles.add(new WholesaleAnalyticsRole());
        return roles;
    }
}
