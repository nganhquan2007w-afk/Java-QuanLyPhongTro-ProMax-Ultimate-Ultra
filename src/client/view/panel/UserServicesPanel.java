package client.view.panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import client.view.component.RoundedPanel;

public class UserServicesPanel extends JPanel {
    private JComboBox<String> cmbServices;
    private JButton btnSubscribe;
    private JTable table;
    private DefaultTableModel tableModel;

    public UserServicesPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initHeader();
        initContent();
    }

    private void initHeader() {
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("Đăng Ký Dịch Vụ & Tiện Ích");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));
        
        JLabel lblDesc = new JLabel("Chọn dịch vụ muốn sử dụng và gửi yêu cầu đến ban quản lý.");
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
        
        cmbServices = new JComboBox<>();
        cmbServices.setPreferredSize(new Dimension(200, 35));
        cmbServices.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        btnSubscribe = new JButton("Đăng ký ngay");
        btnSubscribe.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSubscribe.setBackground(new Color(59, 130, 246));
        btnSubscribe.setForeground(Color.WHITE);
        btnSubscribe.setFocusPainted(false);
        btnSubscribe.setPreferredSize(new Dimension(130, 35));
        
        pnlAction.add(new JLabel("Chọn dịch vụ:"));
        pnlAction.add(cmbServices);
        pnlAction.add(btnSubscribe);

        pnlHeader.add(pnlAction, BorderLayout.EAST);
        add(pnlHeader, BorderLayout.NORTH);
    }

    private void initContent() {
        RoundedPanel pnlContent = new RoundedPanel(15, Color.WHITE);
        pnlContent.setLayout(new BorderLayout());
        pnlContent.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String[] cols = {"Mã ĐK", "Tên Dịch Vụ", "Trạng Thái", "Ngày Đăng Ký"};
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

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        scroll.getViewport().setBackground(Color.WHITE);

        pnlContent.add(scroll, BorderLayout.CENTER);
        add(pnlContent, BorderLayout.CENTER);
    }

    public JComboBox<String> getCmbServices() { return cmbServices; }
    public JButton getBtnSubscribe() { return btnSubscribe; }
    public DefaultTableModel getTableModel() { return tableModel; }
}
