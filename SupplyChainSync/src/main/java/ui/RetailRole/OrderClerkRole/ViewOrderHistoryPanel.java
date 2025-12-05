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
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.RowFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author zhifei
 */
public class ViewOrderHistoryPanel extends javax.swing.JPanel {

    private JPanel userProcessContainer;
    private UserAccount userAccount;
    private StoreManagementOrganization organization;
    private Enterprise enterprise;
    private EcoSystem system;
    private TableRowSorter<DefaultTableModel> sorter;

    /**
     * Creates new form ViewOrderHistoryPanel
     */
    public ViewOrderHistoryPanel(JPanel userProcessContainer, UserAccount account, StoreManagementOrganization storeManagementOrganization, Enterprise enterprise, EcoSystem system) {
        initComponents();
        this.userProcessContainer = userProcessContainer;
        this.userAccount = account;
        this.organization = storeManagementOrganization;
        this.enterprise = enterprise;
        this.system = system;

        initializeFilter();
        populateOrderHistoryTable();
    }

    private void initializeFilter() {
        cmbStatusFilter.removeAllItems();
        cmbStatusFilter.addItem("All");
        cmbStatusFilter.addItem("Delivered");
        cmbStatusFilter.addItem("Completed");
        cmbStatusFilter.addItem("Cancelled");
        cmbStatusFilter.addItem("Rejected");
    }

    private void populateOrderHistoryTable() {
        DefaultTableModel model = (DefaultTableModel) tblOrderHistory.getModel();
        model.setRowCount(0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String filterStatus = (String) cmbStatusFilter.getSelectedItem();

        int deliveredCount = 0;
        int cancelledCount = 0;
        int rejectedCount = 0;
        double totalSpent = 0;

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

                                    // Count statistics
                                    if ("Delivered".equalsIgnoreCase(status) || "Completed".equalsIgnoreCase(status)) {
                                        deliveredCount++;
                                        totalSpent += po.getTotalPrice();
                                    } else if ("Cancelled".equalsIgnoreCase(status)) {
                                        cancelledCount++;
                                    } else if ("Rejected".equalsIgnoreCase(status)) {
                                        rejectedCount++;
                                    }

                                    // Filter by status
                                    boolean shouldShow = false;
                                    if ("All".equals(filterStatus)) {
                                        shouldShow = "Delivered".equalsIgnoreCase(status) ||
                                                    "Completed".equalsIgnoreCase(status) ||
                                                    "Cancelled".equalsIgnoreCase(status) ||
                                                    "Rejected".equalsIgnoreCase(status);
                                    } else {
                                        shouldShow = filterStatus.equalsIgnoreCase(status) ||
                                                    (filterStatus.equals("Delivered") && "Completed".equalsIgnoreCase(status));
                                    }

                                    if (shouldShow) {
                                        Object[] row = new Object[8];
                                        row[0] = "RO-" + String.format("%04d", po.getRequestId());
                                        row[1] = distributor.getName();
                                        row[2] = po.getProductName() != null ? po.getProductName() : "N/A";
                                        row[3] = po.getQuantity();
                                        row[4] = po.getRequestDate() != null ?
                                            dateFormat.format(po.getRequestDate()) : "N/A";
                                        row[5] = po.getResolveDate() != null ?
                                            dateFormat.format(po.getResolveDate()) : "N/A";
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

        // Update summary
        txtDeliveredCount.setText(String.valueOf(deliveredCount));
        txtCancelledCount.setText(String.valueOf(cancelledCount));
        txtRejectedCount.setText(String.valueOf(rejectedCount));
        txtTotalSpent.setText(String.format("$%.2f", totalSpent));
    }

    private RetailPurchaseOrderRequest getSelectedOrder() {
        int selectedRow = tblOrderHistory.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }

        String orderId = (String) tblOrderHistory.getValueAt(selectedRow, 0);
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
        lblDelivered = new javax.swing.JLabel();
        lblCancelled = new javax.swing.JLabel();
        lblRejected = new javax.swing.JLabel();
        lblTotalSpent = new javax.swing.JLabel();
        txtDeliveredCount = new javax.swing.JTextField();
        txtCancelledCount = new javax.swing.JTextField();
        txtRejectedCount = new javax.swing.JTextField();
        txtTotalSpent = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        lblOrderHistory = new javax.swing.JLabel();
        lblFilter = new javax.swing.JLabel();
        cmbStatusFilter = new javax.swing.JComboBox<>();
        btnApplyFilter = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOrderHistory = new javax.swing.JTable();
        btnViewDetails = new javax.swing.JButton();
        btnReorder = new javax.swing.JButton();

        lblTitle.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        lblTitle.setText("Order History");

        btnBack.setText("<< Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        lblSummary.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        lblSummary.setText("Historical Summary:");

        lblDelivered.setText("Delivered Orders:");

        lblCancelled.setText("Cancelled Orders:");

        lblRejected.setText("Rejected Orders:");

        lblTotalSpent.setText("Total Spent:");

        txtDeliveredCount.setEditable(false);
        txtDeliveredCount.setBackground(new java.awt.Color(204, 255, 204));
        txtDeliveredCount.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        txtDeliveredCount.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        txtCancelledCount.setEditable(false);
        txtCancelledCount.setBackground(new java.awt.Color(255, 255, 204));
        txtCancelledCount.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        txtCancelledCount.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        txtRejectedCount.setEditable(false);
        txtRejectedCount.setBackground(new java.awt.Color(255, 204, 204));
        txtRejectedCount.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        txtRejectedCount.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        txtTotalSpent.setEditable(false);
        txtTotalSpent.setBackground(new java.awt.Color(240, 240, 240));
        txtTotalSpent.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        txtTotalSpent.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        lblOrderHistory.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        lblOrderHistory.setText("Order History:");

        lblFilter.setText("Filter by Status:");

        btnApplyFilter.setText("Apply");
        btnApplyFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyFilterActionPerformed(evt);
            }
        });

        tblOrderHistory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Order ID", "Distributor", "Product", "Quantity", "Order Date", "Complete Date", "Status", "Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblOrderHistory);

        btnViewDetails.setText("View Details");
        btnViewDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewDetailsActionPerformed(evt);
            }
        });

        btnReorder.setText("Reorder");
        btnReorder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReorderActionPerformed(evt);
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
                            .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBack)
                            .addComponent(lblSummary)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblDelivered)
                                    .addComponent(lblCancelled)
                                    .addComponent(lblRejected)
                                    .addComponent(lblTotalSpent))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtDeliveredCount, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                    .addComponent(txtCancelledCount)
                                    .addComponent(txtRejectedCount)
                                    .addComponent(txtTotalSpent))))
                        .addGap(0, 300, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblOrderHistory)
                        .addGap(50, 50, 50)
                        .addComponent(lblFilter)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmbStatusFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnApplyFilter, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnViewDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnReorder, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(lblDelivered)
                    .addComponent(txtDeliveredCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCancelled)
                    .addComponent(txtCancelledCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRejected)
                    .addComponent(txtRejectedCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalSpent)
                    .addComponent(txtTotalSpent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblOrderHistory)
                    .addComponent(lblFilter)
                    .addComponent(cmbStatusFilter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnApplyFilter))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnViewDetails)
                    .addComponent(btnReorder))
                .addContainerGap(80, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        userProcessContainer.remove(this);
        CardLayout layout = (CardLayout) userProcessContainer.getLayout();
        layout.previous(userProcessContainer);
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnApplyFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyFilterActionPerformed
        populateOrderHistoryTable();
    }//GEN-LAST:event_btnApplyFilterActionPerformed

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

        details.append("--- Order Timeline ---\n");
        details.append("Order Date: ").append(order.getRequestDate() != null ?
            dateFormat.format(order.getRequestDate()) : "N/A").append("\n");
        details.append("Completion Date: ").append(order.getResolveDate() != null ?
            dateFormat.format(order.getResolveDate()) : "N/A").append("\n");

        if (order.getReceiver() != null) {
            details.append("Processed By: ").append(order.getReceiver().getUsername()).append("\n");
        }

        if (order.getMessage() != null && !order.getMessage().isEmpty()) {
            details.append("\n--- Original Message ---\n");
            details.append(order.getMessage()).append("\n");
        }

        JOptionPane.showMessageDialog(this, details.toString(), "Order Details", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnViewDetailsActionPerformed

    private void btnReorderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReorderActionPerformed
        RetailPurchaseOrderRequest order = getSelectedOrder();
        if (order == null) {
            JOptionPane.showMessageDialog(this, "Please select an order to reorder.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Create a new order based on this order?\n\n" +
            "Product: " + order.getProductName() + "\n" +
            "Quantity: " + order.getQuantity() + "\n" +
            "Total: $" + String.format("%.2f", order.getTotalPrice()),
            "Confirm Reorder",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Navigate to create order panel
            CreateRetailPurchaseOrderPanel panel = new CreateRetailPurchaseOrderPanel(
                userProcessContainer, userAccount, organization, enterprise, system);
            userProcessContainer.add("CreateRetailPurchaseOrderPanel", panel);
            CardLayout layout = (CardLayout) userProcessContainer.getLayout();
            layout.next(userProcessContainer);

            JOptionPane.showMessageDialog(this,
                "Please complete the order in the Create Purchase Order panel.\n" +
                "Previous order details:\n" +
                "Product: " + order.getProductName() + "\n" +
                "Quantity: " + order.getQuantity(),
                "Reorder",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnReorderActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApplyFilter;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnReorder;
    private javax.swing.JButton btnViewDetails;
    private javax.swing.JComboBox<String> cmbStatusFilter;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblCancelled;
    private javax.swing.JLabel lblDelivered;
    private javax.swing.JLabel lblFilter;
    private javax.swing.JLabel lblOrderHistory;
    private javax.swing.JLabel lblRejected;
    private javax.swing.JLabel lblSummary;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTotalSpent;
    private javax.swing.JTable tblOrderHistory;
    private javax.swing.JTextField txtCancelledCount;
    private javax.swing.JTextField txtDeliveredCount;
    private javax.swing.JTextField txtRejectedCount;
    private javax.swing.JTextField txtTotalSpent;
    // End of variables declaration//GEN-END:variables
}
