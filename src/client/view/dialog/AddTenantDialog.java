package client.view.dialog;

import client.view.component.ModernButton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * AddTenantDialog - View (MVC) dùng để nhập thông tin khách thuê mới.
 *
 * NGUYÊN TẮC MVC:
 *   - View này CHỈ chứa UI components và getter/setter.
 *   - KHÔNG gọi bất kỳ Service, DAO hay tầng nghiệp vụ nào.
 *   - Danh sách phòng được Controller truyền vào qua constructor.
 *   - Controller (AdminHomeController / TenantsController) sẽ xử lý sự kiện.
 */
@SuppressWarnings("serial")
public class AddTenantDialog extends JDialog {

    // ─── Input fields ────────────────────────────────────────────────────────
    private JTextField     txtName;
    private JTextField     txtPhone;
    private JTextField     txtCCCD;
    private JComboBox<String> cmbGender;
    private JTextField     txtBirthDate;
    private JTextField     txtAddress;
    private JComboBox<String> cmbRoom;
    private JTextField     txtStartDate;
    private JTextField     txtDuration;

    // ─── Action buttons ───────────────────────────────────────────────────────
    private ModernButton   btnSave;
    private JButton        btnCancel;

    /**
     * @param owner    Frame cha
     * @param roomList Danh sách mã phòng do Controller cung cấp (lấy từ DB qua Service).
     *                 View KHÔNG tự tải danh sách này.
     */
    public AddTenantDialog(Frame owner, String[] roomList) {
        super(owner, "Đăng Ký Khách Thuê Mới", true);
        setSize(470, 690);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        initComponents(roomList);
    }

    /** Constructor fallback không có danh sách phòng (hiển thị combobox trống). */
    public AddTenantDialog(Frame owner) {
        this(owner, new String[]{});
    }

    // ─── Khởi tạo giao diện ──────────────────────────────────────────────────
    private void initComponents(String[] roomList) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(new EmptyBorder(20, 25, 20, 25));
        container.setBackground(Color.WHITE);

        // Tiêu đề
        JLabel lblTitle = new JLabel("Đăng Ký Thành Viên Thuê Phòng");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(15, 23, 42));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(lblTitle);
        container.add(Box.createVerticalStrut(20));

        // ── Các trường nhập liệu ────────────────────────────────────────────
        txtName      = createField(container, "Họ và tên khách thuê (*):");
        txtPhone     = createField(container, "Số điện thoại liên hệ (*):");
        txtCCCD      = createField(container, "Số CMND / CCCD:");
        txtBirthDate = createField(container, "Ngày sinh (dd/MM/yyyy):");
        txtAddress   = createField(container, "Địa chỉ thường trú:");

        // Giới tính
        addLabel(container, "Giới tính:");
        cmbGender = new JComboBox<>(new String[]{"Nam", "Nữ", "Khác"});
        styleCombo(cmbGender);
        container.add(cmbGender);
        container.add(Box.createVerticalStrut(12));

        // Phòng — danh sách do Controller truyền vào, View không tự load
        addLabel(container, "Phân phối vào phòng (*):");
        cmbRoom = new JComboBox<>(roomList);
        styleCombo(cmbRoom);
        container.add(cmbRoom);
        container.add(Box.createVerticalStrut(12));

        txtStartDate = createField(container, "Ngày bắt đầu ở (dd/MM/yyyy):");
        txtDuration  = createField(container, "Thời hạn hợp đồng (VD: 12 tháng):");

        // Giá trị mặc định
        txtStartDate.setText(new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()));
        txtDuration.setText("12 tháng");

        container.add(Box.createVerticalStrut(10));

        // ── Nút hành động ───────────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnCancel = new JButton("Hủy bỏ");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCancel.setPreferredSize(new Dimension(100, 36));
        btnPanel.add(btnCancel);

        btnSave = new ModernButton("Lưu Lại");
        btnSave.setColors(new Color(99, 102, 241), new Color(79, 70, 229), new Color(67, 56, 202));
        btnSave.setPreferredSize(new Dimension(120, 36));
        btnPanel.add(btnSave);

        container.add(btnPanel);

        JScrollPane scroll = new JScrollPane(container);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);
    }

    // ─── Helpers UI ──────────────────────────────────────────────────────────
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

    // ─── Getters cho Controller ───────────────────────────────────────────────
    /** Họ và tên */
    public String getTenantName() { return txtName.getText().trim(); }

    /** Số điện thoại */
    public String getPhone()      { return txtPhone.getText().trim(); }

    /** CMND / CCCD */
    public String getCCCD()       { return txtCCCD.getText().trim(); }

    /** Giới tính */
    public String getGender()     { return (String) cmbGender.getSelectedItem(); }

    /** Ngày sinh dạng chuỗi dd/MM/yyyy */
    public String getBirthDate()  { return txtBirthDate.getText().trim(); }

    /** Địa chỉ */
    public String getAddress()    { return txtAddress.getText().trim(); }

    public String getRoom() {
        String sel = (String) cmbRoom.getSelectedItem();
        return sel == null ? "" : sel;
    }

    /** Ngày bắt đầu thuê */
    public String getStartDate()  { return txtStartDate.getText().trim(); }

    /** Thời hạn hợp đồng (VD: "12 tháng") */
    public String getDuration()   { return txtDuration.getText().trim(); }

    /** Nút Lưu – Controller gắn ActionListener */
    public ModernButton getBtnSave()  { return btnSave; }

    /** Nút Hủy – Controller gắn ActionListener */
    public JButton getBtnCancel()     { return btnCancel; }
}
