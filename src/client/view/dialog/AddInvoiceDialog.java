package client.view.dialog;

import client.view.component.ModernButton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * AddInvoiceDialog - View (MVC) dùng để nhập thông tin hóa đơn mới.
 *
 * NGUYÊN TẮC MVC:
 *   - View này CHỈ chứa UI components và getter/setter.
 *   - KHÔNG gọi bất kỳ Service, DAO hay tầng nghiệp vụ nào.
 *   - Danh sách phòng được Controller truyền vào qua constructor.
 *   - Controller (AdminHomeController / InvoicesController) sẽ xử lý sự kiện.
 */
@SuppressWarnings("serial")
public class AddInvoiceDialog extends JDialog {

    // ─── Input fields ────────────────────────────────────────────────────────
    private JComboBox<String> cmbRoom;
    private JTextField        txtTenantName;
    private JTextField        txtDate;
    private JTextField        txtRent;
    private JTextField        txtElectricity;
    private JTextField        txtWater;
    private JTextField        txtOther;
    
    private java.util.Map<String, String> roomTenantMap;
    private java.util.Map<String, Double> roomPriceMap;

    // ─── Action buttons ───────────────────────────────────────────────────────
    private ModernButton btnSave;
    private JButton      btnCancel;

    /**
     * @param owner         Frame cha
     * @param roomTenantMap Map chứa mã phòng và tên khách thuê tương ứng do Controller cung cấp.
     */
    public AddInvoiceDialog(Frame owner, java.util.Map<String, String> roomTenantMap, java.util.Map<String, Double> roomPriceMap) {
        super(owner, "Lập Hóa Đơn Chi Tiết", true);
        this.roomTenantMap = roomTenantMap;
        this.roomPriceMap = roomPriceMap;
        setSize(470, 660);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        String[] roomList = roomTenantMap.keySet().toArray(new String[0]);
        java.util.Arrays.sort(roomList);
        initComponents(roomList);
    }

    /** Constructor fallback. */
    public AddInvoiceDialog(Frame owner) {
        this(owner, new java.util.HashMap<>(), new java.util.HashMap<>());
    }

    // ─── Khởi tạo giao diện ──────────────────────────────────────────────────
    private void initComponents(String[] roomList) {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(new EmptyBorder(20, 25, 20, 25));
        container.setBackground(Color.WHITE);

        // Tiêu đề
        JLabel lblTitle = new JLabel("Lập Hóa Đơn Dịch Vụ Mới");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(new Color(15, 23, 42));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        container.add(lblTitle);
        container.add(Box.createVerticalStrut(20));

        // Phòng — danh sách do Controller truyền vào, View không tự load
        addLabel(container, "Chọn phòng lập hóa đơn (*):");
        cmbRoom = new JComboBox<>(roomList);
        styleCombo(cmbRoom);
        container.add(cmbRoom);
        container.add(Box.createVerticalStrut(12));

        // ── Các trường nhập liệu ────────────────────────────────────────────
        txtTenantName  = createField(container, "Tên khách thuê (*):");
        txtDate        = createField(container, "Ngày lập hóa đơn (dd/MM/yyyy):");
        txtRent        = createField(container, "Tiền thuê phòng (Nhập số tiền VND) (*):");
        txtElectricity = createField(container, "Chỉ số điện tiêu thụ (kWh):");
        txtWater       = createField(container, "Khối lượng nước tiêu thụ (m³):");
        txtOther       = createField(container, "Chi phí dịch vụ khác (Nhập số tiền VND, VD: 50000):");

        // Giá trị mặc định cho ngày
        txtDate.setText(new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()));

        // Tự động điền tên khách và giá phòng khi chọn phòng
        cmbRoom.addActionListener(e -> {
            String selectedRoom = (String) cmbRoom.getSelectedItem();
            if (selectedRoom != null) {
                if (roomTenantMap != null) {
                    String tName = roomTenantMap.get(selectedRoom);
                    txtTenantName.setText(tName != null ? tName : "");
                }
                if (roomPriceMap != null) {
                    Double price = roomPriceMap.get(selectedRoom);
                    if (price != null) {
                        txtRent.setText(String.format("%.0f", price));
                    } else {
                        txtRent.setText("");
                    }
                }
            }
        });
        
        // Trigger initial selection
        if (roomList.length > 0) {
            cmbRoom.setSelectedIndex(0);
        }

        container.add(Box.createVerticalStrut(10));

        // ── Nút hành động ───────────────────────────────────────────────────
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnCancel = new JButton("Hủy bỏ");
        btnCancel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCancel.setPreferredSize(new Dimension(100, 36));
        btnPanel.add(btnCancel);

        btnSave = new ModernButton("Tạo Hóa Đơn");
        btnSave.setColors(new Color(16, 185, 129), new Color(5, 150, 105), new Color(4, 120, 87));
        btnSave.setPreferredSize(new Dimension(130, 36));
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
    /** Mã phòng đã chọn */
    public String getRoom()        { return (String) cmbRoom.getSelectedItem(); }

    /** Tên khách thuê */
    public String getTenantName()  { return txtTenantName.getText().trim(); }

    /** Ngày lập hóa đơn */
    public String getDate()        { return txtDate.getText().trim(); }

    /** Tiền thuê phòng */
    public String getRent()        { return txtRent.getText().trim(); }

    /** Số kWh điện */
    public String getElectricity() { return txtElectricity.getText().trim(); }

    /** Số m³ nước */
    public String getWater()       { return txtWater.getText().trim(); }

    /** Chi phí khác */
    public String getOther()       { return txtOther.getText().trim(); }

    /** Nút Tạo Hóa Đơn – Controller gắn ActionListener */
    public ModernButton getBtnSave()  { return btnSave; }

    /** Nút Hủy – Controller gắn ActionListener */
    public JButton getBtnCancel()     { return btnCancel; }
}
