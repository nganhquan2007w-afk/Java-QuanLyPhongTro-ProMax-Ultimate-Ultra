package client.view.panel;

import client.view.component.RoundedPanel;
import client.view.component.ModernButton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;

@SuppressWarnings("serial")
public final class ServicesPanel extends JPanel {

    private ModernButton btnEditCat;
    private ModernButton btnAddCat;
    private ModernButton btnDeleteCat;
    private ModernButton btnSaveIdx;
    private DefaultTableModel catModel;
    private DefaultTableModel idxModel;
    private JTable catTable;
    private JTable idxTable;

    public ServicesPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        initComponents();
    }

    private void initComponents() {
        JPanel bodyWrapper = new JPanel();
        bodyWrapper.setLayout(new BoxLayout(bodyWrapper, BoxLayout.Y_AXIS));
        bodyWrapper.setBorder(new EmptyBorder(25, 25, 25, 25));
        bodyWrapper.setOpaque(false);

        // Header Label
        JLabel lblHeader = new JLabel("Danh Mục Dịch Vụ & Ghi Số Điện Nước");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblHeader.setForeground(new Color(15, 23, 42));
        lblHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        bodyWrapper.add(lblHeader);
        bodyWrapper.add(Box.createVerticalStrut(15));

        // Two-part horizontal split layout
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        splitPanel.setOpaque(false);
        splitPanel.setPreferredSize(new Dimension(800, 520));
        splitPanel.setMaximumSize(new Dimension(1600, 520));

        // PART 1: Services Catalog
        RoundedPanel catalogCard = new RoundedPanel(16, Color.WHITE);
        catalogCard.setLayout(new BorderLayout(0, 15));
        catalogCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JPanel catalogHeader = new JPanel(new BorderLayout());
        catalogHeader.setOpaque(false);
        JLabel lblCatTitle = new JLabel("Bảng Đơn Giá Dịch Vụ");
        lblCatTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblCatTitle.setForeground(new Color(15, 23, 42));
        catalogHeader.add(lblCatTitle, BorderLayout.WEST);

        btnEditCat = new ModernButton(" Sửa");
        try {
            java.net.URL url = getClass().getResource("/Image/pencil.png");
            if (url != null) btnEditCat.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        btnEditCat.setColors(new Color(245, 158, 11), new Color(217, 119, 6), new Color(180, 83, 9));

        btnAddCat = new ModernButton(" Thêm");
        try {
            java.net.URL url = getClass().getResource("/Image/add.png");
            if (url != null) btnAddCat.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        btnAddCat.setColors(new Color(16, 185, 129), new Color(5, 150, 105), new Color(4, 120, 87));

        btnDeleteCat = new ModernButton(" Xóa");
        try {
            java.net.URL url = getClass().getResource("/Image/delete.png");
            if (url != null) btnDeleteCat.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH)));
        } catch (Exception e) {}
        btnDeleteCat.setColors(new Color(239, 68, 68), new Color(220, 38, 38), new Color(185, 28, 28));

        JPanel catActionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        catActionPanel.setOpaque(false);
        catActionPanel.add(btnAddCat);
        catActionPanel.add(btnEditCat);
        catActionPanel.add(btnDeleteCat);

        catalogHeader.add(catActionPanel, BorderLayout.EAST);

        catalogCard.add(catalogHeader, BorderLayout.NORTH);

        String[] catCols = {"Tên Dịch Vụ", "Đơn Giá", "Đơn Vị"};
        Object[][] catData = {};
        catModel = new DefaultTableModel(catData, catCols) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        catTable = new JTable(catModel);
        catTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        catTable.setRowHeight(36);
        catTable.setShowGrid(false);
        catTable.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader catHdr = catTable.getTableHeader();
        catHdr.setFont(new Font("Segoe UI", Font.BOLD, 13));
        catHdr.setBackground(new Color(248, 250, 252));
        catHdr.setForeground(new Color(71, 85, 105));
        catHdr.setPreferredSize(new Dimension(100, 32));

        JScrollPane catScroll = new JScrollPane(catTable);
        catScroll.setBorder(BorderFactory.createEmptyBorder());
        catScroll.getViewport().setBackground(Color.WHITE);
        catalogCard.add(catScroll, BorderLayout.CENTER);

        splitPanel.add(catalogCard);

        // PART 2: Electricity & Water Readings Panel
        RoundedPanel indexCard = new RoundedPanel(16, Color.WHITE);
        indexCard.setLayout(new BorderLayout(0, 15));
        indexCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JPanel indexHeader = new JPanel(new BorderLayout());
        indexHeader.setOpaque(false);
        JLabel lblIdxTitle = new JLabel("Nhập Số Điện Nước Tháng Này");
        lblIdxTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblIdxTitle.setForeground(new Color(15, 23, 42));
        indexHeader.add(lblIdxTitle, BorderLayout.WEST);

        btnSaveIdx = new ModernButton("💾 Lưu nhanh chỉ số");
        btnSaveIdx.setColors(new Color(16, 185, 129), new Color(5, 150, 105), new Color(4, 120, 87));
        btnSaveIdx.setPreferredSize(new Dimension(160, 32));
        indexHeader.add(btnSaveIdx, BorderLayout.EAST);

        indexCard.add(indexHeader, BorderLayout.NORTH);

        String[] idxCols = {"Phòng", "Điện Tiêu Thụ (kWh)", "Nước Tiêu Thụ (m3)"};
        Object[][] idxData = {};
        idxModel = new DefaultTableModel(idxData, idxCols) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 1 || c == 2; // Allow editing consumptions
            }
        };
        idxTable = new JTable(idxModel);
        idxTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        idxTable.setRowHeight(36);
        idxTable.setShowGrid(true);
        idxTable.setGridColor(new Color(241, 245, 249));

        JTableHeader idxHdr = idxTable.getTableHeader();
        idxHdr.setFont(new Font("Segoe UI", Font.BOLD, 13));
        idxHdr.setBackground(new Color(248, 250, 252));
        idxHdr.setForeground(new Color(71, 85, 105));
        idxHdr.setPreferredSize(new Dimension(100, 32));

        DefaultTableCellRenderer centerRender = new DefaultTableCellRenderer();
        centerRender.setHorizontalAlignment(JLabel.CENTER);
        for(int i=0; i<idxCols.length; i++) {
            idxTable.getColumnModel().getColumn(i).setCellRenderer(centerRender);
        }

        JScrollPane idxScroll = new JScrollPane(idxTable);
        idxScroll.setBorder(BorderFactory.createEmptyBorder());
        idxScroll.getViewport().setBackground(Color.WHITE);
        indexCard.add(idxScroll, BorderLayout.CENTER);

        splitPanel.add(indexCard);
        bodyWrapper.add(splitPanel);

        JScrollPane scroll = new JScrollPane(bodyWrapper);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(new Color(248, 250, 252));
        add(scroll, BorderLayout.CENTER);
    }

    public void setAdminMode(boolean adminMode) {
        if (btnEditCat != null) btnEditCat.setVisible(adminMode);
        if (btnAddCat != null) btnAddCat.setVisible(adminMode);
        if (btnDeleteCat != null) btnDeleteCat.setVisible(adminMode);
        if (btnSaveIdx != null) btnSaveIdx.setVisible(adminMode);
        
        // Cập nhật lại khả năng chỉnh sửa của bảng chỉ số
        if (idxModel != null) {
            DefaultTableModel newModel = new DefaultTableModel(idxModel.getDataVector(), new java.util.Vector<>(java.util.Arrays.asList("Phòng", "Điện Tiêu Thụ (kWh)", "Nước Tiêu Thụ (m3)"))) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return adminMode && (c == 1 || c == 2);
                }
            };
            idxTable.setModel(newModel);
            idxModel = newModel;
        }
    }

    // Getters
    public ModernButton getBtnEditCat() { return btnEditCat; }
    public ModernButton getBtnAddCat() { return btnAddCat; }
    public ModernButton getBtnDeleteCat() { return btnDeleteCat; }
    public ModernButton getBtnSaveIdx() { return btnSaveIdx; }
    public DefaultTableModel getCatModel() { return catModel; }
    public DefaultTableModel getIdxModel() { return idxModel; }
    public JTable getCatTable() { return catTable; }
    public JTable getIdxTable() { return idxTable; }

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

