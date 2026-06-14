package client.view.panel;

import client.view.component.RoundedPanel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdminFeedbacksPanel extends JPanel {
    private JButton btnResolve;
    private JTable table;
    private DefaultTableModel tableModel;

    public AdminFeedbacksPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initHeader();
        initTable();
    }

    private void initHeader() {
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("Quản Lý Phản Hồi & Góp Ý");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));

        JLabel lblDesc = new JLabel("Xem xét và xử lý các góp ý từ khách thuê phòng.");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDesc.setForeground(new Color(100, 116, 139));

        JPanel pnlText = new JPanel();
        pnlText.setLayout(new BoxLayout(pnlText, BoxLayout.Y_AXIS));
        pnlText.setOpaque(false);
        pnlText.add(lblTitle);
        pnlText.add(Box.createVerticalStrut(5));
        pnlText.add(lblDesc);

        pnlHeader.add(pnlText, BorderLayout.WEST);

        JPanel pnlAction = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlAction.setOpaque(false);

        btnResolve = new JButton("Đánh Dấu Đã Xử Lý");
        btnResolve.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnResolve.setBackground(new Color(59, 130, 246));
        btnResolve.setForeground(Color.WHITE);
        btnResolve.setFocusPainted(false);

        pnlAction.add(btnResolve);
        pnlHeader.add(pnlAction, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);
    }

    private void initTable() {
        RoundedPanel pnlTable = new RoundedPanel(15, Color.WHITE);
        pnlTable.setLayout(new BorderLayout());
        pnlTable.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] cols = {"Mã", "Phòng", "Tiêu Đề", "Nội Dung", "Đánh Giá (*)", "Trạng Thái", "Ngày Tạo"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getColumnModel().getColumn(3).setPreferredWidth(300); // Nội dung dài hơn

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        scroll.getViewport().setBackground(Color.WHITE);

        pnlTable.add(scroll, BorderLayout.CENTER);
        add(pnlTable, BorderLayout.CENTER);
    }

    public JButton getBtnResolve() { return btnResolve; }
    public DefaultTableModel getTableModel() { return tableModel; }
    public JTable getTable() { return table; }
}
