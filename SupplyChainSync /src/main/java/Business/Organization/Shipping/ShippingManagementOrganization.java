/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Organization.Shipping;

import Business.Organization.Organization;
import Business.Role.Role;
import Business.Role.Shipping.ShippingManagerRole;
import java.util.ArrayList;

/**
 *
 * @author chris
 */
public class ShippingManagementOrganization extends Organization {

    public ShippingManagementOrganization() {
        super(Type.ShippingManagement.getValue());
    }

    @Override
    public ArrayList<Role> getSupportedRole() {
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(new ShippingManagerRole());
        return roles;
    }
}
