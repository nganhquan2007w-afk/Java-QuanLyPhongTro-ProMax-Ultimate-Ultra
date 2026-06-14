package client.view.dialog;

import client.view.component.ModernButton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * ReportMaintenanceDialog - Dialog to report a new maintenance/repair issue.
 */
@SuppressWarnings("serial")
public class ReportMaintenanceDialog extends JDialog {
    private JComboBox<String> cmbRoom;
    private JTextField txtDescription;
    private JComboBox<String> cmbPriority;
    private JTextField txtDate;
    private ModernButton btnSave;
    private JButton btnCancel;

    public ReportMaintenanceDialog(Frame owner, String[] roomList) {
        super(owner, "Báo Cáo Sự Cố Mới", true);
        setSize(450, 440);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        initComponents(roomList);
    }
    
    public ReportMaintenanceDialog(Frame owner) {
        this(owner, new String[]{"P.101", "P.102", "P.103", "P.201", "P.202", "P.204", "P.302"});
    }

    private void initComponents(String[] roomList) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(new EmptyBorder(20, 25, 20, 25));
        container.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Báo Cáo Sự Cố Thiết Bị / Phòng");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(15, 23, 42));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(lblTitle);
        container.add(Box.createVerticalStrut(20));

        cmbRoom = new JComboBox<>(roomList);
        cmbRoom.setPreferredSize(new Dimension(380, 32));
        cmbRoom.setMaximumSize(new Dimension(380, 32));
        cmbRoom.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        String userRoom = "P.101";
        if (common.model.UserSession.getInstance() != null) {
            String roomId = common.model.UserSession.getInstance().getRoomId();
            if (roomId != null && !roomId.trim().isEmpty() && !roomId.equals("Chưa có")) {
                userRoom = roomId;
            }
        }
        cmbRoom.setSelectedItem(userRoom);
        // Đã gỡ bỏ cmbRoom.setEnabled(false) để người dùng có thể tự chọn phòng báo cáo

        addLabel(container, "Phòng trọ báo cáo:");
        container.add(cmbRoom);
        container.add(Box.createVerticalStrut(12));

        txtDescription = createField(container, "Nội dung sự cố (Ví dụ: Hỏng vòi hoa sen):");

        String[] priorityList = {"Thấp", "Trung bình", "Cao"};
        cmbPriority = new JComboBox<>(priorityList);
        cmbPriority.setPreferredSize(new Dimension(380, 32));
        cmbPriority.setMaximumSize(new Dimension(380, 32));
        cmbPriority.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addLabel(container, "Mức độ ưu tiên:");
        container.add(cmbPriority);
        container.add(Box.createVerticalStrut(12));

        txtDate = createField(container, "Ngày báo cáo (dd/mm/yyyy):");
        
        // Auto-fill current date
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        txtDate.setText(sdf.format(new java.util.Date()));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnCancel = new JButton("Hủy bỏ");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCancel.setPreferredSize(new Dimension(100, 36));
        btnPanel.add(btnCancel);

        btnSave = new ModernButton("Lưu Lại");
        btnSave.setColors(new Color(245, 158, 11), new Color(217, 119, 6), new Color(180, 83, 9)); // Amber themed
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

    public String getCode() { return "SC-" + (int)(Math.random() * 9000 + 1000); }
    public String getRoom() { return (String) cmbRoom.getSelectedItem(); }
    public String getDesc() { return txtDescription.getText().trim(); }
    public String getPriority() { return (String) cmbPriority.getSelectedItem(); }
    public String getDate() { return txtDate.getText().trim(); }
    public String getStatus() { return "Đang chờ xử lí"; }

    public ModernButton getBtnSave() { return btnSave; }
    public JButton getBtnCancel() { return btnCancel; }
}
