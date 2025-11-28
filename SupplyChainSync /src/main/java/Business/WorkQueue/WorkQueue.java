/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Business.WorkQueue;

import java.util.ArrayList;

/**
 *
 * @author chris
 */
public class WorkQueue {
    
    private ArrayList<WorkRequest> workRequestList;

    public WorkQueue() {
        workRequestList = new ArrayList<>();
    }

    public ArrayList<WorkRequest> getWorkRequestList() {
        return workRequestList;
    }
    
    public void addWorkRequest(WorkRequest request) {
        workRequestList.add(request);
    }
    
    public void removeWorkRequest(WorkRequest request) {
        workRequestList.remove(request);
    }
    
    public WorkRequest findRequestById(int id) {
        for (WorkRequest request : workRequestList) {
            if (request.getRequestId() == id) {
                return request;
            }
        }
        return null;
    }
    
    public ArrayList<WorkRequest> getRequestsByStatus(String status) {
        ArrayList<WorkRequest> filteredList = new ArrayList<>();
        for (WorkRequest request : workRequestList) {
            if (request.getStatus().equals(status)) {
                filteredList.add(request);
            }
        }
        return filteredList;
    }
    
    public ArrayList<WorkRequest> getPendingRequests() {
        return getRequestsByStatus("Pending");
    }
    
    public ArrayList<WorkRequest> getCompletedRequests() {
        return getRequestsByStatus("Completed");
    }
}