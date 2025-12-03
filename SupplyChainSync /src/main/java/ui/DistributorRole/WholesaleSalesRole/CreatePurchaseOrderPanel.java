/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui.DistributorRole.WholesaleSalesRole;

import Business.EcoSystem;
import Business.Enterprise.Enterprise;
import Business.Enterprise.ManufacturerEnterprise;
import Business.Enterprise.ProductDistributorEnterprise;
import Business.Inventory.Inventory;
import Business.Inventory.InventoryItem;
import Business.Network.Network;
import Business.Organization.Distributor.WholesaleSalesOrganization;
import Business.Organization.Organization;
import Business.Product.Product;
import Business.UserAccount.UserAccount;
import Business.WorkQueue.WholesalePurchaseRequest;
import Business.WorkQueue.WorkRequest;
import java.awt.CardLayout;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author chris
 */
public class CreatePurchaseOrderPanel extends javax.swing.JPanel {

    private JPanel userProcessContainer;
    private UserAccount userAccount;
    private WholesaleSalesOrganization organization;
    private Enterprise enterprise;
    private EcoSystem system;
    private Inventory inventory;
    
    private ManufacturerEnterprise selectedManufacturer;
    private Product selectedProduct;
    
    /**
     * Creates new form CreatePurchaseOrderPanel
     */
    public CreatePurchaseOrderPanel(JPanel userProcessContainer, UserAccount account, WholesaleSalesOrganization wholesaleSalesOrganization, Enterprise enterprise, EcoSystem system) {
        initComponents();
        this.userProcessContainer = userProcessContainer;
        this.userAccount = account;
        this.organization = wholesaleSalesOrganization;
        this.enterprise = enterprise;
        this.system = system;
        
        this.inventory = getDistributorInventory();
        
        fieldCurrentStock.setEditable(false);
        fieldCurrentStock.setBackground(new java.awt.Color(240, 240, 240));

        initializeComboBoxes();
        
        addComboBoxListeners();
        
        populateInventoryTable();
        populateMyPurchaseOrdersTable();
    }
    
    private Inventory getDistributorInventory() {
        if (enterprise instanceof ProductDistributorEnterprise) {
            ProductDistributorEnterprise distEnterprise = (ProductDistributorEnterprise) enterprise;
            return distEnterprise.getInventory();
        }
        return enterprise.getInventory();
    }
    

    private void initializeComboBoxes() {
        cmbPriority.removeAllItems();
        cmbPriority.addItem("Normal");
        cmbPriority.addItem("Urgent");
        cmbPriority.addItem("Critical");
        
        populateManufacturerCombo();
        
        cmbProduct.removeAllItems();
        cmbProduct.addItem("-- Select Product --");
    }
    

    private void addComboBoxListeners() {
        cmbManufacturer.addActionListener(e -> {
            onManufacturerSelected();
        });
        
        cmbProduct.addActionListener(e -> {
            onProductSelected();
        });
    }
    

    private void populateManufacturerCombo() {
        cmbManufacturer.removeAllItems();
        cmbManufacturer.addItem("-- Select Manufacturer --");

        for (Network network : system.getNetworkList()) {
            for (Enterprise ent : network.getEnterpriseDirectory().getEnterpriseList()) {
                if (ent instanceof ManufacturerEnterprise) {
                    cmbManufacturer.addItem(ent.getName());
                }
            }
        }
    }

    
    private void onManufacturerSelected() {
        cmbProduct.removeAllItems();
        cmbProduct.addItem("-- Select Product --");
        fieldCurrentStock.setText("");
        selectedManufacturer = null;
        selectedProduct = null;
        
        int selectedIndex = cmbManufacturer.getSelectedIndex();
        if (selectedIndex <= 0) {
            return;
        }
        
        String manufacturerName = (String) cmbManufacturer.getSelectedItem();
        
        for (Network network : system.getNetworkList()) {
            for (Enterprise ent : network.getEnterpriseDirectory().getEnterpriseList()) {
                if (ent instanceof ManufacturerEnterprise && ent.getName().equals(manufacturerName)) {
                    selectedManufacturer = (ManufacturerEnterprise) ent;
                    populateProductCombo(selectedManufacturer);
                    return;
                }
            }
        }
    }
    

    private void populateProductCombo(ManufacturerEnterprise manufacturer) {
        cmbProduct.removeAllItems();
        cmbProduct.addItem("-- Select Product --");
        
        if (manufacturer.getProductCatalog() != null && 
            manufacturer.getProductCatalog().getProductList() != null) {
            for (Product product : manufacturer.getProductCatalog().getProductList()) {
                if (product.isActive()) {
                    cmbProduct.addItem(product.getProductCode() + " - " + product.getProductName());
                }
            }
        }
    }
    

    private void onProductSelected() {
        selectedProduct = null;
        fieldCurrentStock.setText("");
        
        int selectedIndex = cmbProduct.getSelectedIndex();
        if (selectedIndex <= 0 || selectedManufacturer == null) {
            return;
        }
        
        String productSelection = (String) cmbProduct.getSelectedItem();
        if (productSelection == null || !productSelection.contains(" - ")) {
            return;
        }
        
        String productCode = productSelection.split(" - ")[0].trim();
        
        selectedProduct = selectedManufacturer.getProductCatalog().findByCode(productCode);
        
        if (selectedProduct != null && inventory != null) {
            InventoryItem item = inventory.findByProductCode(selectedProduct.getProductCode());
            if (item != null) {
                fieldCurrentStock.setText(String.valueOf(item.getAvailableQuantity()) + " " + 
                    (selectedProduct.getUnit() != null ? selectedProduct.getUnit() : "units"));
            } else {
                fieldCurrentStock.setText("0 (Not in inventory)");
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
    

    private void populateMyPurchaseOrdersTable() {
        DefaultTableModel model = (DefaultTableModel) tblPurchaseOrder.getModel();
        model.setRowCount(0);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Network network : system.getNetworkList()) {
            for (Enterprise ent : network.getEnterpriseDirectory().getEnterpriseList()) {
                if (ent instanceof ManufacturerEnterprise) {
                    ManufacturerEnterprise manufacturer = (ManufacturerEnterprise) ent;
                    
                    for (Organization org : manufacturer.getOrganizationDirectory().getOrganizationList()) {
                        for (WorkRequest request : org.getWorkQueue().getWorkRequestList()) {
                            if (request instanceof WholesalePurchaseRequest) {
                                WholesalePurchaseRequest po = (WholesalePurchaseRequest) request;
                                
                                if (po.getSender() != null && 
                                    po.getSender().getUsername().equals(userAccount.getUsername())) {
                                    
                                    Object[] row = new Object[6];
                                    row[0] = "PO-" + String.format("%04d", po.getRequestId());
                                    row[1] = manufacturer.getName();
                                    row[2] = po.getProductName() != null ? po.getProductName() : "N/A";
                                    row[3] = po.getQuantity();
                                    row[4] = po.getRequestDate() != null ? 
                                        dateFormat.format(po.getRequestDate()) : "N/A";
                                    row[5] = po.getStatus();
                                    
                                    model.addRow(row);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    

    private boolean validateInput() {
        if (cmbManufacturer.getSelectedIndex() <= 0 || selectedManufacturer == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select a manufacturer.", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            cmbManufacturer.requestFocus();
            return false;
        }
        
        if (cmbProduct.getSelectedIndex() <= 0 || selectedProduct == null) {
            JOptionPane.showMessageDialog(this, 
                "Please select a product.", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            cmbProduct.requestFocus();
            return false;
        }
        
        String quantityStr = fieldQuantity.getText().trim();
        if (quantityStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a quantity.", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            fieldQuantity.requestFocus();
            return false;
        }
        
        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Quantity must be greater than 0.", 
                    "Validation Error", 
                    JOptionPane.WARNING_MESSAGE);
                fieldQuantity.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid number for quantity.", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            fieldQuantity.requestFocus();
            return false;
        }
        
        return true;
    }
    

    private void resetFields() {
        cmbManufacturer.setSelectedIndex(0);
        cmbProduct.removeAllItems();
        cmbProduct.addItem("-- Select Product --");
        fieldCurrentStock.setText("");
        fieldQuantity.setText("");
        cmbPriority.setSelectedIndex(0);
        fieldMessage.setText("");
        selectedManufacturer = null;
        selectedProduct = null;
    }
    

    private Organization findManufacturerReceivingOrg(ManufacturerEnterprise manufacturer) {
        for (Organization org : manufacturer.getOrganizationDirectory().getOrganizationList()) {
            if (org.getName().contains("Production") || org.getName().contains("Management")) {
                return org;
            }
        }
        if (!manufacturer.getOrganizationDirectory().getOrganizationList().isEmpty()) {
            return manufacturer.getOrganizationDirectory().getOrganizationList().get(0);
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
        lblCurrentInventory = new javax.swing.JLabel();
        lblPurchaseOrder = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPurchaseOrder = new javax.swing.JTable();
        lblCreateNewPurchaseOrder = new javax.swing.JLabel();
        lblManufacturer = new javax.swing.JLabel();
        lblProduct = new javax.swing.JLabel();
        lblQuantity = new javax.swing.JLabel();
        lblMessage = new javax.swing.JLabel();
        cmbManufacturer = new javax.swing.JComboBox<>();
        cmbProduct = new javax.swing.JComboBox<>();
        fieldQuantity = new javax.swing.JTextField();
        fieldMessage = new javax.swing.JTextField();
        btnSubmit = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblProductInventory = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        fieldSearchProduct = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnReset1 = new javax.swing.JButton();
        lblCurrentStock = new javax.swing.JLabel();
        fieldCurrentStock = new javax.swing.JTextField();
        lblPriority = new javax.swing.JLabel();
        cmbPriority = new javax.swing.JComboBox<>();

        lblTitle.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        lblTitle.setText("📤 Create Purchase Order");

        btnBack.setText("<< Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        lblCurrentInventory.setText("Current Inventory:");

        lblPurchaseOrder.setText("My Purchase Orders:");

        tblPurchaseOrder.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "PO ID", "Manufacturer", "Product", "Quantity", "Date", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblPurchaseOrder);

        lblCreateNewPurchaseOrder.setText("Create New Purchase Order");

        lblManufacturer.setText("Manufacturer:");

        lblProduct.setText("Product:");

        lblQuantity.setText("Quantity:");

        lblMessage.setText("Message:");

        cmbManufacturer.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cmbProduct.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnSubmit.setText("📨 Submit Purchase Order");
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });

        btnReset.setText("🔄 Reset Fields");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
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
        jScrollPane3.setViewportView(tblProductInventory);

        jLabel2.setText("Search Product:");

        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnReset1.setText("Reset");
        btnReset1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReset1ActionPerformed(evt);
            }
        });

        lblCurrentStock.setText("Current Stock:");

        lblPriority.setText("Priority:");

        cmbPriority.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

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
                            .addComponent(jScrollPane2)
                            .addComponent(jScrollPane3)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblPurchaseOrder)
                                    .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnBack)
                                    .addComponent(lblCreateNewPurchaseOrder))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblCurrentInventory)
                                .addGap(204, 204, 204)
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(fieldSearchProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnReset1, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblProduct)
                            .addComponent(lblManufacturer)
                            .addComponent(lblCurrentStock))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cmbManufacturer, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbProduct, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(fieldCurrentStock, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnSubmit)
                .addGap(80, 80, 80))
            .addGroup(layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblPriority)
                    .addComponent(lblQuantity)
                    .addComponent(lblMessage))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(fieldQuantity)
                        .addComponent(cmbPriority, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(fieldMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCurrentInventory)
                    .addComponent(jLabel2)
                    .addComponent(fieldSearchProduct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch)
                    .addComponent(btnReset1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(lblPurchaseOrder)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(lblCreateNewPurchaseOrder)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblManufacturer)
                    .addComponent(cmbManufacturer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProduct)
                    .addComponent(cmbProduct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCurrentStock)
                    .addComponent(fieldCurrentStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblPriority)
                            .addComponent(cmbPriority, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblMessage)
                            .addComponent(fieldMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(fieldQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblQuantity)))
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnReset)
                    .addComponent(btnSubmit))
                .addContainerGap(60, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

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

    private void btnReset1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReset1ActionPerformed
        // TODO add your handling code here:
        // // Only reset in tblProductInventory
        fieldSearchProduct.setText("");
        populateInventoryTable();
    }//GEN-LAST:event_btnReset1ActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        // TODO add your handling code here:
        resetFields();
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        // TODO add your handling code here:
        if (!validateInput()) {
            return;
        }
        
        int quantity = Integer.parseInt(fieldQuantity.getText().trim());
        String priority = (String) cmbPriority.getSelectedItem();
        String message = fieldMessage.getText().trim();
        
        WholesalePurchaseRequest purchaseOrder = new WholesalePurchaseRequest();
        purchaseOrder.setProduct(selectedProduct);
        purchaseOrder.setQuantity(quantity);
        purchaseOrder.setUrgencyLevel(priority);
        purchaseOrder.setSender(userAccount);
        purchaseOrder.setStatus("Pending");
        purchaseOrder.setMessage(message);
        
        Organization receivingOrg = findManufacturerReceivingOrg(selectedManufacturer);
        if (receivingOrg != null) {
            receivingOrg.getWorkQueue().getWorkRequestList().add(purchaseOrder);
            
            String priceInfo = "";
            if (selectedProduct.getUnitPrice() > 0) {
                priceInfo = String.format("\nEstimated Total: $%.2f", purchaseOrder.getTotalPrice());
            }
            
            JOptionPane.showMessageDialog(this, 
                "Purchase Order submitted successfully!\n" +
                "PO ID: PO-" + String.format("%04d", purchaseOrder.getRequestId()) + "\n" +
                "Product: " + selectedProduct.getProductName() + "\n" +
                "Quantity: " + quantity + " " + (selectedProduct.getUnit() != null ? selectedProduct.getUnit() : "units") + "\n" +
                "To: " + selectedManufacturer.getName() + "\n" +
                "Priority: " + priority + priceInfo, 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            resetFields();
            populateMyPurchaseOrdersTable();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Error: Could not find receiving organization at manufacturer.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSubmitActionPerformed

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
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnReset1;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JComboBox<String> cmbManufacturer;
    private javax.swing.JComboBox<String> cmbPriority;
    private javax.swing.JComboBox<String> cmbProduct;
    private javax.swing.JTextField fieldCurrentStock;
    private javax.swing.JTextField fieldMessage;
    private javax.swing.JTextField fieldQuantity;
    private javax.swing.JTextField fieldSearchProduct;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblCreateNewPurchaseOrder;
    private javax.swing.JLabel lblCurrentInventory;
    private javax.swing.JLabel lblCurrentStock;
    private javax.swing.JLabel lblManufacturer;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JLabel lblPriority;
    private javax.swing.JLabel lblProduct;
    private javax.swing.JLabel lblPurchaseOrder;
    private javax.swing.JLabel lblQuantity;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTable tblProductInventory;
    private javax.swing.JTable tblPurchaseOrder;
    // End of variables declaration//GEN-END:variables
}
