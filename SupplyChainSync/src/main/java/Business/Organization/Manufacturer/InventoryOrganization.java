/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Organization.Manufacturer;

import Business.Organization.Organization;
import Business.Role.Role;
import Business.Role.Manufacturer.InventoryManagerRole;
import java.util.ArrayList;

/**
 *
 * @author chris
 */
public class InventoryOrganization extends Organization {

    public InventoryOrganization() {
        super(Type.Inventory.getValue());
    }

    @Override
    public ArrayList<Role> getSupportedRole() {
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(new InventoryManagerRole());
        return roles;
    }
}