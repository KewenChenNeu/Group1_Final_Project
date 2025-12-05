/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui.RetailRole.StoreManagerRole;

import Business.EcoSystem;
import Business.Enterprise.Enterprise;
import Business.Enterprise.RetailEnterprise;
import Business.Inventory.Inventory;
import Business.Inventory.InventoryItem;
import Business.Organization.Retail.StoreManagementOrganization;
import Business.Product.Product;
import Business.UserAccount.UserAccount;
import java.awt.CardLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author zhifei
 */
public class ViewStoreInventoryPanel extends javax.swing.JPanel {

    private JPanel userProcessContainer;
    private UserAccount userAccount;
    private StoreManagementOrganization organization;
    private Enterprise enterprise;
    private EcoSystem system;
    private Inventory inventory;

    /**
     * Creates new form ViewStoreInventoryPanel
     */
    public ViewStoreInventoryPanel(JPanel userProcessContainer, UserAccount account, StoreManagementOrganization storeManagementOrganization, Enterprise enterprise, EcoSystem system) {
        initComponents();

        this.userProcessContainer = userProcessContainer;
        this.userAccount = account;
        this.organization = storeManagementOrganization;
        this.enterprise = enterprise;
        this.system = system;
        this.inventory = getRetailInventory();

        initializeCategoryComboBox();
        initializeStockStatusComboBox();
        populateInventoryTable();
        updateInventorySummary();

        fieldTotalProducts.setEditable(false);
        fieldLowStockItems.setEditable(false);
        fieldTotalQuantity.setEditable(false);
    }

    private Inventory getRetailInventory() {
        if (enterprise instanceof RetailEnterprise) {
            RetailEnterprise retailEnterprise = (RetailEnterprise) enterprise;
            return retailEnterprise.getInventory();
        }
        return enterprise.getInventory();
    }

    private void initializeCategoryComboBox() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("All Categories");

        if (enterprise.getProductCatalog() != null) {
            for (Product product : enterprise.getProductCatalog().getProductList()) {
                String category = product.getCategory();
                if (category != null && !category.isEmpty()) {
                    boolean exists = false;
                    for (int i = 0; i < model.getSize(); i++) {
                        if (model.getElementAt(i).equals(category)) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        model.addElement(category);
                    }
                }
            }
        }
        cmbCategory.setModel(model);
    }

    private void initializeStockStatusComboBox() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("All Status");
        model.addElement("OK");
        model.addElement("Low Stock");
        model.addElement("Critical");
        model.addElement("Out of Stock");
        cmbStockStatus.setModel(model);
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

    private void populateInventoryTableWithFilter() {
        DefaultTableModel model = (DefaultTableModel) tblInventory.getModel();
        model.setRowCount(0);

        if (inventory == null) return;

        String searchKeyword = fieldSearch.getText().toLowerCase().trim();
        String selectedCategory = (String) cmbCategory.getSelectedItem();
        String selectedStatus = (String) cmbStockStatus.getSelectedItem();

        for (InventoryItem item : inventory.getProductInventory()) {
            if (item.getProduct() != null) {
                String code = item.getProduct().getProductCode().toLowerCase();
                String name = item.getProduct().getProductName().toLowerCase();
                boolean matchesSearch = searchKeyword.isEmpty() ||
                        code.contains(searchKeyword) || name.contains(searchKeyword);

                String productCategory = item.getProduct().getCategory();
                boolean matchesCategory = "All Categories".equals(selectedCategory) ||
                        (productCategory != null && productCategory.equals(selectedCategory));

                String status = getStockStatus(item);
                boolean matchesStatus = "All Status".equals(selectedStatus) ||
                        status.contains(selectedStatus);

                if (matchesSearch && matchesCategory && matchesStatus) {
                    Object[] row = new Object[6];
                    row[0] = item.getProduct().getProductCode();
                    row[1] = item.getProduct().getProductName();
                    row[2] = item.getQuantity();
                    row[3] = item.getReservedQuantity();
                    row[4] = item.getAvailableQuantity();
                    row[5] = status;

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
            return "Low Stock";
        } else {
            return "OK";
        }
    }

    private void updateInventorySummary() {
        if (inventory == null) return;

        int totalProducts = 0;
        int lowStockCount = 0;
        int totalQuantity = 0;

        for (InventoryItem item : inventory.getProductInventory()) {
            if (item.getProduct() != null) {
                totalProducts++;
                totalQuantity += item.getQuantity();
                String status = getStockStatus(item);
                if (status.contains("Low") || status.contains("Critical") || status.contains("Out of Stock")) {
                    lowStockCount++;
                }
            }
        }

        fieldTotalProducts.setText(String.valueOf(totalProducts));
        fieldLowStockItems.setText(String.valueOf(lowStockCount));
        fieldTotalQuantity.setText(String.valueOf(totalQuantity));
    }

    private InventoryItem getSelectedInventoryItem() {
        int selectedRow = tblInventory.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }

        String productCode = (String) tblInventory.getValueAt(selectedRow, 0);
        return inventory.findByProductCode(productCode);
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
        lblSearchFilter = new javax.swing.JLabel();
        lblSearchProduct = new javax.swing.JLabel();
        fieldSearch = new javax.swing.JTextField();
        lblCategory = new javax.swing.JLabel();
        cmbCategory = new javax.swing.JComboBox<>();
        lblStockStatus = new javax.swing.JLabel();
        cmbStockStatus = new javax.swing.JComboBox<>();
        btnSearch = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        lblCurrentInventory = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblInventory = new javax.swing.JTable();
        lblInventorySummary = new javax.swing.JLabel();
        lblTotalProducts = new javax.swing.JLabel();
        fieldTotalProducts = new javax.swing.JTextField();
        lblLowStockItems = new javax.swing.JLabel();
        fieldLowStockItems = new javax.swing.JTextField();
        lblTotalQuantity = new javax.swing.JLabel();
        fieldTotalQuantity = new javax.swing.JTextField();
        lblActions = new javax.swing.JLabel();
        btnViewDetails = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();

        lblTitle.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        lblTitle.setText("View Store Inventory");

        btnBack.setText("<< Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        lblSearchFilter.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        lblSearchFilter.setText("Search & Filter:");

        lblSearchProduct.setText("Search Product:");

        lblCategory.setText("Category:");

        cmbCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Categories" }));

        lblStockStatus.setText("Stock Status:");

        cmbStockStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All Status" }));

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

        lblCurrentInventory.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        lblCurrentInventory.setText("Current Inventory:");

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
        jScrollPane1.setViewportView(tblInventory);

        lblInventorySummary.setFont(new java.awt.Font("Helvetica Neue", 1, 13)); // NOI18N
        lblInventorySummary.setText("Inventory Summary:");

        lblTotalProducts.setText("Total Products:");

        fieldTotalProducts.setEditable(false);
        fieldTotalProducts.setBackground(new java.awt.Color(204, 229, 255));
        fieldTotalProducts.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        lblLowStockItems.setText("Low Stock Items:");

        fieldLowStockItems.setEditable(false);
        fieldLowStockItems.setBackground(new java.awt.Color(255, 255, 204));
        fieldLowStockItems.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        lblTotalQuantity.setText("Total Quantity:");

        fieldTotalQuantity.setEditable(false);
        fieldTotalQuantity.setBackground(new java.awt.Color(204, 255, 204));
        fieldTotalQuantity.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        lblActions.setText("Actions:");

        btnViewDetails.setText("View Details");
        btnViewDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewDetailsActionPerformed(evt);
            }
        });

        btnRefresh.setText("Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
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
                            .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBack)
                            .addComponent(lblSearchFilter)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblSearchProduct)
                                    .addComponent(lblCategory)
                                    .addComponent(lblStockStatus))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(fieldSearch)
                                    .addComponent(cmbCategory, 0, 180, Short.MAX_VALUE)
                                    .addComponent(cmbStockStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(30, 30, 30)
                                .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblCurrentInventory)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblInventorySummary)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(lblTotalProducts)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(fieldTotalProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(lblLowStockItems)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(fieldLowStockItems, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(30, 30, 30)
                                .addComponent(lblTotalQuantity)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(fieldTotalQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(lblActions)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnViewDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 88, Short.MAX_VALUE)))
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
                .addGap(18, 18, 18)
                .addComponent(lblSearchFilter)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSearchProduct)
                    .addComponent(fieldSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCategory)
                    .addComponent(cmbCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStockStatus)
                    .addComponent(cmbStockStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch)
                    .addComponent(btnReset))
                .addGap(18, 18, 18)
                .addComponent(lblCurrentInventory)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblInventorySummary)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalProducts)
                    .addComponent(fieldTotalProducts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLowStockItems)
                    .addComponent(fieldLowStockItems, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotalQuantity)
                    .addComponent(fieldTotalQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblActions)
                    .addComponent(btnViewDetails)
                    .addComponent(btnRefresh))
                .addContainerGap(50, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        userProcessContainer.remove(this);
        CardLayout layout = (CardLayout) userProcessContainer.getLayout();
        layout.previous(userProcessContainer);
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        populateInventoryTableWithFilter();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        fieldSearch.setText("");
        cmbCategory.setSelectedIndex(0);
        cmbStockStatus.setSelectedIndex(0);
        populateInventoryTable();
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnViewDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewDetailsActionPerformed
        InventoryItem selectedItem = getSelectedInventoryItem();
        if (selectedItem == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a product from the table.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder details = new StringBuilder();
        details.append("Product Details\n");
        details.append("================\n\n");
        details.append("Product Code: ").append(selectedItem.getProduct().getProductCode()).append("\n");
        details.append("Product Name: ").append(selectedItem.getProduct().getProductName()).append("\n");
        details.append("Category: ").append(selectedItem.getProduct().getCategory() != null ? selectedItem.getProduct().getCategory() : "N/A").append("\n");
        details.append("Unit Price: $").append(String.format("%.2f", selectedItem.getProduct().getUnitPrice())).append("\n\n");
        details.append("Inventory Status\n");
        details.append("================\n\n");
        details.append("Quantity in Stock: ").append(selectedItem.getQuantity()).append("\n");
        details.append("Reserved Quantity: ").append(selectedItem.getReservedQuantity()).append("\n");
        details.append("Available Quantity: ").append(selectedItem.getAvailableQuantity()).append("\n");
        details.append("Min Stock Level: ").append(selectedItem.getMinStockLevel()).append("\n");
        details.append("Max Stock Level: ").append(selectedItem.getMaxStockLevel()).append("\n");
        details.append("Status: ").append(getStockStatus(selectedItem)).append("\n");

        JOptionPane.showMessageDialog(this, details.toString(), "Product Details", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnViewDetailsActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        populateInventoryTable();
        updateInventorySummary();
        JOptionPane.showMessageDialog(this, "Inventory data refreshed.", "Refresh", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnRefreshActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnViewDetails;
    private javax.swing.JComboBox<String> cmbCategory;
    private javax.swing.JComboBox<String> cmbStockStatus;
    private javax.swing.JTextField fieldLowStockItems;
    private javax.swing.JTextField fieldSearch;
    private javax.swing.JTextField fieldTotalProducts;
    private javax.swing.JTextField fieldTotalQuantity;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblActions;
    private javax.swing.JLabel lblCategory;
    private javax.swing.JLabel lblCurrentInventory;
    private javax.swing.JLabel lblInventorySummary;
    private javax.swing.JLabel lblLowStockItems;
    private javax.swing.JLabel lblSearchFilter;
    private javax.swing.JLabel lblSearchProduct;
    private javax.swing.JLabel lblStockStatus;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTotalProducts;
    private javax.swing.JLabel lblTotalQuantity;
    private javax.swing.JTable tblInventory;
    // End of variables declaration//GEN-END:variables
}
