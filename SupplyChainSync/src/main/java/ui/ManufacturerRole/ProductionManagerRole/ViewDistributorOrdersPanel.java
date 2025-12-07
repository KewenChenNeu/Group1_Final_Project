/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui.ManufacturerRole.ProductionManagerRole;

import Business.EcoSystem;
import Business.Enterprise.Enterprise;
import Business.Network.Network;
import Business.Organization.Manufacturer.ProductionManagementOrganization;
import Business.WorkQueue.WholesalePurchaseRequest;
import Business.WorkQueue.WorkRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class ViewDistributorOrdersPanel extends javax.swing.JPanel {

    private JPanel userProcessContainer;
    private ProductionManagementOrganization productionManagementOrganization;
    private Enterprise enterprise;
    private EcoSystem system;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    // 存储当前显示的请求列表，用于根据行索引获取对象
    private List<WholesalePurchaseRequest> currentRequests;
    
    public ViewDistributorOrdersPanel(
            JPanel userProcessContainer, 
            ProductionManagementOrganization productionManagementOrganization, 
            Enterprise enterprise, 
            EcoSystem system) {
        
        this.userProcessContainer = userProcessContainer;
        this.productionManagementOrganization = productionManagementOrganization;
        this.enterprise = enterprise;
        this.system = system;
        this.currentRequests = new ArrayList<>();
        
        initComponents();
        setupTableSelectionListener();
        populateRequestsTable();
        clearDetailFields();
    }
    
    private void setupTableSelectionListener() {
        tblRequests.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = tblRequests.getSelectedRow();
                    if (selectedRow >= 0) {
                        displayRequestDetails(selectedRow);
                    }
                }
            }
        });
    }
    
   
    private void populateRequestsTable() {
        DefaultTableModel model = (DefaultTableModel) tblRequests.getModel();
        model.setRowCount(0);
        currentRequests.clear();
        
        List<WholesalePurchaseRequest> requests = findWholesalePurchaseRequests();
        
        for (WholesalePurchaseRequest request : requests) {
            Object[] row = new Object[6];
            row[0] = request.getRequestId();
            row[1] = getDistributorName(request);
            row[2] = request.getProductName();
            row[3] = request.getQuantity() + " " + request.getUnit();
            row[4] = request.getRequestDate() != null ? 
                     dateFormat.format(request.getRequestDate()) : "N/A";
            row[5] = request.getStatus();
            
            model.addRow(row);
            currentRequests.add(request);
        }
        
        if (requests.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No wholesale purchase requests found from distributors.", 
                "Info", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private List<WholesalePurchaseRequest> findWholesalePurchaseRequests() {
        List<WholesalePurchaseRequest> requests = new ArrayList<>();
        
        for (Network network : system.getNetworkList()) {

            for (Enterprise ent : network.getEnterpriseDirectory().getEnterpriseList()) {
                
                for (WorkRequest wr : ent.getWorkQueue().getWorkRequestList()) {
                    if (wr instanceof WholesalePurchaseRequest) {
                        WholesalePurchaseRequest wpr = (WholesalePurchaseRequest) wr;
                        if (isRequestForThisManufacturer(wpr)) {
                            requests.add(wpr);
                        }
                    }
                }
                
                for (var org : ent.getOrganizationDirectory().getOrganizationList()) {
                    for (WorkRequest wr : org.getWorkQueue().getWorkRequestList()) {
                        if (wr instanceof WholesalePurchaseRequest) {
                            WholesalePurchaseRequest wpr = (WholesalePurchaseRequest) wr;
                            if (isRequestForThisManufacturer(wpr)) {
                                requests.add(wpr);
                            }
                        }
                    }
                }
            }
        }
        
        for (WorkRequest wr : productionManagementOrganization.getWorkQueue().getWorkRequestList()) {
            if (wr instanceof WholesalePurchaseRequest) {
                WholesalePurchaseRequest wpr = (WholesalePurchaseRequest) wr;
                if (!requests.contains(wpr)) {
                    requests.add(wpr);
                }
            }
        }
        
        return requests;
    }
    

    private boolean isRequestForThisManufacturer(WholesalePurchaseRequest request) {
        if (request.getTargetEnterprise() != null && 
            request.getTargetEnterprise().equals(enterprise)) {
            return true;
        }
        

        if (request.getTargetOrganization() != null && 
            request.getTargetOrganization().equals(productionManagementOrganization)) {
            return true;
        }
        
        if (productionManagementOrganization.getWorkQueue().getWorkRequestList().contains(request)) {
            return true;
        }
        
        return false;
    }
    

    private String getDistributorName(WholesalePurchaseRequest request) {
        if (request.getSourceEnterprise() != null) {
            return request.getSourceEnterprise().getName();
        }
        if (request.getSender() != null && request.getSender().getEmployee() != null) {
            return request.getSender().getEmployee().getName();
        }
        return "Unknown Distributor";
    }
    

    private void displayRequestDetails(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= currentRequests.size()) {
            return;
        }
        
        WholesalePurchaseRequest request = currentRequests.get(rowIndex);
        
        fieldId.setText(String.valueOf(request.getRequestId()));
        fieldRetailer.setText(getDistributorName(request));
        fieldProduct.setText(request.getProductName() + " (" + request.getProductCode() + ")");
        fieldQuantity.setText(request.getQuantity() + " " + request.getUnit());
        fieldDate.setText(request.getRequestDate() != null ? 
                         dateFormat.format(request.getRequestDate()) : "N/A");
        fieldMessage.setText(request.getMessage() != null ? request.getMessage() : "");
        

        boolean isPending = WorkRequest.STATUS_PENDING.equals(request.getStatus());
        btnApprove.setEnabled(isPending);
        btnReject.setEnabled(isPending);
    }
    
  
    private void clearDetailFields() {
        fieldId.setText("");
        fieldRetailer.setText("");
        fieldProduct.setText("");
        fieldQuantity.setText("");
        fieldDate.setText("");
        fieldMessage.setText("");
        btnApprove.setEnabled(false);
        btnReject.setEnabled(false);
    }
    
 
    private void approveRequest() {
        int selectedRow = tblRequests.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select a request to approve.", 
                "Warning", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        WholesalePurchaseRequest request = currentRequests.get(selectedRow);
        
        if (!WorkRequest.STATUS_PENDING.equals(request.getStatus())) {
            JOptionPane.showMessageDialog(this, 
                "This request has already been processed (Status: " + request.getStatus() + ").", 
                "Warning", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Approve wholesale purchase request?\n\n" +
            "Product: " + request.getProductName() + "\n" +
            "Quantity: " + request.getQuantity() + " " + request.getUnit() + "\n" +
            "Distributor: " + getDistributorName(request),
            "Confirm Approval", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        request.approve();
        request.setNotes("Approved by Production Manager");
        
        JOptionPane.showMessageDialog(this, 
            "Request approved successfully!\n" +
            "Request ID: " + request.getRequestId() + "\n" +
            "You can now create a shipping request for this order.", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
        
        populateRequestsTable();
        clearDetailFields();
    }
    

    private void rejectRequest() {
        int selectedRow = tblRequests.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select a request to reject.", 
                "Warning", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        WholesalePurchaseRequest request = currentRequests.get(selectedRow);
        
        if (!WorkRequest.STATUS_PENDING.equals(request.getStatus())) {
            JOptionPane.showMessageDialog(this, 
                "This request has already been processed (Status: " + request.getStatus() + ").", 
                "Warning", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 询问拒绝原因
        String reason = JOptionPane.showInputDialog(this, 
            "Please provide a reason for rejection:", 
            "Rejection Reason", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (reason == null || reason.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Rejection cancelled. A reason is required.", 
                "Info", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // 拒绝请求
        request.reject();
        request.setNotes("Rejected: " + reason);
        
        JOptionPane.showMessageDialog(this, 
            "Request rejected successfully.\n" +
            "Request ID: " + request.getRequestId() + "\n" +
            "Reason: " + reason, 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
        
        populateRequestsTable();
        clearDetailFields();
    }
    
           

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator1 = new javax.swing.JSeparator();
        btnBack = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblRequests = new javax.swing.JTable();
        lblTableTitle = new javax.swing.JLabel();
        lblDetails = new javax.swing.JLabel();
        lblRequestId = new javax.swing.JLabel();
        lblRetailer = new javax.swing.JLabel();
        lblProduct = new javax.swing.JLabel();
        lblQuantity = new javax.swing.JLabel();
        lblDate = new javax.swing.JLabel();
        lblMessage = new javax.swing.JLabel();
        fieldId = new javax.swing.JTextField();
        fieldProduct = new javax.swing.JTextField();
        fieldDate = new javax.swing.JTextField();
        fieldRetailer = new javax.swing.JTextField();
        fieldQuantity = new javax.swing.JTextField();
        fieldMessage = new javax.swing.JTextField();
        btnApprove = new javax.swing.JButton();
        btnReject = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        btnBack.setText("<< Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        tblRequests.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Request ID", "Retailer", "Product", "Quantity", "Date", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblRequests);

        lblTableTitle.setText("Incoming Restock Requests");

        lblDetails.setText("Request Details:");

        lblRequestId.setText("Request ID:");

        lblRetailer.setText("Retailer:");

        lblProduct.setText("Product:");

        lblQuantity.setText("Quantity:");

        lblDate.setText("Request Date:");

        lblMessage.setText("Message:");

        fieldId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldIdActionPerformed(evt);
            }
        });

        btnApprove.setText("Approve Request");
        btnApprove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApproveActionPerformed(evt);
            }
        });

        btnReject.setText("Reject Request");
        btnReject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRejectActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jLabel1.setText("Distributor Orders");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator1)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTableTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 759, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblRequestId)
                                    .addComponent(lblDetails)
                                    .addComponent(lblProduct)
                                    .addComponent(lblDate)
                                    .addComponent(lblMessage))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(fieldProduct)
                                            .addComponent(fieldId)
                                            .addComponent(fieldDate, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblRetailer, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(lblQuantity, javax.swing.GroupLayout.Alignment.TRAILING))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(fieldRetailer)
                                            .addComponent(fieldQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addComponent(fieldMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 431, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(13, 13, 13)
                                .addComponent(btnApprove, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(62, 62, 62)
                                .addComponent(btnReject, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 390, Short.MAX_VALUE)
                        .addComponent(btnBack)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnBack)
                        .addGap(0, 2, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblTableTitle)
                .addGap(13, 13, 13)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblDetails)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRequestId)
                    .addComponent(lblRetailer)
                    .addComponent(fieldId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldRetailer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProduct)
                    .addComponent(lblQuantity)
                    .addComponent(fieldProduct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDate)
                    .addComponent(fieldDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMessage)
                    .addComponent(fieldMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(57, 57, 57)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnApprove)
                    .addComponent(btnReject))
                .addContainerGap(240, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void fieldIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fieldIdActionPerformed

    private void btnApproveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApproveActionPerformed
        // TODO add your handling code here:
        approveRequest();
       
    }//GEN-LAST:event_btnApproveActionPerformed

    private void btnRejectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRejectActionPerformed
        // TODO add your handling code here:
        rejectRequest();
    }//GEN-LAST:event_btnRejectActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
         userProcessContainer.remove(this);
        ((java.awt.CardLayout) userProcessContainer.getLayout()).next(userProcessContainer);
        
    }//GEN-LAST:event_btnBackActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApprove;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnReject;
    private javax.swing.JTextField fieldDate;
    private javax.swing.JTextField fieldId;
    private javax.swing.JTextField fieldMessage;
    private javax.swing.JTextField fieldProduct;
    private javax.swing.JTextField fieldQuantity;
    private javax.swing.JTextField fieldRetailer;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblDetails;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JLabel lblProduct;
    private javax.swing.JLabel lblQuantity;
    private javax.swing.JLabel lblRequestId;
    private javax.swing.JLabel lblRetailer;
    private javax.swing.JLabel lblTableTitle;
    private javax.swing.JTable tblRequests;
    // End of variables declaration//GEN-END:variables

    
}
