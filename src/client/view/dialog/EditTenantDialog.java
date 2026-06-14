package client.view.dialog;

import client.view.component.ModernButton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings("serial")
public class EditTenantDialog extends JDialog {

    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtPhone;
    private JTextField txtCCCD;
    private JComboBox<String> cmbGender;
    private JTextField txtBirthDate;
    private JTextField txtAddress;
    private JComboBox<String> cmbRoom;

    private ModernButton btnSave;
    private JButton btnCancel;

    public EditTenantDialog(Frame owner, int tenantId, String fullName, String phone, String cccd, String gender, String birthDate, String address, String currentRoom, String[] roomList) {
        super(owner, "Sửa Thông Tin Khách Thuê", true);
        setSize(470, 620);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        initComponents(tenantId, fullName, phone, cccd, gender, birthDate, address, currentRoom, roomList);
    }

    private void initComponents(int tenantId, String fullName, String phone, String cccd, String gender, String birthDate, String address, String currentRoom, String[] roomList) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(new EmptyBorder(20, 25, 20, 25));
        container.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Cập Nhật Thông Tin");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(15, 23, 42));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(lblTitle);
        container.add(Box.createVerticalStrut(20));

        txtId = createField(container, "ID (Chỉ đọc):");
        txtId.setText(String.valueOf(tenantId));
        txtId.setEditable(false);
        txtId.setBackground(new Color(241, 245, 249));

        txtName = createField(container, "Họ và tên khách thuê (*):");
        txtName.setText(fullName);

        txtPhone = createField(container, "Số điện thoại liên hệ (*):");
        txtPhone.setText(phone);

        txtCCCD = createField(container, "Số CMND / CCCD:");
        txtCCCD.setText(cccd);

        txtBirthDate = createField(container, "Ngày sinh (dd/MM/yyyy):");
        // Reformat date from yyyy-MM-dd to dd/MM/yyyy if necessary
        txtBirthDate.setText(formatDateForDisplay(birthDate));

        addLabel(container, "Giới tính:");
        cmbGender = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        styleCombo(cmbGender);
        if (gender != null) cmbGender.setSelectedItem(gender);
        container.add(cmbGender);
        container.add(Box.createVerticalStrut(12));

        txtAddress = createField(container, "Địa chỉ thường trú:");
        txtAddress.setText(address);

        addLabel(container, "Phòng cần đổi (Chọn phòng trống nếu muốn đổi):");
        cmbRoom = new JComboBox<>(roomList);
        styleCombo(cmbRoom);
        
        // Find and select current room
        if (currentRoom != null && !currentRoom.isEmpty()) {
            for (int i = 0; i < cmbRoom.getItemCount(); i++) {
                if (cmbRoom.getItemAt(i).startsWith(currentRoom)) {
                    cmbRoom.setSelectedIndex(i);
                    break;
                }
            }
        }
        container.add(cmbRoom);
        container.add(Box.createVerticalStrut(12));

        container.add(Box.createVerticalStrut(10));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnCancel = new JButton("Hủy bỏ");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCancel.setPreferredSize(new Dimension(100, 36));
        btnPanel.add(btnCancel);

        btnSave = new ModernButton("Lưu Lại");
        btnSave.setColors(new Color(245, 158, 11), new Color(217, 119, 6), new Color(180, 83, 9));
        btnSave.setPreferredSize(new Dimension(120, 36));
        btnPanel.add(btnSave);

        container.add(btnPanel);

        JScrollPane scroll = new JScrollPane(container);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);
    }

    private String formatDateForDisplay(String dbDate) {
        if (dbDate == null || dbDate.isEmpty()) return "";
        try {
            // Assume DB returns yyyy-MM-dd, UI needs dd/MM/yyyy
            if (dbDate.contains("-")) {
                Date d = new SimpleDateFormat("yyyy-MM-dd").parse(dbDate);
                return new SimpleDateFormat("dd/MM/yyyy").format(d);
            }
        } catch (ParseException e) {
            // Ignore
        }
        return dbDate; // fallback
    }

    private void addLabel(JPanel panel, String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(71, 85, 105));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(5));
    }

    private JTextField createField(JPanel panel, String labelText) {
        addLabel(panel, labelText);
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(410, 32));
        field.setMaximumSize(new Dimension(410, 32));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(field);
        panel.add(Box.createVerticalStrut(12));
        return field;
    }

    private void styleCombo(JComboBox<?> cmb) {
        cmb.setPreferredSize(new Dimension(410, 32));
        cmb.setMaximumSize(new Dimension(410, 32));
        cmb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmb.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    public int getTenantId() { return Integer.parseInt(txtId.getText()); }
    public String getTenantName() { return txtName.getText().trim(); }
    public String getPhone() { return txtPhone.getText().trim(); }
    public String getCCCD() { return txtCCCD.getText().trim(); }
    public String getGender() { return (String) cmbGender.getSelectedItem(); }
    public String getBirthDate() { return txtBirthDate.getText().trim(); }
    public String getAddress() { return txtAddress.getText().trim(); }
    public String getRoom() {
        String sel = (String) cmbRoom.getSelectedItem();
        return sel == null ? "" : sel;
    }

    public ModernButton getBtnSave() { return btnSave; }
    public JButton getBtnCancel() { return btnCancel; }
}
