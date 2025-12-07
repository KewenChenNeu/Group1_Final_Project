/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Organization.RawMaterialSupplier;

import Business.Material.Material;
import Business.Organization.Organization;
import Business.Role.Role;
import Business.Role.RawMaterialSupplier.RMInventoryManagerRole;
import java.util.ArrayList;

/**
 *
 * @author chris
 */
public class RMInventoryOrganization extends Organization {
    
    private ArrayList<Material> inventoryList;

    public RMInventoryOrganization() {
        super(Type.RMInventory.getValue());
        this.inventoryList = new ArrayList<>();
    }
    
    @Override
    public ArrayList<Role> getSupportedRole() {
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(new RMInventoryManagerRole());
        return roles;
    }
    
    public ArrayList<Material> getInventoryList() {
        if (inventoryList == null) {
            inventoryList = new ArrayList<>();
        }
        return inventoryList;
    }

    public Material findMaterialByName(String name) {
        if (inventoryList == null) {
            return null;
        }
        for (Material m : inventoryList) {
            if (m.getMaterialName() != null &&
                m.getMaterialName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }

    public void initializeSampleData() {
        getInventoryList(); 

        Material steel = new Material("RM-001", "Steel Sheets", 10.0, "kg");
        steel.setQuantity(1000);
        inventoryList.add(steel);

        Material copper = new Material("RM-002", "Copper Wire", 15.0, "kg");
        copper.setQuantity(500);
        inventoryList.add(copper);
    }
}
