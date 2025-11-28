/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Business;

import Business.Employee.EmployeeDirectory;
import Business.Network.Network;
import Business.Organization.Organization;
import Business.Role.Role;
import Business.Role.SystemAdminRole;
import Business.UserAccount.UserAccountDirectory;
import Business.WorkQueue.WorkQueue;
import java.util.ArrayList;

/**
 *
 * @author chris
 */
public class EcoSystem extends Organization{
    
    private static EcoSystem instance;
    private ArrayList<Network> networkList;
    
    // Private constructor for singleton pattern
    private EcoSystem() {
        super("Supply Chain EcoSystem");
        networkList = new ArrayList<>();
    }
    
    // Singleton getInstance method
    public static EcoSystem getInstance() {
        if (instance == null) {
            instance = new EcoSystem();
        }
        return instance;
    }
    
    // Reset instance (useful for testing or reloading from DB)
    public static void setInstance(EcoSystem system) {
        instance = system;
    }
    
    public Network createAndAddNetwork() {
        Network network = new Network();
        networkList.add(network);
        return network;
    }
    
    public Network createAndAddNetwork(String name) {
        Network network = new Network(name);
        networkList.add(network);
        return network;
    }
    
    public void addNetwork(Network network) {
        networkList.add(network);
    }
    
    public void removeNetwork(Network network) {
        networkList.remove(network);
    }

    @Override
    public ArrayList<Role> getSupportedRole() {
        ArrayList<Role> roleList = new ArrayList<>();
        roleList.add(new SystemAdminRole());
        return roleList;
    }

    public ArrayList<Network> getNetworkList() {
        return networkList;
    }

    public void setNetworkList(ArrayList<Network> networkList) {
        this.networkList = networkList;
    }
    
    /**
     * Check if username is unique across the entire ecosystem
     */
    public boolean checkIfUserIsUnique(String userName) {
        // Check in EcoSystem's own user directory
        if (!this.getUserAccountDirectory().checkIfUsernameIsUnique(userName)) {
            return false;
        }
        
        // Check in all networks -> enterprises -> organizations
        for (Network network : networkList) {
            for (var enterprise : network.getEnterpriseDirectory().getEnterpriseList()) {
                // Check in enterprise's user directory
                if (!enterprise.getUserAccountDirectory().checkIfUsernameIsUnique(userName)) {
                    return false;
                }
                // Check in each organization's user directory
                for (var organization : enterprise.getOrganizationDirectory().getOrganizationList()) {
                    if (!organization.getUserAccountDirectory().checkIfUsernameIsUnique(userName)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    /**
     * Find a network by name
     */
    public Network findNetworkByName(String name) {
        for (Network network : networkList) {
            if (network.getName().equals(name)) {
                return network;
            }
        }
        return null;
    }
}
