/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Role.Manufacturer;

import Business.EcoSystem;
import Business.Enterprise.Enterprise;
import Business.Organization.Manufacturer.InventoryOrganization;
import Business.Organization.Organization;
import Business.Role.Role;
import Business.UserAccount.UserAccount;
import javax.swing.JPanel;
import ui.ManufacturerRole.InventoryManagerRole.InventoryManagerWorkAreaJPanel;

/**
 *
 * @author chris
 */
public class InventoryManagerRole extends Role {

    @Override
    public JPanel createWorkArea(JPanel userProcessContainer, UserAccount account,
            Organization organization, Enterprise enterprise, EcoSystem system) {
        // TODO: Return InventoryManagerWorkAreaJPanel once UI is created
        return new InventoryManagerWorkAreaJPanel(userProcessContainer, account, (InventoryOrganization)organization, enterprise, system);
    }

    @Override
    public String toString() {
        return "Inventory Manager";
    }
}
