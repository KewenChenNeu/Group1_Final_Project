/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Organization.Retail;

import Business.Organization.Organization;
import Business.Role.Role;
import Business.Role.Retail.RetailAnalyticsRole;
import java.util.ArrayList;

/**
 *
 * @author chris
 */
public class RetailInventoryOrganization extends Organization {

    public RetailInventoryOrganization() {
        super(Type.RetailInventory.getValue());
    }

    @Override
    public ArrayList<Role> getSupportedRole() {
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(new RetailAnalyticsRole());
        return roles;
    }
}

