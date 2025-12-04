/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Role.Distributor;

import Business.EcoSystem;
import Business.Enterprise.Enterprise;
import Business.Organization.Distributor.WholesaleInventoryOrganization;
import Business.Organization.Organization;
import Business.Role.Role;
import Business.UserAccount.UserAccount;
import javax.swing.JPanel;
import ui.DistributorRole.WholesaleInventoryRole.WholesaleInventoryWorkAreaJPanel;

/**
 *
 * @author chris
 */
public class WholesaleInventoryRole extends Role {

    @Override
    public JPanel createWorkArea(JPanel userProcessContainer, UserAccount account,
            Organization organization, Enterprise enterprise, EcoSystem system) {
        // TODO: Return WholesaleInventoryWorkAreaJPanel once UI is created
        return new WholesaleInventoryWorkAreaJPanel(userProcessContainer, account, (WholesaleInventoryOrganization)organization, enterprise, system);
    }

    @Override
    public String toString() {
        return "Wholesale Inventory";
    }
}

