package client.view.panel;

import client.view.component.RoundedPanel;
import client.view.component.ModernButton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * NotificationsPanel - UI representing the system notifications module.
 * Adapts between Admin composer and Tenant reader views.
 */
@SuppressWarnings("serial")
public final class NotificationsPanel extends JPanel {

    private JTextField txtTitle;
    private JTextArea txtContent;
    private ModernButton btnSend;
    private RoundedPanel composeCard;
    private JLabel lblHeader;
    private JTable table;
    private DefaultTableModel tableModel;
    private boolean isAdmin;

    public NotificationsPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        
        boolean adminCheck = false;
        if (common.model.UserSession.getInstance() != null) {
            adminCheck = "ADMIN".equalsIgnoreCase(common.model.UserSession.getInstance().getRole());
        }
        this.isAdmin = adminCheck;
        initComponents();
    }

    private void initComponents() {
        JPanel bodyWrapper = new JPanel();
        bodyWrapper.setLayout(new BoxLayout(bodyWrapper, BoxLayout.Y_AXIS));
        bodyWrapper.setBorder(new EmptyBorder(25, 25, 25, 25));
        bodyWrapper.setOpaque(false);

        // Header Panel
        lblHeader = new JLabel(isAdmin ? "Quản Lý & Phát Thông Báo" : "Thông Báo Từ Ban Quản Lý");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(new Color(15, 23, 42));
        lblHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyWrapper.add(lblHeader);
        bodyWrapper.add(Box.createVerticalStrut(20));

        // Compose Form Card (always build to keep action listeners registered)
        composeCard = new RoundedPanel(16, Color.WHITE);
        composeCard.setLayout(new BoxLayout(composeCard, BoxLayout.Y_AXIS));
        composeCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        composeCard.setMaximumSize(new Dimension(1600, 240));
        composeCard.setPreferredSize(new Dimension(800, 240));
        composeCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblFormTitle = new JLabel("Soạn thảo thông báo mới");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblFormTitle.setForeground(new Color(15, 23, 42));
        composeCard.add(lblFormTitle);
        composeCard.add(Box.createVerticalStrut(12));

        // Title Input
        JPanel titleRow = new JPanel(new BorderLayout(10, 0));
        titleRow.setOpaque(false);
        titleRow.setMaximumSize(new Dimension(1600, 32));
        JLabel lblTitle = new JLabel("Tiêu đề:");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(new Color(71, 85, 105));
        lblTitle.setPreferredSize(new Dimension(70, 32));
        titleRow.add(lblTitle, BorderLayout.WEST);

        txtTitle = new JTextField();
        txtTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleRow.add(txtTitle, BorderLayout.CENTER);
        composeCard.add(titleRow);
        composeCard.add(Box.createVerticalStrut(10));

        // Content Input
        JPanel contentRow = new JPanel(new BorderLayout(10, 0));
        contentRow.setOpaque(false);
        contentRow.setMaximumSize(new Dimension(1600, 80));
        JLabel lblContent = new JLabel("Nội dung:");
        lblContent.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblContent.setForeground(new Color(71, 85, 105));
        lblContent.setPreferredSize(new Dimension(70, 32));
        contentRow.add(lblContent, BorderLayout.WEST);

        txtContent = new JTextArea();
        txtContent.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);
        JScrollPane txtScroll = new JScrollPane(txtContent);
        txtScroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));
        contentRow.add(txtScroll, BorderLayout.CENTER);
        composeCard.add(contentRow);
        composeCard.add(Box.createVerticalStrut(12));

        // Send Button
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        btnRow.setOpaque(false);
        btnRow.setMaximumSize(new Dimension(1600, 36));
        btnSend = new ModernButton(" Phát Thông Báo");
        try {
            java.net.URL url = getClass().getResource("/Image/tbao.png");
            if (url != null) btnSend.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(18, 18, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        btnSend.setColors(new Color(16, 185, 129), new Color(5, 150, 105), new Color(4, 120, 87));
        btnSend.setPreferredSize(new Dimension(160, 36));
        btnSend.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnRow.add(btnSend);
        composeCard.add(btnRow);

        bodyWrapper.add(composeCard);
        bodyWrapper.add(Box.createVerticalStrut(20));

        composeCard.setVisible(isAdmin);

        // Table Card
        RoundedPanel tableCard = new RoundedPanel(16, Color.WHITE);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        tableCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] columnNames = {"Tiêu Đề", "Nội Dung Chi Tiết", isAdmin ? "Ngày Gửi" : "Ngày Nhận"};
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
                    String emptyText = isAdmin ? "Lịch sử gửi thông báo trống." : "Không có thông báo nào từ ban quản lý.";
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

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(new Color(71, 85, 105));
        header.setPreferredSize(new Dimension(100, 36));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)));

        // Setup width constraints
        table.getColumnModel().getColumn(0).setPreferredWidth(180);
        table.getColumnModel().getColumn(1).setPreferredWidth(450);
        table.getColumnModel().getColumn(2).setPreferredWidth(140);

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

    // Getters
    public JTextField getTxtTitle() { return txtTitle; }
    public JTextArea getTxtContent() { return txtContent; }
    public ModernButton getBtnSend() { return btnSend; }
    public JTable getTable() { return table; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public boolean isAdmin() { return isAdmin; }

    public void setAdminMode(boolean adminMode) {
        this.isAdmin = adminMode;
        if (composeCard != null) {
            composeCard.setVisible(adminMode);
        }
        if (lblHeader != null) {
            lblHeader.setText(adminMode ? "Quản Lý & Phát Thông Báo" : "Thông Báo Từ Ban Quản Lý");
        }
        if (table != null) {
            table.getColumnModel().getColumn(2).setHeaderValue(adminMode ? "Ngày Gửi" : "Ngày Nhận");
            table.getTableHeader().repaint();
        }
        revalidate();
        repaint();
    }
}
