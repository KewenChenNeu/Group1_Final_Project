/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Role.Shipping;

import Business.EcoSystem;
import Business.Enterprise.Enterprise;
import Business.Organization.Organization;
import Business.Organization.Shipping.ShippingOperationOrganization;
import Business.Role.Role;
import Business.UserAccount.UserAccount;
import javax.swing.JPanel;
import ui.ShippingRole.ShippingManagerRole.DeliveryStaffWorkAreaJPanel;

/**
 *
 * @author chris
 */
public class DeliveryStaffRole extends Role {
    
    @Override
    public JPanel createWorkArea(JPanel userProcessContainer, UserAccount account,
            Organization organization, Enterprise enterprise, EcoSystem system) {
        // TODO: Return DeliveryStaffWorkAreaJPanel once UI is created
        return new DeliveryStaffWorkAreaJPanel(userProcessContainer, account, (ShippingOperationOrganization)organization, enterprise, system);
    }

    @Override
    public String toString() {
        return "Delivery Staff";
    }
}

