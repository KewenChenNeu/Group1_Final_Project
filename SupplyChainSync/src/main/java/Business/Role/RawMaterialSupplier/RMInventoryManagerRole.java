/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Role.RawMaterialSupplier;

import Business.EcoSystem;
import Business.Enterprise.Enterprise;
import Business.Enterprise.Enterprise;
import Business.Organization.Organization;
import Business.Organization.RawMaterialSupplier.RMInventoryOrganization;
import Business.Role.Role;
import Business.UserAccount.UserAccount;
import javax.swing.JPanel;
import ui.RMSupplierRole.RMInventoryManagerRole.RMInventoryManagerWorkAreaJPanel;

/**
 *
 * @author chris
 */
public class RMInventoryManagerRole extends Role {

    @Override
    public JPanel createWorkArea(JPanel userProcessContainer, UserAccount account,
            Organization organization, Enterprise enterprise, EcoSystem system) {
        // TODO: Return RMInventoryManagerWorkAreaJPanel once UI is created
        return new RMInventoryManagerWorkAreaJPanel(userProcessContainer, account, (RMInventoryOrganization)organization, enterprise, system);
    }

    @Override
    public String toString() {
        return "RM Inventory Manager";
    }
}
