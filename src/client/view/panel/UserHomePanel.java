package client.view.panel;

import client.view.component.RoundedPanel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

@SuppressWarnings("serial")
public final class UserHomePanel extends JPanel {

    private JLabel lblWelcomeRoom;
    private JLabel lblRoomValue;
    private JLabel lblRoomPrice;
    private JLabel lblDueDate;
    private JLabel lblBillStatus;
    private DefaultTableModel svcModel;
    private JTextPane txtFeed;

    public UserHomePanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        initComponents();
    }

    private void initComponents() {
        JPanel bodyWrapper = new JPanel();
        bodyWrapper.setLayout(new BoxLayout(bodyWrapper, BoxLayout.Y_AXIS));
        bodyWrapper.setBorder(new EmptyBorder(25, 25, 25, 25));
        bodyWrapper.setOpaque(false);

        // 1. Greeting Banner
        RoundedPanel welcomeCard = new RoundedPanel(16, new Color(15, 23, 42)); // Slate 900
        welcomeCard.setLayout(new BorderLayout());
        welcomeCard.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        welcomeCard.setMaximumSize(new Dimension(1600, 80));
        welcomeCard.setPreferredSize(new Dimension(800, 80));

        JLabel lblWelcome = new JLabel("Chào mừng trở lại!");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblWelcome.setForeground(Color.WHITE);
        welcomeCard.add(lblWelcome, BorderLayout.WEST);

        lblWelcomeRoom = new JLabel("Chưa nhận phòng", SwingConstants.RIGHT);
        lblWelcomeRoom.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblWelcomeRoom.setForeground(new Color(59, 130, 246));
        welcomeCard.add(lblWelcomeRoom, BorderLayout.EAST);

        bodyWrapper.add(welcomeCard);
        bodyWrapper.add(Box.createVerticalStrut(20));

        // 2. Metric cards grid for Tenant
        JPanel cardGrid = new JPanel(new GridLayout(1, 4, 20, 0));
        cardGrid.setOpaque(false);
        cardGrid.setMaximumSize(new Dimension(1600, 110));
        cardGrid.setPreferredSize(new Dimension(800, 110));

        RoundedPanel card1 = new RoundedPanel(16, new Color(59, 130, 246), new Color(29, 78, 216));
        lblRoomValue = setupMetricCard(card1, "PHÒNG ĐANG Ở", "-", "", "-");
        cardGrid.add(card1);

        RoundedPanel card2 = new RoundedPanel(16, new Color(245, 158, 11), new Color(217, 119, 6));
        lblRoomPrice = setupMetricCard(card2, "GIÁ PHÒNG", "0đ", "", "-");
        cardGrid.add(card2);

        RoundedPanel card3 = new RoundedPanel(16, new Color(16, 185, 129), new Color(4, 120, 87));
        lblDueDate = setupMetricCard(card3, "KỲ HẠN THANH TOÁN", "-", "", "-");
        cardGrid.add(card3);

        RoundedPanel card4 = new RoundedPanel(16, new Color(99, 102, 241), new Color(67, 56, 202));
        lblBillStatus = setupMetricCard(card4, "TRẠNG THÁI BILL", "-", "", "-");
        cardGrid.add(card4);

        bodyWrapper.add(cardGrid);
        bodyWrapper.add(Box.createVerticalStrut(25));

        // 3. Split Screen (Services Table + Bulletin Board)
        JPanel contentSplit = new JPanel(new BorderLayout(20, 0));
        contentSplit.setOpaque(false);

        // Left Panel: Services Table
        RoundedPanel serviceCard = new RoundedPanel(16, Color.WHITE);
        serviceCard.setLayout(new BorderLayout());
        serviceCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblServiceTitle = new JLabel("Dịch Vụ Phòng Trọ Đang Sử Dụng");
        lblServiceTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblServiceTitle.setForeground(new Color(15, 23, 42));
        serviceCard.add(lblServiceTitle, BorderLayout.NORTH);

        String[] svcColumns = {"Tên Dịch Vụ", "Đơn Vị Tính", "Đơn Giá", "Đang Sử Dụng"};
        Object[][] svcData = {};

        svcModel = new DefaultTableModel(svcData, svcColumns) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        JTable svcTable = new JTable(svcModel);
        svcTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        svcTable.setRowHeight(36);
        svcTable.setShowGrid(false);
        svcTable.setIntercellSpacing(new Dimension(0,0));

        JTableHeader svcHdr = svcTable.getTableHeader();
        svcHdr.setFont(new Font("Segoe UI", Font.BOLD, 13));
        svcHdr.setBackground(new Color(248, 250, 252));
        svcHdr.setForeground(new Color(71, 85, 105));
        svcHdr.setPreferredSize(new Dimension(100, 32));

        JScrollPane svcScroll = new JScrollPane(svcTable);
        svcScroll.setBorder(BorderFactory.createEmptyBorder());
        svcScroll.getViewport().setBackground(Color.WHITE);
        serviceCard.add(svcScroll, BorderLayout.CENTER);

        contentSplit.add(serviceCard, BorderLayout.CENTER);

        // Right Panel: Bulletin Board
        RoundedPanel boardCard = new RoundedPanel(16, Color.WHITE);
        boardCard.setLayout(new BorderLayout());
        boardCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        boardCard.setPreferredSize(new Dimension(320, 300));
        boardCard.setMaximumSize(new Dimension(320, 1600));

        JLabel lblBoardTitle = new JLabel("Thông Báo Từ Chủ Nhà (Bản Tin)");
        lblBoardTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBoardTitle.setForeground(new Color(15, 23, 42));
        boardCard.add(lblBoardTitle, BorderLayout.NORTH);

        txtFeed = new JTextPane();
        txtFeed.setContentType("text/html");
        txtFeed.setEditable(false);
        txtFeed.setOpaque(false);
        txtFeed.setText("<html><body style='font-family:Segoe UI, sans-serif; font-size:11px; color:#475569;'>"
                + "<div style='text-align:center; padding-top:40px; color:#94A6B8;'>"
                + "  Không có thông báo mới từ chủ nhà."
                + "</div>"
                + "</body></html>");

        JScrollPane feedScroll = new JScrollPane(txtFeed);
        feedScroll.setBorder(BorderFactory.createEmptyBorder());
        feedScroll.getViewport().setOpaque(false);
        feedScroll.setOpaque(false);
        boardCard.add(feedScroll, BorderLayout.CENTER);

        contentSplit.add(boardCard, BorderLayout.EAST);

        bodyWrapper.add(contentSplit);

        JScrollPane scroll = new JScrollPane(bodyWrapper);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(248, 250, 252));

        add(scroll, BorderLayout.CENTER);
    }

    private JLabel setupMetricCard(RoundedPanel card, String title, String value, String icon, String desc) {
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

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValue.setForeground(Color.WHITE);
        card.add(lblValue, BorderLayout.CENTER);

        JLabel lblDesc = new JLabel(desc);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDesc.setForeground(new Color(255, 255, 255, 200));
        card.add(lblDesc, BorderLayout.SOUTH);
        
        return lblValue;
    }

    public JLabel getLblWelcomeRoom() { return lblWelcomeRoom; }
    public JLabel getLblRoomValue() { return lblRoomValue; }
    public JLabel getLblRoomPrice() { return lblRoomPrice; }
    public JLabel getLblDueDate() { return lblDueDate; }
    public JLabel getLblBillStatus() { return lblBillStatus; }
    public DefaultTableModel getSvcModel() { return svcModel; }
    public JTextPane getTxtFeed() { return txtFeed; }


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
