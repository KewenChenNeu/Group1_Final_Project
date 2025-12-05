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
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
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
public class ViewInventoryPanel extends javax.swing.JPanel {

    private JPanel userProcessContainer;
    private UserAccount userAccount;
    private WholesaleInventoryOrganization organization;
    private Enterprise enterprise;
    private EcoSystem system;
    private Inventory inventory;
    
    /**
     * Creates new form ViewInventoryPanel
     */
    public ViewInventoryPanel(JPanel userProcessContainer, UserAccount account, WholesaleInventoryOrganization wholesaleInventoryOrganization, Enterprise enterprise, EcoSystem system) {
        initComponents();
        
        this.userProcessContainer = userProcessContainer;
        this.userAccount = account;
        this.organization = wholesaleInventoryOrganization;
        this.enterprise = enterprise;
        this.system = system;
        this.inventory = enterprise.getInventory();
        
        // Initialize UI components
        initializeCategoryComboBox();
        initializeStockStatusComboBox();
        
        // Populate data
        populateInventoryTable();
        updateInventorySummary();
        
        // Make summary fields read-only
        fieldTotalProducts.setEditable(false);
        fieldLowStockStatus.setEditable(false);
        fieldTotalQuantity.setEditable(false);
    }
    
    /**
     * Initialize category combo box with available categories
     */
    private void initializeCategoryComboBox() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("All Categories");
        
        // Get unique categories from product catalog
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
        cmbCategory.setModel(model);
    }
    
    /**
     * Initialize stock status combo box
     */
    private void initializeStockStatusComboBox() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        model.addElement("All Status");
        model.addElement("OK");
        model.addElement("Low Stock");
        model.addElement("Critical");
        model.addElement("Out of Stock");
        jComboBox1.setModel(model);
    }
    
    /**
     * Populate inventory table with all products
     */
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
    
    /**
     * Populate inventory table with search and filter criteria
     */
    private void populateInventoryTableWithFilter() {
        DefaultTableModel model = (DefaultTableModel) tblProductInventory.getModel();
        model.setRowCount(0);
        
        if (inventory == null) return;
        
        String searchKeyword = fieldSearchProduct.getText().toLowerCase().trim();
        String selectedCategory = (String) cmbCategory.getSelectedItem();
        String selectedStatus = (String) jComboBox1.getSelectedItem();
        
        for (InventoryItem item : inventory.getProductInventory()) {
            if (item.getProduct() != null) {
                // Check search keyword
                String code = item.getProduct().getProductCode().toLowerCase();
                String name = item.getProduct().getProductName().toLowerCase();
                boolean matchesSearch = searchKeyword.isEmpty() || 
                        code.contains(searchKeyword) || name.contains(searchKeyword);
                
                // Check category filter
                String productCategory = item.getProduct().getCategory();
                boolean matchesCategory = "All Categories".equals(selectedCategory) ||
                        (productCategory != null && productCategory.equals(selectedCategory));
                
                // Check stock status filter
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
    
    /**
     * Get stock status string for an inventory item
     */
    private String getStockStatus(InventoryItem item) {
        int available = item.getAvailableQuantity();
        int minLevel = item.getMinStockLevel();
        
        if (available <= 0) {
            return "🔴 Out of Stock";
        } else if (minLevel > 0 && available <= minLevel) {
            return "🔴 Critical";
        } else if (minLevel > 0 && available <= minLevel * 2) {
            return "⚠️ Low Stock";
        } else {
            return "✅ OK";
        }
    }
    
    /**
     * Update the inventory summary section
     */
    private void updateInventorySummary() {
        if (inventory == null) return;
        
        int totalProducts = inventory.getProductInventory().size();
        int lowStockCount = 0;
        int totalQuantity = 0;
        
        for (InventoryItem item : inventory.getProductInventory()) {
            totalQuantity += item.getQuantity();
            String status = getStockStatus(item);
            if (status.contains("Low") || status.contains("Critical") || status.contains("Out of Stock")) {
                lowStockCount++;
            }
        }
        
        fieldTotalProducts.setText(String.valueOf(totalProducts));
        fieldLowStockStatus.setText(String.valueOf(lowStockCount));
        fieldTotalQuantity.setText(String.valueOf(totalQuantity));
    }
    
    /**
     * Get selected inventory item from table
     */
    private InventoryItem getSelectedInventoryItem() {
        int selectedRow = tblProductInventory.getSelectedRow();
        if (selectedRow < 0) {
            return null;
        }
        
        String productCode = (String) tblProductInventory.getValueAt(selectedRow, 0);
        return inventory.findByProductCode(productCode);
    }
    
    /**
     * Show Edit Stock Dialog
     */
    private void showEditStockDialog(InventoryItem item) {
        JDialog dialog = new JDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                "✏️ Edit Stock",
                true
        );
        dialog.setSize(400, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        
        // Main panel with padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Product info
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Product Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(new JLabel("Product:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(item.getProduct().getProductCode() + " - " + item.getProduct().getProductName()), gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        infoPanel.add(new JLabel("Current Quantity:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(String.valueOf(item.getQuantity())), gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        infoPanel.add(new JLabel("Reserved:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(String.valueOf(item.getReservedQuantity())), gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        infoPanel.add(new JLabel("Available:"), gbc);
        gbc.gridx = 1;
        infoPanel.add(new JLabel(String.valueOf(item.getAvailableQuantity())), gbc);
        
        mainPanel.add(infoPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        
        // Adjustment panel
        JPanel adjustPanel = new JPanel(new GridBagLayout());
        adjustPanel.setBorder(BorderFactory.createTitledBorder("Stock Adjustment"));
        
        // Radio buttons for adjustment type
        JRadioButton rbAdd = new JRadioButton("Add Stock");
        JRadioButton rbRemove = new JRadioButton("Remove Stock");
        JRadioButton rbSetExact = new JRadioButton("Set Exact Value");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbAdd);
        bg.add(rbRemove);
        bg.add(rbSetExact);
        rbAdd.setSelected(true);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        adjustPanel.add(rbAdd, gbc);
        gbc.gridy = 1;
        adjustPanel.add(rbRemove, gbc);
        gbc.gridy = 2;
        adjustPanel.add(rbSetExact, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy = 3; gbc.gridx = 0;
        adjustPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        JSpinner spinQuantity = new JSpinner(new SpinnerNumberModel(0, 0, 999999, 1));
        spinQuantity.setPreferredSize(new Dimension(100, 25));
        adjustPanel.add(spinQuantity, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        adjustPanel.add(new JLabel("Reason:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> cmbReason = new JComboBox<>(new String[]{
                "Inventory Count", "Damaged Goods", "Correction", "Received Shipment", "Other"
        });
        adjustPanel.add(cmbReason, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        adjustPanel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        JTextField txtNotes = new JTextField(15);
        adjustPanel.add(txtNotes, gbc);
        
        mainPanel.add(adjustPanel);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancel = new JButton("Cancel");
        JButton btnConfirm = new JButton("✓ Confirm");
        
        btnCancel.addActionListener(e -> dialog.dispose());
        
        btnConfirm.addActionListener(e -> {
            int quantity = (Integer) spinQuantity.getValue();
            if (quantity <= 0 && !rbSetExact.isSelected()) {
                JOptionPane.showMessageDialog(dialog, 
                        "Please enter a quantity greater than 0.",
                        "Invalid Quantity", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int currentQty = item.getQuantity();
            int newQty = currentQty;
            
            if (rbAdd.isSelected()) {
                newQty = currentQty + quantity;
            } else if (rbRemove.isSelected()) {
                newQty = currentQty - quantity;
                if (newQty < 0) {
                    JOptionPane.showMessageDialog(dialog,
                            "Cannot remove more than current stock (" + currentQty + ").",
                            "Invalid Quantity", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (newQty < item.getReservedQuantity()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Cannot reduce below reserved quantity (" + item.getReservedQuantity() + ").",
                            "Invalid Quantity", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } else if (rbSetExact.isSelected()) {
                newQty = quantity;
                if (newQty < item.getReservedQuantity()) {
                    JOptionPane.showMessageDialog(dialog,
                            "Cannot set below reserved quantity (" + item.getReservedQuantity() + ").",
                            "Invalid Quantity", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            
            // Update the inventory
            item.setQuantity(newQty);
            
            // Refresh table and summary
            populateInventoryTable();
            updateInventorySummary();
            
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
    * Show Add New Product Dialog
    */
   private void showAddNewProductDialog() {
       // Get products not already in inventory
       DefaultComboBoxModel<Product> productModel = new DefaultComboBoxModel<>();
       for (Product product : enterprise.getProductCatalog().getProductList()) {
           if (product.isActive() && inventory.findByProduct(product) == null) {
               productModel.addElement(product);
           }
       }

       // Check if there are products available to add
       if (productModel.getSize() == 0) {
           JOptionPane.showMessageDialog(this,
                   "All products from the catalog are already in inventory,\nor no products exist in the catalog.",
                   "No Products Available", JOptionPane.INFORMATION_MESSAGE);
           return;
       }

       // Create dialog
       JDialog dialog = new JDialog(
               (JFrame) SwingUtilities.getWindowAncestor(this),
               "📦 Add New Product to Inventory",
               true
       );
       dialog.setSize(450, 420);
       dialog.setLocationRelativeTo(this);
       dialog.setLayout(new BorderLayout(10, 10));

       JPanel mainPanel = new JPanel();
       mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
       mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

       // ==================== Product Selection Panel ====================
       JPanel selectPanel = new JPanel(new GridBagLayout());
       selectPanel.setBorder(BorderFactory.createTitledBorder("Select Product from Catalog"));
       GridBagConstraints gbc = new GridBagConstraints();
       gbc.insets = new Insets(5, 5, 5, 5);
       gbc.anchor = GridBagConstraints.WEST;

       JComboBox<Product> cmbProduct = new JComboBox<>(productModel);
       cmbProduct.setPreferredSize(new Dimension(250, 25));

       gbc.gridx = 0; gbc.gridy = 0;
       selectPanel.add(new JLabel("Product:"), gbc);
       gbc.gridx = 1;
       selectPanel.add(cmbProduct, gbc);

       // Labels for product info (will update when selection changes)
       JLabel lblCode = new JLabel("-");
       JLabel lblCategory = new JLabel("-");
       JLabel lblPrice = new JLabel("-");

       gbc.gridx = 0; gbc.gridy = 1;
       selectPanel.add(new JLabel("Code:"), gbc);
       gbc.gridx = 1;
       selectPanel.add(lblCode, gbc);

       gbc.gridx = 0; gbc.gridy = 2;
       selectPanel.add(new JLabel("Category:"), gbc);
       gbc.gridx = 1;
       selectPanel.add(lblCategory, gbc);

       gbc.gridx = 0; gbc.gridy = 3;
       selectPanel.add(new JLabel("Unit Price:"), gbc);
       gbc.gridx = 1;
       selectPanel.add(lblPrice, gbc);

       // Update labels when product selection changes
       cmbProduct.addActionListener(e -> {
           Product selected = (Product) cmbProduct.getSelectedItem();
           if (selected != null) {
               lblCode.setText(selected.getProductCode());
               lblCategory.setText(selected.getCategory() != null ? selected.getCategory() : "-");
               lblPrice.setText(String.format("$%.2f", selected.getUnitPrice()));
           }
       });

       // Trigger initial update
       if (cmbProduct.getSelectedItem() != null) {
           Product selected = (Product) cmbProduct.getSelectedItem();
           lblCode.setText(selected.getProductCode());
           lblCategory.setText(selected.getCategory() != null ? selected.getCategory() : "-");
           lblPrice.setText(String.format("$%.2f", selected.getUnitPrice()));
       }

       mainPanel.add(selectPanel);
       mainPanel.add(Box.createVerticalStrut(10));

       // ==================== Inventory Settings Panel ====================
       JPanel settingsPanel = new JPanel(new GridBagLayout());
       settingsPanel.setBorder(BorderFactory.createTitledBorder("Inventory Settings"));

       JSpinner spinInitialQty = new JSpinner(new SpinnerNumberModel(0, 0, 999999, 1));
       JSpinner spinMinStock = new JSpinner(new SpinnerNumberModel(10, 0, 999999, 1));
       JSpinner spinMaxStock = new JSpinner(new SpinnerNumberModel(1000, 0, 999999, 1));
       JTextField txtLocation = new JTextField(15);

       spinInitialQty.setPreferredSize(new Dimension(100, 25));
       spinMinStock.setPreferredSize(new Dimension(100, 25));
       spinMaxStock.setPreferredSize(new Dimension(100, 25));

       gbc.gridx = 0; gbc.gridy = 0;
       settingsPanel.add(new JLabel("Initial Quantity:"), gbc);
       gbc.gridx = 1;
       settingsPanel.add(spinInitialQty, gbc);

       gbc.gridx = 0; gbc.gridy = 1;
       settingsPanel.add(new JLabel("Min Stock Level:"), gbc);
       gbc.gridx = 1;
       settingsPanel.add(spinMinStock, gbc);

       gbc.gridx = 0; gbc.gridy = 2;
       settingsPanel.add(new JLabel("Max Stock Level:"), gbc);
       gbc.gridx = 1;
       settingsPanel.add(spinMaxStock, gbc);

       gbc.gridx = 0; gbc.gridy = 3;
       settingsPanel.add(new JLabel("Location:"), gbc);
       gbc.gridx = 1;
       settingsPanel.add(txtLocation, gbc);

       mainPanel.add(settingsPanel);

       // ==================== Button Panel ====================
       JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
       JButton btnCancel = new JButton("Cancel");
       JButton btnAdd = new JButton("✓ Add to Inventory");

       btnCancel.addActionListener(e -> dialog.dispose());

       btnAdd.addActionListener(e -> {
           Product selectedProduct = (Product) cmbProduct.getSelectedItem();
           if (selectedProduct == null) {
               JOptionPane.showMessageDialog(dialog,
                       "Please select a product.",
                       "No Product Selected", JOptionPane.WARNING_MESSAGE);
               return;
           }

           int initialQty = (Integer) spinInitialQty.getValue();
           int minStock = (Integer) spinMinStock.getValue();
           int maxStock = (Integer) spinMaxStock.getValue();
           String location = txtLocation.getText().trim();

           if (minStock > maxStock) {
               JOptionPane.showMessageDialog(dialog,
                       "Min stock level cannot be greater than max stock level.",
                       "Invalid Settings", JOptionPane.WARNING_MESSAGE);
               return;
           }

           // Add product to inventory
           InventoryItem newItem = inventory.addProduct(selectedProduct, initialQty, minStock, maxStock, location);

           // Refresh table and summary
           populateInventoryTable();
           updateInventorySummary();

           JOptionPane.showMessageDialog(dialog,
                   "Product added to inventory successfully!\n" +
                           "Product: " + selectedProduct.getProductName() + "\n" +
                           "Initial Quantity: " + initialQty,
                   "Success", JOptionPane.INFORMATION_MESSAGE);
           dialog.dispose();
       });

       buttonPanel.add(btnCancel);
       buttonPanel.add(btnAdd);

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

        btnSearch = new javax.swing.JButton();
        fieldSearchProduct = new javax.swing.JTextField();
        lblProductName = new javax.swing.JLabel();
        lblCurrentInventory = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblProductInventory = new javax.swing.JTable();
        btnReset = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        lblTitle = new javax.swing.JLabel();
        lblSubTitle = new javax.swing.JLabel();
        lblCategory = new javax.swing.JLabel();
        cmbCategory = new javax.swing.JComboBox<>();
        lblStockStatus = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        lblInventorySummary = new javax.swing.JLabel();
        lblTotalProducts = new javax.swing.JLabel();
        fieldTotalProducts = new javax.swing.JTextField();
        fieldLowStockStatus = new javax.swing.JTextField();
        lblLowStockStatus = new javax.swing.JLabel();
        fieldTotalQuantity = new javax.swing.JTextField();
        lblTotalQuantity = new javax.swing.JLabel();
        lblActions = new javax.swing.JLabel();
        btnViewDetails = new javax.swing.JButton();
        btnEditStock = new javax.swing.JButton();
        btnAddNewProduct = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();

        btnSearch.setText("🔍 Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        lblProductName.setText("Search Product By Name:");

        lblCurrentInventory.setText("Current Inventory");

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

        btnReset.setText("↻ Reset");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        lblTitle.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        lblTitle.setText("📋 View Inventory ");

        lblSubTitle.setText("🔍 Search & Filter:");

        lblCategory.setText("Category:");

        cmbCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblStockStatus.setText("Stock Status:");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblInventorySummary.setText("📊 Inventory Summary: ");

        lblTotalProducts.setText("Total Products:");

        lblLowStockStatus.setText("Low Stock Status:");

        lblTotalQuantity.setText("Total Quantity:");

        lblActions.setText("Actions:");

        btnViewDetails.setText("📝 View Details");
        btnViewDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnViewDetailsActionPerformed(evt);
            }
        });

        btnEditStock.setText("✏ Edit Stock");
        btnEditStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditStockActionPerformed(evt);
            }
        });

        btnAddNewProduct.setText("📦 Add New Product");
        btnAddNewProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddNewProductActionPerformed(evt);
            }
        });

        btnBack.setText("<< Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
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
                                .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 556, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblCurrentInventory)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                            .addGap(22, 22, 22)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(lblSubTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGroup(layout.createSequentialGroup()
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addComponent(lblCategory)
                                                        .addComponent(lblProductName)
                                                        .addComponent(lblStockStatus))
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(fieldSearchProduct)
                                                        .addComponent(cmbCategory, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                    .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 759, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(22, 22, 22)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblInventorySummary, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(lblTotalProducts)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(fieldTotalProducts, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(lblLowStockStatus)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(fieldLowStockStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(lblTotalQuantity)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(fieldTotalQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(lblActions)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(btnViewDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnEditStock, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnAddNewProduct, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 53, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(btnBack)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBack)
                .addGap(15, 15, 15)
                .addComponent(lblSubTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProductName)
                    .addComponent(fieldSearchProduct, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCategory)
                    .addComponent(cmbCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStockStatus)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnReset)
                    .addComponent(btnSearch))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblCurrentInventory)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblInventorySummary)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalProducts)
                    .addComponent(fieldTotalProducts, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLowStockStatus)
                    .addComponent(fieldLowStockStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotalQuantity)
                    .addComponent(fieldTotalQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblActions)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnViewDetails)
                        .addComponent(btnEditStock)
                        .addComponent(btnAddNewProduct)))
                .addContainerGap(48, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        populateInventoryTableWithFilter(); 
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        // TODO add your handling code here:
        fieldSearchProduct.setText("");
        cmbCategory.setSelectedIndex(0);
        jComboBox1.setSelectedIndex(0);
        
        // Refresh table with all data
        populateInventoryTable();
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // TODO add your handling code here:
        userProcessContainer.remove(this);
        CardLayout layout = (CardLayout) userProcessContainer.getLayout();
        layout.previous(userProcessContainer);
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnViewDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnViewDetailsActionPerformed
        // TODO add your handling code here:
        InventoryItem selectedItem = getSelectedInventoryItem();
        if (selectedItem == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a product from the table.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Navigate to Product Details Panel
        ProductDetailPanel detailsPanel = new ProductDetailPanel(
                userProcessContainer,
                userAccount,
                organization,
                enterprise,
                system,
                selectedItem
        );
        userProcessContainer.add("ProductDetailsPanel", detailsPanel);
        CardLayout layout = (CardLayout) userProcessContainer.getLayout();
        layout.next(userProcessContainer);
    }//GEN-LAST:event_btnViewDetailsActionPerformed

    private void btnEditStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditStockActionPerformed
        // TODO add your handling code here:
        InventoryItem selectedItem = getSelectedInventoryItem();
        if (selectedItem == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a product from the table.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        showEditStockDialog(selectedItem);
    }//GEN-LAST:event_btnEditStockActionPerformed

    private void btnAddNewProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddNewProductActionPerformed
        // TODO add your handling code here:
        System.out.println("=== Debug Add New Product ===");
        System.out.println("Product Catalog size: " + enterprise.getProductCatalog().getProductList().size());
        System.out.println("Inventory size: " + inventory.getProductInventory().size());

        for (Product p : enterprise.getProductCatalog().getProductList()) {
            boolean inInventory = inventory.findByProduct(p) != null;
            System.out.println("Product: " + p.getProductCode() + " - " + p.getProductName() + 
                    " | Active: " + p.isActive() + " | In Inventory: " + inInventory);
        }
        System.out.println("=============================");

        showAddNewProductDialog();
    }//GEN-LAST:event_btnAddNewProductActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddNewProduct;
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnEditStock;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnViewDetails;
    private javax.swing.JComboBox<String> cmbCategory;
    private javax.swing.JTextField fieldLowStockStatus;
    private javax.swing.JTextField fieldSearchProduct;
    private javax.swing.JTextField fieldTotalProducts;
    private javax.swing.JTextField fieldTotalQuantity;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblActions;
    private javax.swing.JLabel lblCategory;
    private javax.swing.JLabel lblCurrentInventory;
    private javax.swing.JLabel lblInventorySummary;
    private javax.swing.JLabel lblLowStockStatus;
    private javax.swing.JLabel lblProductName;
    private javax.swing.JLabel lblStockStatus;
    private javax.swing.JLabel lblSubTitle;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTotalProducts;
    private javax.swing.JLabel lblTotalQuantity;
    private javax.swing.JTable tblProductInventory;
    // End of variables declaration//GEN-END:variables
}
