/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Role.Retail;

import Business.EcoSystem;
import Business.Enterprise.Enterprise;
import Business.Organization.Organization;
import Business.Organization.Retail.StoreManagementOrganization;
import Business.Role.Role;
import Business.UserAccount.UserAccount;
import javax.swing.JPanel;
import ui.RetailRole.StoreManagerRole.StoreManagerWorkAreaJPanel;

/**
 *
 * @author chris
 */
public class StoreManagerRole extends Role {

    @Override
    public JPanel createWorkArea(JPanel userProcessContainer, UserAccount account,
            Organization organization, Enterprise enterprise, EcoSystem system) {
        // TODO: Return StoreManagerWorkAreaJPanel once UI is created
        return new StoreManagerWorkAreaJPanel(userProcessContainer, account, (StoreManagementOrganization)organization, enterprise, system);
    }

    @Override
    public String toString() {
        return "Store Manager";
    }
}
