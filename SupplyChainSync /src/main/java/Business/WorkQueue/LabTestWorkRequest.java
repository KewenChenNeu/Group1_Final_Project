/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Business.WorkQueue;

import Business.UserAccount.UserAccount;

/**
 *
 * @author raunak
 */
public class LabTestWorkRequest extends WorkRequest{
    
    private String testResult;
    private boolean approval = false;
    private UserAccount approvedBy;
    
    

    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }
    
    
}
