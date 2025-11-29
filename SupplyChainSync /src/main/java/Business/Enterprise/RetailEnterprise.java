/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Enterprise;

import Business.Organization.Retail.StoreManagementOrganization;
import Business.Organization.Retail.RetailInventoryOrganization;
import Business.Role.Role;
import Business.Role.Retail.StoreManagerRole;
import Business.Role.Retail.OrderClerkRole;
import Business.Role.Retail.RetailAnalyticsRole;
import java.util.ArrayList;

/**
 *
 * @author zhaojinkun
 */
public class RetailEnterprise extends Enterprise {

    public RetailEnterprise(String name) {
        super(name, EnterpriseType.Retail);
        initializeOrganizations();
    }

    @Override
    public void initializeOrganizations() {
        // Create and add Store Management Organization
        StoreManagementOrganization storeOrg = new StoreManagementOrganization();
        getOrganizationDirectory().addOrganization(storeOrg);
        
        // Create and add Retail Inventory Organization
        RetailInventoryOrganization inventoryOrg = new RetailInventoryOrganization();
        getOrganizationDirectory().addOrganization(inventoryOrg);
    }

    @Override
    public ArrayList<Role> getSupportedRole() {
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(new StoreManagerRole());
        roles.add(new OrderClerkRole());
        roles.add(new RetailAnalyticsRole());
        return roles;
    }
}
