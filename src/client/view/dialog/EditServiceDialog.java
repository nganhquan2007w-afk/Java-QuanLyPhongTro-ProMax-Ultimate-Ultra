package client.view.dialog;

import client.view.component.RoundTextField;
import client.view.component.ModernButton;
import client.view.component.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EditServiceDialog extends JDialog {
    private RoundTextField txtName;
    private RoundTextField txtPrice;
    private RoundTextField txtUnit;
    private ModernButton btnSave;
    private ModernButton btnCancel;

    public EditServiceDialog(Frame parent) {
        super(parent, "Sửa Dịch Vụ", true);
        setSize(400, 350);
        setLocationRelativeTo(parent);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setBackground(new Color(248, 250, 252));
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));

        RoundedPanel formPanel = new RoundedPanel(16, Color.WHITE);
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240)),
            new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblName = new JLabel("Tên Dịch Vụ:");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblName, gbc);

        gbc.gridy = 1;
        txtName = new RoundTextField(15);
        formPanel.add(txtName, gbc);

        gbc.gridy = 2;
        JLabel lblPrice = new JLabel("Đơn Giá:");
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblPrice, gbc);

        gbc.gridy = 3;
        txtPrice = new RoundTextField(15);
        formPanel.add(txtPrice, gbc);

        gbc.gridy = 4;
        JLabel lblUnit = new JLabel("Đơn Vị Tính:");
        lblUnit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblUnit, gbc);

        gbc.gridy = 5;
        txtUnit = new RoundTextField(15);
        formPanel.add(txtUnit, gbc);

        contentPane.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        btnCancel = new ModernButton("Hủy");
        btnCancel.setColors(new Color(241, 245, 249), new Color(226, 232, 240), new Color(203, 213, 225));
        btnCancel.setForeground(new Color(71, 85, 105));
        btnCancel.setPreferredSize(new Dimension(100, 36));

        btnSave = new ModernButton("Cập Nhật");
        btnSave.setColors(new Color(59, 130, 246), new Color(37, 99, 235), new Color(29, 78, 216));
        btnSave.setPreferredSize(new Dimension(120, 36));

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);

        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(contentPane);
    }

    public void setValues(String name, String price, String unit) {
        txtName.setText(name);
        txtPrice.setText(price);
        txtUnit.setText(unit);
    }

    public String getName() { return txtName.getText().trim(); }
    public String getPrice() { return txtPrice.getText().trim(); }
    public String getUnit() { return txtUnit.getText().trim(); }

    public ModernButton getBtnSave() { return btnSave; }
    public ModernButton getBtnCancel() { return btnCancel; }
}
