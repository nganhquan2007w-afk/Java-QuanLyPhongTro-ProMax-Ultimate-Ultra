package client.view.panel;

import client.view.component.BarChartComponent;
import client.view.component.RoundedPanel;
import client.view.component.ProgressRing;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

@SuppressWarnings("serial")
public final class StatisticsPanel extends JPanel {

    private ProgressRing ring;
    private JLabel lblStat1;
    private JLabel lblStat2;
    private JLabel lblStat3;
    private BarChartComponent barChart;
    private JLabel lblS1Val;
    private JLabel lblS2Val;
    private JLabel lblS3Val;
    private JLabel lblGrowth; // For revenue comparison

    public StatisticsPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        initComponents();
    }

    private void initComponents() {
        JPanel bodyWrapper = new JPanel();
        bodyWrapper.setLayout(new BoxLayout(bodyWrapper, BoxLayout.Y_AXIS));
        bodyWrapper.setBorder(new EmptyBorder(25, 25, 25, 25));
        bodyWrapper.setOpaque(false);

        // 1. Grid split: ProgressRing (left, 35%) + Revenue Bar Chart (right, 65%)
        JPanel chartSplit = new JPanel(new BorderLayout(20, 0));
        chartSplit.setOpaque(false);
        chartSplit.setPreferredSize(new Dimension(800, 360));
        chartSplit.setMaximumSize(new Dimension(1600, 360));

        // Left Panel: Occupancy Circular Ring Card
        RoundedPanel ringCard = new RoundedPanel(16, Color.WHITE);
        ringCard.setLayout(new BorderLayout());
        ringCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        ringCard.setPreferredSize(new Dimension(320, 360));

        JLabel lblRingTitle = new JLabel("Tỷ Lệ Lấp Đầy Phòng");
        lblRingTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblRingTitle.setForeground(new Color(15, 23, 42));
        ringCard.add(lblRingTitle, BorderLayout.NORTH);

        JPanel ringContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
        ringContainer.setOpaque(false);
        ring = new ProgressRing(0, "Đang cho thuê");
        ringContainer.add(ring);
        ringCard.add(ringContainer, BorderLayout.CENTER);

        // Table / Grid of detailed statuses at the bottom of the card
        JPanel statusList = new JPanel(new GridLayout(3, 1, 0, 8));
        statusList.setOpaque(false);

        lblStat1 = new JLabel("\ud83d\udfe2   Phòng đang hoạt động: 0 phòng (0%)");
        lblStat1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStat1.setForeground(new Color(71, 85, 105));

        lblStat2 = new JLabel("\u26aa   Phòng trống sẵn sàng: 0 phòng (0%)");
        lblStat2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStat2.setForeground(new Color(71, 85, 105));

        lblStat3 = new JLabel("\ud83d\udee0\ufe0f   Phòng đang sửa chữa: 0 phòng (0%)");
        lblStat3.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStat3.setForeground(new Color(71, 85, 105));

        statusList.add(lblStat1);
        statusList.add(lblStat2);
        statusList.add(lblStat3);
        ringCard.add(statusList, BorderLayout.SOUTH);

        chartSplit.add(ringCard, BorderLayout.WEST);

        // Right Panel: Revenue BarChart
        RoundedPanel barChartCard = new RoundedPanel(16, Color.WHITE);
        barChartCard.setLayout(new BorderLayout());
        barChartCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel lblChartTitle = new JLabel("Biểu Đồ Doanh Thu 6 Tháng Gần Nhất");
        lblChartTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblChartTitle.setForeground(new Color(15, 23, 42));
        barChartCard.add(lblChartTitle, BorderLayout.NORTH);

        barChart = new BarChartComponent();
        barChartCard.add(barChart, BorderLayout.CENTER);

        // Mini legend block below chart
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        legendPanel.setOpaque(false);
        JLabel lblLegend = new JLabel("Đơn vị: Triệu Đồng (VND)");
        lblLegend.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblLegend.setForeground(new Color(148, 163, 184));
        legendPanel.add(lblLegend);
        barChartCard.add(legendPanel, BorderLayout.SOUTH);

        chartSplit.add(barChartCard, BorderLayout.CENTER);

        bodyWrapper.add(chartSplit);
        bodyWrapper.add(Box.createVerticalStrut(25));

        // 2. Summary numbers card (Total collected, Remaining Debt, Profit Margin)
        RoundedPanel statsFooterPanel = new RoundedPanel(16, Color.WHITE);
        statsFooterPanel.setLayout(new GridLayout(1, 3, 20, 0));
        statsFooterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        statsFooterPanel.setMaximumSize(new Dimension(1600, 100));
        statsFooterPanel.setPreferredSize(new Dimension(800, 100));

        JPanel statCol1 = new JPanel(new GridLayout(3, 1)); // Tăng row để chứa lblGrowth
        statCol1.setOpaque(false);
        JLabel lblS1Title = new JLabel("TỔNG DOANH THU 6 THÁNG");
        lblS1Title.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblS1Title.setForeground(new Color(148, 163, 184));
        lblS1Val = new JLabel("0 đ");
        lblS1Val.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblS1Val.setForeground(new Color(59, 130, 246));
        lblGrowth = new JLabel("So với tháng trước: -");
        lblGrowth.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblGrowth.setForeground(new Color(16, 185, 129));
        statCol1.add(lblS1Title);
        statCol1.add(lblS1Val);
        statCol1.add(lblGrowth);
        statsFooterPanel.add(statCol1);

        JPanel statCol2 = new JPanel(new GridLayout(3, 1));
        statCol2.setOpaque(false);
        JLabel lblS2Title = new JLabel("TỔNG TIỀN NỢ NÓNG TÍCH LŨY");
        lblS2Title.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblS2Title.setForeground(new Color(148, 163, 184));
        lblS2Val = new JLabel("0 đ");
        lblS2Val.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblS2Val.setForeground(new Color(239, 68, 68));
        statCol2.add(lblS2Title);
        statCol2.add(lblS2Val);
        statCol2.add(new JLabel("")); // empty
        statsFooterPanel.add(statCol2);

        JPanel statCol3 = new JPanel(new GridLayout(3, 1));
        statCol3.setOpaque(false);
        JLabel lblS3Title = new JLabel("TỶ LỆ TĂNG TRƯỞNG BÌNH QUÂN");
        lblS3Title.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblS3Title.setForeground(new Color(148, 163, 184));
        lblS3Val = new JLabel("0% / Tháng");
        lblS3Val.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblS3Val.setForeground(new Color(16, 185, 129));
        statCol3.add(lblS3Title);
        statCol3.add(lblS3Val);
        statCol3.add(new JLabel("")); // empty
        statsFooterPanel.add(statCol3);

        bodyWrapper.add(statsFooterPanel);

        JScrollPane scroll = new JScrollPane(bodyWrapper);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(248, 250, 252));

        add(scroll, BorderLayout.CENTER);
    }


    public ProgressRing getRing() { return ring; }
    public JLabel getLblStat1() { return lblStat1; }
    public JLabel getLblStat2() { return lblStat2; }
    public JLabel getLblStat3() { return lblStat3; }
    public BarChartComponent getBarChart() { return barChart; }
    public JLabel getLblS1Val() { return lblS1Val; }
    public JLabel getLblS2Val() { return lblS2Val; }
    public JLabel getLblS3Val() { return lblS3Val; }
    public JLabel getLblGrowth() { return lblGrowth; }

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
