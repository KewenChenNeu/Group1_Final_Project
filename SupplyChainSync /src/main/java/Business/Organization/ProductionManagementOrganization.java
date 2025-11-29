/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Organization;

import Business.Role.Role;
import Business.Role.ProductionManagerRole;
import java.util.ArrayList;

/**
 *
 * @author chris
 */
public class ProductionManagementOrganization extends Organization {

    public ProductionManagementOrganization() {
        super(Type.ProductionManagement.getValue());
    }

    @Override
    public ArrayList<Role> getSupportedRole() {
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(new ProductionManagerRole());
        return roles;
    }
}

