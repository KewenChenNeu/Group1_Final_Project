/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Role.RawMaterialSupplier;

import Business.EcoSystem;
import Business.Enterprise.Enterprise;
import Business.Organization.Organization;
import Business.Organization.RawMaterialSupplier.RMProcurementOrganization;
import Business.Role.Role;
import Business.UserAccount.UserAccount;
import javax.swing.JPanel;
import ui.RMSupplierRole.RMProcurementRole.RMProcurementWorkAreaJPanel;

/**
 *
 * @author chris
 */
public class RMProcurementRole extends Role {

    @Override
    public JPanel createWorkArea(JPanel userProcessContainer, UserAccount account,
            Organization organization, Enterprise enterprise, EcoSystem system) {
        // TODO: Return RMProcurementWorkAreaJPanel once UI is created
        return new RMProcurementWorkAreaJPanel(userProcessContainer, account, (RMProcurementOrganization)organization, enterprise, system);
    }

    @Override
    public String toString() {
        return "RM Procurement";
    }
}

