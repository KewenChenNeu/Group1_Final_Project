/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Role.Manufacturer;

import Business.EcoSystem;
import Business.Enterprise.Enterprise;
import Business.Organization.Manufacturer.ProductionManagementOrganization;
import Business.Organization.Organization;
import Business.Role.Role;
import Business.UserAccount.UserAccount;
import javax.swing.JPanel;
import ui.ManufacturerRole.ProductionManagerRole.ProductionManagerWorkAreaJPanel;

/**
 *
 * @author chris
 */
public class ProductionManagerRole extends Role {

    @Override
    public JPanel createWorkArea(JPanel userProcessContainer, UserAccount account,
            Organization organization, Enterprise enterprise, EcoSystem system) {
        // TODO: Return ProductionManagerWorkAreaJPanel once UI is created
        return new ProductionManagerWorkAreaJPanel(userProcessContainer, account, (ProductionManagementOrganization)organization, enterprise, system);
    }

    @Override
    public String toString() {
        return "Production Manager";
    }
}
