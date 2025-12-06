/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui.DistributorRole.WholesaleAnalyticsRole;

import Business.EcoSystem;
import Business.Enterprise.Enterprise;
import Business.Inventory.Inventory;
import Business.Inventory.InventoryItem;
import Business.Organization.Distributor.WholesaleSalesOrganization;
import Business.UserAccount.UserAccount;
import Business.WorkQueue.ProductShippingRequest;
import Business.WorkQueue.RetailPurchaseOrderRequest;
import Business.WorkQueue.WorkRequest;
import java.awt.CardLayout;
import java.awt.Component;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author chris
 */
public class InventoryAnalyticsPanel extends javax.swing.JPanel {

    private JPanel userProcessContainer;
    private UserAccount account;
    private WholesaleSalesOrganization wholesaleSalesOrganization;
    private Enterprise enterprise;
    private EcoSystem system;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");
    private DecimalFormat decimalFormat = new DecimalFormat("#,##0.0");

    /**
     * Creates new form InventoryAnalyticsPanel
     */
    public InventoryAnalyticsPanel(JPanel userProcessContainer, UserAccount account, 
            WholesaleSalesOrganization wholesaleSalesOrganization, Enterprise enterprise, EcoSystem system) {
        initComponents();
        
        this.userProcessContainer = userProcessContainer;
        this.account = account;
        this.wholesaleSalesOrganization = wholesaleSalesOrganization;
        this.enterprise = enterprise;
        this.system = system;
        
        // Set fields as non-editable
        setFieldsEditable(false);
        
        // Load initial data
        calculateAndDisplayMetrics();
    }
    
    /**
     * Set metric fields as non-editable (display only)
     */
    private void setFieldsEditable(boolean editable) {
        fieldTotalSKUs.setEditable(editable);
        fieldTotalValue.setEditable(editable);
        fieldLowStock.setEditable(editable);
        fieldOutOfStock.setEditable(editable);
    }
    
    /**
     * Main method to calculate and display all inventory metrics
     */
    private void calculateAndDisplayMetrics() {
        // Get inventory from enterprise
        Inventory inventory = enterprise.getInventory();
        
        if (inventory == null || inventory.getProductInventory() == null) {
            clearAllData();
            return;
        }
        
        List<InventoryItem> items = inventory.getProductInventory();
        
        // Calculate key metrics
        int totalSKUs = items.size();
        double totalValue = 0.0;
        int lowStockCount = 0;
        int outOfStockCount = 0;
        
        for (InventoryItem item : items) {
            // Calculate available quantity using correct method
            int available = item.getAvailableQuantity(); // quantity - reservedQuantity
            
            // Get unit price from Product (InventoryItem doesn't have getUnitPrice)
            double unitPrice = 0.0;
            if (item.getProduct() != null) {
                unitPrice = item.getProduct().getUnitPrice();
            }
            
            // Calculate total inventory value
            totalValue += item.getQuantity() * unitPrice;
            
            // Check stock status using correct method
            if (available <= 0) {
                outOfStockCount++;
            } else if (available <= item.getMinStockLevel()) {
                lowStockCount++;
            }
        }
        
        // Display key metrics
        fieldTotalSKUs.setText(String.valueOf(totalSKUs));
        fieldTotalValue.setText(currencyFormat.format(totalValue));
        fieldLowStock.setText(String.valueOf(lowStockCount));
        fieldOutOfStock.setText(String.valueOf(outOfStockCount));
        
        // Populate tables
        populateStockStatusTable(items);
        populateInventoryMovementTable();
        populateTurnoverTable(items);
    }
    
    /**
     * Populate Stock Status by Product table
     */
    private void populateStockStatusTable(List<InventoryItem> items) {
        DefaultTableModel model = (DefaultTableModel) tblProductInventory.getModel();
        model.setRowCount(0);
        
        for (InventoryItem item : items) {
            // Only show PRODUCT type items
            if (item.getItemType() != InventoryItem.ItemType.PRODUCT) {
                continue;
            }
            
            int inStock = item.getQuantity();
            int reserved = item.getReservedQuantity(); // Correct method name
            int available = item.getAvailableQuantity();
            
            // Determine status using correct method
            String status;
            if (available <= 0) {
                status = "🔴 Out of Stock";
            } else if (available <= item.getMinStockLevel()) { // Correct method name
                status = "⚠️ Low Stock";
            } else {
                status = "✅ OK";
            }
            
            // Use helper methods from InventoryItem
            Object[] row = {
                item.getItemCode(),      // getItemCode() handles null check
                item.getItemName(),      // getItemName() handles null check
                inStock,
                reserved,
                status
            };
            model.addRow(row);
        }
        
        if (model.getRowCount() == 0) {
            model.addRow(new Object[]{"--", "No inventory data", "--", "--", "--"});
        }
    }
    
    /**
     * Populate Inventory Movement (Recent) table
     * Shows recent IN (shipments received) and OUT (orders shipped) movements
     */
    private void populateInventoryMovementTable() {
        DefaultTableModel model = (DefaultTableModel) tblSalesByProdcut.getModel();
        model.setRowCount(0);
        
        if (wholesaleSalesOrganization == null) {
            model.addRow(new Object[]{"--", "No movement data", "--", "--"});
            return;
        }
        
        List<WorkRequest> allRequests = wholesaleSalesOrganization.getWorkQueue().getWorkRequestList();
        
        // Collect movements with date info
        List<Object[]> movements = new ArrayList<>();
        
        for (WorkRequest request : allRequests) {
            // IN movements - from ProductShippingRequest (received shipments)
            if (request instanceof ProductShippingRequest) {
                ProductShippingRequest psr = (ProductShippingRequest) request;
                String status = psr.getStatus();
                
                // Only show received/delivered shipments as IN
                if ("Received".equalsIgnoreCase(status) || "Delivered".equalsIgnoreCase(status)) {
                    Date date = psr.getResolveDate() != null ? psr.getResolveDate() : psr.getRequestDate();
                    movements.add(new Object[]{
                        date,
                        psr.getProductName() != null ? psr.getProductName() : "Unknown",
                        "📥 IN",
                        psr.getQuantity()
                    });
                }
            }
            
            // OUT movements - from RetailPurchaseOrderRequest (shipped orders)
            if (request instanceof RetailPurchaseOrderRequest) {
                RetailPurchaseOrderRequest order = (RetailPurchaseOrderRequest) request;
                String status = order.getStatus();
                
                // Only show shipped/delivered/completed orders as OUT
                if ("Shipped".equalsIgnoreCase(status) || 
                    "Delivered".equalsIgnoreCase(status) || 
                    "Completed".equalsIgnoreCase(status)) {
                    Date date = order.getResolveDate() != null ? order.getResolveDate() : order.getRequestDate();
                    movements.add(new Object[]{
                        date,
                        order.getProductName() != null ? order.getProductName() : "Unknown",
                        "📤 OUT",
                        order.getQuantity()
                    });
                }
            }
        }
        
        // Sort by date descending (most recent first)
        movements.sort((a, b) -> {
            Date dateA = (Date) a[0];
            Date dateB = (Date) b[0];
            if (dateA == null && dateB == null) return 0;
            if (dateA == null) return 1;
            if (dateB == null) return -1;
            return dateB.compareTo(dateA);
        });
        
        // Add to table (limit to 20 most recent)
        int count = 0;
        for (Object[] movement : movements) {
            if (count >= 20) break;
            
            Date date = (Date) movement[0];
            String dateStr = date != null ? dateFormat.format(date) : "N/A";
            
            model.addRow(new Object[]{
                dateStr,
                movement[1],
                movement[2],
                movement[3]
            });
            count++;
        }
        
        if (model.getRowCount() == 0) {
            model.addRow(new Object[]{"--", "No recent movements", "--", "--"});
        }
    }
    
    /**
     * Populate Inventory Turnover table
     * Calculates turnover rate and days supply for each product
     */
    private void populateTurnoverTable(List<InventoryItem> items) {
        DefaultTableModel model = (DefaultTableModel) tblTopCustomers.getModel();
        model.setRowCount(0);
        
        if (wholesaleSalesOrganization == null || items.isEmpty()) {
            model.addRow(new Object[]{"--", "--", "--", "--", "--"});
            return;
        }
        
        // Calculate units sold per product from work requests (last 30 days)
        Map<String, Integer> unitsSold = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        Date thirtyDaysAgo = cal.getTime();
        
        List<WorkRequest> allRequests = wholesaleSalesOrganization.getWorkQueue().getWorkRequestList();
        
        for (WorkRequest request : allRequests) {
            if (request instanceof RetailPurchaseOrderRequest) {
                RetailPurchaseOrderRequest order = (RetailPurchaseOrderRequest) request;
                String status = order.getStatus();
                
                // Only count completed orders
                if ("Shipped".equalsIgnoreCase(status) || 
                    "Delivered".equalsIgnoreCase(status) || 
                    "Completed".equalsIgnoreCase(status)) {
                    
                    Date orderDate = order.getRequestDate();
                    if (orderDate != null && orderDate.after(thirtyDaysAgo)) {
                        String productName = order.getProductName();
                        if (productName != null) {
                            unitsSold.merge(productName, order.getQuantity(), Integer::sum);
                        }
                    }
                }
            }
        }
        
        // Calculate turnover metrics for each inventory item
        for (InventoryItem item : items) {
            // Only show PRODUCT type items
            if (item.getItemType() != InventoryItem.ItemType.PRODUCT) {
                continue;
            }
            
            String productName = item.getItemName(); // Use helper method
            int avgStock = item.getQuantity(); // Current stock as average (simplified)
            int sold = unitsSold.getOrDefault(productName, 0);
            
            // Turnover Rate = Units Sold / Average Stock (per 30-day period)
            double turnoverRate = avgStock > 0 ? (double) sold / avgStock : 0.0;
            
            // Days Supply = Average Stock / Daily Usage
            // Daily Usage = Units Sold / 30 days
            double dailyUsage = sold / 30.0;
            double daysSupply = dailyUsage > 0 ? avgStock / dailyUsage : 999.0; // 999 = essentially infinite
            
            Object[] row = {
                productName,
                avgStock,
                sold,
                decimalFormat.format(turnoverRate),
                daysSupply > 365 ? ">365" : decimalFormat.format(daysSupply)
            };
            model.addRow(row);
        }
        
        if (model.getRowCount() == 0) {
            model.addRow(new Object[]{"No turnover data", "--", "--", "--", "--"});
        }
    }
    
    /**
     * Clear all data displays
     */
    private void clearAllData() {
        fieldTotalSKUs.setText("0");
        fieldTotalValue.setText("$0.00");
        fieldLowStock.setText("0");
        fieldOutOfStock.setText("0");
        
        ((DefaultTableModel) tblProductInventory.getModel()).setRowCount(0);
        ((DefaultTableModel) tblSalesByProdcut.getModel()).setRowCount(0);
        ((DefaultTableModel) tblTopCustomers.getModel()).setRowCount(0);
    }
    
    /**
     * Refresh all data
     */
    public void refreshData() {
        calculateAndDisplayMetrics();
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
        lblOutOfStock = new javax.swing.JLabel();
        fieldOutOfStock = new javax.swing.JTextField();
        lblLowStock = new javax.swing.JLabel();
        fieldLowStock = new javax.swing.JTextField();
        lblTotalValue = new javax.swing.JLabel();
        fieldTotalValue = new javax.swing.JTextField();
        fieldTotalSKUs = new javax.swing.JTextField();
        lblTotalSKUs = new javax.swing.JLabel();
        lblKeyMatrics = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblProductInventory = new javax.swing.JTable();
        lblStockStatus = new javax.swing.JLabel();
        lblInventoryMovement = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSalesByProdcut = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblTopCustomers = new javax.swing.JTable();
        btnRefresh = new javax.swing.JButton();
        lblInventoryTurnOver = new javax.swing.JLabel();

        btnBack.setText("<< Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        lblTitle.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        lblTitle.setText("📦 Inventory Analytics ");
        lblTitle.setToolTipText("");

        lblOutOfStock.setText("Out of Stock:");

        lblLowStock.setText("Low Stock:");

        lblTotalValue.setText("Total Value:");

        lblTotalSKUs.setText("Total SKUs:");

        lblKeyMatrics.setText("Key Metrics");

        tblProductInventory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product Code", "Product Name", "In Stock", "Reserved", "Available Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblProductInventory);

        lblStockStatus.setText("Stock Status by Product");

        lblInventoryMovement.setText("Inventory Movement (Recent)");

        tblSalesByProdcut.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Date", "Product", "Type", "Qty"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblSalesByProdcut);

        tblTopCustomers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product", "Avg Stock", "Units Sold", "Turnover Rate", "Days Supply"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(tblTopCustomers);

        btnRefresh.setText("🔄 Refresh");
        btnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshActionPerformed(evt);
            }
        });

        lblInventoryTurnOver.setText("Inventory Turnover");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnBack))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(lblKeyMatrics)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(lblTotalSKUs)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(fieldTotalSKUs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(lblTotalValue)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(fieldTotalValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(lblLowStock)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(fieldLowStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(lblOutOfStock)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(fieldOutOfStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 762, Short.MAX_VALUE)
                                .addComponent(lblStockStatus)
                                .addComponent(lblInventoryMovement)
                                .addComponent(jScrollPane1)
                                .addComponent(jScrollPane3)
                                .addComponent(lblInventoryTurnOver)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBack)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblKeyMatrics)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalSKUs)
                    .addComponent(fieldTotalSKUs, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotalValue)
                    .addComponent(fieldTotalValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLowStock)
                    .addComponent(fieldLowStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblOutOfStock)
                    .addComponent(fieldOutOfStock, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblStockStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblInventoryMovement)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblInventoryTurnOver)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRefresh)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // TODO add your handling code here:
        userProcessContainer.remove(this);
        Component[] componentArray = userProcessContainer.getComponents();
        Component component = componentArray[componentArray.length - 1];
        if (component instanceof WholesaleAnalyticsWorkAreaJPanel) {
            ((WholesaleAnalyticsWorkAreaJPanel) component).refreshDashboard();
        }
        CardLayout layout = (CardLayout) userProcessContainer.getLayout();
        layout.previous(userProcessContainer);
    }//GEN-LAST:event_btnBackActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        calculateAndDisplayMetrics();
    }//GEN-LAST:event_btnRefreshActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JTextField fieldLowStock;
    private javax.swing.JTextField fieldOutOfStock;
    private javax.swing.JTextField fieldTotalSKUs;
    private javax.swing.JTextField fieldTotalValue;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblInventoryMovement;
    private javax.swing.JLabel lblInventoryTurnOver;
    private javax.swing.JLabel lblKeyMatrics;
    private javax.swing.JLabel lblLowStock;
    private javax.swing.JLabel lblOutOfStock;
    private javax.swing.JLabel lblStockStatus;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTotalSKUs;
    private javax.swing.JLabel lblTotalValue;
    private javax.swing.JTable tblProductInventory;
    private javax.swing.JTable tblSalesByProdcut;
    private javax.swing.JTable tblTopCustomers;
    // End of variables declaration//GEN-END:variables
}
