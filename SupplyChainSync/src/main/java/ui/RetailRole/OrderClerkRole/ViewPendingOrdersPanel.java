/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui.RetailRole.OrderClerkRole;

import Business.EcoSystem;
import Business.Enterprise.Enterprise;
import Business.Enterprise.ProductDistributorEnterprise;
import Business.Network.Network;
import Business.Organization.Organization;
import Business.Organization.Retail.StoreManagementOrganization;
import Business.UserAccount.UserAccount;
import Business.WorkQueue.RetailPurchaseOrderRequest;
import Business.WorkQueue.WorkRequest;
import java.awt.CardLayout;
import java.awt.Component;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author zhifei
 */
public class ViewPendingOrdersPanel extends javax.swing.JPanel {

    private JPanel userProcessContainer;
    private UserAccount userAccount;
    private StoreManagementOrganization organization;
    private Enterprise enterprise;
    private EcoSystem system;

    /**
     * Creates new form ViewPendingOrdersPanel
     */
    public ViewPendingOrdersPanel(JPanel userProcessContainer, UserAccount account, StoreManagementOrganization storeManagementOrganization, Enterprise enterprise, EcoSystem system) {
        initComponents();
        this.userProcessContainer = userProcessContainer;
        this.userAccount = account;
        this.organization = storeManagementOrganization;
        this.enterprise = enterprise;
        this.system = system;

        populatePendingOrdersTable();
    }

    private void populatePendingOrdersTable() {
        DefaultTableModel model = (DefaultTableModel) tblPendingOrders.getModel();
        model.setRowCount(0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (Network network : system.getNetworkList()) {
            for (Enterprise ent : network.getEnterpriseDirectory().getEnterpriseList()) {
                if (ent instanceof ProductDistributorEnterprise) {
                    ProductDistributorEnterprise distributor = (ProductDistributorEnterprise) ent;

                    for (Organization org : distributor.getOrganizationDirectory().getOrganizationList()) {
                        for (WorkRequest request : org.getWorkQueue().getWorkRequestList()) {
                            if (request instanceof RetailPurchaseOrderRequest) {
                                RetailPurchaseOrderRequest po = (RetailPurchaseOrderRequest) request;

                                if (po.getSender() != null &&
                                    po.getSender().getUsername().equals(userAccount.getUsername())) {

                                    String status = po.getStatus();
                                    if ("Pending".equalsIgnoreCase(status) ||
                                        "Sent".equalsIgnoreCase(status) ||
                                        "Approved".equalsIgnoreCase(status) ||
                                        "Processing".equalsIgnoreCase(status) ||
                                        "In Transit".equalsIgnoreCase(status)) {

                                        Object[] row = new Object[8];
                                        row[0] = "RO-" + String.format("%04d", po.getRequestId());
                                        row[1] = distributor.getName();
                                        row[2] = po.getProductName() != null ? po.getProductName() : "N/A";
                                        row[3] = po.getQuantity();
                                        row[4] = po.getUrgencyLevel() != null ? po.getUrgencyLevel() : "Normal";
                                        row[5] = po.getRequestDate() != null ?
                                            dateFormat.format(po.getRequestDate()) : "N/A";
                                        row[6] = status;
                                        row[7] = String.format("$%.2f", po.getTotalPrice());

                                        model.addRow(row);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        updateSummary();
    }

    private void updateSummary() {
        int pendingCount = 0;
        int approvedCount = 0;
        int inTransitCount = 0;

        for (Network network : system.getNetworkList()) {
            for (Enterprise ent : network.getEnterpriseDirectory().getEnterpriseList()) {
                if (ent instanceof ProductDistributorEnterprise) {
                    ProductDistributorEnterprise distributor = (ProductDistributorEnterprise) ent;

                    for (Organization org : distributor.getOrganizationDirectory().getOrganizationList()) {
                        for (WorkRequest request : org.getWorkQueue().getWorkRequestList()) {
                            if (request instanceof RetailPurchaseOrderRequest) {
                                RetailPurchaseOrderRequest po = (RetailPurchaseOrderRequest) request;

                                if (po.getSender() != null &&
                                    po.getSender().getUsername().equals(userAccount.getUsername())) {

                                    String status = po.getStatus();
                                    if ("Pending".equalsIgnoreCase(status) || "Sent".equalsIgnoreCase(status)) {
                                        pendingCount++;
                                    } else if ("Approved".equalsIgnoreCase(status) || "Processing".equalsIgnoreCase(status)) {
                                        approvedCount++;
                                    } else if ("In Transit".equalsIgnoreCase(status)) {
                                        inTransitCount++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        txtPendingCount.setText(String.valueOf(pendingCount));
        txtApprovedCount.setText(String.valueOf(approvedCount));
        txtInTransitCount.setText(String.valueOf(inTransitCount));
    }

    private RetailPurchaseOrderRequest getSelectedOrder() {
        int selectedRow = tblPendingOrders.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }

        String orderId = (String) tblPendingOrders.getValueAt(selectedRow, 0);
        int requestId = Integer.parseInt(orderId.replace("RO-", ""));

        for (Network network : system.getNetworkList()) {
            for (Enterprise ent : network.getEnterpriseDirectory().getEnterpriseList()) {
                if (ent instanceof ProductDistributorEnterprise) {
                    ProductDistributorEnterprise distributor = (ProductDistributorEnterprise) ent;

                    for (Organization org : distributor.getOrganizationDirectory().getOrganizationList()) {
                        for (WorkRequest request : org.getWorkQueue().getWorkRequestList()) {
                            if (request instanceof RetailPurchaseOrderRequest) {
                                RetailPurchaseOrderRequest po = (RetailPurchaseOrderRequest) request;
                                if (po.getRequestId() == requestId) {
                                    return po;
                                }
                            }
                        }
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
        btnBack = new javax.swing.JButton();
        lblSummary = new javax.swing.JLabel();
        lblPending = new javax.swing.JLabel();
        lblApproved = new javax.swing.JLabel();
        lblInTransit = new javax.swing.JLabel();
        txtPendingCount = new javax.swing.JTextField();
        txtApprovedCount = new javax.swing.JTextField();
        txtInTransitCount = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        lblPendingOrders = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPendingOrders = new javax.swing.JTable();
        btnRefresh = new javax.swing.JButton();
        btnViewDetails = new javax.swing.JButton();
        btnCancelOrder = new javax.swing.JButton();

        lblTitle.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        lblTitle.setText("View Pending Orders");

        btnBack.setText("<< Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        lblSummary.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        lblSummary.setText("Order Summary:");

        lblPending.setText("Awaiting Approval:");

        lblApproved.setText("Approved/Processing:");

        lblInTransit.setText("In Transit:");

        txtPendingCount.setEditable(false);
        txtPendingCount.setBackground(new java.awt.Color(255, 255, 204));
        txtPendingCount.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        txtPendingCount.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        txtApprovedCount.setEditable(false);
        txtApprovedCount.setBackground(new java.awt.Color(204, 255, 204));
        txtApprovedCount.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        txtApprovedCount.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        txtInTransitCount.setEditable(false);
        txtInTransitCount.setBackground(new java.awt.Color(204, 229, 255));
        txtInTransitCount.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        txtInTransitCount.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        lblPendingOrders.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        lblPendingOrders.setText("Active Orders:");

        tblPendingOrders.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Order ID", "Distributor", "Product", "Quantity", "Priority", "Order Date", "Status", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblPendingOrders);

        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        btnViewDetails.setText("View Details");
        btnViewDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewDetailsActionPerformed(evt);
            }
        });

        btnCancelOrder.setText("Cancel Order");
        btnCancelOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelOrderActionPerformed(evt);
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
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBack)
                            .addComponent(lblSummary)
                            .addComponent(lblPendingOrders)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblPending)
                                    .addComponent(lblApproved)
                                    .addComponent(lblInTransit))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtPendingCount, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                                    .addComponent(txtApprovedCount)
                                    .addComponent(txtInTransitCount))))
                        .addGap(0, 300, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnViewDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancelOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBack)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblSummary)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPending)
                    .addComponent(txtPendingCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblApproved)
                    .addComponent(txtApprovedCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblInTransit)
                    .addComponent(txtInTransitCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblPendingOrders)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRefresh)
                    .addComponent(btnViewDetails)
                    .addComponent(btnCancelOrder))
                .addContainerGap(100, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        userProcessContainer.remove(this);

        Component[] components = userProcessContainer.getComponents();
        if (components.length > 0) {
            Component lastComponent = components[components.length - 1];
            if (lastComponent instanceof OrderClerkWorkAreaJPanel) {
                ((OrderClerkWorkAreaJPanel) lastComponent).updateSummary();
            }
        }

        CardLayout layout = (CardLayout) userProcessContainer.getLayout();
        layout.previous(userProcessContainer);
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        populatePendingOrdersTable();
        JOptionPane.showMessageDialog(this, "Orders refreshed.", "Refresh", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void btnViewDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewDetailsActionPerformed
        RetailPurchaseOrderRequest order = getSelectedOrder();
        if (order == null) {
            JOptionPane.showMessageDialog(this, "Please select an order to view details.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        StringBuilder details = new StringBuilder();
        details.append("Order ID: RO-").append(String.format("%04d", order.getRequestId())).append("\n");
        details.append("Status: ").append(order.getStatus()).append("\n\n");

        details.append("--- Product Details ---\n");
        details.append("Product: ").append(order.getProductName() != null ? order.getProductName() : "N/A").append("\n");
        details.append("Quantity: ").append(order.getQuantity()).append("\n");
        if (order.getProduct() != null && order.getProduct().getUnitPrice() > 0) {
            details.append("Unit Price: $").append(String.format("%.2f", order.getProduct().getUnitPrice())).append("\n");
        }
        details.append("Total Price: $").append(String.format("%.2f", order.getTotalPrice())).append("\n\n");

        details.append("--- Order Information ---\n");
        details.append("Priority: ").append(order.getUrgencyLevel() != null ? order.getUrgencyLevel() : "Normal").append("\n");
        details.append("Order Date: ").append(order.getRequestDate() != null ?
            dateFormat.format(order.getRequestDate()) : "N/A").append("\n");
        details.append("Store: ").append(order.getStoreName() != null ? order.getStoreName() : enterprise.getName()).append("\n");

        if (order.getMessage() != null && !order.getMessage().isEmpty()) {
            details.append("\n--- Message ---\n");
            details.append(order.getMessage()).append("\n");
        }

        if (order.getResolveDate() != null) {
            details.append("\n--- Processing Information ---\n");
            details.append("Processed Date: ").append(dateFormat.format(order.getResolveDate())).append("\n");
            if (order.getReceiver() != null) {
                details.append("Processed By: ").append(order.getReceiver().getUsername()).append("\n");
            }
        }

        JOptionPane.showMessageDialog(this, details.toString(), "Order Details", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnViewDetailsActionPerformed

    private void btnCancelOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelOrderActionPerformed
        RetailPurchaseOrderRequest order = getSelectedOrder();
        if (order == null) {
            JOptionPane.showMessageDialog(this, "Please select an order to cancel.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String status = order.getStatus();
        if (!"Pending".equalsIgnoreCase(status) && !"Sent".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this,
                "Only pending orders can be cancelled.\nThis order is already: " + status,
                "Cannot Cancel", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel this order?\n" +
            "Order ID: RO-" + String.format("%04d", order.getRequestId()) + "\n" +
            "Product: " + order.getProductName(),
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            order.setStatus("Cancelled");

            populatePendingOrdersTable();

            JOptionPane.showMessageDialog(this, "Order cancelled successfully.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnCancelOrderActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnCancelOrder;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnViewDetails;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblApproved;
    private javax.swing.JLabel lblInTransit;
    private javax.swing.JLabel lblPending;
    private javax.swing.JLabel lblPendingOrders;
    private javax.swing.JLabel lblSummary;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTable tblPendingOrders;
    private javax.swing.JTextField txtApprovedCount;
    private javax.swing.JTextField txtInTransitCount;
    private javax.swing.JTextField txtPendingCount;
    // End of variables declaration//GEN-END:variables
}
