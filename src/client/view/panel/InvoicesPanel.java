package client.view.panel;

import client.view.component.BadgeCellRenderer;
import client.view.component.RoundTextField;
import client.view.component.RoundedPanel;
import client.view.component.ModernButton;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * InvoicesPanel - UI representation of the Invoice/Billing Management module.
 * Only contains layout code, using mock datasets.
 */
@SuppressWarnings("serial")
public final class InvoicesPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private RoundTextField txtSearch;
    private JComboBox<String> cmbMonth;
    private JComboBox<String> cmbStatus;
    private ModernButton btnAdd;
    private ModernButton btnExportPdf;
    private ModernButton btnComplete;
    private ModernButton btnPay;

    public InvoicesPanel() {
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

        // Left Controls: Search & Filters
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftPanel.setOpaque(false);
        leftPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtSearch = new RoundTextField(10);
        leftPanel.add(txtSearch);

        JLabel lblMonth = new JLabel("Tháng:");
        try {
            java.net.URL url = getClass().getResource("/Image/filter.png");
            if (url != null) lblMonth.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        lblMonth.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblMonth.setForeground(new Color(71, 85, 105));
        leftPanel.add(lblMonth);

        String[] months = {"Tất cả", "Tháng 05/2026", "Tháng 04/2026", "Tháng 03/2026", "Tháng 12/2025"};
        cmbMonth = new JComboBox<>(months);
        cmbMonth.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbMonth.setBackground(Color.WHITE);
        cmbMonth.setPreferredSize(new Dimension(110, 32));
        cmbMonth.setFocusable(false);
        leftPanel.add(cmbMonth);

        JLabel lblStatus = new JLabel("Trạng thái:");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStatus.setForeground(new Color(71, 85, 105));
        leftPanel.add(lblStatus);

        String[] paymentStatuses = {"Tất cả", "Đã thanh toán", "Chưa thanh toán"};
        cmbStatus = new JComboBox<>(paymentStatuses);
        cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbStatus.setBackground(Color.WHITE);
        cmbStatus.setPreferredSize(new Dimension(110, 32));
        cmbStatus.setFocusable(false);
        leftPanel.add(cmbStatus);

        controlCard.add(leftPanel);
        controlCard.add(Box.createVerticalStrut(10));

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        rightPanel.setOpaque(false);
        rightPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnExportPdf = new ModernButton(" Xuất hóa đơn");
        try {
            java.net.URL url = getClass().getResource("/Image/export.png");
            if (url != null) btnExportPdf.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        btnExportPdf.setColors(new Color(249, 115, 22), new Color(234, 88, 12), new Color(194, 65, 12)); // Orange for PDF
        btnExportPdf.setPreferredSize(new Dimension(140, 36));
        btnExportPdf.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rightPanel.add(btnExportPdf);
        
        btnComplete = new ModernButton(" Hoàn Thành HĐ");
        try {
            java.net.URL url = getClass().getResource("/Image/check.png");
            if (url != null) btnComplete.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        btnComplete.setColors(new Color(16, 185, 129), new Color(5, 150, 105), new Color(4, 120, 87));
        btnComplete.setPreferredSize(new Dimension(145, 36));
        btnComplete.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rightPanel.add(btnComplete);

        btnPay = new ModernButton(" Thanh Toán");
        btnPay.setColors(new Color(245, 158, 11), new Color(217, 119, 6), new Color(180, 83, 9));
        btnPay.setPreferredSize(new Dimension(135, 36));
        btnPay.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rightPanel.add(btnPay);

        btnAdd = new ModernButton(" Lập Hóa Đơn Mới");
        try {
            java.net.URL url = getClass().getResource("/Image/LapHoaDon.png");
            if (url != null) btnAdd.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        btnAdd.setColors(new Color(59, 130, 246), new Color(37, 99, 235), new Color(29, 78, 216));
        btnAdd.setPreferredSize(new Dimension(160, 36));
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rightPanel.add(btnAdd);

        // Hide buttons based on Role
        if (common.model.UserSession.getInstance() != null && !"ADMIN".equalsIgnoreCase(common.model.UserSession.getInstance().getRole())) {
            btnAdd.setVisible(false);
            btnComplete.setVisible(false);
        } else {
            btnPay.setVisible(false);
        }

        controlCard.add(rightPanel);

        bodyWrapper.add(controlCard, BorderLayout.NORTH);

        // Table Card
        RoundedPanel tableCard = new RoundedPanel(16, Color.WHITE);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Columns definition
        String[] columnNames = {"Mã HĐ", "Mã Phòng", "Khách Thuê", "Ngày Lập", "Hạn Nộp", "Tổng Tiền", "Trạng Thái", "Ngày Thu", "Hành Động"};
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
        table.getColumnModel().getColumn(6).setCellRenderer(new BadgeCellRenderer());
        table.getColumnModel().getColumn(8).setCellRenderer(new ActionCellRenderer());

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);

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
    public JComboBox<String> getCmbMonth() { return cmbMonth; }
    public JComboBox<String> getCmbStatus() { return cmbStatus; }
    public ModernButton getBtnAdd() { return btnAdd; }
    public ModernButton getBtnExportPdf() { return btnExportPdf; }
    public ModernButton getBtnComplete() { return btnComplete; }
    public ModernButton getBtnPay() { return btnPay; }
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

    public static class ActionCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            String text = (value != null) ? value.toString() : "Chi Tiết 🔍";
            JPanel container = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
            container.setOpaque(true);
            container.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);

            JLabel label = new JLabel(text) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };

            label.setOpaque(false);
            label.setFont(new Font("Segoe UI", Font.BOLD, 11));
            label.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

            label.setBackground(new Color(238, 242, 255)); // Indigo 50
            label.setForeground(new Color(79, 70, 229));   // Indigo 600

            container.add(label);
            return container;
        }
    }
}
