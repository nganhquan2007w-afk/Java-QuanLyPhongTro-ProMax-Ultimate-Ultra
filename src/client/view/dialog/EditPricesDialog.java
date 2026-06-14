package client.view.dialog;

import client.view.component.ModernButton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * EditPricesDialog - View dialog for editing service prices.
 */
@SuppressWarnings("serial")
public class EditPricesDialog extends JDialog {
    private JTextField txtRent;
    private JTextField txtElec;
    private JTextField txtWater;
    private JTextField txtInternet;
    private ModernButton btnSave;
    private JButton btnCancel;

    public EditPricesDialog(Frame owner) {
        super(owner, "Cập Nhật Đơn Giá Dịch Vụ", true);
        setSize(400, 500);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(new EmptyBorder(20, 25, 20, 25));
        container.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Cấu Hình Biểu Giá Dịch Vụ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(15, 23, 42));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(lblTitle);
        container.add(Box.createVerticalStrut(20));

        txtRent = createField(container, "Đơn giá tiền phòng tiêu chuẩn (VND):");
        txtElec = createField(container, "Đơn giá điện (VND / kWh):");
        txtWater = createField(container, "Đơn giá nước (VND / m3):");
        txtInternet = createField(container, "Cước phí mạng Internet (VND / Tháng):");

        container.add(Box.createVerticalStrut(10));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnCancel = new JButton("Hủy bỏ");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCancel.setPreferredSize(new Dimension(100, 36));
        btnPanel.add(btnCancel);

        btnSave = new ModernButton("Lưu Lại");
        btnSave.setColors(new Color(99, 102, 241), new Color(79, 70, 229), new Color(67, 56, 202));
        btnSave.setPreferredSize(new Dimension(110, 36));
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
        field.setPreferredSize(new Dimension(330, 32));
        field.setMaximumSize(new Dimension(330, 32));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(field);
        panel.add(Box.createVerticalStrut(12));
        return field;
    }

    public String getRent() { return txtRent.getText().trim(); }
    public String getElec() { return txtElec.getText().trim(); }
    public String getWater() { return txtWater.getText().trim(); }
    public String getInternet() { return txtInternet.getText().trim(); }

    public void setValues(String rent, String elec, String water, String internet) {
        txtRent.setText(rent);
        txtElec.setText(elec);
        txtWater.setText(water);
        txtInternet.setText(internet);
    }

    public ModernButton getBtnSave() { return btnSave; }
    public JButton getBtnCancel() { return btnCancel; }
}
