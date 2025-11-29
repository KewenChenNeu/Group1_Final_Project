/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Organization.RawMaterialSupplier;

import Business.Organization.Organization;
import Business.Role.Role;
import Business.Role.RawMaterialSupplier.RMProcurementRole;
import java.util.ArrayList;

/**
 *
 * @author chris
 */
public class RMProcurementOrganization extends Organization {

    public RMProcurementOrganization() {
        super(Type.RMProcurement.getValue());
    }

    @Override
    public ArrayList<Role> getSupportedRole() {
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(new RMProcurementRole());
        return roles;
    }
}