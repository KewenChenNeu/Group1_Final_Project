/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui.ManufacturerRole.ProductionManagerRole;

import Business.EcoSystem;
import Business.Enterprise.Enterprise;
import Business.Enterprise.ShippingEnterprise;
import Business.Network.Network;
import Business.Organization.Manufacturer.ProductionManagementOrganization;
import Business.Organization.Organization;
import Business.Product.Product;
import Business.UserAccount.UserAccount;
import Business.WorkQueue.ProductShippingRequest;
import Business.WorkQueue.WholesalePurchaseRequest;
import Business.WorkQueue.WorkRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

public class ManufacturerProductManageShippingPanel extends javax.swing.JPanel {

    private JPanel userProcessContainer;
    private ProductionManagementOrganization productionManagementOrganization;
    private Enterprise enterprise;
    private EcoSystem system;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    private List<WholesalePurchaseRequest> approvedOrders;
    private List<ProductShippingRequest> shippingRequests;
    private ArrayList<ShippingEnterprise> shippingEnterprises;
    private UserAccount account;
    
    public ManufacturerProductManageShippingPanel(
            JPanel userProcessContainer,
            UserAccount account,
            ProductionManagementOrganization productionManagementOrganization,
            Enterprise enterprise,
            EcoSystem system) {
        
        this.userProcessContainer = userProcessContainer;
        this.productionManagementOrganization = productionManagementOrganization;
        this.enterprise = enterprise;
        this.system = system;
        this.approvedOrders = new ArrayList<>();
        this.shippingRequests = new ArrayList<>();
        this.shippingEnterprises = new ArrayList<>();
        this.account = account;
        
        initComponents();
        initializeCarrierComboBox();
        setupTableSelectionListeners();
        populateApprovedOrdersTable();
        populateShippingRequestsTable();
    }
    
    
    private void initializeCarrierComboBox() {
        cmbCarrier.removeAllItems();
        shippingEnterprises.clear();
        
        for (Network network : system.getNetworkList()) {
            for (Enterprise ent : network.getEnterpriseDirectory().getEnterpriseList()) {
                if (ent instanceof ShippingEnterprise) {
                    ShippingEnterprise shippingEnt = (ShippingEnterprise) ent;
                    cmbCarrier.addItem(shippingEnt.getName());
                    shippingEnterprises.add(shippingEnt);
                }
            }
        }
        
        if (shippingEnterprises.isEmpty()) {
            cmbCarrier.addItem("No Shipping Company Available");
        }
    }

    private void setupTableSelectionListeners() {
        tblApprovedOrders.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = tblApprovedOrders.getSelectedRow();
                    btnCreateShippingRequest.setEnabled(selectedRow >= 0);
                }
            }
        });
    }
    
    private void populateApprovedOrdersTable() {
        DefaultTableModel model = (DefaultTableModel) tblApprovedOrders.getModel();
        model.setRowCount(0);
        approvedOrders.clear();
        
        List<WholesalePurchaseRequest> approved = findApprovedOrders();
        
        for (WholesalePurchaseRequest order : approved) {
            Object[] row = new Object[5];
            row[0] = order.getRequestId();
            row[1] = getDistributorName(order);
            row[2] = order.getProductName();
            row[3] = order.getQuantity();
            row[4] = order.getRequestDate() != null ? 
                     dateFormat.format(order.getRequestDate()) : "N/A";
            
            model.addRow(row);
            approvedOrders.add(order);
        }
        
        System.out.println("Found " + approved.size() + " approved orders for manufacturer");
    }

    private void populateShippingRequestsTable() {
        DefaultTableModel model = (DefaultTableModel) tblShippingStatus.getModel();
        model.setRowCount(0);
        shippingRequests.clear();
        
        List<ProductShippingRequest> requests = findShippingRequests();
        
        for (ProductShippingRequest request : requests) {
            Object[] row = new Object[6];
            row[0] = request.getRequestId();
            row[1] = request.getDestinationStoreName() != null ? 
                     request.getDestinationStoreName() : "Unknown";
            row[2] = request.getProductName();
            row[3] = request.getQuantity();
            row[4] = request.getRequestDate() != null ? 
                     dateFormat.format(request.getRequestDate()) : "N/A";
            row[5] = request.getShippingStatus() != null ? 
                     request.getShippingStatus() : request.getStatus();
            
            model.addRow(row);
            shippingRequests.add(request);
        }
        
        System.out.println("Found " + requests.size() + " shipping requests from manufacturer");
    }
    

    private List<WholesalePurchaseRequest> findApprovedOrders() {
        List<WholesalePurchaseRequest> approved = new ArrayList<>();
        
        for (Network network : system.getNetworkList()) {
            for (Enterprise ent : network.getEnterpriseDirectory().getEnterpriseList()) {
                
                for (WorkRequest wr : ent.getWorkQueue().getWorkRequestList()) {
                    if (wr instanceof WholesalePurchaseRequest) {
                        WholesalePurchaseRequest wpr = (WholesalePurchaseRequest) wr;
                        if (isApprovedOrderForThisManufacturer(wpr) && !approved.contains(wpr)) {
                            approved.add(wpr);
                        }
                    }
                }
                
                for (Organization org : ent.getOrganizationDirectory().getOrganizationList()) {
                    for (WorkRequest wr : org.getWorkQueue().getWorkRequestList()) {
                        if (wr instanceof WholesalePurchaseRequest) {
                            WholesalePurchaseRequest wpr = (WholesalePurchaseRequest) wr;
                            if (isApprovedOrderForThisManufacturer(wpr) && !approved.contains(wpr)) {
                                approved.add(wpr);
                            }
                        }
                    }
                }
            }
        }
        
        for (WorkRequest wr : productionManagementOrganization.getWorkQueue().getWorkRequestList()) {
            if (wr instanceof WholesalePurchaseRequest) {
                WholesalePurchaseRequest wpr = (WholesalePurchaseRequest) wr;
                if (WorkRequest.STATUS_APPROVED.equals(wpr.getStatus()) && !approved.contains(wpr)) {
                    approved.add(wpr);
                }
            }
        }
        
        return approved;
    }
    

    private boolean isApprovedOrderForThisManufacturer(WholesalePurchaseRequest request) {
        if (!WorkRequest.STATUS_APPROVED.equals(request.getStatus())) {
            return false;
        }
        

        if (request.getTargetEnterprise() != null && 
            request.getTargetEnterprise().equals(enterprise)) {
            return true;
        }
        

        if (request.getTargetOrganization() != null && 
            request.getTargetOrganization().equals(productionManagementOrganization)) {
            return true;
        }
        

        if (productionManagementOrganization.getWorkQueue().getWorkRequestList().contains(request)) {
            return true;
        }
        
        return false;
    }
    
  
    private List<ProductShippingRequest> findShippingRequests() {
        List<ProductShippingRequest> requests = new ArrayList<>();
        

        for (Network network : system.getNetworkList()) {
            for (Enterprise ent : network.getEnterpriseDirectory().getEnterpriseList()) {
                

                for (WorkRequest wr : ent.getWorkQueue().getWorkRequestList()) {
                    if (wr instanceof ProductShippingRequest) {
                        ProductShippingRequest psr = (ProductShippingRequest) wr;
                        if (isShippingRequestFromThisManufacturer(psr) && !requests.contains(psr)) {
                            requests.add(psr);
                        }
                    }
                }
                

                for (Organization org : ent.getOrganizationDirectory().getOrganizationList()) {
                    for (WorkRequest wr : org.getWorkQueue().getWorkRequestList()) {
                        if (wr instanceof ProductShippingRequest) {
                            ProductShippingRequest psr = (ProductShippingRequest) wr;
                            if (isShippingRequestFromThisManufacturer(psr) && !requests.contains(psr)) {
                                requests.add(psr);
                            }
                        }
                    }
                }
            }
        }
        
        return requests;
    }
    

    private boolean isShippingRequestFromThisManufacturer(ProductShippingRequest request) {

        if (request.getSourceEnterprise() != null && 
            request.getSourceEnterprise().equals(enterprise)) {
            return true;
        }
        

        if (request.getSourceOrganization() != null &&
            request.getSourceOrganization().equals(productionManagementOrganization)) {
            return true;
        }
        
        if (request.getSender() != null) {
            for (var userAccount : productionManagementOrganization.getUserAccountDirectory().getUserAccountList()) {
                if (userAccount.equals(request.getSender())) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
 
    private String getDistributorName(WholesalePurchaseRequest request) {
        if (request.getSourceEnterprise() != null) {
            return request.getSourceEnterprise().getName();
        }
        if (request.getSender() != null && request.getSender().getEmployee() != null) {
            return request.getSender().getEmployee().getName();
        }
        return "Unknown Distributor";
    }
    
    
    private String getDistributerName(WholesalePurchaseRequest request) {
        if (request.getSender() != null) {
            if (request.getSender().getEmployee() != null && 
                request.getSender().getEmployee().getName() != null) {
                return request.getSender().getEmployee().getName();
            }
            return request.getSender().getUsername();
        }
        return "Unknown Retailer";
    }
    
    
    private String generateTrackingNumber() {
        return "TRK" + System.currentTimeMillis() % 1000000000;
    }
    
    private Date calculateEstimatedDelivery() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 5);
        return cal.getTime();
    }

    

    private void createShippingRequest() {
        int selectedRow = tblApprovedOrders.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select an approved order to create shipping request.",
                "Warning",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        WholesalePurchaseRequest selectedOrder = approvedOrders.get(selectedRow);
        int carrierIndex = cmbCarrier.getSelectedIndex();
        if (carrierIndex < 0 || shippingEnterprises.isEmpty() || carrierIndex >= shippingEnterprises.size()) {
            JOptionPane.showMessageDialog(this, 
                "Please select a valid shipping company.", 
                "Invalid Carrier", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        ShippingEnterprise selectedShippingEnterprise = shippingEnterprises.get(carrierIndex);
        String notes = fieldNote.getText().trim();
        
        // Find ShippingEnterprise Organization
        Organization receivingOrg = null;
        for (Organization org : selectedShippingEnterprise.getOrganizationDirectory().getOrganizationList()) {
            if (org.getName().contains("Management")) {
                receivingOrg = org;
                break;
            }
        }
        if (receivingOrg == null && !selectedShippingEnterprise.getOrganizationDirectory().getOrganizationList().isEmpty()) {
            receivingOrg = selectedShippingEnterprise.getOrganizationDirectory().getOrganizationList().get(0);
        }
        
        if (receivingOrg == null) {
            JOptionPane.showMessageDialog(this, 
                "Error: Selected shipping company has no organization.\nPlease contact system administrator.", 
                "Configuration Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // create WholesalesShippingRequest
        ProductShippingRequest shippingRequest = new ProductShippingRequest();
        
        // product information
        shippingRequest.setProduct(selectedOrder.getProduct());
        shippingRequest.setProductCode(selectedOrder.getProductCode());
        shippingRequest.setProductName(selectedOrder.getProductName());
        shippingRequest.setQuantity(selectedOrder.getQuantity());
        if (selectedOrder.getUnit() != null) {
            shippingRequest.setUnit(selectedOrder.getUnit());
        }
        
        // retail information
        shippingRequest.setOriginAddress(enterprise.getName() + " Distribution Center");
        shippingRequest.setDestinationAddress(getDistributerName(selectedOrder));
        
        
        shippingRequest.setCarrierName(selectedShippingEnterprise.getName());
        shippingRequest.setTrackingNumber(generateTrackingNumber());
        shippingRequest.setEstimatedDeliveryDate(calculateEstimatedDelivery());
        shippingRequest.setShippingStatus(ProductShippingRequest.SHIP_STATUS_PENDING);
        
        shippingRequest.setSender(account);
        shippingRequest.setStatus("Pending");
        shippingRequest.setMessage(notes.isEmpty() ? "Shipping request for retail order" : notes);
        
//        shippingRequest.setDestinationAddress("sasd");
//        shippingRequest.setOriginAddress("1231");
        
        // add to Shipping Organization WorkQueue
        receivingOrg.getWorkQueue().getWorkRequestList().add(shippingRequest);
        
        selectedOrder.setStatus("Shipping");

        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        JOptionPane.showMessageDialog(this, 
            "Shipping Request created successfully!\n\n" +
            "Tracking #: " + shippingRequest.getTrackingNumber() + "\n" +
            "Product: " + shippingRequest.getProductName() + "\n" +
            "Quantity: " + shippingRequest.getQuantity() + "\n" +
            "Carrier: " + selectedShippingEnterprise.getName() + "\n" +
            "Est. Delivery: " + dateFormat.format(shippingRequest.getEstimatedDeliveryDate()), 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
        
        fieldNote.setText("");
        

        populateApprovedOrdersTable();
        populateShippingRequestsTable();
        fieldNote.setText("");
//       
    }
    

    private Enterprise findShippingEnterprise() {
        for (Network network : system.getNetworkList()) {
            for (Enterprise ent : network.getEnterpriseDirectory().getEnterpriseList()) {
                if (ent.getEnterpriseType() == Enterprise.EnterpriseType.Shipping) {
                    return ent;
                }
            }
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

        jSeparator2 = new javax.swing.JSeparator();
        lblTitle = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        btnBack = new javax.swing.JButton();
        lblApprovedOrders = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblApprovedOrders = new javax.swing.JTable();
        btnCreateShippingRequest = new javax.swing.JButton();
        lblShippingRequestStatus = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblShippingStatus = new javax.swing.JTable();
        lblShippingOptions = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        cmbCarrier = new javax.swing.JComboBox<>();
        lblNotes = new javax.swing.JLabel();
        fieldNote = new javax.swing.JTextField();

        lblTitle.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        lblTitle.setText("Manage Shipping");

        btnBack.setText("<< Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        lblApprovedOrders.setText("Approved Orders (Ready to Ship)");

        tblApprovedOrders.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Request ID", "Distributor", "Product", "Quantity", "Approved Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblApprovedOrders);

        btnCreateShippingRequest.setText("Create Shipping Request");
        btnCreateShippingRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateShippingRequestActionPerformed(evt);
            }
        });

        lblShippingRequestStatus.setText("Shipping Requests Status");

        tblShippingStatus.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ship ID", "Retailer", "Product", "Qty", "Ship Date", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblShippingStatus);

        lblShippingOptions.setText("Shipping Options:");

        jLabel1.setText("Carrier:");

        cmbCarrier.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblNotes.setText("Notes:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnBack))
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnCreateShippingRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(87, 87, 87))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(14, 14, 14)
                                .addComponent(lblApprovedOrders))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(110, 110, 110)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cmbCarrier, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblShippingRequestStatus)
                                    .addComponent(lblNotes))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(fieldNote, javax.swing.GroupLayout.PREFERRED_SIZE, 396, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblShippingOptions))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblTitle))
                    .addComponent(btnBack))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblApprovedOrders)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblShippingOptions)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(cmbCarrier, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblNotes)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(fieldNote, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCreateShippingRequest)
                .addGap(5, 5, 5)
                .addComponent(lblShippingRequestStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(75, 75, 75))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnCreateShippingRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateShippingRequestActionPerformed
        // TODO add your handling code here:
        createShippingRequest();
    }//GEN-LAST:event_btnCreateShippingRequestActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // TODO add your handling code here:
        userProcessContainer.remove(this);
        ((java.awt.CardLayout) userProcessContainer.getLayout()).next(userProcessContainer);
        
    }//GEN-LAST:event_btnBackActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnCreateShippingRequest;
    private javax.swing.JComboBox<String> cmbCarrier;
    private javax.swing.JTextField fieldNote;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblApprovedOrders;
    private javax.swing.JLabel lblNotes;
    private javax.swing.JLabel lblShippingOptions;
    private javax.swing.JLabel lblShippingRequestStatus;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JTable tblApprovedOrders;
    private javax.swing.JTable tblShippingStatus;
    // End of variables declaration//GEN-END:variables
}
