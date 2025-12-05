/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui.DistributorRole.WholesaleSalesRole;

import Business.EcoSystem;
import Business.Enterprise.Enterprise;
import Business.Enterprise.ProductDistributorEnterprise;
import Business.Inventory.Inventory;
import Business.Inventory.InventoryItem;
import Business.Organization.Distributor.WholesaleInventoryOrganization;
import Business.Organization.Distributor.WholesaleSalesOrganization;
import Business.Organization.Organization;
import Business.UserAccount.UserAccount;
import Business.WorkQueue.RetailPurchaseOrderRequest;
import Business.WorkQueue.WorkRequest;
import java.awt.CardLayout;
import java.awt.Component;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author chris
 */
public class ViewRetailOrdersPanel extends javax.swing.JPanel {

    private JPanel userProcessContainer;
    private UserAccount userAccount;
    private WholesaleSalesOrganization organization;
    private Enterprise enterprise;
    private EcoSystem system;
    private Inventory inventory;
    
    public ViewRetailOrdersPanel(JPanel userProcessContainer, UserAccount account, 
            WholesaleSalesOrganization wholesaleSalesOrganization, Enterprise enterprise, EcoSystem system) {
        initComponents();
        
        this.userProcessContainer = userProcessContainer;
        this.userAccount = account;
        this.organization = wholesaleSalesOrganization;
        this.enterprise = enterprise;
        this.system = system;
        
        // get inventory information
        this.inventory = getDistributorInventory();
        
        setFieldsEditable(false);
        
        addTableSelectionListener();
        
        populateRequestsTable();
        populateInventoryTable();
    }
    
    private Inventory getDistributorInventory() {
        if (enterprise instanceof ProductDistributorEnterprise) {
            ProductDistributorEnterprise distEnterprise = (ProductDistributorEnterprise) enterprise;
            return distEnterprise.getInventory();
        }
        return new Inventory();
    }
    
    private void setFieldsEditable(boolean editable) {
        fieldId.setEditable(editable);
        fieldRetailer.setEditable(editable);
        fieldProduct.setEditable(editable);
        fieldQuantity.setEditable(editable);
        fieldDate.setEditable(editable);
        fieldMessage.setEditable(editable);
    }
    
    private void addTableSelectionListener() {
        tblRequests.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    displaySelectedRequestDetails();
                }
            }
        });
    }
    

    private void populateRequestsTable() {
        DefaultTableModel model = (DefaultTableModel) tblRequests.getModel();
        model.setRowCount(0);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        for (WorkRequest request : organization.getWorkQueue().getWorkRequestList()) {
            if (request instanceof RetailPurchaseOrderRequest) {
                RetailPurchaseOrderRequest retailRequest = (RetailPurchaseOrderRequest) request;
                
                String status = retailRequest.getStatus();
                if ("Pending".equalsIgnoreCase(status) || "Sent".equalsIgnoreCase(status)) {
                    Object[] row = new Object[6];
                    row[0] = retailRequest.getRequestId();
                    row[1] = retailRequest.getStoreName() != null ? 
                             retailRequest.getStoreName() : "Unknown";
                    row[2] = retailRequest.getProductName() != null ? 
                             retailRequest.getProductName() : "N/A";
                    row[3] = retailRequest.getQuantity();
                    row[4] = retailRequest.getRequestDate() != null ? 
                             sdf.format(retailRequest.getRequestDate()) : "N/A";
                    row[5] = status;
                    
                    model.addRow(row);
                }
            }
        }
    }
    
    
    private void populateInventoryTable() {
        DefaultTableModel model = (DefaultTableModel) tblProductInventory.getModel();
        model.setRowCount(0);
        
        if (inventory == null) return;
        
        for (InventoryItem item : inventory.getProductInventory()) {
            if (item.getProduct() != null) {
                Object[] row = new Object[6];
                row[0] = item.getProduct().getProductCode();
                row[1] = item.getProduct().getProductName();
                row[2] = item.getQuantity();
                row[3] = item.getReservedQuantity();
                row[4] = item.getAvailableQuantity();
                row[5] = getStockStatus(item);
                
                model.addRow(row);
            }
        }
    }
    

    private void populateInventoryTableWithSearch(String searchKeyword) {
        DefaultTableModel model = (DefaultTableModel) tblProductInventory.getModel();
        model.setRowCount(0);
        
        if (inventory == null) return;
        
        String keyword = searchKeyword.toLowerCase().trim();
        
        for (InventoryItem item : inventory.getProductInventory()) {
            if (item.getProduct() != null) {
                String code = item.getProduct().getProductCode().toLowerCase();
                String name = item.getProduct().getProductName().toLowerCase();
                
                if (code.contains(keyword) || name.contains(keyword)) {
                    Object[] row = new Object[6];
                    row[0] = item.getProduct().getProductCode();
                    row[1] = item.getProduct().getProductName();
                    row[2] = item.getQuantity();
                    row[3] = item.getReservedQuantity();
                    row[4] = item.getAvailableQuantity();
                    row[5] = getStockStatus(item);
                    
                    model.addRow(row);
                }
            }
        }
    }
    

    private String getStockStatus(InventoryItem item) {
        int available = item.getAvailableQuantity();
        int minLevel = item.getMinStockLevel();
        
        if (available <= 0) {
            return "🔴 Out of Stock";
        } else if (minLevel > 0 && available <= minLevel) {
            return "🔴 Critical";
        } else if (minLevel > 0 && available <= minLevel * 2) {
            return "⚠️ Low";
        } else {
            return "✅ OK";
        }
    }
    

    private void displaySelectedRequestDetails() {
        int selectedRow = tblRequests.getSelectedRow();
        
        if (selectedRow < 0) {
            clearDetailFields();
            return;
        }
        
        String requestId = tblRequests.getValueAt(selectedRow, 0).toString();
        RetailPurchaseOrderRequest selectedRequest = findRequestById(requestId);
        
        if (selectedRequest != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            
            fieldId.setText(String.valueOf(selectedRequest.getRequestId()));
            fieldRetailer.setText(selectedRequest.getStoreName() != null ? 
                                  selectedRequest.getStoreName() : "Unknown");
            fieldProduct.setText(selectedRequest.getProductName() != null ? 
                                 selectedRequest.getProductName() : "N/A");
            fieldQuantity.setText(String.valueOf(selectedRequest.getQuantity()));
            fieldDate.setText(selectedRequest.getRequestDate() != null ? 
                              sdf.format(selectedRequest.getRequestDate()) : "N/A");
            fieldMessage.setText(selectedRequest.getMessage() != null ? 
                                 selectedRequest.getMessage() : "");
        }
    }
    
    private RetailPurchaseOrderRequest findRequestById(String requestId) {
        for (WorkRequest request : organization.getWorkQueue().getWorkRequestList()) {
            if (request instanceof RetailPurchaseOrderRequest) {
                RetailPurchaseOrderRequest retailRequest = (RetailPurchaseOrderRequest) request;
                if (String.valueOf(retailRequest.getRequestId()).equals(requestId)) {
                    return retailRequest;
                }
            }
        }
        return null;
    }
    
    private void clearDetailFields() {
        fieldId.setText("");
        fieldRetailer.setText("");
        fieldProduct.setText("");
        fieldQuantity.setText("");
        fieldDate.setText("");
        fieldMessage.setText("");
    }
    
    private RetailPurchaseOrderRequest getSelectedRequest() {
        int selectedRow = tblRequests.getSelectedRow();
        if (selectedRow < 0) return null;
        String requestId = tblRequests.getValueAt(selectedRow, 0).toString();
        return findRequestById(requestId);
    }
    
    private boolean checkInventoryAvailability(RetailPurchaseOrderRequest request) {
        if (request.getProductCode() == null || inventory == null) {
            return false;
        }
        InventoryItem item = inventory.findByProductCode(request.getProductCode());
        if (item == null) return false;
        return item.getAvailableQuantity() >= request.getQuantity();
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
        btnRefresh = new javax.swing.JButton();
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
        jScrollPane2 = new javax.swing.JScrollPane();
        tblProductInventory = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        fieldSearchProduct = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();

        lblTitle.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        lblTitle.setText("📥 View Retail Orders");

        btnBack.setText("<< Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        btnRefresh.setText("🔄 Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
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

        btnApprove.setText("✅ Approve Request");
        btnApprove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApproveActionPerformed(evt);
            }
        });

        btnReject.setText("❌ Reject Request");
        btnReject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRejectActionPerformed(evt);
            }
        });

        tblProductInventory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product ID", "Product Name", "In Stock", "Reserved", "Available", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblProductInventory);

        jLabel1.setText("Current Inventory");

        jLabel2.setText("Search Product:");

        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnReset.setText("Reset");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
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
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblTableTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 759, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnApprove, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(62, 62, 62)
                                .addComponent(btnReject, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(105, 105, 105))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnBack)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnRefresh))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 759, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 192, Short.MAX_VALUE)
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(fieldSearchProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                                            .addComponent(fieldDate, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblRetailer, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(lblQuantity, javax.swing.GroupLayout.Alignment.TRAILING))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(fieldRetailer)
                                            .addComponent(fieldQuantity, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)))
                                    .addComponent(fieldMessage))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnReset, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(0, 18, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnRefresh, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnBack))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(fieldSearchProduct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch)
                    .addComponent(btnReset))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnApprove)
                    .addComponent(btnReject))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        populateRequestsTable();
        populateInventoryTable();
        clearDetailFields();
        fieldSearchProduct.setText("");
    }//GEN-LAST:event_btnRefreshActionPerformed

    private void fieldIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fieldIdActionPerformed

    private void btnApproveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApproveActionPerformed
        // TODO add your handling code here:
        RetailPurchaseOrderRequest selectedRequest = getSelectedRequest();

        if (selectedRequest == null) {
            JOptionPane.showMessageDialog(this, "Please select a request to approve.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        InventoryItem item = null;
        if (inventory != null && selectedRequest.getProductCode() != null) {
            item = inventory.findByProductCode(selectedRequest.getProductCode());
        }

        if (item == null) {
            JOptionPane.showMessageDialog(this, 
                "Warning: Product not found in inventory!", 
                "Product Not Found", JOptionPane.WARNING_MESSAGE);
        } else if (item.getAvailableQuantity() < selectedRequest.getQuantity()) {
            int choice = JOptionPane.showConfirmDialog(this,
                "Warning: Insufficient inventory!\n" +
                "Available: " + item.getAvailableQuantity() + "\n" +
                "Requested: " + selectedRequest.getQuantity() + "\n" +
                "Do you still want to approve?",
                "Insufficient Inventory", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice != JOptionPane.YES_OPTION) return;
        }

        selectedRequest.setStatus("Approved");
        selectedRequest.setReceiver(userAccount);

        if (item != null) {
            item.reserveStock(selectedRequest.getQuantity());
        }

        JOptionPane.showMessageDialog(this, 
            "Request approved successfully!\nPlease proceed to Manage Shipping to arrange delivery.", 
            "Success", JOptionPane.INFORMATION_MESSAGE);

        populateRequestsTable();
        populateInventoryTable();
        clearDetailFields();
    }//GEN-LAST:event_btnApproveActionPerformed

    private void btnRejectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRejectActionPerformed
        // TODO add your handling code here:
        RetailPurchaseOrderRequest selectedRequest = getSelectedRequest();
        
        if (selectedRequest == null) {
            JOptionPane.showMessageDialog(this, "Please select a request to reject.", 
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String reason = JOptionPane.showInputDialog(this, "Enter rejection reason:", 
            "Rejection Reason", JOptionPane.QUESTION_MESSAGE);
        if (reason == null) return;
        
        selectedRequest.setStatus("Rejected");
        selectedRequest.setReceiver(userAccount);
        if (reason != null && !reason.trim().isEmpty()) {
            String existingMsg = selectedRequest.getMessage() != null ? selectedRequest.getMessage() : "";
            selectedRequest.setMessage(existingMsg + "\n[Rejected]: " + reason);
        }
        
        JOptionPane.showMessageDialog(this, "Request rejected.", "Rejected", JOptionPane.INFORMATION_MESSAGE);
        populateRequestsTable();
        clearDetailFields();
    }//GEN-LAST:event_btnRejectActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        // Only search in tblProductInventory
        String keyword = fieldSearchProduct.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term.", 
                "Empty Search", JOptionPane.WARNING_MESSAGE);
            return;
        }
        populateInventoryTableWithSearch(keyword);
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        // TODO add your handling code here:
        // // Only reset in tblProductInventory
        fieldSearchProduct.setText("");
        populateInventoryTable();
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // TODO add your handling code here:
        userProcessContainer.remove(this);
        Component[] components = userProcessContainer.getComponents();
        for (Component component : components) {
            if (component instanceof WholesaleSalesWorkAreaJPanel) {
                ((WholesaleSalesWorkAreaJPanel) component).updateSummary();
                break;
            }
        }
        CardLayout layout = (CardLayout) userProcessContainer.getLayout();
        layout.previous(userProcessContainer);
    }//GEN-LAST:event_btnBackActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnApprove;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnReject;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnSearch;
    private javax.swing.JTextField fieldDate;
    private javax.swing.JTextField fieldId;
    private javax.swing.JTextField fieldMessage;
    private javax.swing.JTextField fieldProduct;
    private javax.swing.JTextField fieldQuantity;
    private javax.swing.JTextField fieldRetailer;
    private javax.swing.JTextField fieldSearchProduct;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblDate;
    private javax.swing.JLabel lblDetails;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JLabel lblProduct;
    private javax.swing.JLabel lblQuantity;
    private javax.swing.JLabel lblRequestId;
    private javax.swing.JLabel lblRetailer;
    private javax.swing.JLabel lblTableTitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTable tblProductInventory;
    private javax.swing.JTable tblRequests;
    // End of variables declaration//GEN-END:variables

    
}
