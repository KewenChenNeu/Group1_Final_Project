/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui.DistributorRole.WholesaleInventoryRole;

import Business.EcoSystem;
import Business.Enterprise.Enterprise;
import Business.Inventory.Inventory;
import Business.Inventory.InventoryItem;
import Business.Organization.Distributor.WholesaleInventoryOrganization;
import Business.Product.Product;
import Business.UserAccount.UserAccount;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

/**
 *
 * @author chris
 */
public class ProductDetailPanel extends javax.swing.JPanel {

    private JPanel userProcessContainer;
    private UserAccount account;
    private WholesaleInventoryOrganization organization;
    private Enterprise enterprise;
    private EcoSystem system;
    private InventoryItem selectedItem;
    
    /**
     * Creates new form ProductDetailPanel
     */
    public ProductDetailPanel(JPanel userProcessContainer, UserAccount account, WholesaleInventoryOrganization wholesaleInventoryOrganization, Enterprise enterprise, EcoSystem system, InventoryItem selectedItem) {
        initComponents();
        
        this.userProcessContainer = userProcessContainer;
        this.account = account;
        this.organization = wholesaleInventoryOrganization;
        this.enterprise = enterprise;
        this.system = system;
        this.selectedItem = selectedItem;
        
        // Make all text fields read-only
        setFieldsReadOnly();
        
        // Populate the product details
        populateProductDetails();
    }
    
    /**
     * Set all text fields as read-only
     */
    private void setFieldsReadOnly() {
        fieldProductCode.setEditable(false);
        fieldProductName.setEditable(false);
        fieldCategory.setEditable(false);
        fieldDescription.setEditable(false);
        fieldUnitPrice.setEditable(false);
        fieldUnit.setEditable(false);
        fieldStatus.setEditable(false);
        fieldCurrentQuantity.setEditable(false);
        fieldReservedQuantity.setEditable(false);
        fieldAvailaleQuantity.setEditable(false);
        fieldMinStockLevel.setEditable(false);
        fieldMaxStockLevel.setEditable(false);
        fieldLocation.setEditable(false);
        fieldLastUpdated.setEditable(false);
        fieldStockStatus.setEditable(false);
    }
    
    /**
     * Populate all fields with product and inventory information
     */
    private void populateProductDetails() {
        if (selectedItem == null) {
            return;
        }
        
        Product product = selectedItem.getProduct();
        
        // Basic Information
        if (product != null) {
            fieldProductCode.setText(product.getProductCode() != null ? product.getProductCode() : "N/A");
            fieldProductName.setText(product.getProductName() != null ? product.getProductName() : "N/A");
            fieldCategory.setText(product.getCategory() != null ? product.getCategory() : "N/A");
            fieldDescription.setText(product.getDescription() != null ? product.getDescription() : "N/A");
            fieldUnitPrice.setText(String.format("$%.2f", product.getUnitPrice()));
            fieldUnit.setText(product.getUnit() != null ? product.getUnit() : "N/A");
            fieldStatus.setText(product.isActive() ? "Active" : "Inactive");
        }
        
        // Inventory Information
        fieldCurrentQuantity.setText(String.valueOf(selectedItem.getQuantity()));
        fieldReservedQuantity.setText(String.valueOf(selectedItem.getReservedQuantity()));
        fieldAvailaleQuantity.setText(String.valueOf(selectedItem.getAvailableQuantity()));
        fieldMinStockLevel.setText(String.valueOf(selectedItem.getMinStockLevel()));
        fieldMaxStockLevel.setText(String.valueOf(selectedItem.getMaxStockLevel()));
        fieldLocation.setText(selectedItem.getLocation() != null ? selectedItem.getLocation() : "N/A");
        
        // Last Updated
        if (selectedItem.getLastUpdated() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            fieldLastUpdated.setText(sdf.format(selectedItem.getLastUpdated()));
        } else {
            fieldLastUpdated.setText("N/A");
        }
        
        // Stock Status
        fieldStockStatus.setText(getStockStatus(selectedItem));
    }
    
    /**
     * Get stock status string based on inventory levels
     */
    private String getStockStatus(InventoryItem item) {
        int available = item.getAvailableQuantity();
        int minStock = item.getMinStockLevel();
        
        if (available <= 0) {
            return "🔴 Out of Stock";
        } else if (available <= minStock) {
            return "🔴 Critical";
        } else if (available <= minStock * 2) {
            return "⚠️ Low Stock";
        } else {
            return "✅ OK";
        }
    }
    
    /**
     * Show the Edit Stock dialog
     */
    private void showEditStockDialog() {
        if (selectedItem == null) {
            JOptionPane.showMessageDialog(this, "No product selected.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Product product = selectedItem.getProduct();
        
        // Create dialog
        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Edit Stock - " + product.getProductName(), JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Current Info Panel
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Current Stock Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(new JLabel("Product:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(product.getProductCode() + " - " + product.getProductName()), gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        infoPanel.add(new JLabel("Current Quantity:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(String.valueOf(selectedItem.getQuantity())), gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        infoPanel.add(new JLabel("Reserved:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(String.valueOf(selectedItem.getReservedQuantity())), gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        infoPanel.add(new JLabel("Available:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(String.valueOf(selectedItem.getAvailableQuantity())), gbc);
        
        mainPanel.add(infoPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // Adjustment Panel
        JPanel adjustPanel = new JPanel(new GridBagLayout());
        adjustPanel.setBorder(BorderFactory.createTitledBorder("Stock Adjustment"));
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Radio buttons for adjustment type
        JRadioButton rbAdd = new JRadioButton("Add Stock");
        JRadioButton rbRemove = new JRadioButton("Remove Stock");
        JRadioButton rbSet = new JRadioButton("Set Exact Value");
        rbAdd.setSelected(true);
        
        ButtonGroup adjustGroup = new ButtonGroup();
        adjustGroup.add(rbAdd);
        adjustGroup.add(rbRemove);
        adjustGroup.add(rbSet);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3;
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.add(rbAdd);
        radioPanel.add(rbRemove);
        radioPanel.add(rbSet);
        adjustPanel.add(radioPanel, gbc);
        
        // Quantity
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        adjustPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        JSpinner spinQuantity = new JSpinner(new SpinnerNumberModel(0, 0, 999999, 1));
        spinQuantity.setPreferredSize(new Dimension(100, 25));
        adjustPanel.add(spinQuantity, gbc);
        
        // Reason
        gbc.gridx = 0; gbc.gridy = 2;
        adjustPanel.add(new JLabel("Reason:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        JComboBox<String> cbReason = new JComboBox<>(new String[]{
            "Inventory Count",
            "Damaged Goods",
            "Correction",
            "Received Shipment",
            "Other"
        });
        adjustPanel.add(cbReason, gbc);
        
        // Notes
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 3;
        adjustPanel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        JTextField txtNotes = new JTextField(20);
        adjustPanel.add(txtNotes, gbc);
        
        mainPanel.add(adjustPanel);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Cancel");
        JButton btnConfirm = new JButton("Confirm");
        
        btnCancel.addActionListener(e -> dialog.dispose());
        
        btnConfirm.addActionListener(e -> {
            int quantity = (Integer) spinQuantity.getValue();
            int currentQty = selectedItem.getQuantity();
            int reservedQty = selectedItem.getReservedQuantity();
            int newQty;
            
            if (rbAdd.isSelected()) {
                newQty = currentQty + quantity;
            } else if (rbRemove.isSelected()) {
                newQty = currentQty - quantity;
                if (newQty < 0) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Cannot remove more than current quantity.", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (newQty < reservedQty) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Cannot reduce quantity below reserved amount (" + reservedQty + ").", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else { // Set exact
                newQty = quantity;
                if (newQty < reservedQty) {
                    JOptionPane.showMessageDialog(dialog, 
                        "Cannot set quantity below reserved amount (" + reservedQty + ").", 
                        "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Update the inventory item
            selectedItem.setQuantity(newQty);
            
            // Refresh the display
            populateProductDetails();
            
            JOptionPane.showMessageDialog(dialog, 
                "Stock updated successfully!\nNew quantity: " + newQty, 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });
        
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnConfirm);
        
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
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
        lblTitle = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        lblBasicInformation = new javax.swing.JLabel();
        lblProductCode = new javax.swing.JLabel();
        lblProdcutName = new javax.swing.JLabel();
        lblCategory = new javax.swing.JLabel();
        lblDescription = new javax.swing.JLabel();
        lblUnitPrice = new javax.swing.JLabel();
        lblUnit = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        lblInventoryInformation = new javax.swing.JLabel();
        lblCurrentQuantity = new javax.swing.JLabel();
        lblReservedQuantity = new javax.swing.JLabel();
        lblAvailableQuantity = new javax.swing.JLabel();
        lblMinStockLevel = new javax.swing.JLabel();
        lblMaxStockLevel = new javax.swing.JLabel();
        lblLocation = new javax.swing.JLabel();
        lblLastUpdated = new javax.swing.JLabel();
        lblStockStatus = new javax.swing.JLabel();
        fieldProductCode = new javax.swing.JTextField();
        fieldProductName = new javax.swing.JTextField();
        fieldCategory = new javax.swing.JTextField();
        fieldDescription = new javax.swing.JTextField();
        fieldUnitPrice = new javax.swing.JTextField();
        fieldUnit = new javax.swing.JTextField();
        fieldStatus = new javax.swing.JTextField();
        fieldCurrentQuantity = new javax.swing.JTextField();
        fieldReservedQuantity = new javax.swing.JTextField();
        fieldAvailaleQuantity = new javax.swing.JTextField();
        fieldMinStockLevel = new javax.swing.JTextField();
        fieldMaxStockLevel = new javax.swing.JTextField();
        fieldLocation = new javax.swing.JTextField();
        fieldLastUpdated = new javax.swing.JTextField();
        fieldStockStatus = new javax.swing.JTextField();
        btnEditStock = new javax.swing.JButton();

        btnBack.setText("<< Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        lblTitle.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        lblTitle.setText("📦 Product Details");

        lblBasicInformation.setText("Basic Information");

        lblProductCode.setText("Product Code:");

        lblProdcutName.setText("Product Name:");

        lblCategory.setText("Category:");

        lblDescription.setText("Description:");

        lblUnitPrice.setText("Unit Price:");

        lblUnit.setText("Unit:");

        lblStatus.setText("Status:");

        lblInventoryInformation.setText("Inventory Information:");

        lblCurrentQuantity.setText("Current Quantity:");

        lblReservedQuantity.setText("Reserved Quantity:");

        lblAvailableQuantity.setText("Available Quantity:");

        lblMinStockLevel.setText("Min Stock Level:");

        lblMaxStockLevel.setText("Max Stock Level:");

        lblLocation.setText("Location:");

        lblLastUpdated.setText("Last Updated:");

        lblStockStatus.setText("Stock Status:");

        btnEditStock.setText("✏ Edit Stock");
        btnEditStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditStockActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator1)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnBack))
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblBasicInformation)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(lblReservedQuantity)
                                            .addComponent(lblCurrentQuantity)
                                            .addComponent(lblAvailableQuantity)
                                            .addComponent(lblMinStockLevel)
                                            .addComponent(lblMaxStockLevel)
                                            .addComponent(lblLocation)
                                            .addComponent(lblLastUpdated)
                                            .addComponent(lblStatus)
                                            .addComponent(lblUnit)
                                            .addComponent(lblUnitPrice)
                                            .addComponent(lblDescription)
                                            .addComponent(lblCategory)
                                            .addComponent(lblProdcutName)
                                            .addComponent(lblProductCode)))
                                    .addComponent(lblInventoryInformation))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(fieldProductCode, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fieldProductName, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fieldCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fieldDescription, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fieldUnitPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fieldUnit, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fieldStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fieldCurrentQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fieldReservedQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fieldAvailaleQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fieldMinStockLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fieldLastUpdated, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fieldMaxStockLevel, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(fieldLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(47, 47, 47)
                                .addComponent(lblStockStatus)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(fieldStockStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 362, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnEditStock, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(90, 90, 90))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBack)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblBasicInformation)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProductCode)
                    .addComponent(fieldProductCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProdcutName)
                    .addComponent(fieldProductName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCategory)
                    .addComponent(fieldCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDescription)
                    .addComponent(fieldDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUnitPrice)
                    .addComponent(fieldUnitPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUnit)
                    .addComponent(fieldUnit, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStatus)
                    .addComponent(fieldStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15)
                .addComponent(lblInventoryInformation)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCurrentQuantity)
                    .addComponent(fieldCurrentQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblReservedQuantity)
                    .addComponent(fieldReservedQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblAvailableQuantity)
                    .addComponent(fieldAvailaleQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMinStockLevel)
                    .addComponent(fieldMinStockLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMaxStockLevel)
                    .addComponent(fieldMaxStockLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLocation)
                    .addComponent(fieldLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLastUpdated)
                    .addComponent(fieldLastUpdated, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStockStatus)
                    .addComponent(fieldStockStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnEditStock)
                .addContainerGap(56, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // TODO add your handling code here:
        // Navigate back to ViewInventoryPanel
        userProcessContainer.remove(this);
        
        // Find ViewInventoryPanel and refresh its table
        Component[] components = userProcessContainer.getComponents();
        for (Component comp : components) {
            if (comp instanceof ViewInventoryPanel) {
                ((ViewInventoryPanel) comp).refreshTable();
                break;
            }
        }
        
        CardLayout layout = (CardLayout) userProcessContainer.getLayout();
        layout.previous(userProcessContainer);

    }//GEN-LAST:event_btnBackActionPerformed

    private void btnEditStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditStockActionPerformed
        // TODO add your handling code here:
        showEditStockDialog();
    }//GEN-LAST:event_btnEditStockActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnEditStock;
    private javax.swing.JTextField fieldAvailaleQuantity;
    private javax.swing.JTextField fieldCategory;
    private javax.swing.JTextField fieldCurrentQuantity;
    private javax.swing.JTextField fieldDescription;
    private javax.swing.JTextField fieldLastUpdated;
    private javax.swing.JTextField fieldLocation;
    private javax.swing.JTextField fieldMaxStockLevel;
    private javax.swing.JTextField fieldMinStockLevel;
    private javax.swing.JTextField fieldProductCode;
    private javax.swing.JTextField fieldProductName;
    private javax.swing.JTextField fieldReservedQuantity;
    private javax.swing.JTextField fieldStatus;
    private javax.swing.JTextField fieldStockStatus;
    private javax.swing.JTextField fieldUnit;
    private javax.swing.JTextField fieldUnitPrice;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblAvailableQuantity;
    private javax.swing.JLabel lblBasicInformation;
    private javax.swing.JLabel lblCategory;
    private javax.swing.JLabel lblCurrentQuantity;
    private javax.swing.JLabel lblDescription;
    private javax.swing.JLabel lblInventoryInformation;
    private javax.swing.JLabel lblLastUpdated;
    private javax.swing.JLabel lblLocation;
    private javax.swing.JLabel lblMaxStockLevel;
    private javax.swing.JLabel lblMinStockLevel;
    private javax.swing.JLabel lblProdcutName;
    private javax.swing.JLabel lblProductCode;
    private javax.swing.JLabel lblReservedQuantity;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblStockStatus;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUnit;
    private javax.swing.JLabel lblUnitPrice;
    // End of variables declaration//GEN-END:variables
}
