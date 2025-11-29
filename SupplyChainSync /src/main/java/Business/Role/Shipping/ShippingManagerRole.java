/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Role.Shipping;

import Business.EcoSystem;
import Business.Enterprise.Enterprise;
import Business.Organization.Organization;
import Business.Organization.Shipping.ShippingManagementOrganization;
import Business.Role.Role;
import Business.UserAccount.UserAccount;
import javax.swing.JPanel;
import ui.ShippingRole.DeliveryStaffRole.ShippingManagerWorkAreaJPanel;

/**
 *
 * @author chris
 */
public class ShippingManagerRole extends Role {

    @Override
    public JPanel createWorkArea(JPanel userProcessContainer, UserAccount account,
            Organization organization, Enterprise enterprise, EcoSystem system) {
        // TODO: Return ShippingManagerWorkAreaJPanel once UI is created
        return new ShippingManagerWorkAreaJPanel(userProcessContainer, account, (ShippingManagementOrganization)organization, enterprise, system);
    }

    @Override
    public String toString() {
        return "Shipping Manager";
    }
}

