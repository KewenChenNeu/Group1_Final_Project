/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui.DistributorRole.WholesaleAnalyticsRole;

import Business.EcoSystem;
import Business.Enterprise.Enterprise;
import Business.Organization.Distributor.WholesaleSalesOrganization;
import Business.UserAccount.UserAccount;
import Business.WorkQueue.DeliveryConfirmationRequest;
import Business.WorkQueue.RetailPurchaseOrderRequest;
import Business.WorkQueue.WorkRequest;
import java.awt.CardLayout;
import java.awt.Component;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author chris
 */
public class SalesAnalyticsPanel extends javax.swing.JPanel {

    private JPanel userProcessContainer;
    private UserAccount account;
    private WholesaleSalesOrganization wholesaleSalesOrganization;
    private Enterprise enterprise;
    private EcoSystem system;
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");
    private DecimalFormat percentFormat = new DecimalFormat("0.0%");

    /**
     * Creates new form SalesAnalyticsPanel
     */
    public SalesAnalyticsPanel(JPanel userProcessContainer, UserAccount account, 
            WholesaleSalesOrganization wholesaleSalesOrganization, Enterprise enterprise, EcoSystem system) {
        initComponents();
        
        this.userProcessContainer = userProcessContainer;
        this.account = account;
        this.wholesaleSalesOrganization = wholesaleSalesOrganization;
        this.enterprise = enterprise;
        this.system = system;
        
        initializePeriodCombo();
        setFieldsEditable(false);
        calculateAndDisplayMetrics();
    }
    
    private void initializePeriodCombo() {
        cmbPeriod.removeAllItems();
        cmbPeriod.addItem("All Time");
        cmbPeriod.addItem("This Month");
        cmbPeriod.addItem("This Quarter");
        cmbPeriod.addItem("This Year");
        cmbPeriod.addItem("Last 30 Days");
        cmbPeriod.addItem("Last 90 Days");
        cmbPeriod.addItem("Custom");
        cmbPeriod.setSelectedIndex(0);
        
        cmbPeriod.addActionListener(e -> {
            String selected = (String) cmbPeriod.getSelectedItem();
            boolean isCustom = "Custom".equals(selected);
            fieldDateFrom.setEnabled(isCustom);
            fieldDateTo.setEnabled(isCustom);
            if (!isCustom && selected != null) {
                updateDateRangeFields(selected);
            }
        });
        
        fieldDateFrom.setEnabled(false);
        fieldDateTo.setEnabled(false);
    }
    
    private void updateDateRangeFields(String period) {
        Calendar cal = Calendar.getInstance();
        Date endDate = cal.getTime();
        Date startDate = null;
        
        switch (period) {
            case "This Month":
                cal.set(Calendar.DAY_OF_MONTH, 1);
                startDate = cal.getTime();
                break;
            case "This Quarter":
                int month = cal.get(Calendar.MONTH);
                int quarterStartMonth = (month / 3) * 3;
                cal.set(Calendar.MONTH, quarterStartMonth);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                startDate = cal.getTime();
                break;
            case "This Year":
                cal.set(Calendar.MONTH, Calendar.JANUARY);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                startDate = cal.getTime();
                break;
            case "Last 30 Days":
                cal.add(Calendar.DAY_OF_MONTH, -30);
                startDate = cal.getTime();
                break;
            case "Last 90 Days":
                cal.add(Calendar.DAY_OF_MONTH, -90);
                startDate = cal.getTime();
                break;
            default:
                fieldDateFrom.setText("");
                fieldDateTo.setText("");
                return;
        }
        fieldDateFrom.setText(dateFormat.format(startDate));
        fieldDateTo.setText(dateFormat.format(endDate));
    }
    
    private void setFieldsEditable(boolean editable) {
        fieldTotalOrders.setEditable(editable);
        fieldTotalRevenue.setEditable(editable);
        fieldAvgOrder.setEditable(editable);
        fieldFillRate.setEditable(editable);
    }
    
    private Date getStartDate() {
        String fromText = fieldDateFrom.getText().trim();
        if (fromText.isEmpty()) return null;
        try {
            return dateFormat.parse(fromText);
        } catch (ParseException e) {
            return null;
        }
    }
    
    private Date getEndDate() {
        String toText = fieldDateTo.getText().trim();
        if (toText.isEmpty()) return null;
        try {
            return dateFormat.parse(toText);
        } catch (ParseException e) {
            return null;
        }
    }
    
    private boolean isWithinDateRange(Date date, Date startDate, Date endDate) {
        if (date == null) return true;
        if (startDate != null && date.before(startDate)) return false;
        if (endDate != null && date.after(endDate)) return false;
        return true;
    }
    

    private void calculateAndDisplayMetrics() {
        if (wholesaleSalesOrganization == null) {
            clearAllData();
            return;
        }
        
        Date startDate = getStartDate();
        Date endDate = getEndDate();
        List<WorkRequest> allRequests = wholesaleSalesOrganization.getWorkQueue().getWorkRequestList();
        
        int totalOrders = 0;
        int completedOrders = 0;
        double totalRevenue = 0.0;      
        double totalOrderValue = 0.0;   
        
        Map<String, Integer> productQuantities = new HashMap<>();
        Map<String, Double> productRevenue = new HashMap<>();
        
        Map<String, Integer> customerOrderCount = new HashMap<>();
        Map<String, Double> customerOrderValue = new HashMap<>();  
        Map<String, Date> customerLastOrder = new HashMap<>();
        Map<String, String> customerLatestStatus = new HashMap<>();
        
        Map<String, Integer> statusCounts = new LinkedHashMap<>();
        statusCounts.put("Pending", 0);
        statusCounts.put("Processing", 0);
        statusCounts.put("Approved", 0);
        statusCounts.put("Shipped", 0);
        statusCounts.put("Delivered", 0);
        statusCounts.put("Completed", 0);
        
        for (WorkRequest request : allRequests) {
            if (request instanceof RetailPurchaseOrderRequest) {
                RetailPurchaseOrderRequest order = (RetailPurchaseOrderRequest) request;
                
                if (!isWithinDateRange(order.getRequestDate(), startDate, endDate)) {
                    continue;
                }
                
                totalOrders++;
                String status = order.getStatus() != null ? order.getStatus() : "Pending";
                
                // Update status counts
                if (statusCounts.containsKey(status)) {
                    statusCounts.put(status, statusCounts.get(status) + 1);
                } else {
                    statusCounts.put(status, 1);
                }
                
                String customerName = order.getStoreName() != null ? order.getStoreName() : "Unknown";
                double orderValue = order.getQuantity() * order.getUnitPrice();
                totalOrderValue += orderValue;
                
                customerOrderCount.merge(customerName, 1, Integer::sum);
                customerOrderValue.merge(customerName, orderValue, Double::sum);
                
                Date orderDate = order.getRequestDate();
                if (orderDate != null) {
                    Date existingDate = customerLastOrder.get(customerName);
                    if (existingDate == null || orderDate.after(existingDate)) {
                        customerLastOrder.put(customerName, orderDate);
                        customerLatestStatus.put(customerName, status);
                    }
                }
                
                String productName = order.getProductName();
                if (productName != null) {
                    productQuantities.merge(productName, order.getQuantity(), Integer::sum);
                    productRevenue.merge(productName, orderValue, Double::sum);
                }
                
                if ("Completed".equalsIgnoreCase(status) || 
                    "Delivered".equalsIgnoreCase(status) ||
                    "Shipped".equalsIgnoreCase(status)) {
                    completedOrders++;
                    totalRevenue += orderValue;
                }
            }
        }
        
        double avgOrderValue = totalOrders > 0 ? totalOrderValue / totalOrders : 0.0;
        double fillRate = totalOrders > 0 ? (double) completedOrders / totalOrders : 0.0;
        
        fieldTotalOrders.setText(String.valueOf(totalOrders));
        fieldTotalRevenue.setText(currencyFormat.format(totalRevenue));
        fieldAvgOrder.setText(currencyFormat.format(avgOrderValue));
        fieldFillRate.setText(percentFormat.format(fillRate));
        
        populateSalesByProductTable(productQuantities, productRevenue);
        populateOrderStatusTable(statusCounts);
        populateTopCustomersTable(customerOrderCount, customerOrderValue, customerLastOrder, customerLatestStatus);
    }
    
    private void populateSalesByProductTable(Map<String, Integer> quantities, Map<String, Double> revenue) {
        DefaultTableModel model = (DefaultTableModel) tblSalesByProdcut.getModel();
        model.setRowCount(0);
        
        List<Map.Entry<String, Double>> sortedProducts = new ArrayList<>(revenue.entrySet());
        sortedProducts.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        
        for (Map.Entry<String, Double> entry : sortedProducts) {
            String productName = entry.getKey();
            int qty = quantities.getOrDefault(productName, 0);
            double rev = entry.getValue();
            model.addRow(new Object[]{productName, qty, currencyFormat.format(rev)});
        }
        
        if (model.getRowCount() == 0) {
            model.addRow(new Object[]{"No sales data", "--", "--"});
        }
    }
    
    private void populateOrderStatusTable(Map<String, Integer> statusCounts) {
        DefaultTableModel model = (DefaultTableModel) tblOrderStatus.getModel();
        model.setRowCount(0);
        
        for (Map.Entry<String, Integer> entry : statusCounts.entrySet()) {
            if (entry.getValue() > 0) {
                model.addRow(new Object[]{entry.getKey(), entry.getValue()});
            }
        }
        
        if (model.getRowCount() == 0) {
            model.addRow(new Object[]{"No orders", "0"});
        }
    }
    

    private void populateTopCustomersTable(Map<String, Integer> orderCounts, 
                                           Map<String, Double> orderValue,
                                           Map<String, Date> lastOrder,
                                           Map<String, String> latestStatus) {
        DefaultTableModel model = (DefaultTableModel) tblTopCustomers.getModel();
        model.setRowCount(0);
        
        List<String> customers = new ArrayList<>(orderCounts.keySet());
        customers.sort((a, b) -> {
            double valueA = orderValue.getOrDefault(a, 0.0);
            double valueB = orderValue.getOrDefault(b, 0.0);
            if (valueA != valueB) {
                return Double.compare(valueB, valueA);
            }
            return Integer.compare(orderCounts.getOrDefault(b, 0), orderCounts.getOrDefault(a, 0));
        });
        
        int count = 0;
        for (String customerName : customers) {
            if (count >= 10) break;
            
            int orders = orderCounts.getOrDefault(customerName, 0);
            double totalValue = orderValue.getOrDefault(customerName, 0.0);
            Date lastOrderDate = lastOrder.get(customerName);
            String status = latestStatus.getOrDefault(customerName, "N/A");
            
            model.addRow(new Object[]{
                customerName,
                orders,
                currencyFormat.format(totalValue),
                lastOrderDate != null ? dateFormat.format(lastOrderDate) : "N/A",
                status
            });
            count++;
        }
        
        if (model.getRowCount() == 0) {
            model.addRow(new Object[]{"No customer data", "--", "--", "--", "--"});
        }
    }
    
    private void clearAllData() {
        fieldTotalOrders.setText("0");
        fieldTotalRevenue.setText("$0.00");
        fieldAvgOrder.setText("$0.00");
        fieldFillRate.setText("0.0%");
        ((DefaultTableModel) tblSalesByProdcut.getModel()).setRowCount(0);
        ((DefaultTableModel) tblOrderStatus.getModel()).setRowCount(0);
        ((DefaultTableModel) tblTopCustomers.getModel()).setRowCount(0);
    }
    
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

        jSeparator1 = new javax.swing.JSeparator();
        lblTitle = new javax.swing.JLabel();
        btnBack = new javax.swing.JButton();
        lblTimePeriodFilter = new javax.swing.JLabel();
        lblPeriod = new javax.swing.JLabel();
        cmbPeriod = new javax.swing.JComboBox<>();
        btnSearch = new javax.swing.JButton();
        fieldDateTo = new javax.swing.JTextField();
        lblDataTo = new javax.swing.JLabel();
        fieldDateFrom = new javax.swing.JTextField();
        lblDateRange = new javax.swing.JLabel();
        lblKeyMatrics = new javax.swing.JLabel();
        lblTotalOrders = new javax.swing.JLabel();
        fieldTotalOrders = new javax.swing.JTextField();
        fieldTotalRevenue = new javax.swing.JTextField();
        lblTotalRevenue = new javax.swing.JLabel();
        fieldAvgOrder = new javax.swing.JTextField();
        lblAvgOrder = new javax.swing.JLabel();
        fieldFillRate = new javax.swing.JTextField();
        lblFillRate = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblSalesByProdcut = new javax.swing.JTable();
        lblSalesByProdcut = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblOrderStatus = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblTopCustomers = new javax.swing.JTable();
        lblTopCustomers = new javax.swing.JLabel();
        btnRefresh = new javax.swing.JButton();

        lblTitle.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        lblTitle.setText("📊 Sales Analytics");
        lblTitle.setToolTipText("");

        btnBack.setText("<< Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        lblTimePeriodFilter.setText("Time Period Filter");

        lblPeriod.setText("Period:");

        cmbPeriod.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnSearch.setText("🔍 Search");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        lblDataTo.setText("To");

        lblDateRange.setText("Date Range: From");

        lblKeyMatrics.setText("Key Metrics");

        lblTotalOrders.setText("Total Orders:");

        lblTotalRevenue.setText("Total Revenue:");

        lblAvgOrder.setText("Avg Order:");

        lblFillRate.setText("Fill Rate:");

        tblSalesByProdcut.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product Name", "Qty Sold", "Revenue"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblSalesByProdcut);

        lblSalesByProdcut.setText("Sales By Product");

        tblOrderStatus.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Stauts", "Count"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblOrderStatus);

        jLabel2.setText("Order Status");

        tblTopCustomers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Customer", "Orders", "Total Spent", "Last Order", "Status"
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

        lblTopCustomers.setText("Top Customers");

        btnRefresh.setText("🔄 Refresh");
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
                    .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(lblSalesByProdcut)
                            .addGap(442, 442, 442)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel2)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(0, 165, Short.MAX_VALUE)
                                    .addComponent(lblTotalOrders)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(fieldTotalOrders, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(lblTotalRevenue)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(fieldTotalRevenue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(lblAvgOrder)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(fieldAvgOrder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(lblFillRate))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblTimePeriodFilter)
                                        .addComponent(lblKeyMatrics))
                                    .addGap(0, 0, Short.MAX_VALUE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(fieldFillRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jScrollPane3)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 519, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(237, 237, 237)))))
                    .addComponent(lblTopCustomers)
                    .addComponent(btnBack)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblPeriod)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmbPeriod, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblDateRange)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fieldDateFrom, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lblDataTo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fieldDateTo, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBack)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTimePeriodFilter)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPeriod)
                    .addComponent(cmbPeriod, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDateRange)
                    .addComponent(fieldDateFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDataTo)
                    .addComponent(fieldDateTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSearch))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblKeyMatrics)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalOrders)
                    .addComponent(fieldTotalOrders, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotalRevenue)
                    .addComponent(fieldTotalRevenue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAvgOrder)
                    .addComponent(fieldAvgOrder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFillRate)
                    .addComponent(fieldFillRate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSalesByProdcut)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lblTopCustomers)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRefresh)
                .addContainerGap(47, Short.MAX_VALUE))
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

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
        String period = (String) cmbPeriod.getSelectedItem();
        if ("Custom".equals(period)) {
            String fromText = fieldDateFrom.getText().trim();
            String toText = fieldDateTo.getText().trim();
            
            if (!fromText.isEmpty()) {
                try {
                    dateFormat.parse(fromText);
                } catch (ParseException e) {
                    JOptionPane.showMessageDialog(this, 
                        "Invalid 'From' date. Use yyyy-MM-dd format.",
                        "Date Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            if (!toText.isEmpty()) {
                try {
                    dateFormat.parse(toText);
                } catch (ParseException e) {
                    JOptionPane.showMessageDialog(this, 
                        "Invalid 'To' date. Use yyyy-MM-dd format.",
                        "Date Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }
        calculateAndDisplayMetrics();
    }//GEN-LAST:event_btnSearchActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        // TODO add your handling code here:
        calculateAndDisplayMetrics();
    }//GEN-LAST:event_btnRefreshActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnSearch;
    private javax.swing.JComboBox<String> cmbPeriod;
    private javax.swing.JTextField fieldAvgOrder;
    private javax.swing.JTextField fieldDateFrom;
    private javax.swing.JTextField fieldDateTo;
    private javax.swing.JTextField fieldFillRate;
    private javax.swing.JTextField fieldTotalOrders;
    private javax.swing.JTextField fieldTotalRevenue;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblAvgOrder;
    private javax.swing.JLabel lblDataTo;
    private javax.swing.JLabel lblDateRange;
    private javax.swing.JLabel lblFillRate;
    private javax.swing.JLabel lblKeyMatrics;
    private javax.swing.JLabel lblPeriod;
    private javax.swing.JLabel lblSalesByProdcut;
    private javax.swing.JLabel lblTimePeriodFilter;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblTopCustomers;
    private javax.swing.JLabel lblTotalOrders;
    private javax.swing.JLabel lblTotalRevenue;
    private javax.swing.JTable tblOrderStatus;
    private javax.swing.JTable tblSalesByProdcut;
    private javax.swing.JTable tblTopCustomers;
    // End of variables declaration//GEN-END:variables
}
