/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Enterprise;

import Business.Organization.RawMaterialSupplier.RMProcurementOrganization;
import Business.Organization.RawMaterialSupplier.RMInventoryOrganization;
import Business.Role.Role;
import Business.Role.RawMaterialSupplier.RMProcurementRole;
import Business.Role.RawMaterialSupplier.RMInventoryManagerRole;
import java.util.ArrayList;

/**
 *
 * @author zhaojinkun
 */
public class RawMaterialSupplierEnterprise extends Enterprise {

    public RawMaterialSupplierEnterprise(String name) {
        super(name, EnterpriseType.RawMaterialSupplier);
        initializeOrganizations();
    }

    @Override
    public void initializeOrganizations() {
        // Create and add RM Procurement Organization
        RMProcurementOrganization rmProcurementOrg = new RMProcurementOrganization();
        getOrganizationDirectory().addOrganization(rmProcurementOrg);
        
        // Create and add RM Inventory Organization
        RMInventoryOrganization rmInventoryOrg = new RMInventoryOrganization();
        getOrganizationDirectory().addOrganization(rmInventoryOrg);
    }

    @Override
    public ArrayList<Role> getSupportedRole() {
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(new RMProcurementRole());
        roles.add(new RMInventoryManagerRole());
        return roles;
    }
}
