/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui.DistributorRole.WholesaleSalesRole;

import Business.EcoSystem;
import Business.Enterprise.Enterprise;
import Business.Enterprise.ManufacturerEnterprise;
import Business.Enterprise.ShippingEnterprise;
import Business.Network.Network;
import Business.Organization.Distributor.WholesaleSalesOrganization;
import Business.Organization.Organization;
import Business.UserAccount.UserAccount;
import Business.WorkQueue.ProductShippingRequest;
import Business.WorkQueue.RetailPurchaseOrderRequest;
import Business.WorkQueue.WholesalePurchaseRequest;
import Business.WorkQueue.WholesalesShippingRequest;
import Business.WorkQueue.WorkRequest;
import java.awt.CardLayout;
import javax.swing.JPanel;

/**
 *
 * @author chris
 */
public class WholesaleSalesWorkAreaJPanel extends javax.swing.JPanel {

    private JPanel userProcessContainer;
    private UserAccount userAccount;
    private WholesaleSalesOrganization organization;
    private Enterprise enterprise;
    private EcoSystem system;
    
    /**
     * Creates new form WholesaleSalesWorkAreaJPanel
     */
    public WholesaleSalesWorkAreaJPanel(JPanel userProcessContainer, UserAccount account, WholesaleSalesOrganization wholesaleSalesOrganization, Enterprise enterprise, EcoSystem system) {
        initComponents();
        this.userProcessContainer = userProcessContainer;
        this.userAccount = account;
        this.organization = wholesaleSalesOrganization;
        this.enterprise = enterprise;
        this.system = system;
        
        populateInfo();
        updateSummary();
    }
    
    private void populateInfo() {
        lblEnterpriseName.setText(enterprise.getName());
        lblOrganizationName.setText(organization.getName());
        lblUserName.setText(userAccount.getEmployee().getName());
    }

    void updateSummary() {
        int pendingRestockRequests = 0;
        int activePurchaseOrders = 0;
        int shipmentsInProgress = 0;
        
        for (WorkRequest request : organization.getWorkQueue().getWorkRequestList()) {
            if (request instanceof RetailPurchaseOrderRequest) {
                String status = request.getStatus();
                if ("Pending".equalsIgnoreCase(status) || "Sent".equalsIgnoreCase(status)) {
                    pendingRestockRequests++;
                }
            }
        }
        
        for (Network network : system.getNetworkList()) {
            for (Enterprise ent : network.getEnterpriseDirectory().getEnterpriseList()) {
                if (ent instanceof ManufacturerEnterprise) {
                    for (Organization org : ent.getOrganizationDirectory().getOrganizationList()) {
                        for (WorkRequest request : org.getWorkQueue().getWorkRequestList()) {
                            if (request instanceof WholesalePurchaseRequest) {
                                WholesalePurchaseRequest purchaseReq = (WholesalePurchaseRequest) request;
                                if (request.getSender() != null && 
                                    request.getSender().getUsername().equals(userAccount.getUsername())) {
                                    String status = request.getStatus();
                                    if (!"Completed".equalsIgnoreCase(status) && 
                                        !"Rejected".equalsIgnoreCase(status) &&
                                        !"Cancelled".equalsIgnoreCase(status)) {
                                        activePurchaseOrders++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        for (Network network : system.getNetworkList()) {
            for (Enterprise ent : network.getEnterpriseDirectory().getEnterpriseList()) {
                if (ent instanceof ShippingEnterprise) {
                    for (Organization org : ent.getOrganizationDirectory().getOrganizationList()) {
                        for (WorkRequest request : org.getWorkQueue().getWorkRequestList()) {
                            if (request instanceof WholesalesShippingRequest) {
                                WholesalesShippingRequest shipReq = (WholesalesShippingRequest) request;
                                String shippingStatus = shipReq.getShippingStatus();
                                if (shippingStatus != null && 
                                    !WholesalesShippingRequest.SHIP_STATUS_DELIVERED.equalsIgnoreCase(shippingStatus)) {
                                    if (request.getSender() != null && 
                                        request.getSender().getUsername().equals(userAccount.getUsername())) {
                                        shipmentsInProgress++;
                                    }
                                }
                            }

                            if (request instanceof ProductShippingRequest) {
                                ProductShippingRequest shipReq = (ProductShippingRequest) request;
                                String shippingStatus = shipReq.getShippingStatus();
                                if (shippingStatus != null && 
                                    !ProductShippingRequest.SHIP_STATUS_DELIVERED.equalsIgnoreCase(shippingStatus)) {
                                    if (request.getSender() != null && 
                                        request.getSender().getUsername().equals(userAccount.getUsername())) {
                                        shipmentsInProgress++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        lblNumberOfRestockRequest.setText(String.valueOf(pendingRestockRequests));
        lblNumberOfActiveRequest.setText(String.valueOf(activePurchaseOrders));
        lblNumberOfShipments.setText(String.valueOf(shipmentsInProgress));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTitle = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        lblEnterprise = new javax.swing.JLabel();
        lblOrganization = new javax.swing.JLabel();
        lblUser = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        lblEnterpriseName = new javax.swing.JLabel();
        lblOrganizationName = new javax.swing.JLabel();
        lblUserName = new javax.swing.JLabel();
        lblQuickSummary = new javax.swing.JLabel();
        lblPendingRestockRequest = new javax.swing.JLabel();
        lblActivePurchaseOrders = new javax.swing.JLabel();
        lblShipments = new javax.swing.JLabel();
        lblNumberOfRestockRequest = new javax.swing.JLabel();
        lblNumberOfActiveRequest = new javax.swing.JLabel();
        lblNumberOfShipments = new javax.swing.JLabel();
        btnViewRetailOrders = new javax.swing.JButton();
        btnManageShipping = new javax.swing.JButton();
        btnCreatePurchaseOrder = new javax.swing.JButton();
        btnDeliveryConfirmation = new javax.swing.JButton();
        btnEditProfile = new javax.swing.JButton();

        lblTitle.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        lblTitle.setText("📦 Wholesale Sales Work Area");
        lblTitle.setToolTipText("");

        lblEnterprise.setText("Enterprise:");

        lblOrganization.setText("Organization:");

        lblUser.setText("User:");

        lblQuickSummary.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        lblQuickSummary.setText("Quick Summary:");

        lblPendingRestockRequest.setText("Pending Restock Requests:");

        lblActivePurchaseOrders.setText("Active Purchase Orders:");

        lblShipments.setText("Shipments In Progress:");

        lblNumberOfRestockRequest.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N

        lblNumberOfActiveRequest.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N

        lblNumberOfShipments.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N

        btnViewRetailOrders.setText("📥 View Retail Orders");
        btnViewRetailOrders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewRetailOrdersActionPerformed(evt);
            }
        });

        btnManageShipping.setText("🚚 Manage Shipping");
        btnManageShipping.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManageShippingActionPerformed(evt);
            }
        });

        btnCreatePurchaseOrder.setText("📤 Create Purchase Order");
        btnCreatePurchaseOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreatePurchaseOrderActionPerformed(evt);
            }
        });

        btnDeliveryConfirmation.setText("✅ Delivery Confirmation");
        btnDeliveryConfirmation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeliveryConfirmationActionPerformed(evt);
            }
        });

        btnEditProfile.setText("👤  Edit Profile");
        btnEditProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditProfileActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 266, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblEnterprise)
                                    .addComponent(lblOrganization)
                                    .addComponent(lblUser))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblEnterpriseName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblUserName, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                                    .addComponent(lblOrganizationName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(lblQuickSummary))
                        .addGap(146, 146, 146))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator2)
                            .addComponent(jSeparator1))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblActivePurchaseOrders)
                            .addComponent(lblPendingRestockRequest)
                            .addComponent(lblShipments))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblNumberOfRestockRequest, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                            .addComponent(lblNumberOfActiveRequest, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblNumberOfShipments, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnManageShipping, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnViewRetailOrders, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnCreatePurchaseOrder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnDeliveryConfirmation, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnEditProfile, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(130, 130, 130))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblEnterprise, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblEnterpriseName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblOrganization, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblOrganizationName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblUserName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblQuickSummary)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblPendingRestockRequest)
                            .addComponent(lblNumberOfRestockRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(15, 15, 15)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNumberOfActiveRequest, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblActivePurchaseOrders))
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNumberOfShipments, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblShipments)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnViewRetailOrders, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(btnCreatePurchaseOrder)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnManageShipping)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnDeliveryConfirmation)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnEditProfile)
                .addContainerGap(252, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnViewRetailOrdersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewRetailOrdersActionPerformed
        // TODO add your handling code here:
        ViewRetailOrdersPanel panel = new ViewRetailOrdersPanel(userProcessContainer, userAccount, organization, enterprise, system);
        userProcessContainer.add("ViewRetailOrdersPanel", panel);
        CardLayout layout = (CardLayout) userProcessContainer.getLayout();
        layout.next(userProcessContainer);
    }//GEN-LAST:event_btnViewRetailOrdersActionPerformed

    private void btnCreatePurchaseOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreatePurchaseOrderActionPerformed
        // TODO add your handling code here:
        CreatePurchaseOrderPanel panel = new CreatePurchaseOrderPanel(userProcessContainer, userAccount, organization, enterprise, system);
        userProcessContainer.add("CreatePurchaseOrderPanel", panel);
        CardLayout layout = (CardLayout) userProcessContainer.getLayout();
        layout.next(userProcessContainer);
    }//GEN-LAST:event_btnCreatePurchaseOrderActionPerformed

    private void btnManageShippingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManageShippingActionPerformed
        // TODO add your handling code here:
        ManageShippingPanel panel = new ManageShippingPanel(userProcessContainer, userAccount, organization, enterprise, system);
        userProcessContainer.add("ManageShippingPanel", panel);
        CardLayout layout = (CardLayout) userProcessContainer.getLayout();
        layout.next(userProcessContainer);
    }//GEN-LAST:event_btnManageShippingActionPerformed

    private void btnDeliveryConfirmationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeliveryConfirmationActionPerformed
        // TODO add your handling code here:
        DeliveryConfirmationPanel panel = new DeliveryConfirmationPanel(userProcessContainer, userAccount, organization, enterprise, system);
        userProcessContainer.add("DeliveryConfirmationPanel", panel);
        CardLayout layout = (CardLayout) userProcessContainer.getLayout();
        layout.next(userProcessContainer);
    }//GEN-LAST:event_btnDeliveryConfirmationActionPerformed

    private void btnEditProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditProfileActionPerformed
        // TODO add your handling code here:
        SalesEditProfilePanel panel = new SalesEditProfilePanel(userProcessContainer, userAccount, organization, enterprise, system);
        userProcessContainer.add("SalesEditProfilePanel", panel);
        CardLayout layout = (CardLayout) userProcessContainer.getLayout();
        layout.next(userProcessContainer);
    }//GEN-LAST:event_btnEditProfileActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCreatePurchaseOrder;
    private javax.swing.JButton btnDeliveryConfirmation;
    private javax.swing.JButton btnEditProfile;
    private javax.swing.JButton btnManageShipping;
    private javax.swing.JButton btnViewRetailOrders;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblActivePurchaseOrders;
    private javax.swing.JLabel lblEnterprise;
    private javax.swing.JLabel lblEnterpriseName;
    private javax.swing.JLabel lblNumberOfActiveRequest;
    private javax.swing.JLabel lblNumberOfRestockRequest;
    private javax.swing.JLabel lblNumberOfShipments;
    private javax.swing.JLabel lblOrganization;
    private javax.swing.JLabel lblOrganizationName;
    private javax.swing.JLabel lblPendingRestockRequest;
    private javax.swing.JLabel lblQuickSummary;
    private javax.swing.JLabel lblShipments;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUser;
    private javax.swing.JLabel lblUserName;
    // End of variables declaration//GEN-END:variables

    
}
