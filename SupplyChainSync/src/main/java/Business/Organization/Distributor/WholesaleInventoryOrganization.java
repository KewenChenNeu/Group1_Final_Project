/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Organization.Distributor;

import Business.Inventory.Inventory;
import Business.Organization.Organization;
import Business.Role.Role;
import Business.Role.Distributor.WholesaleInventoryRole;
import java.util.ArrayList;

/**
 *
 * @author chris
 */
public class WholesaleInventoryOrganization extends Organization {

    private Inventory inventory;

    public WholesaleInventoryOrganization() {
        super(Type.WholesaleInventory.getValue());
        this.inventory = new Inventory();
    }
    
    public Inventory getInventory() {
        return inventory;
    }
    
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public ArrayList<Role> getSupportedRole() {
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(new WholesaleInventoryRole());
        return roles;
    }
}
