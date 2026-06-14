package client.view.dialog;

import client.view.component.ModernButton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * AddContractDialog - Dialog to input details for a new tenancy contract.
 */
@SuppressWarnings("serial")
public class AddContractDialog extends JDialog {
    private JComboBox<String> cmbRoom;
    private JTextField txtTenantName;
    private JTextField txtStartDate;
    private JTextField txtEndDate;
    private JTextField txtDeposit;
    private JComboBox<String> cmbStatus;
    private ModernButton btnSave;
    private JButton btnCancel;

    /**
     * @param owner    Frame cha
     * @param roomList Danh sách phòng do Controller cung cấp (không hardcode trong View).
     */
    public AddContractDialog(Frame owner, String[] roomList) {
        super(owner, "Tạo Hợp Đồng Mới", true);
        setSize(450, 640);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        initComponents(roomList);
    }

    /** Fallback không có danh sách phòng. */
    public AddContractDialog(Frame owner) {
        this(owner, new String[]{});
    }

    private void initComponents(String[] roomList) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(new EmptyBorder(20, 25, 20, 25));
        container.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Tạo Hợp Đồng Thuê Phòng");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(15, 23, 42));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(lblTitle);
        container.add(Box.createVerticalStrut(20));

        // Phòng — danh sách do Controller truyền vào, View không tự load
        cmbRoom = new JComboBox<>(roomList);
        cmbRoom.setPreferredSize(new Dimension(380, 32));
        cmbRoom.setMaximumSize(new Dimension(380, 32));
        cmbRoom.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addLabel(container, "Chọn phòng trọ:");
        container.add(cmbRoom);
        container.add(Box.createVerticalStrut(12));

        txtTenantName = createField(container, "Họ và tên khách thuê:");
        txtStartDate = createField(container, "Ngày bắt đầu (dd/mm/yyyy):");
        txtEndDate = createField(container, "Ngày kết thúc (dd/mm/yyyy):");
        txtDeposit = createField(container, "Tiền đặt cọc (Ví dụ: 5.000.000 VND):");

        String[] statusList = {"Còn hiệu lực", "Sắp hết hạn", "Đã thanh lý"};
        cmbStatus = new JComboBox<>(statusList);
        cmbStatus.setPreferredSize(new Dimension(380, 32));
        cmbStatus.setMaximumSize(new Dimension(380, 32));
        cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addLabel(container, "Trạng thái hợp đồng:");
        container.add(cmbStatus);
        container.add(Box.createVerticalStrut(15));

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

    public String getRoom() { return (String) cmbRoom.getSelectedItem(); }
    public String getTenantName() { return txtTenantName.getText().trim(); }
    public String getStartDate() { return txtStartDate.getText().trim(); }
    public String getEndDate() { return txtEndDate.getText().trim(); }
    public String getDeposit() { return txtDeposit.getText().trim(); }
    public String getStatus() { return (String) cmbStatus.getSelectedItem(); }

    public ModernButton getBtnSave() { return btnSave; }
    public JButton getBtnCancel() { return btnCancel; }
}
