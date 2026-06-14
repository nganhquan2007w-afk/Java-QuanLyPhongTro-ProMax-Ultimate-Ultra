package client.view.dialog;

import client.view.component.ModernButton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * AddRoomDialog - View dialog for entering new room details.
 */
@SuppressWarnings("serial")
public class AddRoomDialog extends JDialog {
    private JTextField txtCode;
    private JTextField txtArea;
    private JComboBox<String> cmbType;
    private JTextField txtPrice;
    private JTextField txtCapacity;
    private JTextField txtDesc;
    private ModernButton btnSave;
    private JButton btnCancel;

    public AddRoomDialog(Frame owner) {
        super(owner, "Thêm Phòng Mới", true);
        setSize(450, 560);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(new EmptyBorder(20, 25, 20, 25));
        container.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Nhập Thông Tin Phòng Mới");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(15, 23, 42));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(lblTitle);
        container.add(Box.createVerticalStrut(20));

        txtCode = createField(container, "Mã phòng (Ví dụ: P.105):");
        txtArea = createField(container, "Khu vực (Ví dụ: Khu nhà A):");

        String[] types = {"Tiêu chuẩn", "Cao cấp", "Giá rẻ"};
        cmbType = new JComboBox<>(types);
        cmbType.setPreferredSize(new Dimension(380, 32));
        cmbType.setMaximumSize(new Dimension(380, 32));
        cmbType.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addLabel(container, "Loại phòng:");
        container.add(cmbType);
        container.add(Box.createVerticalStrut(12));

        txtPrice = createField(container, "Đơn giá thuê (VND / Tháng):");
        txtCapacity = createField(container, "Số người ở tối đa:");
        txtDesc = createField(container, "Mô tả tiện ích:");

        container.add(Box.createVerticalStrut(10));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnCancel = new JButton("Hủy bỏ");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCancel.setPreferredSize(new Dimension(100, 36));
        btnPanel.add(btnCancel);

        btnSave = new ModernButton("Lưu Lại");
        btnSave.setColors(new Color(59, 130, 246), new Color(37, 99, 235), new Color(29, 78, 216));
        btnSave.setPreferredSize(new Dimension(120, 36));
        btnPanel.add(btnSave);

        container.add(btnPanel);
        add(container, BorderLayout.CENTER);
    }

    private void addLabel(JPanel panel, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(new Color(71, 85, 105));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
    }

    private JTextField createField(JPanel panel, String labelText) {
        addLabel(panel, labelText);
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(380, 32));
        field.setMaximumSize(new Dimension(380, 32));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(field);
        panel.add(Box.createVerticalStrut(12));
        return field;
    }

    public String getCode() { return txtCode.getText().trim(); }
    public String getArea() { return txtArea.getText().trim(); }
    public String getRoomType() { return (String) cmbType.getSelectedItem(); }
    public String getPrice() { return txtPrice.getText().trim(); }
    public String getCapacity() { return txtCapacity.getText().trim(); }
    public String getDesc() { return txtDesc.getText().trim(); }

    public void setCode(String code) { txtCode.setText(code); txtCode.setEditable(false); }
    public void setArea(String area) { txtArea.setText(area); }
    public void setRoomType(String type) { cmbType.setSelectedItem(type); }
    public void setPrice(String price) { txtPrice.setText(price); }
    public void setCapacity(String capacity) { txtCapacity.setText(capacity); }
    public void setDesc(String desc) { txtDesc.setText(desc); }
    public void setTitleText(String title) { setTitle(title); }

    public ModernButton getBtnSave() { return btnSave; }
    public JButton getBtnCancel() { return btnCancel; }
}
