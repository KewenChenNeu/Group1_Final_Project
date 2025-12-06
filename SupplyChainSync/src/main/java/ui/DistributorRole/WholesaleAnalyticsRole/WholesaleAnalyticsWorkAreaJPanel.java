/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui.DistributorRole.WholesaleAnalyticsRole;

import Business.EcoSystem;
import Business.Enterprise.Enterprise;
import Business.Inventory.Inventory;
import Business.Inventory.InventoryItem;
import Business.Organization.Distributor.WholesaleSalesOrganization;
import Business.UserAccount.UserAccount;
import Business.WorkQueue.DeliveryConfirmationRequest;
import Business.WorkQueue.RetailPurchaseOrderRequest;
import Business.WorkQueue.WorkRequest;
import java.awt.CardLayout;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;

/**
 *
 * @author chris
 */
public class WholesaleAnalyticsWorkAreaJPanel extends javax.swing.JPanel {

    private JPanel userProcessContainer;
    private UserAccount account;
    private WholesaleSalesOrganization wholesaleSalesOrganization;
    private Enterprise enterprise;
    private EcoSystem system;

    public WholesaleAnalyticsWorkAreaJPanel(JPanel userProcessContainer, UserAccount account, 
            WholesaleSalesOrganization wholesaleSalesOrganization, Enterprise enterprise, EcoSystem system) {
        initComponents();
        
        this.userProcessContainer = userProcessContainer;
        this.account = account;
        this.wholesaleSalesOrganization = wholesaleSalesOrganization;
        this.enterprise = enterprise;
        this.system = system;
        
        populateDashboard();
    }
    
    /**
     * Populate the dashboard with enterprise info and quick summary statistics
     */
    private void populateDashboard() {
        // Set enterprise, organization, and user info
        lblEnterpriseName.setText(enterprise.getName());
        lblOrganizationName.setText(wholesaleSalesOrganization.getName());
        lblUserName.setText(account.getEmployee().getName());
        
        // Calculate and display quick summary
        calculateQuickSummary();
    }
    
    /**
     * Calculate quick summary statistics from work queue data
     */
    private void calculateQuickSummary() {
        int ordersCompleted = 0;
        Map<String, Integer> productSalesCount = new HashMap<>();
        
        // Iterate through work requests in the sales organization
        for (WorkRequest request : wholesaleSalesOrganization.getWorkQueue().getWorkRequestList()) {
            // Count completed orders from DeliveryConfirmationRequest
            if (request instanceof DeliveryConfirmationRequest) {
                DeliveryConfirmationRequest dcr = (DeliveryConfirmationRequest) request;
                if (dcr.isConfirmed()) {
                    ordersCompleted++;
                    // Track product for top product calculation
                    String productName = dcr.getProductName();
                    if (productName != null && !productName.isEmpty()) {
                        productSalesCount.merge(productName, dcr.getQuantityDelivered(), Integer::sum);
                    }
                }
            }
            // Also count completed RetailPurchaseOrderRequests
            else if (request instanceof RetailPurchaseOrderRequest) {
                RetailPurchaseOrderRequest rpor = (RetailPurchaseOrderRequest) request;
                if ("Completed".equalsIgnoreCase(rpor.getStatus()) || 
                    "Shipped".equalsIgnoreCase(rpor.getStatus()) ||
                    "Delivered".equalsIgnoreCase(rpor.getStatus())) {
                    ordersCompleted++;
                    // Track product for top product calculation
                    String productName = rpor.getProductName();
                    if (productName != null && !productName.isEmpty()) {
                        productSalesCount.merge(productName, rpor.getQuantity(), Integer::sum);
                    }
                }
            }
        }
        
        // Display orders completed
        lblNumOrderCompleted.setText(String.valueOf(ordersCompleted));
        
        // Find top product (most sold by quantity)
        String topProduct = "N/A";
        int maxQuantity = 0;
        for (Map.Entry<String, Integer> entry : productSalesCount.entrySet()) {
            if (entry.getValue() > maxQuantity) {
                maxQuantity = entry.getValue();
                topProduct = entry.getKey();
            }
        }
        
        // If no sales data, try to get most stocked product from inventory
        if ("N/A".equals(topProduct) && enterprise.getInventory() != null) {
            Inventory inventory = enterprise.getInventory();
            int maxStock = 0;
            for (InventoryItem item : inventory.getProductInventory()) {
                if (item.getQuantity() > maxStock) {
                    maxStock = item.getQuantity();
                    topProduct = item.getItemName();
                }
            }
        }
        
        lblNameTopProduct.setText(topProduct);
    }
    
    /**
     * Refresh the dashboard data
     */
    public void refreshDashboard() {
        calculateQuickSummary();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblNumOrderCompleted = new javax.swing.JLabel();
        lblNameTopProduct = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        btnSalesAnalytics = new javax.swing.JButton();
        btnInventoryAnalytics = new javax.swing.JButton();
        lblQuickSummary = new javax.swing.JLabel();
        lblOrdersCompleted = new javax.swing.JLabel();
        lblTitle = new javax.swing.JLabel();
        lblTopProduct = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        btnEditProfile = new javax.swing.JButton();
        lblOrganization = new javax.swing.JLabel();
        lblUser = new javax.swing.JLabel();
        lblEnterpriseName = new javax.swing.JLabel();
        lblOrganizationName = new javax.swing.JLabel();
        lblUserName = new javax.swing.JLabel();
        lblEnterprise = new javax.swing.JLabel();

        lblNumOrderCompleted.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N

        lblNameTopProduct.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N

        btnSalesAnalytics.setText("📊 Sales Analytics");
        btnSalesAnalytics.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalesAnalyticsActionPerformed(evt);
            }
        });

        btnInventoryAnalytics.setText("📦 Inventory Analytics");
        btnInventoryAnalytics.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInventoryAnalyticsActionPerformed(evt);
            }
        });

        lblQuickSummary.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        lblQuickSummary.setText("Quick Summary:");

        lblOrdersCompleted.setText("Orders Completed:");

        lblTitle.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        lblTitle.setText("📈 Wholesale Analytics Work Area ");
        lblTitle.setToolTipText("");

        lblTopProduct.setText("Top Product:");

        btnEditProfile.setText("👤  Edit Profile");
        btnEditProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditProfileActionPerformed(evt);
            }
        });

        lblOrganization.setText("Organization:");

        lblUser.setText("User:");

        lblEnterprise.setText("Enterprise:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jSeparator1)
                        .addContainerGap())
                    .addComponent(jSeparator2)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblTopProduct)
                            .addComponent(lblOrdersCompleted))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblNameTopProduct, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                            .addComponent(lblNumOrderCompleted, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 74, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnEditProfile, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(btnSalesAnalytics, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnInventoryAnalytics, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(124, 124, 124))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblQuickSummary))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblEnterprise)
                            .addComponent(lblOrganization)
                            .addComponent(lblUser))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblOrganizationName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblEnterpriseName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 384, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
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
                    .addComponent(lblEnterpriseName, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblOrganization, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblOrganizationName, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblQuickSummary)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblOrdersCompleted)
                            .addComponent(lblNumOrderCompleted, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(15, 15, 15)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNameTopProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTopProduct)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnSalesAnalytics, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(btnInventoryAnalytics)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnEditProfile)
                .addContainerGap(311, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSalesAnalyticsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalesAnalyticsActionPerformed
        // TODO add your handling code here:
        SalesAnalyticsPanel salesPanel = new SalesAnalyticsPanel(userProcessContainer, 
                account, wholesaleSalesOrganization, enterprise, system);
        userProcessContainer.add("SalesAnalyticsPanel", salesPanel);
        CardLayout layout = (CardLayout) userProcessContainer.getLayout();
        layout.next(userProcessContainer);
    }//GEN-LAST:event_btnSalesAnalyticsActionPerformed

    private void btnInventoryAnalyticsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInventoryAnalyticsActionPerformed
        // TODO add your handling code here:
        InventoryAnalyticsPanel inventoryPanel = new InventoryAnalyticsPanel(userProcessContainer, 
                account, wholesaleSalesOrganization, enterprise, system);
        userProcessContainer.add("InventoryAnalyticsPanel", inventoryPanel);
        CardLayout layout = (CardLayout) userProcessContainer.getLayout();
        layout.next(userProcessContainer);
    }//GEN-LAST:event_btnInventoryAnalyticsActionPerformed

    private void btnEditProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditProfileActionPerformed
        // TODO add your handling code here:
        AnalyticsEditProfilePanel editPanel = new AnalyticsEditProfilePanel(userProcessContainer, account);
        userProcessContainer.add("AnalyticsEditProfilePanel", editPanel);
        CardLayout layout = (CardLayout) userProcessContainer.getLayout();
        layout.next(userProcessContainer);
    }//GEN-LAST:event_btnEditProfileActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEditProfile;
    private javax.swing.JButton btnInventoryAnalytics;
    private javax.swing.JButton btnSalesAnalytics;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblEnterprise;
    private javax.swing.JLabel lblEnterpriseName;
    private javax.swing.JLabel lblNameTopProduct;
    private javax.swing.JLabel lblNumOrderCompleted;
    private javax.swing.JLabel lblOrdersCompleted;
    private javax.swing.JLabel lblOrganization;
    private javax.swing.JLabel lblOrganizationName;
    private javax.swing.JLabel lblQuickSummary;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTopProduct;
    private javax.swing.JLabel lblUser;
    private javax.swing.JLabel lblUserName;
    // End of variables declaration//GEN-END:variables
}
