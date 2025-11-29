/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Role.Distributor;

import Business.EcoSystem;
import Business.Enterprise.Enterprise;
import Business.Organization.Distributor.WholesaleSalesOrganization;
import Business.Organization.Organization;
import Business.Role.Role;
import Business.UserAccount.UserAccount;
import javax.swing.JPanel;
import ui.DistributorRole.WholesaleAnalyticsRole.WholesaleAnalyticsWorkAreaJPanel;

/**
 *
 * @author chris
 */
public class WholesaleAnalyticsRole extends Role {

    @Override
    public JPanel createWorkArea(JPanel userProcessContainer, UserAccount account,
            Organization organization, Enterprise enterprise, EcoSystem system) {
        // TODO: Return WholesaleAnalyticsWorkAreaJPanel once UI is created
        return new WholesaleAnalyticsWorkAreaJPanel(userProcessContainer, account, (WholesaleSalesOrganization)organization, enterprise, system);
    }

    @Override
    public String toString() {
        return "Wholesale Analytics";
    }
}
