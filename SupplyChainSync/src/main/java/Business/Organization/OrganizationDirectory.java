/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Business.Organization;

import Business.Organization.Organization.Type;
import java.util.ArrayList;

/**
 *
 * @author chris
 */
public class OrganizationDirectory {
    
    private ArrayList<Organization> organizationList;

    public OrganizationDirectory() {
        organizationList = new ArrayList<>();
    }

    public ArrayList<Organization> getOrganizationList() {
        return organizationList;
    }
    
    public void addOrganization(Organization organization) {
        organizationList.add(organization);
    }
    
    public void removeOrganization(Organization organization) {
        organizationList.remove(organization);
    }
    
    public Organization findOrganizationById(int id) {
        for (Organization org : organizationList) {
            if (org.getOrganizationID() == id) {
                return org;
            }
        }
        return null;
    }
    
    public Organization findOrganizationByName(String name) {
        for (Organization org : organizationList) {
            if (org.getName().equals(name)) {
                return org;
            }
        }
        return null;
    }
}