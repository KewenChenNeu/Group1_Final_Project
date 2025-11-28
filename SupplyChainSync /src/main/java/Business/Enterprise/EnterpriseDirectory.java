/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Business.Enterprise;

import java.util.ArrayList;

/**
 *
 * @author Chris
 */
public class EnterpriseDirectory {
    
    private ArrayList<Enterprise> enterpriseList;

    public EnterpriseDirectory() {
        enterpriseList = new ArrayList<>();
    }

    public ArrayList<Enterprise> getEnterpriseList() {
        return enterpriseList;
    }

    public void setEnterpriseList(ArrayList<Enterprise> enterpriseList) {
        this.enterpriseList = enterpriseList;
    }
    
    public void addEnterprise(Enterprise enterprise) {
        enterpriseList.add(enterprise);
    }
    
    public void removeEnterprise(Enterprise enterprise) {
        enterpriseList.remove(enterprise);
    }
    
    public Enterprise findEnterpriseByName(String name) {
        for (Enterprise enterprise : enterpriseList) {
            if (enterprise.getName().equals(name)) {
                return enterprise;
            }
        }
        return null;
    }
    
    public Enterprise findEnterpriseByType(Enterprise.EnterpriseType type) {
        for (Enterprise enterprise : enterpriseList) {
            if (enterprise.getEnterpriseType() == type) {
                return enterprise;
            }
        }
        return null;
    }
}
