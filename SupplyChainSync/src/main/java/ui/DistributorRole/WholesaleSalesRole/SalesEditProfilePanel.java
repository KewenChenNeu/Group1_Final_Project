/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui.DistributorRole.WholesaleSalesRole;

import Business.EcoSystem;
import Business.Enterprise.Enterprise;
import Business.Organization.Distributor.WholesaleSalesOrganization;
import Business.UserAccount.UserAccount;
import java.awt.CardLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author chris
 */
public class SalesEditProfilePanel extends javax.swing.JPanel {
    
    private JPanel userProcessContainer;
    private UserAccount userAccount;
    private WholesaleSalesOrganization organization;
    private Enterprise enterprise;
    private EcoSystem system;

    /**
     * Creates new form SalesEditProfilePanel
     */
    public SalesEditProfilePanel(JPanel userProcessContainer, UserAccount account, WholesaleSalesOrganization wholesaleSalesOrganization, Enterprise enterprise, EcoSystem system) {
        initComponents();
        
        this.userProcessContainer = userProcessContainer;
        this.userAccount = account;
        this.organization = wholesaleSalesOrganization;
        this.enterprise = enterprise;
        this.system = system;
        
        setReadOnlyFields();

        populateUserInfo();
    }
    
    private void setReadOnlyFields() {
        fieldUserName.setEditable(false);
        fieldRole.setEditable(false);
        fieldOrganization.setEditable(false);
        
        fieldUserName.setBackground(new java.awt.Color(240, 240, 240));
        fieldRole.setBackground(new java.awt.Color(240, 240, 240));
        fieldOrganization.setBackground(new java.awt.Color(240, 240, 240));
    }
    
    private void populateUserInfo() {
        if (userAccount != null) {
            fieldUserName.setText(userAccount.getUsername());
            
            fieldRole.setText(userAccount.getRole() != null ? 
                userAccount.getRole().getClass().getSimpleName().replace("Role", "") : "N/A");
            
            fieldOrganization.setText(organization != null ? organization.getName() : "N/A");
            if (userAccount.getEmployee() != null) {
                fieldFullName.setText(userAccount.getEmployee().getName() != null ? 
                    userAccount.getEmployee().getName() : "");
                
                fieldEmail.setText(userAccount.getEmployee().getEmail() != null ? 
                     userAccount.getEmployee().getEmail() : "");
                
                fieldPhone.setText(userAccount.getEmployee().getPhone() != null ? 
                    userAccount.getEmployee().getPhone() : "");

            }
        }
    }
    
    
    private boolean validateUserInfo() {
        String fullName = fieldFullName.getText().trim();
        
        if (fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Full Name cannot be empty.", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            fieldFullName.requestFocus();
            return false;
        }
        
        String email = fieldEmail.getText().trim();
        if (!email.isEmpty() && !isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid email address.", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            fieldEmail.requestFocus();
            return false;
        }
        
        String phone = fieldPhone.getText().trim();
        if (!phone.isEmpty() && !isValidPhone(phone)) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid phone number.", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            fieldPhone.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
    
    private boolean isValidPhone(String phone) {
        // allow numbers, space, brackets...
        return phone.matches("^[0-9\\s\\-\\(\\)]{7,15}$");
    }
    
    private boolean validatePasswordChange() {
        String currentPassword = fieldCurrPassword.getText().trim();
        String newPassword = fieldNewPassword.getText().trim();
        String confirmPassword = fieldConfirmPassword.getText().trim();
        
        if (!currentPassword.equals(userAccount.getPassword())) {
            JOptionPane.showMessageDialog(this, 
                "Current password is incorrect.", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            fieldCurrPassword.requestFocus();
            return false;
        }
        
        if (newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "New password cannot be empty.", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            fieldNewPassword.requestFocus();
            return false;
        }
        
        if (newPassword.length() < 4) {
            JOptionPane.showMessageDialog(this, 
                "New password must be at least 4 characters.", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            fieldNewPassword.requestFocus();
            return false;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, 
                "New password and confirm password do not match.", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            fieldConfirmPassword.requestFocus();
            return false;
        }
        
        if (newPassword.equals(currentPassword)) {
            JOptionPane.showMessageDialog(this, 
                "New password must be different from current password.", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            fieldNewPassword.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void clearPasswordFields() {
        fieldCurrPassword.setText("");
        fieldNewPassword.setText("");
        fieldConfirmPassword.setText("");
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
        lblUserInformation = new javax.swing.JLabel();
        lblUserName = new javax.swing.JLabel();
        lblFullName = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        lblPhone = new javax.swing.JLabel();
        lblRole = new javax.swing.JLabel();
        lblOrganization = new javax.swing.JLabel();
        fieldUserName = new javax.swing.JTextField();
        fieldFullName = new javax.swing.JTextField();
        fieldEmail = new javax.swing.JTextField();
        fieldPhone = new javax.swing.JTextField();
        fieldRole = new javax.swing.JTextField();
        fieldOrganization = new javax.swing.JTextField();
        btnSaveChanges = new javax.swing.JButton();
        lblChangePassword = new javax.swing.JLabel();
        lblCurrentPassword = new javax.swing.JLabel();
        lblNewPassword = new javax.swing.JLabel();
        lblConfirmPassword = new javax.swing.JLabel();
        fieldCurrPassword = new javax.swing.JTextField();
        fieldNewPassword = new javax.swing.JTextField();
        fieldConfirmPassword = new javax.swing.JTextField();
        btnChangePassword = new javax.swing.JButton();

        lblTitle.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        lblTitle.setText("👤 Edit Profile");

        btnBack.setText("<< Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        lblUserInformation.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        lblUserInformation.setText("User Information");

        lblUserName.setText("Username:");

        lblFullName.setText("Full Name:");

        lblEmail.setText("Email:");

        lblPhone.setText("Phone:");

        lblRole.setText("Role:");

        lblOrganization.setText("Organization:");

        btnSaveChanges.setText("💾 Save Changes");
        btnSaveChanges.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveChangesActionPerformed(evt);
            }
        });

        lblChangePassword.setFont(new java.awt.Font("Helvetica Neue", 1, 14)); // NOI18N
        lblChangePassword.setText("Change Password");

        lblCurrentPassword.setText("Current Password:");

        lblNewPassword.setText("New Password:");

        lblConfirmPassword.setText("Confirm Password:");

        btnChangePassword.setText("🔐 Change Password");
        btnChangePassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChangePasswordActionPerformed(evt);
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnBack)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblUserName)
                                    .addComponent(lblUserInformation, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblFullName)
                                    .addComponent(lblEmail)
                                    .addComponent(lblPhone)
                                    .addComponent(lblRole)
                                    .addComponent(lblOrganization))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(fieldRole, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                                    .addComponent(fieldPhone)
                                    .addComponent(fieldEmail)
                                    .addComponent(fieldFullName)
                                    .addComponent(fieldUserName)
                                    .addComponent(fieldOrganization))
                                .addGap(63, 63, 63)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(btnChangePassword, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnSaveChanges, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblCurrentPassword)
                                    .addComponent(lblChangePassword, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblNewPassword)
                                    .addComponent(lblConfirmPassword))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(fieldNewPassword, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(fieldCurrPassword)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(fieldConfirmPassword)))
                                .addGap(229, 229, 229)))
                        .addGap(0, 133, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnBack)
                .addGap(27, 27, 27)
                .addComponent(lblUserInformation)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUserName)
                    .addComponent(fieldUserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFullName)
                    .addComponent(fieldFullName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEmail)
                    .addComponent(fieldEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPhone)
                    .addComponent(fieldPhone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRole)
                    .addComponent(fieldRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblOrganization)
                    .addComponent(fieldOrganization, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSaveChanges))
                .addGap(18, 18, 18)
                .addComponent(lblChangePassword)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCurrentPassword)
                    .addComponent(fieldCurrPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNewPassword)
                    .addComponent(fieldNewPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblConfirmPassword)
                    .addComponent(fieldConfirmPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnChangePassword))
                .addContainerGap(124, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnSaveChangesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveChangesActionPerformed
        // TODO add your handling code here:
        if (!validateUserInfo()) {
            return;
        }
        
        if (userAccount.getEmployee() != null) {
            userAccount.getEmployee().setName(fieldFullName.getText().trim());
            userAccount.getEmployee().setEmail(fieldEmail.getText().trim());
            userAccount.getEmployee().setPhone(fieldPhone.getText().trim());
        }
        
        JOptionPane.showMessageDialog(this, 
            "Profile updated successfully!", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnSaveChangesActionPerformed

    private void btnChangePasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChangePasswordActionPerformed
        // TODO add your handling code here:
        if (!validatePasswordChange()) {
            return;
        }
        
        userAccount.setPassword(fieldNewPassword.getText().trim());

        clearPasswordFields();
        
        JOptionPane.showMessageDialog(this, 
            "Password changed successfully!", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnChangePasswordActionPerformed

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // TODO add your handling code here:
        userProcessContainer.remove(this);
        CardLayout layout = (CardLayout) userProcessContainer.getLayout();
        layout.previous(userProcessContainer);
    }//GEN-LAST:event_btnBackActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnChangePassword;
    private javax.swing.JButton btnSaveChanges;
    private javax.swing.JTextField fieldConfirmPassword;
    private javax.swing.JTextField fieldCurrPassword;
    private javax.swing.JTextField fieldEmail;
    private javax.swing.JTextField fieldFullName;
    private javax.swing.JTextField fieldNewPassword;
    private javax.swing.JTextField fieldOrganization;
    private javax.swing.JTextField fieldPhone;
    private javax.swing.JTextField fieldRole;
    private javax.swing.JTextField fieldUserName;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lblChangePassword;
    private javax.swing.JLabel lblConfirmPassword;
    private javax.swing.JLabel lblCurrentPassword;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblFullName;
    private javax.swing.JLabel lblNewPassword;
    private javax.swing.JLabel lblOrganization;
    private javax.swing.JLabel lblPhone;
    private javax.swing.JLabel lblRole;
    private javax.swing.JLabel lblTitle;
    private javax.swing.JLabel lblUserInformation;
    private javax.swing.JLabel lblUserName;
    // End of variables declaration//GEN-END:variables
}
