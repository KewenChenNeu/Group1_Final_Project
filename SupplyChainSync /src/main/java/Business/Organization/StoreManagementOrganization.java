/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Business.Organization;

import Business.Role.Role;
import Business.Role.StoreManagerRole;
import Business.Role.OrderClerkRole;
import java.util.ArrayList;

/**
 *
 * @author chris
 */
public class StoreManagementOrganization extends Organization {

    public StoreManagementOrganization() {
        super(Type.StoreManagement.getValue());
    }

    @Override
    public ArrayList<Role> getSupportedRole() {
        ArrayList<Role> roles = new ArrayList<>();
        roles.add(new StoreManagerRole());
        roles.add(new OrderClerkRole());
        return roles;
    }
}