/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Business.Network;

import Business.Enterprise.EnterpriseDirectory;

/**
 *
 * @author Chris
 */
public class Network {
    private String name;
    private EnterpriseDirectory enterpriseDirectory;
    private int networkId;
    private static int counter = 0;
    
    public Network() {
        enterpriseDirectory = new EnterpriseDirectory();
        networkId = counter;
        counter++;
    }
    
    public Network(String name) {
        this();
        this.name = name;
    }

    public int getNetworkId() {
        return networkId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EnterpriseDirectory getEnterpriseDirectory() {
        return enterpriseDirectory;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
}
