/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Organization;

import Business.Role.Role;
import Business.Role.WholesaleSalesRole;
import Business.Role.WholesaleAnalyticsRole;
import java.util.ArrayList;

/**
 *
 * @author chris
 */
public class WholesaleSalesOrganization extends Organization {

    public WholesaleSalesOrganization() {
        super(Type.WholesaleSales.getValue());
    }

    @Override
    public ArrayList<Role> getSupportedRole() {
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(new WholesaleSalesRole());
        roles.add(new WholesaleAnalyticsRole());
        return roles;
    }
}
