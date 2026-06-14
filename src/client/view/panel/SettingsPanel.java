package client.view.panel;

import client.view.component.RoundedPanel;
import client.view.component.ModernButton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel Cài đặt hệ thống (View trong mô hình MVC)
 */
@SuppressWarnings("serial")
public final class SettingsPanel extends JPanel {

    private JTextField txtName;
    private JTextField txtPhone;
    private JPasswordField txtPassOld;
    private JPasswordField txtPassNew;
    private ModernButton btnSaveProfile;
    private ModernButton btnSavePassword;

    public SettingsPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        initComponents();
    }

    private void initComponents() {
        JPanel bodyWrapper = new JPanel();
        bodyWrapper.setLayout(new BoxLayout(bodyWrapper, BoxLayout.Y_AXIS));
        bodyWrapper.setBorder(new EmptyBorder(25, 25, 25, 25));
        bodyWrapper.setOpaque(false);

        // Header Label
        JLabel lblHeader = new JLabel("Cài Đặt Tài Khoản & Bảo Mật");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(new Color(15, 23, 42));
        lblHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyWrapper.add(lblHeader);
        bodyWrapper.add(Box.createVerticalStrut(20));

        // Center card wrapper
        JPanel cardWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        cardWrapper.setOpaque(false);
        cardWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        // CARD 1: Profile Info Settings
        RoundedPanel infoCard = new RoundedPanel(16, Color.WHITE);
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        infoCard.setPreferredSize(new Dimension(460, 310));
        infoCard.setMaximumSize(new Dimension(460, 310));

        JLabel lblInfoTitle = new JLabel("Thông Tin Cá Nhân");
        lblInfoTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblInfoTitle.setForeground(new Color(15, 23, 42));
        lblInfoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoCard.add(lblInfoTitle);
        infoCard.add(Box.createVerticalStrut(15));

        txtName = createField(infoCard, "Họ và tên:");
        txtPhone = createField(infoCard, "Số điện thoại:");

        infoCard.add(Box.createVerticalStrut(10));
        btnSaveProfile = new ModernButton(" Lưu Thông Tin");
        btnSaveProfile.setColors(new Color(59, 130, 246), new Color(37, 99, 235), new Color(29, 78, 216));
        btnSaveProfile.setPreferredSize(new Dimension(160, 36));
        btnSaveProfile.setMaximumSize(new Dimension(160, 36));
        btnSaveProfile.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSaveProfile.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoCard.add(btnSaveProfile);

        // CARD 2: Password Change
        RoundedPanel passCard = new RoundedPanel(16, Color.WHITE);
        passCard.setLayout(new BoxLayout(passCard, BoxLayout.Y_AXIS));
        passCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        passCard.setPreferredSize(new Dimension(460, 310));
        passCard.setMaximumSize(new Dimension(460, 310));

        JLabel lblPassTitle = new JLabel("Thay Đổi Mật Khẩu");
        lblPassTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblPassTitle.setForeground(new Color(15, 23, 42));
        lblPassTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        passCard.add(lblPassTitle);
        passCard.add(Box.createVerticalStrut(15));

        txtPassOld = createPasswordField(passCard, "Mật khẩu hiện tại:");
        txtPassNew = createPasswordField(passCard, "Mật khẩu mới:");

        passCard.add(Box.createVerticalStrut(10));
        btnSavePassword = new ModernButton(" Đổi Mật Khẩu");
        btnSavePassword.setColors(new Color(16, 185, 129), new Color(5, 150, 105), new Color(4, 120, 87));
        btnSavePassword.setPreferredSize(new Dimension(160, 36));
        btnSavePassword.setMaximumSize(new Dimension(160, 36));
        btnSavePassword.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSavePassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        passCard.add(btnSavePassword);

        // Add cards to wrapper
        cardWrapper.add(infoCard);
        cardWrapper.add(passCard);
        bodyWrapper.add(cardWrapper);

        JScrollPane scroll = new JScrollPane(bodyWrapper);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(248, 250, 252));

        add(scroll, BorderLayout.CENTER);
    }

    private void addLabel(JPanel panel, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(71, 85, 105));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(4));
    }

    private JTextField createField(JPanel panel, String labelText) {
        addLabel(panel, labelText);
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(410, 32));
        field.setMaximumSize(new Dimension(410, 32));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(field);
        panel.add(Box.createVerticalStrut(10));
        return field;
    }

    private JPasswordField createPasswordField(JPanel panel, String labelText) {
        addLabel(panel, labelText);
        JPasswordField field = new JPasswordField();
        field.setPreferredSize(new Dimension(410, 32));
        field.setMaximumSize(new Dimension(410, 32));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(field);
        panel.add(Box.createVerticalStrut(10));
        return field;
    }

    public String getFullName() {
        return txtName.getText().trim();
    }

    public String getPhone() {
        return txtPhone.getText().trim();
    }

    public void setFullName(String val) {
        txtName.setText(val);
    }

    public void setPhone(String val) {
        txtPhone.setText(val);
    }

    public String getOldPassword() {
        return new String(txtPassOld.getPassword());
    }

    public String getNewPassword() {
        return new String(txtPassNew.getPassword());
    }

    public void clearPasswordFields() {
        txtPassOld.setText("");
        txtPassNew.setText("");
    }

    public ModernButton getBtnSaveProfile() {
        return btnSaveProfile;
    }

    public ModernButton getBtnSavePassword() {
        return btnSavePassword;
    }

    public void setProfileSavingState(boolean saving) {
        btnSaveProfile.setEnabled(!saving);
        if (saving) {
            btnSaveProfile.setText("Đang lưu...");
        } else {
            btnSaveProfile.setText(" Lưu Thông Tin");
        }
    }

    public void setPasswordSavingState(boolean saving) {
        btnSavePassword.setEnabled(!saving);
        if (saving) {
            btnSavePassword.setText("Đang lưu...");
        } else {
            btnSavePassword.setText(" Đổi Mật Khẩu");
        }
    }
}
