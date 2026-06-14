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
 * ContractsPanel - UI representation of the Contract/Lease Management module.
 */
@SuppressWarnings("serial")
public final class ContractsPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private RoundTextField txtSearch;
    private JComboBox<String> cmbStatus;
    private ModernButton btnAdd;

    public ContractsPanel() {
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

        String[] statuses = {"Tất cả", "Còn hiệu lực", "Sắp hết hạn", "Đã thanh lý"};
        cmbStatus = new JComboBox<>(statuses);
        cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbStatus.setBackground(Color.WHITE);
        cmbStatus.setPreferredSize(new Dimension(130, 32));
        cmbStatus.setFocusable(false);
        leftPanel.add(cmbStatus);

        controlCard.add(leftPanel, BorderLayout.WEST);

        // Right Controls
        btnAdd = new ModernButton(" Tạo Hợp Đồng Mới");
        try {
            java.net.URL url = getClass().getResource("/Image/ThemHopDong.png");
            if (url != null) {
                Image img = new javax.swing.ImageIcon(url).getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                btnAdd.setIcon(new javax.swing.ImageIcon(img));
            }
        } catch (Exception e) {}
        btnAdd.setColors(new Color(59, 130, 246), new Color(37, 99, 235), new Color(29, 78, 216));
        btnAdd.setPreferredSize(new Dimension(180, 36));
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        controlCard.add(btnAdd, BorderLayout.EAST);

        bodyWrapper.add(controlCard);
        bodyWrapper.add(Box.createVerticalStrut(20));

        // Table Card
        RoundedPanel tableCard = new RoundedPanel(16, Color.WHITE);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        String[] columnNames = {"Mã HĐ", "Phòng", "Khách Thuê", "Ngày Bắt Đầu", "Ngày Kết Thúc", "Tiền Đặt Cọc", "Trạng Thái"};
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



    // Getters
    public RoundTextField getTxtSearch() { return txtSearch; }
    public JComboBox<String> getCmbStatus() { return cmbStatus; }
    public ModernButton getBtnAdd() { return btnAdd; }
    public JTable getTable() { return table; }
    public DefaultTableModel getTableModel() { return tableModel; }
}
