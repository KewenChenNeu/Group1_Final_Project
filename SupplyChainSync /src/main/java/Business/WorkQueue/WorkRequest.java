/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Business.WorkQueue;

import Business.Enterprise.Enterprise;
import Business.Organization.Organization;
import Business.UserAccount.UserAccount;
import java.util.Date;

/**
 *
 * @author chris
 */
public abstract class WorkRequest {

    // Status constants
    public static final String STATUS_PENDING = "Pending";
    public static final String STATUS_APPROVED = "Approved";
    public static final String STATUS_REJECTED = "Rejected";
    public static final String STATUS_IN_PROGRESS = "In Progress";
    public static final String STATUS_COMPLETED = "Completed";
    public static final String STATUS_CANCELLED = "Cancelled";
    public static final String STATUS_SHIPPED = "Shipped";
    public static final String STATUS_DELIVERED = "Delivered";
    public static final String STATUS_AWAITING_CONFIRMATION = "Awaiting Confirmation";

    private int requestId;
    private static int counter = 1000;
    
    // Message and notes
    private String message;
    private String notes;
    
    // User level tracking
    private UserAccount sender;
    private UserAccount receiver;
    
    // Organization level tracking
    private Organization sourceOrganization;
    private Organization targetOrganization;
    
    // Enterprise level tracking
    private Enterprise sourceEnterprise;
    private Enterprise targetEnterprise;
    
    // Status and dates
    private String status;
    private Date requestDate;
    private Date resolveDate;
    private Date lastUpdated;
    
    // Priority (1 = Highest, 5 = Lowest)
    private int priority;
    
    public WorkRequest() {
        requestId = counter++;
        requestDate = new Date();
        lastUpdated = new Date();
        status = STATUS_PENDING;
        priority = 3; // Default medium priority
    }

    // ==================== ID ====================
    
    public int getRequestId() {
        return requestId;
    }

    // ==================== Message & Notes ====================
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        this.lastUpdated = new Date();
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
        this.lastUpdated = new Date();
    }

    // ==================== User Level ====================
    
    public UserAccount getSender() {
        return sender;
    }

    public void setSender(UserAccount sender) {
        this.sender = sender;
    }

    public UserAccount getReceiver() {
        return receiver;
    }

    public void setReceiver(UserAccount receiver) {
        this.receiver = receiver;
    }

    // ==================== Organization Level ====================
    
    public Organization getSourceOrganization() {
        return sourceOrganization;
    }

    public void setSourceOrganization(Organization sourceOrganization) {
        this.sourceOrganization = sourceOrganization;
    }

    public Organization getTargetOrganization() {
        return targetOrganization;
    }

    public void setTargetOrganization(Organization targetOrganization) {
        this.targetOrganization = targetOrganization;
    }

    // ==================== Enterprise Level ====================
    
    public Enterprise getSourceEnterprise() {
        return sourceEnterprise;
    }

    public void setSourceEnterprise(Enterprise sourceEnterprise) {
        this.sourceEnterprise = sourceEnterprise;
    }

    public Enterprise getTargetEnterprise() {
        return targetEnterprise;
    }

    public void setTargetEnterprise(Enterprise targetEnterprise) {
        this.targetEnterprise = targetEnterprise;
    }

    // ==================== Status ====================
    
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.lastUpdated = new Date();
        
        // Auto-set resolve date when completed/delivered
        if (STATUS_COMPLETED.equals(status) || STATUS_DELIVERED.equals(status)) {
            if (this.resolveDate == null) {
                this.resolveDate = new Date();
            }
        }
    }
    
    public boolean isPending() {
        return STATUS_PENDING.equals(status);
    }
    
    public boolean isCompleted() {
        return STATUS_COMPLETED.equals(status) || STATUS_DELIVERED.equals(status);
    }
    
    public boolean isCancelled() {
        return STATUS_CANCELLED.equals(status) || STATUS_REJECTED.equals(status);
    }

    // ==================== Dates ====================
    
    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public Date getResolveDate() {
        return resolveDate;
    }

    public void setResolveDate(Date resolveDate) {
        this.resolveDate = resolveDate;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    // ==================== Priority ====================
    
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public String getPriorityLabel() {
        switch (priority) {
            case 1: return "Urgent";
            case 2: return "High";
            case 3: return "Medium";
            case 4: return "Low";
            case 5: return "Lowest";
            default: return "Medium";
        }
    }

    // ==================== Utility Methods ====================
    
    /**
     * Approve this request
     */
    public void approve() {
        setStatus(STATUS_APPROVED);
    }
    
    /**
     * Reject this request
     */
    public void reject() {
        setStatus(STATUS_REJECTED);
    }
    
    /**
     * Mark as in progress
     */
    public void startProcessing() {
        setStatus(STATUS_IN_PROGRESS);
    }
    
    /**
     * Complete this request
     */
    public void complete() {
        setStatus(STATUS_COMPLETED);
        this.resolveDate = new Date();
    }
    
    /**
     * Cancel this request
     */
    public void cancel() {
        setStatus(STATUS_CANCELLED);
    }
    
    @Override
    public String toString() {
        return "Request #" + requestId + " - " + status;
    }
}
