package client.view.panel;

import client.view.component.BadgeCellRenderer;
import client.view.component.RoundedPanel;
import client.view.component.ModernButton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

@SuppressWarnings("serial")
public final class AdminHomePanel extends JPanel {

    // Nút hành động nhanh
    private ModernButton btnAddRoom;
    private ModernButton btnAddTenant;
    private ModernButton btnCreateBill;
    private ModernButton btnImportData;
    private ModernButton btnExportData;

    // Metrics labels
    private JLabel lblTotalRoomsVal;
    private JLabel lblRentedRoomsVal;
    private JLabel lblEmptyRoomsVal;
    private JLabel lblRevenueVal;
    
    private JLabel lblTotalRoomsDesc;
    private JLabel lblRentedRoomsDesc;
    private JLabel lblEmptyRoomsDesc;
    private JLabel lblRevenueDesc;
    
    // Table
    private DefaultTableModel tableModel;
    private JTable table;

    public AdminHomePanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        initComponents();
    }

    private void initComponents() {
        JPanel bodyWrapper = new JPanel();
        bodyWrapper.setLayout(new BoxLayout(bodyWrapper, BoxLayout.Y_AXIS));
        bodyWrapper.setBorder(new EmptyBorder(25, 25, 25, 25));
        bodyWrapper.setOpaque(false);

        // 1. Metric cards grid
        JPanel cardGrid = new JPanel(new GridLayout(1, 4, 20, 0));
        cardGrid.setOpaque(false);
        cardGrid.setMaximumSize(new Dimension(1600, 110));
        cardGrid.setPreferredSize(new Dimension(800, 110));

        RoundedPanel card1 = new RoundedPanel(16, new Color(59, 130, 246), new Color(29, 78, 216));
        lblTotalRoomsVal = new JLabel("0 Phòng");
        lblTotalRoomsDesc = new JLabel("-");
        setupMetricCard(card1, "TỔNG SỐ PHÒNG", lblTotalRoomsVal, "", lblTotalRoomsDesc);
        cardGrid.add(card1);

        RoundedPanel card2 = new RoundedPanel(16, new Color(16, 185, 129), new Color(4, 120, 87));
        lblRentedRoomsVal = new JLabel("0 Phòng");
        lblRentedRoomsDesc = new JLabel("-");
        setupMetricCard(card2, "PHÒNG ĐÃ THUÊ", lblRentedRoomsVal, "", lblRentedRoomsDesc);
        cardGrid.add(card2);

        RoundedPanel card3 = new RoundedPanel(16, new Color(99, 102, 241), new Color(67, 56, 202));
        lblEmptyRoomsVal = new JLabel("0 Phòng");
        lblEmptyRoomsDesc = new JLabel("-");
        setupMetricCard(card3, "PHÒNG CÒN TRỐNG", lblEmptyRoomsVal, "", lblEmptyRoomsDesc);
        cardGrid.add(card3);

        RoundedPanel card4 = new RoundedPanel(16, new Color(245, 158, 11), new Color(217, 119, 6));
        lblRevenueVal = new JLabel("0đ");
        lblRevenueDesc = new JLabel("-");
        setupMetricCard(card4, "DOANH THU THÁNG", lblRevenueVal, "", lblRevenueDesc);
        cardGrid.add(card4);

        bodyWrapper.add(cardGrid);
        bodyWrapper.add(Box.createVerticalStrut(25));

        // 2. Grid Layout: Wide Invoice JTable + Quick Actions
        JPanel gridSplitPanel = new JPanel(new BorderLayout(20, 0));
        gridSplitPanel.setOpaque(false);

        // Left Table Panel
        RoundedPanel tableCard = new RoundedPanel(16, Color.WHITE);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JPanel tableHeaderPanel = new JPanel(new BorderLayout());
        tableHeaderPanel.setOpaque(false);
        JLabel lblTableTitle = new JLabel("Hóa Đơn & Giao Dịch Gần Đây");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTableTitle.setForeground(new Color(15, 23, 42));
        tableHeaderPanel.add(lblTableTitle, BorderLayout.WEST);

        JLabel lblViewAll = new JLabel("Xem tất cả");
        lblViewAll.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblViewAll.setForeground(new Color(59, 130, 246));
        lblViewAll.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tableHeaderPanel.add(lblViewAll, BorderLayout.EAST);

        tableCard.add(tableHeaderPanel, BorderLayout.NORTH);
        tableCard.add(Box.createVerticalStrut(15), BorderLayout.CENTER);

        // Table setup
        String[] columnNames = {"Mã Phòng", "Khách Thuê", "Ngày Lập", "Tổng Tiền", "Trạng Thái"};
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
        table.getColumnModel().getColumn(4).setCellRenderer(new BadgeCellRenderer());

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
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

        gridSplitPanel.add(tableCard, BorderLayout.CENTER);

        // Right Actions Panel
        RoundedPanel actionCard = new RoundedPanel(16, Color.WHITE);
        actionCard.setLayout(new BoxLayout(actionCard, BoxLayout.Y_AXIS));
        actionCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        actionCard.setPreferredSize(new Dimension(280, 300));
        actionCard.setMaximumSize(new Dimension(280, 1600));

        JLabel lblActionTitle = new JLabel("Lối Tắt Thao Tác Nhanh");
        lblActionTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblActionTitle.setForeground(new Color(15, 23, 42));
        lblActionTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        actionCard.add(lblActionTitle);
        actionCard.add(Box.createVerticalStrut(20));

        btnAddRoom = new ModernButton(" Thêm Phòng Mới");
        try {
            java.net.URL url = getClass().getResource("/Image/add.png");
            if (url != null) btnAddRoom.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        btnAddRoom.setHorizontalAlignment(SwingConstants.LEFT);
        btnAddRoom.setColors(new Color(59, 130, 246), new Color(37, 99, 235), new Color(29, 78, 216));
        btnAddRoom.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAddRoom.setMaximumSize(new Dimension(240, 44));
        btnAddRoom.setPreferredSize(new Dimension(240, 44));
        actionCard.add(btnAddRoom);
        actionCard.add(Box.createVerticalStrut(12));

        btnAddTenant = new ModernButton(" Thêm Khách Thuê");
        try {
            java.net.URL url = getClass().getResource("/Image/ThemUser.png");
            if (url != null) btnAddTenant.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        btnAddTenant.setHorizontalAlignment(SwingConstants.LEFT);
        btnAddTenant.setColors(new Color(99, 102, 241), new Color(79, 70, 229), new Color(67, 56, 202));
        btnAddTenant.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAddTenant.setMaximumSize(new Dimension(240, 44));
        btnAddTenant.setPreferredSize(new Dimension(240, 44));
        actionCard.add(btnAddTenant);
        actionCard.add(Box.createVerticalStrut(12));

        btnCreateBill = new ModernButton(" Lập Hóa Đơn Mới");
        try {
            java.net.URL url = getClass().getResource("/Image/LapHoaDon.png");
            if (url != null) btnCreateBill.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        btnCreateBill.setHorizontalAlignment(SwingConstants.LEFT);
        btnCreateBill.setColors(new Color(16, 185, 129), new Color(5, 150, 105), new Color(4, 120, 87));
        btnCreateBill.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCreateBill.setMaximumSize(new Dimension(240, 44));
        btnCreateBill.setPreferredSize(new Dimension(240, 44));
        actionCard.add(btnCreateBill);
        actionCard.add(Box.createVerticalStrut(12));

        btnImportData = new ModernButton(" Nhập Dữ Liệu (Excel)");
        try {
            java.net.URL url = getClass().getResource("/Image/import.png");
            if (url != null) btnImportData.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        btnImportData.setHorizontalAlignment(SwingConstants.LEFT);
        btnImportData.setColors(new Color(245, 158, 11), new Color(217, 119, 6), new Color(180, 83, 9));
        btnImportData.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnImportData.setMaximumSize(new Dimension(240, 44));
        btnImportData.setPreferredSize(new Dimension(240, 44));
        actionCard.add(btnImportData);
        actionCard.add(Box.createVerticalStrut(12));

        btnExportData = new ModernButton(" Xuất Báo Cáo (Excel)");
        try {
            java.net.URL url = getClass().getResource("/Image/export.png");
            if (url != null) btnExportData.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        btnExportData.setHorizontalAlignment(SwingConstants.LEFT);
        btnExportData.setColors(new Color(100, 116, 139), new Color(71, 85, 105), new Color(51, 65, 85));
        btnExportData.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnExportData.setMaximumSize(new Dimension(240, 44));
        btnExportData.setPreferredSize(new Dimension(240, 44));
        actionCard.add(btnExportData);

        gridSplitPanel.add(actionCard, BorderLayout.EAST);

        bodyWrapper.add(gridSplitPanel);

        JScrollPane scroll = new JScrollPane(bodyWrapper);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(248, 250, 252));

        add(scroll, BorderLayout.CENTER);
    }

    /** Trả về nút "Thêm Phòng Mới" để Controller gắn ActionListener */
    public ModernButton getBtnAddRoom()    { return btnAddRoom; }

    /** Trả về nút "Thêm Khách Thuê" để Controller gắn ActionListener */
    public ModernButton getBtnAddTenant()  { return btnAddTenant; }

    /** Trả về nút "Lập Hóa Đơn Mới" để Controller gắn ActionListener */
    public ModernButton getBtnCreateBill() { return btnCreateBill; }

    /** Trả về nút "Nhập Dữ Liệu" để Controller gắn ActionListener */
    public ModernButton getBtnImportData() { return btnImportData; }

    /** Trả về nút "Xuất Báo Cáo" để Controller gắn ActionListener */
    public ModernButton getBtnExportData() { return btnExportData; }

    public JLabel getLblTotalRoomsVal() { return lblTotalRoomsVal; }
    public JLabel getLblRentedRoomsVal() { return lblRentedRoomsVal; }
    public JLabel getLblEmptyRoomsVal() { return lblEmptyRoomsVal; }
    public JLabel getLblRevenueVal() { return lblRevenueVal; }

    public JLabel getLblTotalRoomsDesc() { return lblTotalRoomsDesc; }
    public JLabel getLblRentedRoomsDesc() { return lblRentedRoomsDesc; }
    public JLabel getLblEmptyRoomsDesc() { return lblEmptyRoomsDesc; }
    public JLabel getLblRevenueDesc() { return lblRevenueDesc; }
    
    public DefaultTableModel getTableModel() { return tableModel; }
    public JTable getTable() { return table; }

    private void setupMetricCard(RoundedPanel card, String title, JLabel lblValue, String icon, JLabel lblDesc) {
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(15, 18, 15, 18));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(new Color(255, 255, 255, 180));
        topRow.add(lblTitle, BorderLayout.WEST);

        if (!icon.isEmpty()) {
            JLabel lblIcon = new JLabel(icon);
            lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 24));
            lblIcon.setForeground(Color.WHITE);
            topRow.add(lblIcon, BorderLayout.EAST);
        }

        card.add(topRow, BorderLayout.NORTH);

        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValue.setForeground(Color.WHITE);
        card.add(lblValue, BorderLayout.CENTER);

        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDesc.setForeground(new Color(255, 255, 255, 200));
        card.add(lblDesc, BorderLayout.SOUTH);
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponentsCustom() {
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
    }// </editor-fold>//GEN-END:initComponents
}
