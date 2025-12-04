/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Business.WorkQueue;

import Business.Enterprise.Enterprise;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

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
    
    // ==================== Basic Operations ====================
    
    public void addWorkRequest(WorkRequest request) {
        workRequestList.add(request);
    }
    
    public void removeWorkRequest(WorkRequest request) {
        workRequestList.remove(request);
    }
    
    public void clearAll() {
        workRequestList.clear();
    }
    
    public int size() {
        return workRequestList.size();
    }
    
    public boolean isEmpty() {
        return workRequestList.isEmpty();
    }
    
    // ==================== Find by ID ====================
    
    public WorkRequest findRequestById(int id) {
        for (WorkRequest request : workRequestList) {
            if (request.getRequestId() == id) {
                return request;
            }
        }
        return null;
    }
    
    // ==================== Filter by Status ====================
    
    public ArrayList<WorkRequest> getRequestsByStatus(String status) {
        ArrayList<WorkRequest> filteredList = new ArrayList<>();
        for (WorkRequest request : workRequestList) {
            if (request.getStatus() != null && request.getStatus().equals(status)) {
                filteredList.add(request);
            }
        }
        return filteredList;
    }
    
    public ArrayList<WorkRequest> getPendingRequests() {
        return getRequestsByStatus(WorkRequest.STATUS_PENDING);
    }
    
    public ArrayList<WorkRequest> getApprovedRequests() {
        return getRequestsByStatus(WorkRequest.STATUS_APPROVED);
    }
    
    public ArrayList<WorkRequest> getInProgressRequests() {
        return getRequestsByStatus(WorkRequest.STATUS_IN_PROGRESS);
    }
    
    public ArrayList<WorkRequest> getCompletedRequests() {
        return getRequestsByStatus(WorkRequest.STATUS_COMPLETED);
    }
    
    public ArrayList<WorkRequest> getRejectedRequests() {
        return getRequestsByStatus(WorkRequest.STATUS_REJECTED);
    }
    
    public ArrayList<WorkRequest> getCancelledRequests() {
        return getRequestsByStatus(WorkRequest.STATUS_CANCELLED);
    }
    
    /**
     * Get all active (non-completed, non-cancelled) requests
     */
    public ArrayList<WorkRequest> getActiveRequests() {
        ArrayList<WorkRequest> filteredList = new ArrayList<>();
        for (WorkRequest request : workRequestList) {
            if (!request.isCompleted() && !request.isCancelled()) {
                filteredList.add(request);
            }
        }
        return filteredList;
    }
    
    // ==================== Filter by Type ====================
    
    /**
     * Get requests of a specific type
     * @param type The class type of the request (e.g., RawMaterialRestockRequest.class)
     */
    @SuppressWarnings("unchecked")
    public <T extends WorkRequest> ArrayList<T> getRequestsByType(Class<T> type) {
        ArrayList<T> filteredList = new ArrayList<>();
        for (WorkRequest request : workRequestList) {
            if (type.isInstance(request)) {
                filteredList.add((T) request);
            }
        }
        return filteredList;
    }
    
    // ==================== Filter by Enterprise ====================
    
    /**
     * Get requests from a specific source enterprise
     */
    public ArrayList<WorkRequest> getRequestsFromEnterprise(Enterprise enterprise) {
        ArrayList<WorkRequest> filteredList = new ArrayList<>();
        for (WorkRequest request : workRequestList) {
            if (request.getSourceEnterprise() != null && 
                request.getSourceEnterprise().equals(enterprise)) {
                filteredList.add(request);
            }
        }
        return filteredList;
    }
    
    /**
     * Get requests targeted to a specific enterprise
     */
    public ArrayList<WorkRequest> getRequestsToEnterprise(Enterprise enterprise) {
        ArrayList<WorkRequest> filteredList = new ArrayList<>();
        for (WorkRequest request : workRequestList) {
            if (request.getTargetEnterprise() != null && 
                request.getTargetEnterprise().equals(enterprise)) {
                filteredList.add(request);
            }
        }
        return filteredList;
    }
    
    // ==================== Filter by Priority ====================
    
    /**
     * Get requests with priority level (1=Urgent to 5=Lowest)
     */
    public ArrayList<WorkRequest> getRequestsByPriority(int priority) {
        ArrayList<WorkRequest> filteredList = new ArrayList<>();
        for (WorkRequest request : workRequestList) {
            if (request.getPriority() == priority) {
                filteredList.add(request);
            }
        }
        return filteredList;
    }
    
    /**
     * Get urgent requests (priority 1 or 2)
     */
    public ArrayList<WorkRequest> getUrgentRequests() {
        ArrayList<WorkRequest> filteredList = new ArrayList<>();
        for (WorkRequest request : workRequestList) {
            if (request.getPriority() <= 2) {
                filteredList.add(request);
            }
        }
        return filteredList;
    }
    
    // ==================== Filter by Date ====================
    
    /**
     * Get requests created after a specific date
     */
    public ArrayList<WorkRequest> getRequestsAfterDate(Date date) {
        ArrayList<WorkRequest> filteredList = new ArrayList<>();
        for (WorkRequest request : workRequestList) {
            if (request.getRequestDate() != null && request.getRequestDate().after(date)) {
                filteredList.add(request);
            }
        }
        return filteredList;
    }
    
    /**
     * Get requests created before a specific date
     */
    public ArrayList<WorkRequest> getRequestsBeforeDate(Date date) {
        ArrayList<WorkRequest> filteredList = new ArrayList<>();
        for (WorkRequest request : workRequestList) {
            if (request.getRequestDate() != null && request.getRequestDate().before(date)) {
                filteredList.add(request);
            }
        }
        return filteredList;
    }
    
    // ==================== Sorting ====================
    
    /**
     * Get all requests sorted by date (newest first)
     */
    public ArrayList<WorkRequest> getRequestsSortedByDateDesc() {
        ArrayList<WorkRequest> sortedList = new ArrayList<>(workRequestList);
        sortedList.sort((r1, r2) -> {
            if (r1.getRequestDate() == null) return 1;
            if (r2.getRequestDate() == null) return -1;
            return r2.getRequestDate().compareTo(r1.getRequestDate());
        });
        return sortedList;
    }
    
    /**
     * Get all requests sorted by priority (urgent first)
     */
    public ArrayList<WorkRequest> getRequestsSortedByPriority() {
        ArrayList<WorkRequest> sortedList = new ArrayList<>(workRequestList);
        sortedList.sort(Comparator.comparingInt(WorkRequest::getPriority));
        return sortedList;
    }
    
    // ==================== Statistics ====================
    
    /**
     * Count requests by status
     */
    public int countByStatus(String status) {
        return getRequestsByStatus(status).size();
    }
    
    /**
     * Count pending requests
     */
    public int countPending() {
        return getPendingRequests().size();
    }
    
    /**
     * Count completed requests
     */
    public int countCompleted() {
        return getCompletedRequests().size();
    }
}
