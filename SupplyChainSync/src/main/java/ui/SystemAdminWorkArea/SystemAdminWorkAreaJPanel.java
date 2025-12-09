/*
 * System Admin Work Area Panel
 */
package ui.SystemAdminWorkArea;

import Business.EcoSystem;
import Business.Employee.Employee;
import Business.Enterprise.Enterprise;
import Business.Network.Network;
import Business.Organization.Organization;
import Business.Role.*;
import Business.Role.Distributor.*;
import Business.Role.Manufacturer.*;
import Business.Role.RawMaterialSupplier.*;
import Business.Role.Retail.*;
import Business.Role.Shipping.*;
import Business.UserAccount.UserAccount;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author SystemAdmin
 */
public class SystemAdminWorkAreaJPanel extends javax.swing.JPanel {

    private JPanel userProcessContainer;
    private EcoSystem ecosystem;

    /**
     * Creates new form SystemAdminWorkAreaJPanel
     */
    public SystemAdminWorkAreaJPanel(JPanel userProcessContainer, EcoSystem ecosystem) {
        this.userProcessContainer = userProcessContainer;
        this.ecosystem = ecosystem;
        initComponents();
        populateEnterprisesTable();
        populateUsersTable();
    }

    private void populateEnterprisesTable() {
        DefaultTableModel model = (DefaultTableModel) tblEnterprises.getModel();
        model.setRowCount(0);

        for (Network network : ecosystem.getNetworkList()) {
            for (Enterprise enterprise : network.getEnterpriseDirectory().getEnterpriseList()) {
                String name = enterprise.getName();
                String type = enterprise.getClass().getSimpleName().replace("Enterprise", "");
                int orgCount = enterprise.getOrganizationDirectory().getOrganizationList().size();

                // Count users across all organizations
                int userCount = 0;
                for (Organization org : enterprise.getOrganizationDirectory().getOrganizationList()) {
                    userCount += org.getUserAccountDirectory().getUserAccountList().size();
                }

                model.addRow(new Object[]{name, type, orgCount, userCount});
            }
        }
    }

    private void populateUsersTable() {
        DefaultTableModel model = (DefaultTableModel) tblUsers.getModel();
        model.setRowCount(0);

        for (Network network : ecosystem.getNetworkList()) {
            for (Enterprise enterprise : network.getEnterpriseDirectory().getEnterpriseList()) {
                for (Organization org : enterprise.getOrganizationDirectory().getOrganizationList()) {
                    for (UserAccount user : org.getUserAccountDirectory().getUserAccountList()) {
                        String username = user.getUsername();
                        String name = user.getEmployee() != null ? user.getEmployee().getName() : "N/A";
                        String enterpriseName = enterprise.getName();
                        String orgName = org.getName();
                        String role = user.getRole() != null ? user.getRole().toString() : "N/A";

                        model.addRow(new Object[]{username, name, enterpriseName, orgName, role});
                    }
                }
            }
        }
    }

    private UserAccount findUserAccount(String username, String enterpriseName, String orgName) {
        for (Network network : ecosystem.getNetworkList()) {
            for (Enterprise enterprise : network.getEnterpriseDirectory().getEnterpriseList()) {
                if (enterprise.getName().equals(enterpriseName)) {
                    for (Organization org : enterprise.getOrganizationDirectory().getOrganizationList()) {
                        if (org.getName().equals(orgName)) {
                            for (UserAccount user : org.getUserAccountDirectory().getUserAccountList()) {
                                if (user.getUsername().equals(username)) {
                                    return user;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    private void showEditUserDialog(UserAccount user) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Edit User", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JTextField txtUsername = new JTextField(user.getUsername(), 20);
        dialog.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        dialog.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JPasswordField txtPassword = new JPasswordField(user.getPassword(), 20);
        dialog.add(txtPassword, gbc);

        // Name
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        dialog.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        String currentName = user.getEmployee() != null ? user.getEmployee().getName() : "";
        JTextField txtName = new JTextField(currentName, 20);
        dialog.add(txtName, gbc);

        // Role
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        dialog.add(new JLabel("Role:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JComboBox<String> cmbRole = new JComboBox<>(new String[]{
            "SystemAdminRole",
            "StoreManagerRole",
            "OrderClerkRole",
            "RetailAnalyticsRole",
            "WholesaleSalesRole",
            "WholesaleInventoryRole",
            "WholesaleAnalyticsRole",
            "ProductionManagerRole",
            "InventoryManagerRole",
            "ShippingManagerRole",
            "DeliveryStaffRole",
            "RMProcurementRole",
            "RMInventoryManagerRole"
        });

        // Set current role
        String currentRole = user.getRole() != null ? user.getRole().getClass().getSimpleName() : "";
        cmbRole.setSelectedItem(currentRole);
        dialog.add(cmbRole, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        JButton btnSave = new JButton("Save");
        JButton btnCancel = new JButton("Cancel");

        btnSave.addActionListener(e -> {
            // Validate
            if (txtUsername.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Username cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (new String(txtPassword.getPassword()).trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Password cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (txtName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Name cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update user
            user.setUsername(txtUsername.getText().trim());
            user.setPassword(new String(txtPassword.getPassword()).trim());

            if (user.getEmployee() != null) {
                user.getEmployee().setName(txtName.getText().trim());
            }

            // Update role
            String selectedRole = (String) cmbRole.getSelectedItem();
            Role newRole = createRoleFromString(selectedRole);
            if (newRole != null) {
                user.setRole(newRole);
            }

            JOptionPane.showMessageDialog(dialog, "User updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
            populateUsersTable();
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        dialog.add(buttonPanel, gbc);

        dialog.setVisible(true);
    }

    private Role createRoleFromString(String roleName) {
        switch (roleName) {
            case "SystemAdminRole":
                return new SystemAdminRole();
            case "StoreManagerRole":
                return new StoreManagerRole();
            case "OrderClerkRole":
                return new OrderClerkRole();
            case "RetailAnalyticsRole":
                return new RetailAnalyticsRole();
            case "WholesaleSalesRole":
                return new WholesaleSalesRole();
            case "WholesaleInventoryRole":
                return new WholesaleInventoryRole();
            case "WholesaleAnalyticsRole":
                return new WholesaleAnalyticsRole();
            case "ProductionManagerRole":
                return new ProductionManagerRole();
            case "InventoryManagerRole":
                return new InventoryManagerRole();
            case "ShippingManagerRole":
                return new ShippingManagerRole();
            case "DeliveryStaffRole":
                return new DeliveryStaffRole();
            case "RMProcurementRole":
                return new RMProcurementRole();
            case "RMInventoryManagerRole":
                return new RMInventoryManagerRole();
            default:
                return null;
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
        lblEnterpriseOverview = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblEnterprises = new javax.swing.JTable();
        jSeparator2 = new javax.swing.JSeparator();
        lblUserManagement = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblUsers = new javax.swing.JTable();
        btnEditUser = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        lblTitle.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        lblTitle.setText("System Admin Dashboard");

        lblEnterpriseOverview.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        lblEnterpriseOverview.setText("Enterprise & Organization Overview");

        tblEnterprises.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Enterprise", "Type", "Organizations", "Users"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblEnterprises);

        lblUserManagement.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        lblUserManagement.setText("User Management");

        tblUsers.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Username", "Name", "Enterprise", "Organization", "Role"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(tblUsers);

        btnEditUser.setText("Edit User");
        btnEditUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditUserActionPerformed(evt);
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
                    .addComponent(jSeparator2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblEnterpriseOverview)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblUserManagement)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnEditUser, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblEnterpriseOverview)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblUserManagement)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEditUser)
                    .addComponent(btnRefresh))
                .addContainerGap(50, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnEditUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditUserActionPerformed
        int selectedRow = tblUsers.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) tblUsers.getValueAt(selectedRow, 0);
        String enterpriseName = (String) tblUsers.getValueAt(selectedRow, 2);
        String orgName = (String) tblUsers.getValueAt(selectedRow, 3);

        // Find the user account
        UserAccount userToEdit = findUserAccount(username, enterpriseName, orgName);
        if (userToEdit == null) {
            JOptionPane.showMessageDialog(this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        showEditUserDialog(userToEdit);
    }//GEN-LAST:event_btnEditUserActionPerformed

    private void btnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshActionPerformed
        populateEnterprisesTable();
        populateUsersTable();
    }//GEN-LAST:event_btnRefreshActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEditUser;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblEnterpriseOverview;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUserManagement;
    private javax.swing.JTable tblEnterprises;
    private javax.swing.JTable tblUsers;
    // End of variables declaration//GEN-END:variables
}
