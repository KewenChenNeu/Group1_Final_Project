/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Enterprise;

import Business.Organization.Shipping.ShippingManagementOrganization;
import Business.Organization.Shipping.ShippingOperationOrganization;
import Business.Role.Role;
import Business.Role.Shipping.ShippingManagerRole;
import Business.Role.Shipping.DeliveryStaffRole;
import java.util.ArrayList;

/**
 *
 * @author zhaojinkun
 */
public class ShippingEnterprise extends Enterprise {

    public ShippingEnterprise(String name) {
        super(name, EnterpriseType.Shipping);
        initializeOrganizations();
    }

    @Override
    public void initializeOrganizations() {
        // Create and add Shipping Management Organization
        ShippingManagementOrganization managementOrg = new ShippingManagementOrganization();
        getOrganizationDirectory().addOrganization(managementOrg);
        
        // Create and add Shipping Operation Organization
        ShippingOperationOrganization operationOrg = new ShippingOperationOrganization();
        getOrganizationDirectory().addOrganization(operationOrg);
    }

    @Override
    public ArrayList<Role> getSupportedRole() {
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(new ShippingManagerRole());
        roles.add(new DeliveryStaffRole());
        return roles;
    }
}

