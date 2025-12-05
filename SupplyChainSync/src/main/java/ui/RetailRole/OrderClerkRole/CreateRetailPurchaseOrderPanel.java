/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui.RetailRole.OrderClerkRole;

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
import Business.Product.Product;
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
public class CreateRetailPurchaseOrderPanel extends javax.swing.JPanel {

    private JPanel userProcessContainer;
    private UserAccount userAccount;
    private StoreManagementOrganization organization;
    private Enterprise enterprise;
    private EcoSystem system;
    private Inventory inventory;

    private ProductDistributorEnterprise selectedDistributor;
    private Product selectedProduct;

    /**
     * Creates new form CreateRetailPurchaseOrderPanel
     */
    public CreateRetailPurchaseOrderPanel(JPanel userProcessContainer, UserAccount account, StoreManagementOrganization storeManagementOrganization, Enterprise enterprise, EcoSystem system) {
        initComponents();
        this.userProcessContainer = userProcessContainer;
        this.userAccount = account;
        this.organization = storeManagementOrganization;
        this.enterprise = enterprise;
        this.system = system;

        this.inventory = getRetailInventory();

        txtCurrentStock.setEditable(false);
        txtCurrentStock.setBackground(new java.awt.Color(240, 240, 240));

        initializeComboBoxes();
        addComboBoxListeners();
        populateInventoryTable();
        populateMyOrdersTable();
    }

    private Inventory getRetailInventory() {
        if (enterprise instanceof RetailEnterprise) {
            RetailEnterprise retailEnterprise = (RetailEnterprise) enterprise;
            return retailEnterprise.getInventory();
        }
        return enterprise.getInventory();
    }

    private void initializeComboBoxes() {
        cmbPriority.removeAllItems();
        cmbPriority.addItem("Normal");
        cmbPriority.addItem("Urgent");
        cmbPriority.addItem("Critical");

        populateDistributorCombo();

        cmbProduct.removeAllItems();
        cmbProduct.addItem("-- Select Product --");
    }

    private void addComboBoxListeners() {
        cmbDistributor.addActionListener(e -> {
            onDistributorSelected();
        });

        cmbProduct.addActionListener(e -> {
            onProductSelected();
        });
    }

    private void populateDistributorCombo() {
        cmbDistributor.removeAllItems();
        cmbDistributor.addItem("-- Select Distributor --");

        for (Network network : system.getNetworkList()) {
            for (Enterprise ent : network.getEnterpriseDirectory().getEnterpriseList()) {
                if (ent instanceof ProductDistributorEnterprise) {
                    cmbDistributor.addItem(ent.getName());
                }
            }
        }
    }

    private void onDistributorSelected() {
        cmbProduct.removeAllItems();
        cmbProduct.addItem("-- Select Product --");
        txtCurrentStock.setText("");
        selectedDistributor = null;
        selectedProduct = null;

        int selectedIndex = cmbDistributor.getSelectedIndex();
        if (selectedIndex <= 0) {
            return;
        }

        String distributorName = (String) cmbDistributor.getSelectedItem();

        for (Network network : system.getNetworkList()) {
            for (Enterprise ent : network.getEnterpriseDirectory().getEnterpriseList()) {
                if (ent instanceof ProductDistributorEnterprise && ent.getName().equals(distributorName)) {
                    selectedDistributor = (ProductDistributorEnterprise) ent;
                    populateProductCombo(selectedDistributor);
                    return;
                }
            }
        }
    }

    private void populateProductCombo(ProductDistributorEnterprise distributor) {
        cmbProduct.removeAllItems();
        cmbProduct.addItem("-- Select Product --");

        if (distributor.getProductCatalog() != null &&
            distributor.getProductCatalog().getProductList() != null) {
            for (Product product : distributor.getProductCatalog().getProductList()) {
                if (product.isActive()) {
                    cmbProduct.addItem(product.getProductCode() + " - " + product.getProductName());
                }
            }
        }
    }

    private void onProductSelected() {
        selectedProduct = null;
        txtCurrentStock.setText("");

        int selectedIndex = cmbProduct.getSelectedIndex();
        if (selectedIndex <= 0 || selectedDistributor == null) {
            return;
        }

        String productSelection = (String) cmbProduct.getSelectedItem();
        if (productSelection == null || !productSelection.contains(" - ")) {
            return;
        }

        String productCode = productSelection.split(" - ")[0].trim();

        selectedProduct = selectedDistributor.getProductCatalog().findByCode(productCode);

        if (selectedProduct != null && inventory != null) {
            InventoryItem item = inventory.findByProductCode(selectedProduct.getProductCode());
            if (item != null) {
                txtCurrentStock.setText(String.valueOf(item.getAvailableQuantity()) + " " +
                    (selectedProduct.getUnit() != null ? selectedProduct.getUnit() : "units"));
            } else {
                txtCurrentStock.setText("0 (Not in inventory)");
            }
        }
    }

    private void populateInventoryTable() {
        DefaultTableModel model = (DefaultTableModel) tblInventory.getModel();
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
        DefaultTableModel model = (DefaultTableModel) tblInventory.getModel();
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
            return "Out of Stock";
        } else if (minLevel > 0 && available <= minLevel) {
            return "Critical";
        } else if (minLevel > 0 && available <= minLevel * 2) {
            return "Low";
        } else {
            return "OK";
        }
    }

    private void populateMyOrdersTable() {
        DefaultTableModel model = (DefaultTableModel) tblMyOrders.getModel();
        model.setRowCount(0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

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

                                    Object[] row = new Object[6];
                                    row[0] = "RO-" + String.format("%04d", po.getRequestId());
                                    row[1] = distributor.getName();
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
        if (cmbDistributor.getSelectedIndex() <= 0 || selectedDistributor == null) {
            JOptionPane.showMessageDialog(this,
                "Please select a distributor.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            cmbDistributor.requestFocus();
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

        String quantityStr = txtQuantity.getText().trim();
        if (quantityStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a quantity.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            txtQuantity.requestFocus();
            return false;
        }

        try {
            int quantity = Integer.parseInt(quantityStr);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this,
                    "Quantity must be greater than 0.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                txtQuantity.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid number for quantity.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            txtQuantity.requestFocus();
            return false;
        }

        return true;
    }

    private void resetFields() {
        cmbDistributor.setSelectedIndex(0);
        cmbProduct.removeAllItems();
        cmbProduct.addItem("-- Select Product --");
        txtCurrentStock.setText("");
        txtQuantity.setText("");
        cmbPriority.setSelectedIndex(0);
        txtMessage.setText("");
        selectedDistributor = null;
        selectedProduct = null;
    }

    private Organization findDistributorReceivingOrg(ProductDistributorEnterprise distributor) {
        for (Organization org : distributor.getOrganizationDirectory().getOrganizationList()) {
            if (org instanceof WholesaleSalesOrganization) {
                return org;
            }
        }
        if (!distributor.getOrganizationDirectory().getOrganizationList().isEmpty()) {
            return distributor.getOrganizationDirectory().getOrganizationList().get(0);
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
        lblMyOrders = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMyOrders = new javax.swing.JTable();
        lblCreateNewOrder = new javax.swing.JLabel();
        lblDistributor = new javax.swing.JLabel();
        lblProduct = new javax.swing.JLabel();
        lblQuantity = new javax.swing.JLabel();
        lblMessage = new javax.swing.JLabel();
        cmbDistributor = new javax.swing.JComboBox<>();
        cmbProduct = new javax.swing.JComboBox<>();
        txtQuantity = new javax.swing.JTextField();
        txtMessage = new javax.swing.JTextField();
        btnSubmit = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblInventory = new javax.swing.JTable();
        lblSearch = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnResetSearch = new javax.swing.JButton();
        lblCurrentStock = new javax.swing.JLabel();
        txtCurrentStock = new javax.swing.JTextField();
        lblPriority = new javax.swing.JLabel();
        cmbPriority = new javax.swing.JComboBox<>();

        lblTitle.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        lblTitle.setText("Create Purchase Order");

        btnBack.setText("<< Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        lblCurrentInventory.setText("Current Store Inventory:");

        lblMyOrders.setText("My Purchase Orders:");

        tblMyOrders.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Order ID", "Distributor", "Product", "Quantity", "Date", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblMyOrders);

        lblCreateNewOrder.setText("Create New Purchase Order:");

        lblDistributor.setText("Distributor:");

        lblProduct.setText("Product:");

        lblQuantity.setText("Quantity:");

        lblMessage.setText("Message:");

        btnSubmit.setText("Submit Order");
        btnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSubmitActionPerformed(evt);
            }
        });

        btnReset.setText("Reset");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        tblInventory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product Code", "Product Name", "In Stock", "Reserved", "Available", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblInventory);

        lblSearch.setText("Search:");

        btnSearch.setText("Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        btnResetSearch.setText("Reset");
        btnResetSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetSearchActionPerformed(evt);
            }
        });

        lblCurrentStock.setText("Current Stock:");

        lblPriority.setText("Priority:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSeparator1)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblMyOrders)
                            .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBack)
                            .addComponent(lblCreateNewOrder))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblCurrentInventory)
                        .addGap(150, 150, 150)
                        .addComponent(lblSearch)
                        .addGap(18, 18, 18)
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnResetSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 50, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblProduct)
                            .addComponent(lblDistributor)
                            .addComponent(lblCurrentStock)
                            .addComponent(lblQuantity)
                            .addComponent(lblPriority)
                            .addComponent(lblMessage))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnSubmit, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(cmbDistributor, 0, 200, Short.MAX_VALUE)
                                .addComponent(cmbProduct, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtCurrentStock)
                                .addComponent(txtQuantity)
                                .addComponent(cmbPriority, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(txtMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCurrentInventory)
                    .addComponent(lblSearch)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch)
                    .addComponent(btnResetSearch))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(lblMyOrders)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(lblCreateNewOrder)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDistributor)
                    .addComponent(cmbDistributor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProduct)
                    .addComponent(cmbProduct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCurrentStock)
                    .addComponent(txtCurrentStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblQuantity)
                    .addComponent(txtQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPriority)
                    .addComponent(cmbPriority, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMessage)
                    .addComponent(txtMessage, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnReset)
                    .addComponent(btnSubmit))
                .addContainerGap(50, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a search term.",
                "Empty Search", JOptionPane.WARNING_MESSAGE);
            return;
        }
        populateInventoryTableWithSearch(keyword);
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnResetSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetSearchActionPerformed
        txtSearch.setText("");
        populateInventoryTable();
    }//GEN-LAST:event_btnResetSearchActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        resetFields();
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSubmitActionPerformed
        if (!validateInput()) {
            return;
        }

        int quantity = Integer.parseInt(txtQuantity.getText().trim());
        String priority = (String) cmbPriority.getSelectedItem();
        String message = txtMessage.getText().trim();

        RetailPurchaseOrderRequest purchaseOrder = new RetailPurchaseOrderRequest();
        purchaseOrder.setProduct(selectedProduct);
        purchaseOrder.setQuantity(quantity);
        purchaseOrder.setUrgencyLevel(priority);
        purchaseOrder.setSender(userAccount);
        purchaseOrder.setStatus("Pending");
        purchaseOrder.setMessage(message);
        purchaseOrder.setStoreName(enterprise.getName());

        Organization receivingOrg = findDistributorReceivingOrg(selectedDistributor);
        if (receivingOrg != null) {
            receivingOrg.getWorkQueue().getWorkRequestList().add(purchaseOrder);

            String priceInfo = "";
            if (selectedProduct.getUnitPrice() > 0) {
                priceInfo = String.format("\nEstimated Total: $%.2f", purchaseOrder.getTotalPrice());
            }

            JOptionPane.showMessageDialog(this,
                "Purchase Order submitted successfully!\n" +
                "Order ID: RO-" + String.format("%04d", purchaseOrder.getRequestId()) + "\n" +
                "Product: " + selectedProduct.getProductName() + "\n" +
                "Quantity: " + quantity + " " + (selectedProduct.getUnit() != null ? selectedProduct.getUnit() : "units") + "\n" +
                "To: " + selectedDistributor.getName() + "\n" +
                "Priority: " + priority + priceInfo,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

            resetFields();
            populateMyOrdersTable();
        } else {
            JOptionPane.showMessageDialog(this,
                "Error: Could not find receiving organization at distributor.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSubmitActionPerformed

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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnResetSearch;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSubmit;
    private javax.swing.JComboBox<String> cmbDistributor;
    private javax.swing.JComboBox<String> cmbPriority;
    private javax.swing.JComboBox<String> cmbProduct;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblCreateNewOrder;
    private javax.swing.JLabel lblCurrentInventory;
    private javax.swing.JLabel lblCurrentStock;
    private javax.swing.JLabel lblDistributor;
    private javax.swing.JLabel lblMessage;
    private javax.swing.JLabel lblMyOrders;
    private javax.swing.JLabel lblPriority;
    private javax.swing.JLabel lblProduct;
    private javax.swing.JLabel lblQuantity;
    private javax.swing.JLabel lblSearch;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTable tblInventory;
    private javax.swing.JTable tblMyOrders;
    private javax.swing.JTextField txtCurrentStock;
    private javax.swing.JTextField txtMessage;
    private javax.swing.JTextField txtQuantity;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
