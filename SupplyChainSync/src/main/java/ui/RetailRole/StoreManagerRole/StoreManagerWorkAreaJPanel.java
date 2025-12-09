/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui.RetailRole.StoreManagerRole;

import Business.EcoSystem;
import Business.Enterprise.Enterprise;
import Business.Enterprise.ProductDistributorEnterprise;
import Business.Enterprise.RetailEnterprise;
import Business.Inventory.Inventory;
import Business.Inventory.InventoryItem;
import Business.Network.Network;
import Business.Organization.Distributor.WholesaleSalesOrganization;
import Business.Organization.Organization;
import Business.Organization.Retail.StoreManagementOrganization;
import Business.UserAccount.UserAccount;
import Business.WorkQueue.RetailPurchaseOrderRequest;
import Business.WorkQueue.WorkRequest;
import java.awt.CardLayout;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author zhifei
 */
public class StoreManagerWorkAreaJPanel extends javax.swing.JPanel {

    private JPanel userProcessContainer;
    private UserAccount userAccount;
    private StoreManagementOrganization organization;
    private Enterprise enterprise;
    private EcoSystem system;
    private Inventory inventory;

    /**
     * Creates new form StoreManagerWorkAreaJPanel
     */
    public StoreManagerWorkAreaJPanel(JPanel userProcessContainer, UserAccount account, StoreManagementOrganization storeManagementOrganization, Enterprise enterprise, EcoSystem system) {
        initComponents();
        this.userProcessContainer = userProcessContainer;
        this.userAccount = account;
        this.organization = storeManagementOrganization;
        this.enterprise = enterprise;
        this.system = system;

        this.inventory = getRetailInventory();

        populateInfo();
        updateSummary();
        populatePendingOrdersTable();
    }

    private Inventory getRetailInventory() {
        if (enterprise instanceof RetailEnterprise) {
            RetailEnterprise retailEnterprise = (RetailEnterprise) enterprise;
            return retailEnterprise.getInventory();
        }
        return enterprise.getInventory();
    }

    private void populateInfo() {
        txtEnterpriseName.setText(enterprise.getName());
        txtOrganizationName.setText(organization.getName());
        txtUserName.setText(userAccount.getEmployee().getName());
    }

    void updateSummary() {
        int totalProducts = 0;
        int lowStockItems = 0;
        int outOfStockItems = 0;

        if (inventory != null) {
            for (InventoryItem item : inventory.getProductInventory()) {
                if (item.getProduct() != null) {
                    totalProducts++;
                    int available = item.getAvailableQuantity();
                    int minLevel = item.getMinStockLevel();

                    if (available <= 0) {
                        outOfStockItems++;
                    } else if (minLevel > 0 && available <= minLevel) {
                        lowStockItems++;
                    }
                }
            }
        }

        int totalStaff = organization.getUserAccountDirectory().getUserAccountList().size();

        txtTotalProducts.setText(String.valueOf(totalProducts));
        txtLowStock.setText(String.valueOf(lowStockItems));
        txtOutOfStock.setText(String.valueOf(outOfStockItems));
        txtTotalStaff.setText(String.valueOf(totalStaff));
    }

    private void populatePendingOrdersTable() {
        DefaultTableModel model = (DefaultTableModel) tblPendingOrders.getModel();
        model.setRowCount(0);

        // Update table columns to include Target Distributor
        model.setColumnIdentifiers(new String[] {
            "Order ID", "Product", "Quantity", "Total Price", "Target Distributor", "Status"
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Get pending orders from local organization's work queue (waiting for Store Manager approval)
        for (WorkRequest request : organization.getWorkQueue().getWorkRequestList()) {
            if (request instanceof RetailPurchaseOrderRequest) {
                RetailPurchaseOrderRequest order = (RetailPurchaseOrderRequest) request;
                String status = order.getStatus();

                // Show orders that need approval (Pending status)
                if ("Pending".equalsIgnoreCase(status)) {
                    Object[] row = new Object[6];
                    row[0] = order.getRequestId();
                    row[1] = order.getProduct() != null ? order.getProduct().getProductName() : "N/A";
                    row[2] = order.getQuantity();
                    row[3] = order.getTotalPrice() > 0 ? String.format("$%.2f", order.getTotalPrice()) : "N/A";
                    row[4] = order.getTargetDistributorName() != null ? order.getTargetDistributorName() : "N/A";
                    row[5] = status;
                    model.addRow(row);
                }
            }
        }
    }

    private RetailPurchaseOrderRequest getSelectedOrder() {
        int selectedRow = tblPendingOrders.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }

        Object orderIdObj = tblPendingOrders.getValueAt(selectedRow, 0);
        String orderId = String.valueOf(orderIdObj);

        // Search in local organization's work queue
        for (WorkRequest request : organization.getWorkQueue().getWorkRequestList()) {
            if (request instanceof RetailPurchaseOrderRequest) {
                RetailPurchaseOrderRequest order = (RetailPurchaseOrderRequest) request;
                if (orderId.equals(String.valueOf(order.getRequestId()))) {
                    return order;
                }
            }
        }
        return null;
    }

    private Organization findDistributorReceivingOrg(String distributorName) {
        for (Network network : system.getNetworkList()) {
            for (Enterprise ent : network.getEnterpriseDirectory().getEnterpriseList()) {
                if (ent instanceof ProductDistributorEnterprise && ent.getName().equals(distributorName)) {
                    ProductDistributorEnterprise distributor = (ProductDistributorEnterprise) ent;

                    // Find WholesaleSalesOrganization
                    for (Organization org : distributor.getOrganizationDirectory().getOrganizationList()) {
                        if (org instanceof WholesaleSalesOrganization) {
                            return org;
                        }
                    }
                    // Fallback to first organization
                    if (!distributor.getOrganizationDirectory().getOrganizationList().isEmpty()) {
                        return distributor.getOrganizationDirectory().getOrganizationList().get(0);
                    }
                }
            }
        }
        return null;
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
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        lblEnterprise = new javax.swing.JLabel();
        txtEnterpriseName = new javax.swing.JTextField();
        lblOrganization = new javax.swing.JLabel();
        txtOrganizationName = new javax.swing.JTextField();
        lblUser = new javax.swing.JLabel();
        txtUserName = new javax.swing.JTextField();
        lblStoreSummary = new javax.swing.JLabel();
        lblTotalProducts = new javax.swing.JLabel();
        txtTotalProducts = new javax.swing.JTextField();
        lblLowStock = new javax.swing.JLabel();
        txtLowStock = new javax.swing.JTextField();
        lblOutOfStock = new javax.swing.JLabel();
        txtOutOfStock = new javax.swing.JTextField();
        lblTotalStaff = new javax.swing.JLabel();
        txtTotalStaff = new javax.swing.JTextField();
        lblPendingOrders = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPendingOrders = new javax.swing.JTable();
        btnViewInventory = new javax.swing.JButton();
        btnApprove = new javax.swing.JButton();
        btnReject = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();

        lblTitle.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        lblTitle.setText("Store Manager Work Area");

        lblEnterprise.setText("Enterprise:");

        txtEnterpriseName.setEditable(false);

        lblOrganization.setText("Organization:");

        txtOrganizationName.setEditable(false);

        lblUser.setText("User:");

        txtUserName.setEditable(false);

        lblStoreSummary.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        lblStoreSummary.setText("Store Summary:");

        lblTotalProducts.setText("Total Products:");

        txtTotalProducts.setEditable(false);
        txtTotalProducts.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        lblLowStock.setText("Low Stock:");

        txtLowStock.setEditable(false);
        txtLowStock.setBackground(new java.awt.Color(255, 255, 204));
        txtLowStock.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        lblOutOfStock.setText("Out of Stock:");

        txtOutOfStock.setEditable(false);
        txtOutOfStock.setBackground(new java.awt.Color(255, 204, 204));
        txtOutOfStock.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        lblTotalStaff.setText("Total Staff:");

        txtTotalStaff.setEditable(false);
        txtTotalStaff.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        lblPendingOrders.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        lblPendingOrders.setText("Pending Orders for Approval:");

        tblPendingOrders.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Order ID", "Product", "Quantity", "Total Price", "Date", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblPendingOrders);

        btnViewInventory.setText("View Store Inventory");
        btnViewInventory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewInventoryActionPerformed(evt);
            }
        });

        btnApprove.setBackground(new java.awt.Color(204, 255, 204));
        btnApprove.setText("Approve");
        btnApprove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApproveActionPerformed(evt);
            }
        });

        btnReject.setBackground(new java.awt.Color(255, 204, 204));
        btnReject.setText("Reject");
        btnReject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRejectActionPerformed(evt);
            }
        });

        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addComponent(jSeparator2)
                    .addComponent(jSeparator3)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 750, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblStoreSummary)
                            .addComponent(lblPendingOrders)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblEnterprise)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtEnterpriseName, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(20, 20, 20)
                                        .addComponent(lblOrganization)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtOrganizationName, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(20, 20, 20)
                                        .addComponent(lblUser)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtUserName, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblTotalProducts)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtTotalProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(20, 20, 20)
                                        .addComponent(lblLowStock)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtLowStock, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(20, 20, 20)
                                        .addComponent(lblOutOfStock)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtOutOfStock, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(20, 20, 20)
                                        .addComponent(lblTotalStaff)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtTotalStaff, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnViewInventory, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnApprove, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnReject, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEnterprise)
                    .addComponent(txtEnterpriseName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblOrganization)
                    .addComponent(txtOrganizationName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUser)
                    .addComponent(txtUserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStoreSummary)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalProducts)
                    .addComponent(txtTotalProducts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLowStock)
                    .addComponent(txtLowStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblOutOfStock)
                    .addComponent(txtOutOfStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotalStaff)
                    .addComponent(txtTotalStaff, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblPendingOrders)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnViewInventory)
                    .addComponent(btnApprove)
                    .addComponent(btnReject)
                    .addComponent(btnRefresh))
                .addContainerGap(100, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnViewInventoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewInventoryActionPerformed
        ViewStoreInventoryPanel panel = new ViewStoreInventoryPanel(userProcessContainer, userAccount, organization, enterprise, system);
        userProcessContainer.add("ViewStoreInventoryPanel", panel);
        CardLayout layout = (CardLayout) userProcessContainer.getLayout();
        layout.next(userProcessContainer);
    }//GEN-LAST:event_btnViewInventoryActionPerformed

    private void btnApproveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApproveActionPerformed
        RetailPurchaseOrderRequest order = getSelectedOrder();
        if (order == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select an order from the table.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Approve and send order " + order.getRequestId() + " to " +
                order.getTargetDistributorName() + "?",
                "Confirm Approval", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // Find the target distributor and send the order
            Organization distributorOrg = findDistributorReceivingOrg(order.getTargetDistributorName());

            if (distributorOrg != null) {
                // Remove from local work queue
                organization.getWorkQueue().getWorkRequestList().remove(order);

                // Update status and send to distributor
                order.setStatus("Sent");
                order.setReceiver(userAccount);
                distributorOrg.getWorkQueue().getWorkRequestList().add(order);

                populatePendingOrdersTable();
                JOptionPane.showMessageDialog(this,
                        "Order approved and sent to " + order.getTargetDistributorName() + "!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Error: Could not find distributor " + order.getTargetDistributorName(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnApproveActionPerformed

    private void btnRejectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRejectActionPerformed
        RetailPurchaseOrderRequest order = getSelectedOrder();
        if (order == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select an order from the table.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String reason = JOptionPane.showInputDialog(this,
                "Enter rejection reason:",
                "Reject Order", JOptionPane.QUESTION_MESSAGE);

        if (reason != null && !reason.trim().isEmpty()) {
            order.setStatus("Rejected");
            order.setMessage("Rejected: " + reason);
            order.setReceiver(userAccount);
            populatePendingOrdersTable();
            JOptionPane.showMessageDialog(this,
                    "Order rejected.",
                    "Rejected", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnRejectActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        updateSummary();
        populatePendingOrdersTable();
    }//GEN-LAST:event_btnRefreshActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApprove;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnReject;
    private javax.swing.JButton btnViewInventory;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JLabel lblEnterprise;
    private javax.swing.JLabel lblLowStock;
    private javax.swing.JLabel lblOrganization;
    private javax.swing.JLabel lblOutOfStock;
    private javax.swing.JLabel lblPendingOrders;
    private javax.swing.JLabel lblStoreSummary;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTotalProducts;
    private javax.swing.JLabel lblTotalStaff;
    private javax.swing.JLabel lblUser;
    private javax.swing.JTable tblPendingOrders;
    private javax.swing.JTextField txtEnterpriseName;
    private javax.swing.JTextField txtLowStock;
    private javax.swing.JTextField txtOrganizationName;
    private javax.swing.JTextField txtOutOfStock;
    private javax.swing.JTextField txtTotalProducts;
    private javax.swing.JTextField txtTotalStaff;
    private javax.swing.JTextField txtUserName;
    // End of variables declaration//GEN-END:variables
}
