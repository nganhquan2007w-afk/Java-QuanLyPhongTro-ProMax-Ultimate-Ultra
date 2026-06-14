package client.view.panel;

import client.view.component.RoundedPanel;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class FeedbackPanel extends JPanel {
    private JTextField txtTitle;
    private JTextArea txtContent;
    private JComboBox<String> cmbRating;
    private JButton btnSubmit;
    private JTable table;
    private DefaultTableModel tableModel;

    public FeedbackPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(248, 250, 252));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        initHeader();
        initForm();
        initTable();
    }

    private void initHeader() {
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);

        JLabel lblTitle = new JLabel("Góp Ý & Phản Hồi");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 41, 59));
        
        JLabel lblDesc = new JLabel("Gửi ý kiến đóng góp hoặc đánh giá chất lượng dịch vụ khu trọ.");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblDesc.setForeground(new Color(100, 116, 139));
        
        JPanel pnlText = new JPanel();
        pnlText.setLayout(new BoxLayout(pnlText, BoxLayout.Y_AXIS));
        pnlText.setOpaque(false);
        pnlText.add(lblTitle);
        pnlText.add(Box.createVerticalStrut(5));
        pnlText.add(lblDesc);

        pnlHeader.add(pnlText, BorderLayout.WEST);
        add(pnlHeader, BorderLayout.NORTH);
    }

    private void initForm() {
        RoundedPanel pnlForm = new RoundedPanel(15, Color.WHITE);
        pnlForm.setLayout(new GridBagLayout());
        pnlForm.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
        pnlForm.add(new JLabel("Tiêu đề:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.9;
        txtTitle = new JTextField();
        txtTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlForm.add(txtTitle, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.1;
        pnlForm.add(new JLabel("Đánh giá:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.9;
        cmbRating = new JComboBox<>(new String[]{"5 - Rất tốt", "4 - Tốt", "3 - Bình thường", "2 - Tệ", "1 - Rất tệ"});
        cmbRating.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pnlForm.add(cmbRating, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.1; gbc.anchor = GridBagConstraints.NORTH;
        pnlForm.add(new JLabel("Nội dung:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 0.9; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        txtContent = new JTextArea(4, 20);
        txtContent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtContent.setLineWrap(true);
        txtContent.setWrapStyleWord(true);
        pnlForm.add(new JScrollPane(txtContent), gbc);

        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0.9; gbc.weighty = 0; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.EAST;
        btnSubmit = new JButton("Gửi phản hồi");
        btnSubmit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSubmit.setBackground(new Color(59, 130, 246));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFocusPainted(false);
        pnlForm.add(btnSubmit, gbc);

        add(pnlForm, BorderLayout.CENTER);
    }

    private void initTable() {
        RoundedPanel pnlTable = new RoundedPanel(15, Color.WHITE);
        pnlTable.setLayout(new BorderLayout());
        pnlTable.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pnlTable.setPreferredSize(new Dimension(800, 300));

        String[] cols = {"Tiêu Đề", "Nội Dung", "Đánh Giá", "Trạng Thái", "Ngày Gửi"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(241, 245, 249));

        JScrollPane scroll = new JScrollPane(table);
        pnlTable.add(new JLabel("Lịch sử phản hồi:"), BorderLayout.NORTH);
        pnlTable.add(scroll, BorderLayout.CENTER);

        add(pnlTable, BorderLayout.SOUTH);
    }

    public JTextField getTxtTitle() { return txtTitle; }
    public JTextArea getTxtContent() { return txtContent; }
    public JComboBox<String> getCmbRating() { return cmbRating; }
    public JButton getBtnSubmit() { return btnSubmit; }
    public DefaultTableModel getTableModel() { return tableModel; }
}
