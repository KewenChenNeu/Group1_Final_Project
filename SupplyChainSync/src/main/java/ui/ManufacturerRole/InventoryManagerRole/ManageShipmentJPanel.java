/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui.ManufacturerRole.InventoryManagerRole;

import Business.EcoSystem;
import Business.Enterprise.Enterprise;
import Business.Network.Network;
import Business.Organization.Manufacturer.InventoryOrganization;
import Business.Organization.Organization;
import Business.UserAccount.UserAccount;
import Business.WorkQueue.MaterialShippingRequest;
import Business.WorkQueue.WorkRequest;
import java.awt.CardLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author zhaojinkun
 */
public class ManageShipmentJPanel extends javax.swing.JPanel {
    private JPanel userProcessContainer;
    private UserAccount account;
    private InventoryOrganization organization;
    private Enterprise enterprise;
    private EcoSystem system;
    
    // Date format for display
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    // Status constants for receive
    private static final String RECEIVE_STATUS_RECEIVED = "Received";
    
    // List to store shipment objects corresponding to table rows
    private List<MaterialShippingRequest> pendingShipmentsList = new ArrayList<>();
    /**
     * Creates new form ManageShipmentJPanel
     */
    public ManageShipmentJPanel(JPanel userProcessContainer, UserAccount account, InventoryOrganization inventoryOrganization, Enterprise enterprise, EcoSystem system) {
        this.userProcessContainer = userProcessContainer;
        this.account = account;
        this.organization = inventoryOrganization;
        this.enterprise = enterprise;
        this.system = system;
        initComponents();
        initializeStatusComboBox();
        
        // Load data
        populatePendingShipmentsTable("All");
        populateReceivedTable();
    }
    
    private void initializeStatusComboBox() {
        cmbStatus.removeAllItems();
        cmbStatus.addItem("All");
        cmbStatus.addItem(MaterialShippingRequest.SHIP_STATUS_PENDING);
        cmbStatus.addItem(MaterialShippingRequest.SHIP_STATUS_IN_TRANSIT);
        cmbStatus.addItem(MaterialShippingRequest.SHIP_STATUS_PICKED_UP);
        cmbStatus.addItem(MaterialShippingRequest.SHIP_STATUS_DELIVERED);
    }
    
    private void populatePendingShipmentsTable(String selectedStatus) {
        if (selectedStatus == null) {
            return; 
        }
        DefaultTableModel model = (DefaultTableModel) tblShipments.getModel();
        model.setRowCount(0);
        pendingShipmentsList.clear();
        
        List<MaterialShippingRequest> shipments = getPendingShipments();
        
        for (MaterialShippingRequest msr : shipments) {
            if (!selectedStatus.equals("All") && !msr.getShippingStatus().equals(selectedStatus)) {
                continue;
            }
            Object[] row = new Object[7];
            row[0] = msr;
            row[1] = msr.getTrackingNumber() != null ? msr.getTrackingNumber() : "N/A";
            row[2] = msr.getMaterialCode() + " - " + msr.getMaterialName();
            row[3] = msr.getQuantity() + " " + (msr.getUnit() != null ? msr.getUnit() : "");
            row[4] = msr.getOriginAddress() != null ? msr.getOriginAddress() : "Manufacturer";
            row[5] = msr.getRequestDate() != null ? dateFormat.format(msr.getRequestDate()) : "N/A";
            row[6] = msr.getShippingStatus() != null ? msr.getShippingStatus() : "Pending";
            
            model.addRow(row);
            pendingShipmentsList.add(msr);
        }
        tblShipments.getColumnModel().getColumn(0).setMinWidth(0);
        tblShipments.getColumnModel().getColumn(0).setMaxWidth(0);
        tblShipments.getColumnModel().getColumn(0).setWidth(0);
    }
    
    
    private void populateReceivedTable() {
        DefaultTableModel model = (DefaultTableModel) tblReceived.getModel();
        model.setRowCount(0);

        List<MaterialShippingRequest> receivedShipments = getReceivedShipments();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (MaterialShippingRequest msr : receivedShipments) {
            Object[] row = new Object[5];
            row[0] = msr.getTrackingNumber() != null ? msr.getTrackingNumber() : "N/A";
            row[1] = (msr.getMaterialCode() != null ? msr.getMaterialCode() : "") 
                     + " - " + (msr.getMaterialName() != null ? msr.getMaterialName() : "");
            row[2] = msr.getQuantity() + " " + (msr.getUnit() != null ? msr.getUnit() : "");
            row[3] = msr.getActualDeliveryDate() != null ? dateFormat.format(msr.getActualDeliveryDate()) : "N/A";
            row[4] = msr.getReceiver() != null ? msr.getReceiver().getUsername() : "N/A";

            model.addRow(row);
        }
    }
    
    /**
     * Get all pending shipments destined for this distributor
     */
    private List<MaterialShippingRequest> getPendingShipments() {
        List<MaterialShippingRequest> shipments = new ArrayList<>();

        // Search all networks & enterprises
        for (Network network : system.getNetworkList()) {
            for (Enterprise ent : network.getEnterpriseDirectory().getEnterpriseList()) {

                // Search enterprise queue
                for (WorkRequest wr : ent.getWorkQueue().getWorkRequestList()) {
                    if (wr instanceof MaterialShippingRequest) {
                        MaterialShippingRequest msr = (MaterialShippingRequest) wr;
                        if (isShipmentForThisManufacture(msr) && !isReceived(msr)) {
                            shipments.add(msr);
                        }
                    }
                }

                // Search organization queues inside enterprise
                for (Organization org : ent.getOrganizationDirectory().getOrganizationList()) {
                    for (WorkRequest wr : org.getWorkQueue().getWorkRequestList()) {
                        if (wr instanceof MaterialShippingRequest) {
                            MaterialShippingRequest msr = (MaterialShippingRequest) wr;
                            if (isShipmentForThisManufacture(msr) && !isReceived(msr)) {
                                if (!shipments.contains(msr)) {
                                    shipments.add(msr);
                                }
                            }
                        }
                    }
                }
            }
        }

        // Also check current organization
        for (WorkRequest wr : organization.getWorkQueue().getWorkRequestList()) {
            if (wr instanceof MaterialShippingRequest) {
                MaterialShippingRequest msr = (MaterialShippingRequest) wr;
                if (!isReceived(msr) && !shipments.contains(msr)) {
                    shipments.add(msr);
                }
            }
        }

        return shipments;
    }
    
    private List<MaterialShippingRequest> getReceivedShipments() {
        List<MaterialShippingRequest> received = new ArrayList<>();

        for (Network network : system.getNetworkList()) {
            for (Enterprise ent : network.getEnterpriseDirectory().getEnterpriseList()) {

                // Enterprise queue
                for (WorkRequest wr : ent.getWorkQueue().getWorkRequestList()) {
                    if (wr instanceof MaterialShippingRequest) {
                        MaterialShippingRequest msr = (MaterialShippingRequest) wr;
                        if (isShipmentForThisManufacture(msr) && isReceived(msr)) {
                            received.add(msr);
                        }
                    }
                }

                // Organization queues
                for (Organization org : ent.getOrganizationDirectory().getOrganizationList()) {
                    for (WorkRequest wr : org.getWorkQueue().getWorkRequestList()) {
                        if (wr instanceof MaterialShippingRequest) {
                            MaterialShippingRequest msr = (MaterialShippingRequest) wr;
                            if (isShipmentForThisManufacture(msr) && isReceived(msr)) {
                                if (!received.contains(msr)) {
                                    received.add(msr);
                                }
                            }
                        }
                    }
                }
            }
        }

        // Check current organization
        for (WorkRequest wr : organization.getWorkQueue().getWorkRequestList()) {
            if (wr instanceof MaterialShippingRequest) {
                MaterialShippingRequest msr = (MaterialShippingRequest) wr;
                if (isReceived(msr) && !received.contains(msr)) {
                    received.add(msr);
                }
            }
        }

        return received;
    }
    
    
        /**
     * Check if shipment is destined for this distributor
     */
    private boolean isShipmentForThisManufacture(MaterialShippingRequest msr) {
        String destAddr = msr.getDestinationAddress();
        String factoryName = enterprise.getName();  

        if (destAddr != null) {
            String addr = destAddr.toLowerCase();

            if (addr.contains("manufacture") || addr.contains("factory") || addr.contains("plant")) {
                return true;
            }

            if (addr.contains(factoryName.toLowerCase())) {
                return true;
            }
        }

        return organization.getWorkQueue().getWorkRequestList().contains(msr);
    }
    
    private boolean isReceived(MaterialShippingRequest msr) {
        return MaterialShippingRequest.SHIP_STATUS_DELIVERED.equals(msr.getShippingStatus())
                || msr.isDelivered()
                || (msr.getActualDeliveryDate() != null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnBack = new javax.swing.JButton();
        btnViewDetail = new javax.swing.JButton();
        btnReceiveShipment = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblShipments = new javax.swing.JTable();
        lblReveived = new javax.swing.JLabel();
        lblFilter = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblReceived = new javax.swing.JTable();
        cmbStatus = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        lblPendingShipments = new javax.swing.JLabel();

        setLayout(null);

        btnBack.setText("<< Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });
        add(btnBack);
        btnBack.setBounds(670, 4, 80, 23);

        btnViewDetail.setText("View Details");
        btnViewDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewDetailActionPerformed(evt);
            }
        });
        add(btnViewDetail);
        btnViewDetail.setBounds(6, 266, 152, 23);

        btnReceiveShipment.setText("Receive Shipment");
        btnReceiveShipment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReceiveShipmentActionPerformed(evt);
            }
        });
        add(btnReceiveShipment);
        btnReceiveShipment.setBounds(176, 266, 158, 23);

        tblShipments.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Object", "Tracking Number", "Material", "Qty", "From", "Ship Date", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblShipments);

        add(jScrollPane1);
        jScrollPane1.setBounds(6, 105, 738, 149);

        lblReveived.setText("Recently Received (History)");
        add(lblReveived);
        lblReveived.setBounds(6, 307, 161, 17);

        lblFilter.setText("Filter:");
        add(lblFilter);
        lblFilter.setBounds(18, 73, 32, 17);

        tblReceived.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tracking Number", "Product", "Qty", "Receive Date", "Received By"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblReceived);

        add(jScrollPane2);
        jScrollPane2.setBounds(6, 330, 744, 191);

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbStatusActionPerformed(evt);
            }
        });
        add(cmbStatus);
        cmbStatus.setBounds(62, 70, 156, 23);

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        jLabel1.setText("Shipment Management");
        add(jLabel1);
        jLabel1.setBounds(0, 0, 318, 30);
        add(jSeparator1);
        jSeparator1.setBounds(6, 36, 744, 10);

        lblPendingShipments.setText("Pending Shipments");
        add(lblPendingShipments);
        lblPendingShipments.setBounds(10, 40, 320, 17);
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // TODO add your handling code here:
        userProcessContainer.remove(this);
        CardLayout layout = (CardLayout) userProcessContainer.getLayout();
        layout.previous(userProcessContainer);
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnViewDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewDetailActionPerformed
        // TODO add your handling code here:
//        ProductShippingRequest shipment = getSelectedShipment();
//        showShipmentDetailsDialog(shipment);

        int selectedRow = tblShipments.getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a shipment to view details.");
            return;
        }

        MaterialShippingRequest msr = (MaterialShippingRequest) tblShipments.getValueAt(selectedRow, 0);

        StringBuilder sb = new StringBuilder();

        sb.append("Tracking Number: ").append(msr.getTrackingNumber() != null ? msr.getTrackingNumber() : "N/A").append("\n");
        sb.append("Material Code: ").append(msr.getMaterialCode() != null ? msr.getMaterialCode() : "N/A").append("\n");
        sb.append("Material Name: ").append(msr.getMaterialName() != null ? msr.getMaterialName() : "N/A").append("\n");
        sb.append("Quantity: ").append(msr.getQuantity()).append(" ").append(msr.getUnit() != null ? msr.getUnit() : "").append("\n");
        sb.append("Origin Address: ").append(msr.getOriginAddress() != null ? msr.getOriginAddress() : "N/A").append("\n");
        sb.append("Destination Address: ").append(msr.getDestinationAddress() != null ? msr.getDestinationAddress() : "N/A").append("\n");
        sb.append("Carrier: ").append(msr.getCarrierName() != null ? msr.getCarrierName() : "N/A").append("\n");
        sb.append("Shipping Cost: $").append(String.format("%.2f", msr.getShippingCost())).append("\n");
        sb.append("Shipping Status: ").append(msr.getShippingStatus() != null ? msr.getShippingStatus() : "Pending").append("\n");
        sb.append("Request Status: ").append(msr.getStatus() != null ? msr.getStatus() : "Pending").append("\n");
        sb.append("Message: ").append(msr.getMessage() != null ? msr.getMessage() : "").append("\n");
        sb.append("Request Date: ").append(msr.getRequestDate() != null ? dateFormat.format(msr.getRequestDate()) : "N/A").append("\n");
        sb.append("Actual Delivery Date: ").append(msr.getActualDeliveryDate() != null ? dateFormat.format(msr.getActualDeliveryDate()) : "N/A");

        if (msr.getActualDeliveryDate()!= null) {
            sb.append("Delivered Date: ").append(msr.getActualDeliveryDate()).append("\n");
        }
        
        if (msr.getEstimatedDeliveryDate()!= null) {
            sb.append("Estimated Delivery Date ").append(msr.getEstimatedDeliveryDate()).append("\n");
        }

        if (msr.getSender() != null) {
            sb.append("Sender: ").append(msr.getSender().getUsername()).append("\n");
        }

        if (msr.getReceiver() != null) {
            sb.append("Receiver: ").append(msr.getReceiver().getUsername()).append("\n");
        }

        JOptionPane.showMessageDialog(this, sb.toString(), "Shipment Details", JOptionPane.INFORMATION_MESSAGE);

    }//GEN-LAST:event_btnViewDetailActionPerformed

    private void btnReceiveShipmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReceiveShipmentActionPerformed
        // TODO add your handling code here:
        int selectedRow = tblShipments.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a shipment to receive.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        MaterialShippingRequest msr = pendingShipmentsList.get(selectedRow);

        if (msr.isDelivered()) {
            JOptionPane.showMessageDialog(this, "This shipment has already been received.", "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        msr.markDelivered();
        msr.setReceiver(account); 
        msr.setActualDeliveryDate(new Date());

        JOptionPane.showMessageDialog(this, "Shipment marked as received successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);

        String selectedStatus = (String) cmbStatus.getSelectedItem();
        populatePendingShipmentsTable(selectedStatus);
        populateReceivedTable();
    }//GEN-LAST:event_btnReceiveShipmentActionPerformed

    private void cmbStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbStatusActionPerformed
        // TODO add your handling code here:
        String selectedStatus = (String) cmbStatus.getSelectedItem();
        populatePendingShipmentsTable(selectedStatus);
    }//GEN-LAST:event_cmbStatusActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnReceiveShipment;
    private javax.swing.JButton btnViewDetail;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblFilter;
    private javax.swing.JLabel lblPendingShipments;
    private javax.swing.JLabel lblReveived;
    private javax.swing.JTable tblReceived;
    private javax.swing.JTable tblShipments;
    // End of variables declaration//GEN-END:variables
}
