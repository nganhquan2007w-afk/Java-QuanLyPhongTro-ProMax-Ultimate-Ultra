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

/**
 * MaintenancePanel - UI representation of the Maintenance & Maintenance Issue Management module.
 */
@SuppressWarnings("serial")
public final class MaintenancePanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private RoundTextField txtSearch;
    private JComboBox<String> cmbStatus;
    private ModernButton btnAdd;
    private ModernButton btnApprove;
    private ModernButton btnReject;
    private ModernButton btnComplete;
    private ModernButton btnRefresh;

    public MaintenancePanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        initComponents();
    }

    private void initComponents() {
        JPanel bodyWrapper = new JPanel();
        bodyWrapper.setLayout(new BoxLayout(bodyWrapper, BoxLayout.Y_AXIS));
        bodyWrapper.setBorder(new EmptyBorder(25, 25, 25, 25));
        bodyWrapper.setOpaque(false);

        // Header Control Card
        RoundedPanel controlCard = new RoundedPanel(16, Color.WHITE);
        controlCard.setLayout(new BorderLayout(15, 0));
        controlCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        controlCard.setMaximumSize(new Dimension(1600, 70));
        controlCard.setPreferredSize(new Dimension(800, 70));

        // Left Controls
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftPanel.setOpaque(false);

        txtSearch = new RoundTextField(20);
        leftPanel.add(txtSearch);

        JLabel lblFilter = new JLabel("Trạng thái:");
        try {
            java.net.URL url = getClass().getResource("/Image/filter.png");
            if (url != null) lblFilter.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblFilter.setForeground(new Color(71, 85, 105));
        leftPanel.add(lblFilter);

        String[] statuses = {"Tất cả", "Chờ xử lý", "Đang sửa chữa", "Đã hoàn thành"};
        cmbStatus = new JComboBox<>(statuses);
        cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbStatus.setBackground(Color.WHITE);
        cmbStatus.setPreferredSize(new Dimension(130, 32));
        cmbStatus.setFocusable(false);
        leftPanel.add(cmbStatus);

        // Right Controls: Action Buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        btnApprove = new ModernButton(" Duyệt Yêu Cầu");
        try {
            java.net.URL url = getClass().getResource("/Image/check.png");
            if (url != null) btnApprove.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        btnApprove.setColors(new Color(16, 185, 129), new Color(5, 150, 105), new Color(4, 120, 87)); // Green
        btnApprove.setPreferredSize(new Dimension(140, 36));
        btnApprove.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rightPanel.add(btnApprove);

        btnReject = new ModernButton(" Từ Chối Yêu Cầu");
        try {
            java.net.URL url = getClass().getResource("/Image/delete.png");
            if (url != null) btnReject.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        btnReject.setColors(new Color(239, 68, 68), new Color(220, 38, 38), new Color(185, 28, 28)); // Red
        btnReject.setPreferredSize(new Dimension(145, 36));
        btnReject.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rightPanel.add(btnReject);

        btnComplete = new ModernButton(" Hoàn Thành");
        try {
            java.net.URL url = getClass().getResource("/Image/check.png");
            if (url != null) btnComplete.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        btnComplete.setColors(new Color(59, 130, 246), new Color(37, 99, 235), new Color(29, 78, 216)); // Blue
        btnComplete.setPreferredSize(new Dimension(120, 36));
        btnComplete.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rightPanel.add(btnComplete);

        btnRefresh = new ModernButton(" Làm Mới");
        try {
            java.net.URL url = getClass().getResource("/Image/refresh.png");
            if (url != null) btnRefresh.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        btnRefresh.setColors(new Color(100, 116, 139), new Color(71, 85, 105), new Color(51, 65, 85)); // Slate
        btnRefresh.setPreferredSize(new Dimension(100, 36));
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rightPanel.add(btnRefresh);

        btnAdd = new ModernButton(" Báo Cáo Sự Cố Mới");
        try {
            java.net.URL url = getClass().getResource("/Image/baocao.png");
            if (url != null) btnAdd.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        btnAdd.setColors(new Color(245, 158, 11), new Color(217, 119, 6), new Color(180, 83, 9)); // Amber
        btnAdd.setPreferredSize(new Dimension(180, 36));
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        rightPanel.add(btnAdd);

        // Role-based visibility setup
        boolean isAdmin = false;
        if (common.model.UserSession.getInstance() != null) {
            isAdmin = "ADMIN".equalsIgnoreCase(common.model.UserSession.getInstance().getRole());
        }
        if (isAdmin) {
            btnAdd.setVisible(false);
        } else {
            btnApprove.setVisible(false);
            btnReject.setVisible(false);
            btnComplete.setVisible(false);
        }

        controlCard.add(rightPanel, BorderLayout.EAST);

        bodyWrapper.add(controlCard);
        bodyWrapper.add(Box.createVerticalStrut(20));

        // Table Card
        RoundedPanel tableCard = new RoundedPanel(16, Color.WHITE);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        String[] columnNames = {"Mã SC", "Phòng", "Nội Dung Sự Cố", "Mức Độ", "Ngày Gửi", "Trạng Thái"};
        Object[][] data = {};

        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getRowCount() == 0) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(148, 163, 184)); // Slate 400
                    g2.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                    FontMetrics fm = g2.getFontMetrics();
                    String emptyText = "Không có yêu cầu sửa chữa nào.";
                    int x = (getWidth() - fm.stringWidth(emptyText)) / 2;
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(emptyText, x, y);
                    g2.dispose();
                }
            }
        };
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
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
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

        bodyWrapper.add(tableCard);

        JScrollPane scroll = new JScrollPane(bodyWrapper);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(248, 250, 252));

        add(scroll, BorderLayout.CENTER);
    }

    public void setAdminMode(boolean adminMode) {
        if (btnApprove != null) {
            btnApprove.setVisible(adminMode);
        }
        if (btnReject != null) {
            btnReject.setVisible(adminMode);
        }
        if (btnComplete != null) {
            btnComplete.setVisible(adminMode);
        }
        if (btnAdd != null) {
            btnAdd.setVisible(!adminMode);
        }
        revalidate();
        repaint();
    }

    // Getters
    public RoundTextField getTxtSearch() { return txtSearch; }
    public JComboBox<String> getCmbStatus() { return cmbStatus; }
    public ModernButton getBtnAdd() { return btnAdd; }
    public ModernButton getBtnApprove() { return btnApprove; }
    public ModernButton getBtnReject() { return btnReject; }
    public ModernButton getBtnComplete() { return btnComplete; }
    public ModernButton getBtnRefresh() { return btnRefresh; }
    public JTable getTable() { return table; }
    public DefaultTableModel getTableModel() { return tableModel; }
}
