/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Role.Retail;

import Business.EcoSystem;
import Business.Enterprise.Enterprise;
import Business.Organization.Organization;
import Business.Organization.Retail.RetailInventoryOrganization;
import Business.Role.Role;
import Business.UserAccount.UserAccount;
import javax.swing.JPanel;
import ui.RetailRole.OrderClerkRole.OrderClerkWorkAreaJPanel;

/**
 *
 * @author chris
 */
public class OrderClerkRole extends Role {

    @Override
    public JPanel createWorkArea(JPanel userProcessContainer, UserAccount account,
            Organization organization, Enterprise enterprise, EcoSystem system) {
        // TODO: Return OrderClerkWorkAreaJPanel once UI is created
        return new OrderClerkWorkAreaJPanel(userProcessContainer, account, (RetailInventoryOrganization)organization, enterprise, system);
    }

    @Override
    public String toString() {
        return "Order Clerk";
    }
}
