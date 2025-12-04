/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui.DistributorRole.WholesaleSalesRole;

import Business.EcoSystem;
import Business.Enterprise.Enterprise;
import Business.Enterprise.ShippingEnterprise;
import Business.Network.Network;
import Business.Organization.Distributor.WholesaleSalesOrganization;
import Business.Organization.Organization;
import Business.UserAccount.UserAccount;
import Business.WorkQueue.RetailPurchaseOrderRequest;
import Business.WorkQueue.WholesalesShippingRequest;
import Business.WorkQueue.WorkRequest;
import java.awt.CardLayout;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author chris
 */
public class ManageShippingPanel extends javax.swing.JPanel {

    private JPanel userProcessContainer;
    private UserAccount userAccount;
    private WholesaleSalesOrganization organization;
    private Enterprise enterprise;
    private EcoSystem system;
    
    private ArrayList<RetailPurchaseOrderRequest> approvedOrdersList;
    
    private ArrayList<ShippingEnterprise> shippingEnterprises;
    
    /**
     * Creates new form ManageShippingPanel
     */
    public ManageShippingPanel(JPanel userProcessContainer, UserAccount account, WholesaleSalesOrganization wholesaleSalesOrganization, Enterprise enterprise, EcoSystem system) {
        initComponents();
        
        this.userProcessContainer = userProcessContainer;
        this.userAccount = account;
        this.organization = wholesaleSalesOrganization;
        this.enterprise = enterprise;
        this.system = system;
        this.approvedOrdersList = new ArrayList<>();
        this.shippingEnterprises = new ArrayList<>();
        
        initializeCarrierComboBox();
        
        populateApprovedOrdersTable();
        populateShippingStatusTable();
        
    }
    
    private void initializeCarrierComboBox() {
        cmbCarrier.removeAllItems();
        shippingEnterprises.clear();
        
        for (Network network : system.getNetworkList()) {
            for (Enterprise ent : network.getEnterpriseDirectory().getEnterpriseList()) {
                if (ent instanceof ShippingEnterprise) {
                    ShippingEnterprise shippingEnt = (ShippingEnterprise) ent;
                    cmbCarrier.addItem(shippingEnt.getName());
                    shippingEnterprises.add(shippingEnt);
                }
            }
        }
        
        if (shippingEnterprises.isEmpty()) {
            cmbCarrier.addItem("No Shipping Company Available");
        }
    }
    
    private void populateApprovedOrdersTable() {
        DefaultTableModel model = (DefaultTableModel) tblApprovedOrders.getModel();
        model.setRowCount(0);
        approvedOrdersList.clear();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        for (WorkRequest request : organization.getWorkQueue().getWorkRequestList()) {
            if (request instanceof RetailPurchaseOrderRequest) {
                RetailPurchaseOrderRequest retailOrder = (RetailPurchaseOrderRequest) request;
                
                if ("Approved".equalsIgnoreCase(retailOrder.getStatus())) {
                    approvedOrdersList.add(retailOrder);
                    
                    Object[] row = new Object[5];
                    row[0] = "REQ-" + String.format("%04d", retailOrder.getRequestId());
                    row[1] = getRetailerName(retailOrder);
                    row[2] = retailOrder.getProductName() != null ? retailOrder.getProductName() : "N/A";
                    row[3] = retailOrder.getQuantity();
                    row[4] = retailOrder.getResolveDate() != null ? 
                        dateFormat.format(retailOrder.getResolveDate()) : 
                        (retailOrder.getRequestDate() != null ? dateFormat.format(retailOrder.getRequestDate()) : "N/A");
                    
                    model.addRow(row);
                }
            }
        }
    }
    
    private String getRetailerName(RetailPurchaseOrderRequest request) {
        if (request.getSender() != null) {
            if (request.getSender().getEmployee() != null && 
                request.getSender().getEmployee().getName() != null) {
                return request.getSender().getEmployee().getName();
            }
            return request.getSender().getUsername();
        }
        return "Unknown Retailer";
    }
    
    private void populateShippingStatusTable() {
        DefaultTableModel model = (DefaultTableModel) tblShippingStatus.getModel();
        model.setRowCount(0);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Network network : system.getNetworkList()) {
            for (Enterprise ent : network.getEnterpriseDirectory().getEnterpriseList()) {
                if (ent instanceof ShippingEnterprise) {
                    for (Organization org : ent.getOrganizationDirectory().getOrganizationList()) {
                        for (WorkRequest request : org.getWorkQueue().getWorkRequestList()) {
                            if (request instanceof WholesalesShippingRequest) {
                                WholesalesShippingRequest shippingReq = (WholesalesShippingRequest) request;
                                
                                if (shippingReq.getSender() != null && 
                                    shippingReq.getSender().getUsername().equals(userAccount.getUsername())) {
                                    
                                    Object[] row = new Object[6];
                                    row[0] = "SHP-" + String.format("%04d", shippingReq.getRequestId());
                                    row[1] = shippingReq.getDestinationAddress() != null ? 
                                        shippingReq.getDestinationAddress() : "N/A";
                                    row[2] = shippingReq.getProductName() != null ? 
                                        shippingReq.getProductName() : "N/A";
                                    row[3] = shippingReq.getQuantity();
                                    row[4] = shippingReq.getRequestDate() != null ? 
                                        dateFormat.format(shippingReq.getRequestDate()) : "N/A";
                                    row[5] = getShippingStatusDisplay(shippingReq.getShippingStatus());
                                    
                                    model.addRow(row);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private String getShippingStatusDisplay(String status) {
        if (status == null) return "❓ Unknown";
        
        switch (status) {
            case WholesalesShippingRequest.SHIP_STATUS_PENDING:
                return "⏳ Pending";
            case WholesalesShippingRequest.SHIP_STATUS_PICKED_UP:
                return "📦 Picked Up";
            case WholesalesShippingRequest.SHIP_STATUS_IN_TRANSIT:
                return "🚚 In Transit";
            case WholesalesShippingRequest.SHIP_STATUS_DELIVERED:
                return "✅ Delivered";
            default:
                return status;
        }
    }
    
    private String generateTrackingNumber() {
        return "TRK" + System.currentTimeMillis() % 1000000000;
    }
    
    private Date calculateEstimatedDelivery() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 5);
        return cal.getTime();
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
        lblApprovedOrders = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblApprovedOrders = new javax.swing.JTable();
        btnCreateShippingRequest = new javax.swing.JButton();
        lblShippingRequestStatus = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblShippingStatus = new javax.swing.JTable();
        btnCreateShippingRequest1 = new javax.swing.JButton();
        lblShippingOptions = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        cmbCarrier = new javax.swing.JComboBox<>();
        lblNotes = new javax.swing.JLabel();
        fieldNote = new javax.swing.JTextField();

        lblTitle.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        lblTitle.setText("🚚 Manage Shipping");

        btnBack.setText("<< Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        lblApprovedOrders.setText("Approved Orders (Ready to Ship)");

        tblApprovedOrders.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Request ID", "Retailer", "Product", "Quantity", "Approved Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblApprovedOrders);

        btnCreateShippingRequest.setText("📦 Create Shipping Request");
        btnCreateShippingRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateShippingRequestActionPerformed(evt);
            }
        });

        lblShippingRequestStatus.setText("Shipping Requests Status");

        tblShippingStatus.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ship ID", "Retailer", "Product", "Qty", "Ship Date", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblShippingStatus);

        btnCreateShippingRequest1.setText("🔄 Refresh Status");
        btnCreateShippingRequest1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateShippingRequest1ActionPerformed(evt);
            }
        });

        lblShippingOptions.setText("Shipping Options:");

        jLabel1.setText("Carrier:");

        cmbCarrier.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblNotes.setText("Notes:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnBack)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(lblApprovedOrders)))
                        .addGap(0, 556, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1)
                            .addComponent(jScrollPane2)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblShippingRequestStatus)
                                    .addComponent(lblNotes))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(fieldNote, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(btnCreateShippingRequest1, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(91, 91, 91))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(btnCreateShippingRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(87, 87, 87))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblShippingOptions)
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(layout.createSequentialGroup()
                .addGap(116, 116, 116)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cmbCarrier, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
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
                .addComponent(lblApprovedOrders)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblShippingOptions)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cmbCarrier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblNotes)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(fieldNote, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCreateShippingRequest)
                .addGap(5, 5, 5)
                .addComponent(lblShippingRequestStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCreateShippingRequest1)
                .addGap(17, 17, 17))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnCreateShippingRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateShippingRequestActionPerformed
        // TODO add your handling code here:
        int selectedRow = tblApprovedOrders.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select an approved order to create shipping request.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Verify the line is available
        if (selectedRow >= approvedOrdersList.size()) {
            JOptionPane.showMessageDialog(this, 
                "Invalid selection. Please refresh and try again.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // check is it an available Carrier
        int carrierIndex = cmbCarrier.getSelectedIndex();
        if (carrierIndex < 0 || shippingEnterprises.isEmpty() || carrierIndex >= shippingEnterprises.size()) {
            JOptionPane.showMessageDialog(this, 
                "Please select a valid shipping company.", 
                "Invalid Carrier", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        RetailPurchaseOrderRequest selectedOrder = approvedOrdersList.get(selectedRow);
        ShippingEnterprise selectedShippingEnterprise = shippingEnterprises.get(carrierIndex);
        String notes = fieldNote.getText().trim();
        
        // Find ShippingEnterprise Organization
        Organization receivingOrg = null;
        for (Organization org : selectedShippingEnterprise.getOrganizationDirectory().getOrganizationList()) {
            if (org.getName().contains("Management")) {
                receivingOrg = org;
                break;
            }
        }
        if (receivingOrg == null && !selectedShippingEnterprise.getOrganizationDirectory().getOrganizationList().isEmpty()) {
            receivingOrg = selectedShippingEnterprise.getOrganizationDirectory().getOrganizationList().get(0);
        }
        
        if (receivingOrg == null) {
            JOptionPane.showMessageDialog(this, 
                "Error: Selected shipping company has no organization.\nPlease contact system administrator.", 
                "Configuration Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // create WholesalesShippingRequest
        WholesalesShippingRequest shippingRequest = new WholesalesShippingRequest();
        
        // product information
        shippingRequest.setProduct(selectedOrder.getProduct());
        shippingRequest.setProductCode(selectedOrder.getProductCode());
        shippingRequest.setProductName(selectedOrder.getProductName());
        shippingRequest.setQuantity(selectedOrder.getQuantity());
        if (selectedOrder.getUnit() != null) {
            shippingRequest.setUnit(selectedOrder.getUnit());
        }
        
        // retail information
        shippingRequest.setOriginAddress(enterprise.getName() + " Distribution Center");
        shippingRequest.setDestinationAddress(getRetailerName(selectedOrder));
        
        
        shippingRequest.setCarrierName(selectedShippingEnterprise.getName());
        shippingRequest.setTrackingNumber(generateTrackingNumber());
        shippingRequest.setEstimatedDeliveryDate(calculateEstimatedDelivery());
        shippingRequest.setShippingStatus(WholesalesShippingRequest.SHIP_STATUS_PENDING);
        
        shippingRequest.setSender(userAccount);
        shippingRequest.setStatus("Pending");
        shippingRequest.setMessage(notes.isEmpty() ? "Shipping request for retail order" : notes);
        
        // add to Shipping Organization WorkQueue
        receivingOrg.getWorkQueue().getWorkRequestList().add(shippingRequest);
        
        selectedOrder.setStatus("Shipping");

        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        JOptionPane.showMessageDialog(this, 
            "Shipping Request created successfully!\n\n" +
            "Tracking #: " + shippingRequest.getTrackingNumber() + "\n" +
            "Product: " + shippingRequest.getProductName() + "\n" +
            "Quantity: " + shippingRequest.getQuantity() + "\n" +
            "Carrier: " + selectedShippingEnterprise.getName() + "\n" +
            "Est. Delivery: " + dateFormat.format(shippingRequest.getEstimatedDeliveryDate()), 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
        
        fieldNote.setText("");
        

        populateApprovedOrdersTable();
        populateShippingStatusTable();
    }//GEN-LAST:event_btnCreateShippingRequestActionPerformed

    private void btnCreateShippingRequest1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateShippingRequest1ActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_btnCreateShippingRequest1ActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // TODO add your handling code here:
        userProcessContainer.remove(this);
        
        Component[] components = userProcessContainer.getComponents();
        if (components.length > 0) {
            Component lastComponent = components[components.length - 1];
            if (lastComponent instanceof WholesaleSalesWorkAreaJPanel) {
                ((WholesaleSalesWorkAreaJPanel) lastComponent).updateSummary();
            }
        }
        
        CardLayout layout = (CardLayout) userProcessContainer.getLayout();
        layout.previous(userProcessContainer);
    }//GEN-LAST:event_btnBackActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnCreateShippingRequest;
    private javax.swing.JButton btnCreateShippingRequest1;
    private javax.swing.JComboBox<String> cmbCarrier;
    private javax.swing.JTextField fieldNote;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblApprovedOrders;
    private javax.swing.JLabel lblNotes;
    private javax.swing.JLabel lblShippingOptions;
    private javax.swing.JLabel lblShippingRequestStatus;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTable tblApprovedOrders;
    private javax.swing.JTable tblShippingStatus;
    // End of variables declaration//GEN-END:variables
}
