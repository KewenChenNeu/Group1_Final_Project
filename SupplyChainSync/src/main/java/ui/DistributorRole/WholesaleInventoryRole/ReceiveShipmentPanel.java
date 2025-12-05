/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui.DistributorRole.WholesaleInventoryRole;

import Business.EcoSystem;
import Business.Enterprise.Enterprise;
import Business.Inventory.Inventory;
import Business.Inventory.InventoryItem;
import Business.Network.Network;
import Business.Organization.Distributor.WholesaleInventoryOrganization;
import Business.Organization.Organization;
import Business.Product.Product;
import Business.UserAccount.UserAccount;
import Business.WorkQueue.ProductShippingRequest;
import Business.WorkQueue.WorkRequest;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;


/**
 *
 * @author chris
 */
public class ReceiveShipmentPanel extends javax.swing.JPanel {

    private JPanel userProcessContainer;
    private UserAccount account;
    private WholesaleInventoryOrganization organization;
    private Enterprise enterprise;
    private EcoSystem system;
    
    // Date format for display
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    // Status constants for receive
    private static final String RECEIVE_STATUS_RECEIVED = "Received";
    
    // List to store shipment objects corresponding to table rows
    private List<ProductShippingRequest> pendingShipmentsList = new ArrayList<>();
    
    /**
     * Creates new form ReceiveShipmentPanel
     */
    public ReceiveShipmentPanel(JPanel userProcessContainer, UserAccount account, WholesaleInventoryOrganization wholesaleInventoryOrganization, Enterprise enterprise, EcoSystem system) {
        initComponents();
        
        this.userProcessContainer = userProcessContainer;
        this.account = account;
        this.organization = wholesaleInventoryOrganization;
        this.enterprise = enterprise;
        this.system = system;
        
        // Initialize components
        initializeStatusComboBox();
        setFieldsReadOnly();
        
        // Load data
        populatePendingShipmentsTable();
        populateReceivedTable();
        updateSummary();
    }
    
    /**
     * Initialize status filter combo box
     */
    private void initializeStatusComboBox() {
        cmbStatus.removeAllItems();
        cmbStatus.addItem("All");
        cmbStatus.addItem(ProductShippingRequest.SHIP_STATUS_PENDING);
        cmbStatus.addItem(ProductShippingRequest.SHIP_STATUS_IN_TRANSIT);
        cmbStatus.addItem(ProductShippingRequest.SHIP_STATUS_OUT_FOR_DELIVERY);
        cmbStatus.addItem(ProductShippingRequest.SHIP_STATUS_DELIVERED);
    }
    
    /**
     * Set summary fields as read-only
     */
    private void setFieldsReadOnly() {
        fieldPending.setEditable(false);
        fieldInTransit.setEditable(false);
        fieldReadyToReveive.setEditable(false);
    }
    
    /**
     * Get all pending shipments destined for this distributor
     */
    private List<ProductShippingRequest> getPendingShipments() {
        List<ProductShippingRequest> shipments = new ArrayList<>();
        
        // Search through all networks and enterprises for ProductShippingRequests
        // that are destined to this distributor and not yet received
        for (Network network : system.getNetworkList()) {
            for (Enterprise ent : network.getEnterpriseDirectory().getEnterpriseList()) {
                // Check enterprise work queue
                for (WorkRequest wr : ent.getWorkQueue().getWorkRequestList()) {
                    if (wr instanceof ProductShippingRequest) {
                        ProductShippingRequest psr = (ProductShippingRequest) wr;
                        if (isShipmentForThisDistributor(psr) && !isReceived(psr)) {
                            shipments.add(psr);
                        }
                    }
                }
                
                // Check organization work queues
                for (Organization org : ent.getOrganizationDirectory().getOrganizationList()) {
                    for (WorkRequest wr : org.getWorkQueue().getWorkRequestList()) {
                        if (wr instanceof ProductShippingRequest) {
                            ProductShippingRequest psr = (ProductShippingRequest) wr;
                            if (isShipmentForThisDistributor(psr) && !isReceived(psr)) {
                                // Avoid duplicates
                                if (!shipments.contains(psr)) {
                                    shipments.add(psr);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Also check the current organization's work queue
        for (WorkRequest wr : organization.getWorkQueue().getWorkRequestList()) {
            if (wr instanceof ProductShippingRequest) {
                ProductShippingRequest psr = (ProductShippingRequest) wr;
                if (!isReceived(psr) && !shipments.contains(psr)) {
                    shipments.add(psr);
                }
            }
        }
        
        return shipments;
    }
    
    /**
     * Get all received shipments (history)
     */
    private List<ProductShippingRequest> getReceivedShipments() {
        List<ProductShippingRequest> received = new ArrayList<>();
        
        // Search through all sources for received shipments
        for (Network network : system.getNetworkList()) {
            for (Enterprise ent : network.getEnterpriseDirectory().getEnterpriseList()) {
                for (WorkRequest wr : ent.getWorkQueue().getWorkRequestList()) {
                    if (wr instanceof ProductShippingRequest) {
                        ProductShippingRequest psr = (ProductShippingRequest) wr;
                        if (isShipmentForThisDistributor(psr) && isReceived(psr)) {
                            received.add(psr);
                        }
                    }
                }
                
                for (Organization org : ent.getOrganizationDirectory().getOrganizationList()) {
                    for (WorkRequest wr : org.getWorkQueue().getWorkRequestList()) {
                        if (wr instanceof ProductShippingRequest) {
                            ProductShippingRequest psr = (ProductShippingRequest) wr;
                            if (isShipmentForThisDistributor(psr) && isReceived(psr)) {
                                if (!received.contains(psr)) {
                                    received.add(psr);
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Check current organization
        for (WorkRequest wr : organization.getWorkQueue().getWorkRequestList()) {
            if (wr instanceof ProductShippingRequest) {
                ProductShippingRequest psr = (ProductShippingRequest) wr;
                if (isReceived(psr) && !received.contains(psr)) {
                    received.add(psr);
                }
            }
        }
        
        return received;
    }
    
    /**
     * Check if shipment is destined for this distributor
     */
    private boolean isShipmentForThisDistributor(ProductShippingRequest psr) {
        String destName = psr.getDestinationStoreName();
        String destAddr = psr.getDestinationAddress();
        String enterpriseName = enterprise.getName();
        
        // Check if destination matches this distributor
        if (destName != null && destName.toLowerCase().contains("distribution")) {
            return true;
        }
        if (destName != null && destName.toLowerCase().contains(enterpriseName.toLowerCase())) {
            return true;
        }
        if (destAddr != null && destAddr.toLowerCase().contains("distribution")) {
            return true;
        }
        
        // If no specific destination, check if it's in this organization's queue
        return organization.getWorkQueue().getWorkRequestList().contains(psr);
    }
    
    /**
     * Check if shipment has been received
     */
    private boolean isReceived(ProductShippingRequest psr) {
        return RECEIVE_STATUS_RECEIVED.equals(psr.getStatus()) || 
               (psr.getReceiver() != null && psr.getStatus() != null && 
                psr.getStatus().contains("Received"));
    }
    
    /**
     * Populate pending shipments table
     */
    private void populatePendingShipmentsTable() {
        DefaultTableModel model = (DefaultTableModel) tblShipments.getModel();
        model.setRowCount(0);
        pendingShipmentsList.clear();
        
        List<ProductShippingRequest> shipments = getPendingShipments();
        
        for (ProductShippingRequest psr : shipments) {
            Object[] row = new Object[6];
            row[0] = psr.getTrackingNumber() != null ? psr.getTrackingNumber() : "N/A";
            row[1] = psr.getProductCode() + " - " + psr.getProductName();
            row[2] = psr.getQuantity() + " " + (psr.getUnit() != null ? psr.getUnit() : "");
            row[3] = psr.getOriginAddress() != null ? psr.getOriginAddress() : "Manufacturer";
            row[4] = psr.getRequestDate() != null ? dateFormat.format(psr.getRequestDate()) : "N/A";
            row[5] = psr.getShippingStatus() != null ? psr.getShippingStatus() : "Pending";
            
            model.addRow(row);
            pendingShipmentsList.add(psr);
        }
    }
    
    /**
     * Populate pending shipments table with filter
     */
    private void populatePendingShipmentsTableWithFilter() {
        DefaultTableModel model = (DefaultTableModel) tblShipments.getModel();
        model.setRowCount(0);
        pendingShipmentsList.clear();
        
        List<ProductShippingRequest> shipments = getPendingShipments();
        
        // Get filter values
        String statusFilter = (String) cmbStatus.getSelectedItem();
        String dateFromStr = fieldDateFrom.getText().trim();
        String dateToStr = fieldDateTo.getText().trim();
        
        Date dateFrom = null;
        Date dateTo = null;
        
        try {
            if (!dateFromStr.isEmpty()) {
                dateFrom = dateFormat.parse(dateFromStr);
            }
            if (!dateToStr.isEmpty()) {
                dateTo = dateFormat.parse(dateToStr);
            }
        } catch (ParseException e) {
            // Ignore parse errors
        }
        
        for (ProductShippingRequest psr : shipments) {
            // Apply status filter
            if (!"All".equals(statusFilter)) {
                if (!statusFilter.equals(psr.getShippingStatus())) {
                    continue;
                }
            }
            
            // Apply date filter
            Date shipDate = psr.getRequestDate();
            if (dateFrom != null && shipDate != null && shipDate.before(dateFrom)) {
                continue;
            }
            if (dateTo != null && shipDate != null && shipDate.after(dateTo)) {
                continue;
            }
            
            Object[] row = new Object[6];
            row[0] = psr.getTrackingNumber() != null ? psr.getTrackingNumber() : "N/A";
            row[1] = psr.getProductCode() + " - " + psr.getProductName();
            row[2] = psr.getQuantity() + " " + (psr.getUnit() != null ? psr.getUnit() : "");
            row[3] = psr.getOriginAddress() != null ? psr.getOriginAddress() : "Manufacturer";
            row[4] = psr.getRequestDate() != null ? dateFormat.format(psr.getRequestDate()) : "N/A";
            row[5] = psr.getShippingStatus() != null ? psr.getShippingStatus() : "Pending";
            
            model.addRow(row);
            pendingShipmentsList.add(psr);
        }
    }
    
    /**
     * Populate received history table
     */
    private void populateReceivedTable() {
        DefaultTableModel model = (DefaultTableModel) tblReceived.getModel();
        model.setRowCount(0);
        
        List<ProductShippingRequest> received = getReceivedShipments();
        
        for (ProductShippingRequest psr : received) {
            Object[] row = new Object[5];
            row[0] = psr.getTrackingNumber() != null ? psr.getTrackingNumber() : "N/A";
            row[1] = psr.getProductCode() + " - " + psr.getProductName();
            row[2] = psr.getQuantity() + " " + (psr.getUnit() != null ? psr.getUnit() : "");
            row[3] = psr.getResolveDate() != null ? dateFormat.format(psr.getResolveDate()) : "N/A";
            // Show receiver's employee name instead of username
            if (psr.getReceiver() != null && psr.getReceiver().getEmployee() != null) {
                row[4] = psr.getReceiver().getEmployee().getName();
            } else if (psr.getReceiver() != null) {
                row[4] = psr.getReceiver().getUsername();
            } else {
                row[4] = "N/A";
            }
            
            model.addRow(row);
        }
    }
    
    /**
     * Update summary statistics
     */
    private void updateSummary() {
        List<ProductShippingRequest> shipments = getPendingShipments();
        
        int pending = 0;
        int inTransit = 0;
        int readyToReceive = 0;
        
        for (ProductShippingRequest psr : shipments) {
            String status = psr.getShippingStatus();
            if (ProductShippingRequest.SHIP_STATUS_PENDING.equals(status)) {
                pending++;
            } else if (ProductShippingRequest.SHIP_STATUS_IN_TRANSIT.equals(status)) {
                inTransit++;
            } else if (ProductShippingRequest.SHIP_STATUS_DELIVERED.equals(status) ||
                       ProductShippingRequest.SHIP_STATUS_OUT_FOR_DELIVERY.equals(status)) {
                readyToReceive++;
            }
        }
        
        fieldPending.setText(String.valueOf(pending));
        fieldInTransit.setText(String.valueOf(inTransit));
        fieldReadyToReveive.setText(String.valueOf(readyToReceive));
    }
    
    /**
     * Get selected shipment from table
     */
    private ProductShippingRequest getSelectedShipment() {
        int selectedRow = tblShipments.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= pendingShipmentsList.size()) {
            return null;
        }
        return pendingShipmentsList.get(selectedRow);
    }
    
    /**
     * Show shipment details dialog
     */
    private void showShipmentDetailsDialog(ProductShippingRequest shipment) {
        if (shipment == null) {
            JOptionPane.showMessageDialog(this, "Please select a shipment first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Shipment Details", JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Tracking Number
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Tracking Number:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(shipment.getTrackingNumber() != null ? shipment.getTrackingNumber() : "N/A"), gbc);
        
        // Product
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Product:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(shipment.getProductCode() + " - " + shipment.getProductName()), gbc);
        
        // Quantity
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(shipment.getQuantity() + " " + (shipment.getUnit() != null ? shipment.getUnit() : "")), gbc);
        
        // Origin
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Origin:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(shipment.getOriginAddress() != null ? shipment.getOriginAddress() : "N/A"), gbc);
        
        // Destination
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Destination:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(shipment.getDestinationAddress() != null ? shipment.getDestinationAddress() : "N/A"), gbc);
        
        // Ship Date
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Ship Date:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(shipment.getRequestDate() != null ? dateFormat.format(shipment.getRequestDate()) : "N/A"), gbc);
        
        // Expected Delivery
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Expected Delivery:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(shipment.getEstimatedDeliveryDate() != null ? dateFormat.format(shipment.getEstimatedDeliveryDate()) : "N/A"), gbc);
        
        // Carrier
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Carrier:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(shipment.getCarrierName() != null ? shipment.getCarrierName() : "N/A"), gbc);
        
        // Status
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(shipment.getShippingStatus() != null ? shipment.getShippingStatus() : "N/A"), gbc);
        
        // Message
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        mainPanel.add(new JLabel("Message:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(new JLabel(shipment.getMessage() != null ? shipment.getMessage() : "N/A"), gbc);
        
        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dialog.dispose());
        buttonPanel.add(btnClose);
        
        dialog.add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    /**
     * Show receive shipment dialog
     */
    private void showReceiveShipmentDialog(ProductShippingRequest shipment) {
        if (shipment == null) {
            JOptionPane.showMessageDialog(this, "Please select a shipment first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Check if shipment can be received (should be Delivered or Out for Delivery)
        String status = shipment.getShippingStatus();
        if (!ProductShippingRequest.SHIP_STATUS_DELIVERED.equals(status) && 
            !ProductShippingRequest.SHIP_STATUS_OUT_FOR_DELIVERY.equals(status)) {
            JOptionPane.showMessageDialog(this, 
                "This shipment is not ready to receive yet.\nCurrent status: " + status, 
                "Cannot Receive", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Receive Shipment", JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 450);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Shipment Info Panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Shipment Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(new JLabel("Tracking #:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(shipment.getTrackingNumber() != null ? shipment.getTrackingNumber() : "N/A"), gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        infoPanel.add(new JLabel("Product:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(shipment.getProductCode() + " - " + shipment.getProductName()), gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        infoPanel.add(new JLabel("Expected Qty:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(shipment.getQuantity() + " " + (shipment.getUnit() != null ? shipment.getUnit() : "")), gbc);
        
        mainPanel.add(infoPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // Receiving Details Panel
        JPanel receivePanel = new JPanel(new GridBagLayout());
        receivePanel.setBorder(BorderFactory.createTitledBorder("Receiving Details"));
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Actual Quantity Received
        gbc.gridx = 0; gbc.gridy = 0;
        receivePanel.add(new JLabel("Actual Qty Received:"), gbc);
        gbc.gridx = 1;
        JSpinner spinQty = new JSpinner(new SpinnerNumberModel(shipment.getQuantity(), 0, 999999, 1));
        spinQty.setPreferredSize(new Dimension(100, 25));
        receivePanel.add(spinQty, gbc);
        
        // Condition
        gbc.gridx = 0; gbc.gridy = 1;
        receivePanel.add(new JLabel("Condition:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> cmbCondition = new JComboBox<>(new String[]{"Good", "Damaged", "Partial"});
        receivePanel.add(cmbCondition, gbc);
        
        // Storage Location
        gbc.gridx = 0; gbc.gridy = 2;
        receivePanel.add(new JLabel("Storage Location:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> cmbLocation = new JComboBox<>(new String[]{
            "Distribution Center A",
            "Distribution Center B", 
            "Distribution Center C",
            "Distribution Center D",
            "Warehouse Main"
        });
        receivePanel.add(cmbLocation, gbc);
        
        // Notes
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        receivePanel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        JTextArea txtNotes = new JTextArea(3, 20);
        txtNotes.setLineWrap(true);
        JScrollPane notesScroll = new JScrollPane(txtNotes);
        receivePanel.add(notesScroll, gbc);
        
        // Update inventory checkbox
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        JCheckBox chkUpdateInventory = new JCheckBox("Update inventory automatically", true);
        receivePanel.add(chkUpdateInventory, gbc);
        
        mainPanel.add(receivePanel);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Cancel");
        JButton btnConfirm = new JButton("✅ Confirm Receipt");
        
        btnCancel.addActionListener(e -> dialog.dispose());
        
        btnConfirm.addActionListener(e -> {
            int actualQty = (Integer) spinQty.getValue();
            String condition = (String) cmbCondition.getSelectedItem();
            String location = (String) cmbLocation.getSelectedItem();
            String notes = txtNotes.getText();
            boolean updateInventory = chkUpdateInventory.isSelected();
            
            // Update shipment status
            shipment.setStatus(RECEIVE_STATUS_RECEIVED);
            shipment.setReceiver(account);
            shipment.setResolveDate(new Date());
            
            // Update inventory if checkbox is selected
            if (updateInventory) {
                updateInventoryForReceivedShipment(shipment, actualQty, location);
            }
            
            // Refresh tables
            populatePendingShipmentsTable();
            populateReceivedTable();
            updateSummary();
            
            // Show success message
            String message = "Shipment received successfully!\n\n" +
                            "Product: " + shipment.getProductName() + "\n" +
                            "Quantity: " + actualQty + "\n" +
                            "Condition: " + condition + "\n" +
                            "Location: " + location;
            if (updateInventory) {
                message += "\n\nInventory has been updated.";
            }
            
            JOptionPane.showMessageDialog(dialog, message, "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });
        
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnConfirm);
        
        dialog.add(new JScrollPane(mainPanel), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    /**
     * Update inventory when shipment is received
     */
    private void updateInventoryForReceivedShipment(ProductShippingRequest shipment, int actualQty, String location) {
        Inventory inventory = enterprise.getInventory();
        
        // Find product in inventory by code
        InventoryItem item = inventory.findByProductCode(shipment.getProductCode());
        
        if (item != null) {
            // Product exists - add quantity
            item.setQuantity(item.getQuantity() + actualQty);
            item.setLastUpdated(new Date());
        } else {
            // Product doesn't exist - try to add it
            // First, find the product in the catalog
            Product product = enterprise.getProductCatalog().findByCode(shipment.getProductCode());
            if (product != null) {
                inventory.addProduct(product, actualQty, 50, 1000, location);
            }
        }
    }
    
    /**
     * Refresh all data
     */
    public void refreshData() {
        populatePendingShipmentsTable();
        populateReceivedTable();
        updateSummary();
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
        lblTitle = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        lblPendingShipments = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblShipments = new javax.swing.JTable();
        lblFilter = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        lblDateRange = new javax.swing.JLabel();
        fieldDateFrom = new javax.swing.JTextField();
        lblDataTo = new javax.swing.JLabel();
        fieldDateTo = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnViewDetail = new javax.swing.JButton();
        btnReceiveShipment = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        lblReveived = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblReceived = new javax.swing.JTable();
        lblSummary = new javax.swing.JLabel();
        lblPending = new javax.swing.JLabel();
        fieldPending = new javax.swing.JTextField();
        lblInTransit = new javax.swing.JLabel();
        fieldInTransit = new javax.swing.JTextField();
        lblReadyToReceive = new javax.swing.JLabel();
        fieldReadyToReveive = new javax.swing.JTextField();

        lblTitle.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        lblTitle.setText("📦 Receive Shipment");

        btnBack.setText("<< Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        lblPendingShipments.setText("Pending Shipments");

        tblShipments.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tracking Number", "Product", "Qty", "From", "Ship Date", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblShipments);

        lblFilter.setText("Filter:");

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblDateRange.setText("Date Range: From");

        lblDataTo.setText("To");

        btnSearch.setText("🔍 Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnViewDetail.setText("📋 View Details");
        btnViewDetail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewDetailActionPerformed(evt);
            }
        });

        btnReceiveShipment.setText("✅ Receive Shipment");
        btnReceiveShipment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReceiveShipmentActionPerformed(evt);
            }
        });

        btnRefresh.setText("🔄 Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        lblReveived.setText("Recently Received (History)");

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

        lblSummary.setText("Summary:");

        lblPending.setText("Pending:");

        lblInTransit.setText("In Transit:");

        lblReadyToReceive.setText("Ready to Receive:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(120, 120, 120)
                                .addComponent(btnViewDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnReceiveShipment, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lblReveived)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator1)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnBack)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGap(6, 6, 6)
                                                .addComponent(lblFilter)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(lblDateRange))
                                            .addComponent(lblPendingShipments))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(fieldDateFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblDataTo)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(fieldDateTo, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(34, 34, 34)
                                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 60, Short.MAX_VALUE))
                            .addComponent(jScrollPane2))))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(91, 91, 91)
                .addComponent(lblSummary)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblPending)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(fieldPending, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblInTransit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(fieldInTransit, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblReadyToReceive)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(fieldReadyToReveive, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBack)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblPendingShipments)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFilter)
                    .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDateRange)
                    .addComponent(fieldDateFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDataTo)
                    .addComponent(fieldDateTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnViewDetail)
                    .addComponent(btnReceiveShipment)
                    .addComponent(btnRefresh))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblReveived)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSummary)
                    .addComponent(lblPending)
                    .addComponent(fieldPending, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblInTransit)
                    .addComponent(fieldInTransit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblReadyToReceive)
                    .addComponent(fieldReadyToReveive, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 20, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // TODO add your handling code here:
        userProcessContainer.remove(this);
        CardLayout layout = (CardLayout) userProcessContainer.getLayout();
        layout.previous(userProcessContainer);
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        populatePendingShipmentsTableWithFilter();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnViewDetailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewDetailActionPerformed
        // TODO add your handling code here:
        ProductShippingRequest shipment = getSelectedShipment();
        showShipmentDetailsDialog(shipment);
    }//GEN-LAST:event_btnViewDetailActionPerformed

    private void btnReceiveShipmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReceiveShipmentActionPerformed
        // TODO add your handling code here:
        ProductShippingRequest shipment = getSelectedShipment();
        showReceiveShipmentDialog(shipment);
    }//GEN-LAST:event_btnReceiveShipmentActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        refreshData();
    }//GEN-LAST:event_btnRefreshActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnReceiveShipment;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnViewDetail;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JTextField fieldDateFrom;
    private javax.swing.JTextField fieldDateTo;
    private javax.swing.JTextField fieldInTransit;
    private javax.swing.JTextField fieldPending;
    private javax.swing.JTextField fieldReadyToReveive;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblDataTo;
    private javax.swing.JLabel lblDateRange;
    private javax.swing.JLabel lblFilter;
    private javax.swing.JLabel lblInTransit;
    private javax.swing.JLabel lblPending;
    private javax.swing.JLabel lblPendingShipments;
    private javax.swing.JLabel lblReadyToReceive;
    private javax.swing.JLabel lblReveived;
    private javax.swing.JLabel lblSummary;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTable tblReceived;
    private javax.swing.JTable tblShipments;
    // End of variables declaration//GEN-END:variables
}
