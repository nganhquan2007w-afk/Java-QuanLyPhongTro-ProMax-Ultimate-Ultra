package client.view.panel;

import client.view.component.BadgeCellRenderer;
import client.view.component.RoundTextField;
import client.view.component.RoundedPanel;
import client.view.component.ModernButton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

@SuppressWarnings("serial")
public final class TenantsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private RoundTextField txtSearch;
    private JComboBox<String> cmbRooms;
    private ModernButton btnAdd;
    private ModernButton btnDelete;
    private ModernButton btnEdit;

    public TenantsPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        initComponents();
    }

    private void initComponents() {
        JPanel bodyWrapper = new JPanel();
        bodyWrapper.setLayout(new BorderLayout(0, 20));
        bodyWrapper.setBorder(new EmptyBorder(25, 25, 25, 25));
        bodyWrapper.setOpaque(false);

        // Header Control Card
        RoundedPanel controlCard = new RoundedPanel(16, Color.WHITE);
        controlCard.setLayout(new BoxLayout(controlCard, BoxLayout.Y_AXIS));
        controlCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // Left Controls: Search & Room filter
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftPanel.setOpaque(false);
        leftPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtSearch = new RoundTextField("Tìm kiếm theo...", 20, 12, true);
        leftPanel.add(txtSearch);

        JLabel lblRoom = new JLabel("Lọc theo phòng:");
        try {
            java.net.URL url = getClass().getResource("/Image/filter.png");
            if (url != null) lblRoom.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        lblRoom.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblRoom.setForeground(new Color(71, 85, 105));
        leftPanel.add(lblRoom);

        String[] rooms = {"Tất cả phòng"};
        cmbRooms = new JComboBox<>(rooms);
        cmbRooms.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbRooms.setBackground(Color.WHITE);
        cmbRooms.setPreferredSize(new Dimension(130, 32));
        cmbRooms.setFocusable(false);
        leftPanel.add(cmbRooms);

        controlCard.add(leftPanel);
        controlCard.add(Box.createVerticalStrut(10));

        // Right Controls: Register Button
        btnDelete = new ModernButton(" Xóa Khách Thuê");
        try {
            java.net.URL url = getClass().getResource("/Image/delete.png");
            if (url != null) btnDelete.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        btnDelete.setColors(new Color(239, 68, 68), new Color(220, 38, 38), new Color(185, 28, 28));
        btnDelete.setPreferredSize(new Dimension(160, 36));
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        btnEdit = new ModernButton(" Sửa Khách Thuê");
        try {
            java.net.URL url = getClass().getResource("/Image/pencil.png");
            if (url != null) btnEdit.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        btnEdit.setColors(new Color(245, 158, 11), new Color(217, 119, 6), new Color(180, 83, 9));
        btnEdit.setPreferredSize(new Dimension(160, 36));
        btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnAdd = new ModernButton(" Đăng Ký Khách Mới");
        try {
            java.net.URL url = getClass().getResource("/Image/ThemUser.png");
            if (url != null) btnAdd.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        btnAdd.setColors(new Color(99, 102, 241), new Color(79, 70, 229), new Color(67, 56, 202));
        btnAdd.setPreferredSize(new Dimension(180, 36));
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        rightPanel.setOpaque(false);
        rightPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rightPanel.add(btnDelete);
        rightPanel.add(btnEdit);
        rightPanel.add(btnAdd);
        controlCard.add(rightPanel);

        bodyWrapper.add(controlCard, BorderLayout.NORTH);

        // Table Card
        RoundedPanel tableCard = new RoundedPanel(16, Color.WHITE);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Table columns
        String[] columnNames = {"ID", "Họ và Tên", "Mã Phòng", "Số Điện Thoại", "Số CCCD", "Ngày Bắt Đầu", "Thời Hạn HĐ", "Trạng Thái"};
        Object[][] data = {};

        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(241, 245, 249));
        table.setSelectionForeground(new Color(15, 23, 42));
        table.getColumnModel().getColumn(7).setCellRenderer(new BadgeCellRenderer()); // Reuse BadgeCellRenderer or custom one.

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        
        // Show ID column with a fixed width
        table.getColumnModel().getColumn(0).setMinWidth(50);
        table.getColumnModel().getColumn(0).setMaxWidth(80);
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(new Color(71, 85, 105));
        header.setPreferredSize(new Dimension(100, 36));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        bodyWrapper.add(tableCard, BorderLayout.CENTER);

        JScrollPane scroll = new JScrollPane(bodyWrapper);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(248, 250, 252));

        add(scroll, BorderLayout.CENTER);
    }

    // Getters
    public RoundTextField getTxtSearch() { return txtSearch; }
    public JComboBox<String> getCmbRooms() { return cmbRooms; }
    public ModernButton getBtnAdd() { return btnAdd; }
    public ModernButton getBtnDelete() { return btnDelete; }
    public ModernButton getBtnEdit() { return btnEdit; }
    public JTable getTable() { return table; }
    public DefaultTableModel getTableModel() { return tableModel; }

// <editor-fold defaultstate="collapsed" desc="Generated Code">
private void initComponentsNetBeans() {

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGap(0, 400, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGap(0, 300, Short.MAX_VALUE)
    );
}// </editor-fold>


// Variables declaration - do not modify
// End of variables declaration

}
