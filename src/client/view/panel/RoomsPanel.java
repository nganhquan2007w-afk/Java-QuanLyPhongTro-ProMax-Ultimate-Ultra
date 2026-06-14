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
public final class RoomsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private RoundTextField txtSearch;
    private JComboBox<String> cmbStatus;
    private ModernButton btnAdd;
    private ModernButton btnEdit;
    private ModernButton btnMaintenance;
    private ModernButton btnDelete;
    private ModernButton btnExport;
    private ModernButton btnImport;

    public RoomsPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        initComponents();
    }

    private void initComponents() {
        JPanel bodyWrapper = new JPanel();
        bodyWrapper.setLayout(new BorderLayout(0, 20));
        bodyWrapper.setBorder(new EmptyBorder(25, 25, 25, 25));
        bodyWrapper.setOpaque(false);

        // Header Control Card (Search + Filters + Add Button)
        RoundedPanel controlCard = new RoundedPanel(16, Color.WHITE);
        controlCard.setLayout(new BoxLayout(controlCard, BoxLayout.Y_AXIS));
        controlCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        // Left Controls: Search & Filter Combo
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftPanel.setOpaque(false);
        leftPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtSearch = new RoundTextField("Tìm kiếm theo...", 20, 12, true);
        leftPanel.add(txtSearch);

        JLabel lblFilter = new JLabel("Trạng thái:");
        try {
            java.net.URL url = getClass().getResource("/Image/filter.png");
            if (url != null) lblFilter.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblFilter.setForeground(new Color(71, 85, 105));
        leftPanel.add(lblFilter);

        String[] statuses = {"Tất cả", "Trống", "Đang thuê", "Bảo trì"};
        cmbStatus = new JComboBox<>(statuses);
        cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbStatus.setBackground(Color.WHITE);
        cmbStatus.setPreferredSize(new Dimension(130, 32));
        cmbStatus.setFocusable(false);
        leftPanel.add(cmbStatus);

        controlCard.add(leftPanel);
        controlCard.add(Box.createVerticalStrut(10));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        rightPanel.setOpaque(false);
        rightPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnExport = new ModernButton(" Xuất CSV");
        try {
            java.net.URL url = getClass().getResource("/Image/export.png");
            if (url != null) btnExport.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        btnExport.setColors(new Color(16, 185, 129), new Color(5, 150, 105), new Color(4, 120, 87));
        btnExport.setPreferredSize(new Dimension(130, 36));
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rightPanel.add(btnExport);

        btnImport = new ModernButton(" Nhập CSV");
        try {
            java.net.URL url = getClass().getResource("/Image/import.png");
            if (url != null) btnImport.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        btnImport.setColors(new Color(245, 158, 11), new Color(217, 119, 6), new Color(180, 83, 9));
        btnImport.setPreferredSize(new Dimension(130, 36));
        btnImport.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rightPanel.add(btnImport);

        btnDelete = new ModernButton(" Xóa Phòng");
        try {
            java.net.URL url = getClass().getResource("/Image/delete.png");
            if (url != null) btnDelete.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        btnDelete.setColors(new Color(239, 68, 68), new Color(220, 38, 38), new Color(185, 28, 28));
        btnDelete.setPreferredSize(new Dimension(130, 36));
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rightPanel.add(btnDelete);

        btnEdit = new ModernButton(" Sửa Phòng");
        try {
            java.net.URL url = getClass().getResource("/Image/pencil.png");
            if (url != null) btnEdit.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        btnEdit.setColors(new Color(245, 158, 11), new Color(217, 119, 6), new Color(180, 83, 9));
        btnEdit.setPreferredSize(new Dimension(130, 36));
        btnEdit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rightPanel.add(btnEdit);

        btnMaintenance = new ModernButton(" Bảo Trì");
        try {
            java.net.URL url = getClass().getResource("/Image/SuaChua.png");
            if (url != null) btnMaintenance.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        btnMaintenance.setColors(new Color(139, 92, 246), new Color(124, 58, 237), new Color(109, 40, 217));
        btnMaintenance.setPreferredSize(new Dimension(130, 36));
        btnMaintenance.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rightPanel.add(btnMaintenance);

        btnAdd = new ModernButton(" Thêm Phòng");
        try {
            java.net.URL url = getClass().getResource("/Image/add.png");
            if (url != null) btnAdd.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        btnAdd.setColors(new Color(59, 130, 246), new Color(37, 99, 235), new Color(29, 78, 216));
        btnAdd.setPreferredSize(new Dimension(140, 36));
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
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

        // Column definitions
        String[] columnNames = {"Mã Phòng", "Khu Vực", "Loại Phòng", "Đơn Giá (Tháng)", "Số Khách Tối Đa", "Trạng Thái", "Mô Tả"};
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
        table.getColumnModel().getColumn(5).setCellRenderer(new BadgeCellRenderer());

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

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
    public JComboBox<String> getCmbStatus() { return cmbStatus; }
    public ModernButton getBtnAdd() { return btnAdd; }
    public ModernButton getBtnEdit() { return btnEdit; }
    public ModernButton getBtnMaintenance() { return btnMaintenance; }
    public ModernButton getBtnDelete() { return btnDelete; }
    public ModernButton getBtnExport() { return btnExport; }
    public ModernButton getBtnImport() { return btnImport; }
    public JTable getTable() { return table; }
    public DefaultTableModel getTableModel() { return tableModel; }

    public void setAdminMode(boolean adminMode) {
        if (btnAdd != null) btnAdd.setVisible(adminMode);
        if (btnEdit != null) btnEdit.setVisible(adminMode);
        if (btnDelete != null) btnDelete.setVisible(adminMode);
        if (btnExport != null) btnExport.setVisible(adminMode);
        if (btnImport != null) btnImport.setVisible(adminMode);
        revalidate();
        repaint();
    }

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
