/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui.DistributorRole.WholesaleSalesRole;

import Business.EcoSystem;
import Business.Enterprise.Enterprise;
import Business.Organization.Distributor.WholesaleSalesOrganization;
import Business.UserAccount.UserAccount;
import Business.WorkQueue.DeliveryConfirmationRequest;
import Business.WorkQueue.WorkRequest;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author chris
 */
public class DeliveryConfirmationPanel extends javax.swing.JPanel {

    private JPanel userProcessContainer;
    private UserAccount userAccount;
    private WholesaleSalesOrganization organization;
    private Enterprise enterprise;
    private EcoSystem system;
    
    private ArrayList<DeliveryConfirmationRequest> pendingConfirmations;
    
    /**
     * Creates new form DeliveryConfirmationPanel
     */
    public DeliveryConfirmationPanel(JPanel userProcessContainer, UserAccount account, WholesaleSalesOrganization wholesaleSalesOrganization, Enterprise enterprise, EcoSystem system) {
        initComponents();
        
        this.userProcessContainer = userProcessContainer;
        this.userAccount = account;
        this.organization = wholesaleSalesOrganization;
        this.enterprise = enterprise;
        this.system = system;
        this.pendingConfirmations = new ArrayList<>();
        
        setDetailFieldsReadOnly();
        
        setupTableSelectionListener();
        
        populatePendingConfirmationsTable();
        populateConfirmedHistoryTable();

        clearDetailFields();
        
    }
    
    private void setDetailFieldsReadOnly() {
        Color readOnlyColor = new Color(240, 240, 240);
        
        fieldShipmentID.setEditable(false);
        fieldShipmentID.setBackground(readOnlyColor);
        
        fieldRetailer.setEditable(false);
        fieldRetailer.setBackground(readOnlyColor);
        
        fieldProduct.setEditable(false);
        fieldProduct.setBackground(readOnlyColor);
        
        fieldQuantity.setEditable(false);
        fieldQuantity.setBackground(readOnlyColor);
        
        fieldDeliveryDate.setEditable(false);
        fieldDeliveryDate.setBackground(readOnlyColor);
        
        fieldDeliveryStaff.setEditable(false);
        fieldDeliveryStaff.setBackground(readOnlyColor);
        
    }
    
    private void setupTableSelectionListener() {
        tblConfirmation.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblConfirmation.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = tblConfirmation.getSelectedRow();
                    if (selectedRow >= 0 && selectedRow < pendingConfirmations.size()) {
                        displayConfirmationDetails(pendingConfirmations.get(selectedRow));
                    } else {
                        clearDetailFields();
                    }
                }
            }
        });
    }
    
    private void populatePendingConfirmationsTable() {
        DefaultTableModel model = (DefaultTableModel) tblConfirmation.getModel();
        model.setRowCount(0);
        pendingConfirmations.clear();
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        for (WorkRequest request : organization.getWorkQueue().getWorkRequestList()) {
            if (request instanceof DeliveryConfirmationRequest) {
                DeliveryConfirmationRequest confirmation = (DeliveryConfirmationRequest) request;
                
                if (!confirmation.isConfirmed()) {
                    pendingConfirmations.add(confirmation);
                    
                    Object[] row = new Object[6];
                    row[0] = "CNF-" + String.format("%04d", confirmation.getRequestId());
                    row[1] = getShipmentId(confirmation);
                    row[2] = confirmation.getStoreName() != null ? confirmation.getStoreName() : "N/A";
                    row[3] = confirmation.getProductName() != null ? confirmation.getProductName() : "N/A";
                    row[4] = confirmation.getDeliveryDate() != null ? 
                        dateFormat.format(confirmation.getDeliveryDate()) : "N/A";
                    row[5] = getConditionDisplay(confirmation.getConditionOnArrival());
                    
                    model.addRow(row);
                }
            }
        }
    }
    
    private String getConditionDisplay(String condition) {
        if (condition == null) return "N/A";
        
        switch (condition) {
            case DeliveryConfirmationRequest.CONDITION_GOOD:
                return "✅ Good";
            case DeliveryConfirmationRequest.CONDITION_DAMAGED:
                return "❌ Damaged";
            case DeliveryConfirmationRequest.CONDITION_PARTIAL:
                return "⚠️ Partial";
            default:
                return condition;
        }
    }
    
    private String getShipmentId(DeliveryConfirmationRequest confirmation) {
        if (confirmation.getOriginalShippingRequest() != null) {
            return "SHP-" + String.format("%04d", confirmation.getOriginalShippingRequest().getRequestId());
        }
        return "N/A";
    }
    
    private void displayConfirmationDetails(DeliveryConfirmationRequest confirmation) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        fieldShipmentID.setText(getShipmentId(confirmation));
        fieldRetailer.setText(confirmation.getStoreName() != null ? confirmation.getStoreName() : "N/A");
        fieldProduct.setText(confirmation.getProductName() != null ? confirmation.getProductName() : "N/A");
        fieldQuantity.setText(String.valueOf(confirmation.getQuantityDelivered()));
        fieldDeliveryDate.setText(confirmation.getDeliveryDate() != null ? 
            dateFormat.format(confirmation.getDeliveryDate()) : "N/A");
        fieldDeliveryStaff.setText(confirmation.getDeliveredBy() != null ? 
            confirmation.getDeliveredBy() : "N/A");
        fieldNote.setText(confirmation.getDeliveryNotes() != null ? 
            confirmation.getDeliveryNotes() : "");
    }
    
    private void clearDetailFields() {
        fieldShipmentID.setText("");
        fieldRetailer.setText("");
        fieldProduct.setText("");
        fieldQuantity.setText("");
        fieldDeliveryDate.setText("");
        fieldDeliveryStaff.setText("");
        fieldNote.setText("");
    }
    
    private void populateConfirmedHistoryTable() {
        DefaultTableModel model = (DefaultTableModel) tblConfirmedDelivery.getModel();
        model.setRowCount(0);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        for (WorkRequest request : organization.getWorkQueue().getWorkRequestList()) {
            if (request instanceof DeliveryConfirmationRequest) {
                DeliveryConfirmationRequest confirmation = (DeliveryConfirmationRequest) request;

                if (confirmation.isConfirmed()) {
                    Object[] row = new Object[5];
                    row[0] = "CNF-" + String.format("%04d", confirmation.getRequestId());
                    row[1] = confirmation.getStoreName() != null ? confirmation.getStoreName() : "N/A";
                    row[2] = confirmation.getProductName() != null ? confirmation.getProductName() : "N/A";
                    row[3] = confirmation.getConfirmationDate() != null ? 
                        dateFormat.format(confirmation.getConfirmationDate()) : "N/A";
                    row[4] = confirmation.getReceivedBy() != null ? confirmation.getReceivedBy() : "N/A";
                    
                    model.addRow(row);
                }
            }
        }
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
        lblConfirmationTitle = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblConfirmation = new javax.swing.JTable();
        lblSummeryTitle = new javax.swing.JLabel();
        lblShippingID = new javax.swing.JLabel();
        lblRetailer = new javax.swing.JLabel();
        lblProduct = new javax.swing.JLabel();
        lblQuantity = new javax.swing.JLabel();
        lblDeliveredDate = new javax.swing.JLabel();
        lblDeliveryStaff = new javax.swing.JLabel();
        fieldShipmentID = new javax.swing.JTextField();
        fieldRetailer = new javax.swing.JTextField();
        fieldProduct = new javax.swing.JTextField();
        fieldDeliveryDate = new javax.swing.JTextField();
        fieldQuantity = new javax.swing.JTextField();
        fieldDeliveryStaff = new javax.swing.JTextField();
        lblNote = new javax.swing.JLabel();
        fieldNote = new javax.swing.JTextField();
        btnConfirm = new javax.swing.JButton();
        lblConfirmedDeliveries = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblConfirmedDelivery = new javax.swing.JTable();
        btnCreateShippingRequest1 = new javax.swing.JButton();

        lblTitle.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        lblTitle.setText("✅ Delivery Confirmation  ");

        btnBack.setText("<< Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        lblConfirmationTitle.setText("Pending Delivery Confirmations");

        tblConfirmation.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Confirm ID", "Shipment ID", "Retailer", "Product", "Delivered Date", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblConfirmation);
        if (tblConfirmation.getColumnModel().getColumnCount() > 0) {
            tblConfirmation.getColumnModel().getColumn(1).setHeaderValue("Shipment ID");
            tblConfirmation.getColumnModel().getColumn(5).setHeaderValue("Status");
        }

        lblSummeryTitle.setText("Confirmation Details");

        lblShippingID.setText("Shipment ID:");

        lblRetailer.setText("Retailer:");

        lblProduct.setText("Product:");

        lblQuantity.setText("Quantity:");

        lblDeliveredDate.setText("Delivered Date:");

        lblDeliveryStaff.setText("Delivery Staff:");

        lblNote.setText("Notes:");

        btnConfirm.setText("✅ Confirm Delivery");
        btnConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmActionPerformed(evt);
            }
        });

        lblConfirmedDeliveries.setText("Confirmed Deliveries History");

        tblConfirmedDelivery.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Confirm ID", "Retailer", "Product", "Confirmed Date", "Confirmed By"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblConfirmedDelivery);

        btnCreateShippingRequest1.setText("🔄 Refresh Status");
        btnCreateShippingRequest1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateShippingRequest1ActionPerformed(evt);
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblShippingID)
                                    .addComponent(lblSummeryTitle)
                                    .addComponent(lblProduct)
                                    .addComponent(lblDeliveredDate)
                                    .addComponent(lblNote))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(fieldProduct)
                                            .addComponent(fieldShipmentID)
                                            .addComponent(fieldDeliveryDate, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE))
                                        .addGap(54, 54, 54)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(lblDeliveryStaff)
                                            .addComponent(lblRetailer)
                                            .addComponent(lblQuantity))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(fieldRetailer, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                                            .addComponent(fieldQuantity)
                                            .addComponent(fieldDeliveryStaff)))
                                    .addComponent(fieldNote)))
                            .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBack)
                            .addComponent(lblConfirmationTitle))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblConfirmedDeliveries)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCreateShippingRequest1, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36))
            .addGroup(layout.createSequentialGroup()
                .addGap(240, 240, 240)
                .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(lblConfirmationTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblSummeryTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblShippingID)
                    .addComponent(lblRetailer)
                    .addComponent(fieldShipmentID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldRetailer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProduct)
                    .addComponent(lblQuantity)
                    .addComponent(fieldProduct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDeliveredDate)
                    .addComponent(lblDeliveryStaff)
                    .addComponent(fieldDeliveryDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldDeliveryStaff, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNote)
                    .addComponent(fieldNote, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnConfirm)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblConfirmedDeliveries)
                    .addComponent(btnCreateShippingRequest1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmActionPerformed
        // TODO add your handling code here:
        int selectedRow = tblConfirmation.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select a pending delivery confirmation.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check the selected role
        if (selectedRow >= pendingConfirmations.size()) {
            JOptionPane.showMessageDialog(this, 
                "Invalid selection. Please refresh and try again.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        DeliveryConfirmationRequest selectedConfirmation = pendingConfirmations.get(selectedRow);
        
        // Verify the people
        String confirmedBy = userAccount.getEmployee() != null ? 
            userAccount.getEmployee().getName() : userAccount.getUsername();
        
        int option = JOptionPane.showConfirmDialog(this, 
            "Confirm delivery for:\n\n" +
            "Product: " + selectedConfirmation.getProductName() + "\n" +
            "Quantity: " + selectedConfirmation.getQuantityDelivered() + "\n" +
            "Retailer: " + selectedConfirmation.getStoreName() + "\n" +
            "Condition: " + selectedConfirmation.getConditionOnArrival() + "\n\n" +
            "Confirm this delivery?", 
            "Confirm Delivery", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (option != JOptionPane.YES_OPTION) {
            return;
        }
        
        selectedConfirmation.confirmDelivery(confirmedBy);
        
        String notes = fieldNote.getText().trim();
        if (!notes.isEmpty()) {
            String existingNotes = selectedConfirmation.getDeliveryNotes();
            if (existingNotes != null && !existingNotes.isEmpty()) {
                if (!existingNotes.equals(notes)) {
                    selectedConfirmation.setDeliveryNotes(existingNotes + "\n[Sales Note] " + notes);
                }
            } else {
                selectedConfirmation.setDeliveryNotes("[Sales Note] " + notes);
            }
        }
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        JOptionPane.showMessageDialog(this, 
            "Delivery confirmed successfully!\n\n" +
            "Confirmation ID: CNF-" + String.format("%04d", selectedConfirmation.getRequestId()) + "\n" +
            "Confirmed By: " + confirmedBy + "\n" +
            "Confirmed Date: " + dateFormat.format(selectedConfirmation.getConfirmationDate()), 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
        
        populatePendingConfirmationsTable();
        populateConfirmedHistoryTable();
        
        clearDetailFields();
    }//GEN-LAST:event_btnConfirmActionPerformed

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

    private void btnCreateShippingRequest1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateShippingRequest1ActionPerformed
        // TODO add your handling code here:
        populatePendingConfirmationsTable();
        populateConfirmedHistoryTable();
        clearDetailFields();
        
        JOptionPane.showMessageDialog(this, 
            "Tables refreshed successfully!", 
            "Refresh", 
            JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnCreateShippingRequest1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnConfirm;
    private javax.swing.JButton btnCreateShippingRequest1;
    private javax.swing.JTextField fieldDeliveryDate;
    private javax.swing.JTextField fieldDeliveryStaff;
    private javax.swing.JTextField fieldNote;
    private javax.swing.JTextField fieldProduct;
    private javax.swing.JTextField fieldQuantity;
    private javax.swing.JTextField fieldRetailer;
    private javax.swing.JTextField fieldShipmentID;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblConfirmationTitle;
    private javax.swing.JLabel lblConfirmedDeliveries;
    private javax.swing.JLabel lblDeliveredDate;
    private javax.swing.JLabel lblDeliveryStaff;
    private javax.swing.JLabel lblNote;
    private javax.swing.JLabel lblProduct;
    private javax.swing.JLabel lblQuantity;
    private javax.swing.JLabel lblRetailer;
    private javax.swing.JLabel lblShippingID;
    private javax.swing.JLabel lblSummeryTitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTable tblConfirmation;
    private javax.swing.JTable tblConfirmedDelivery;
    // End of variables declaration//GEN-END:variables
}
