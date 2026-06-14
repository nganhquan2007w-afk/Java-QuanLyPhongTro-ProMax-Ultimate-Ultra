package client.controller.handler;

import client.controller.InvoicesController;
import client.view.dialog.AddInvoiceDialog;
import client.view.dialog.ViewInvoiceDetailDialog;
import client.view.panel.InvoicesPanel;
import common.model.Invoice;
import client.service.InvoiceService;
import client.util.DateFormatter;

import javax.swing.*;
import java.awt.Frame;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Xử lý các logic nghiệp vụ giao diện (Dialog) của Hóa Đơn.
 * Được tách ra từ InvoicesController để giảm tải kích thước file.
 */
public class InvoiceActionHandler {

    public static void handleViewDetail(InvoicesController controller, InvoicesPanel view, List<Invoice> cachedInvoices, long[] prices, Runnable reloadCallback) {
        int selectedRow = view.getTable().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(view,
                "Vui lòng chọn một hóa đơn từ danh sách để xem chi tiết!",
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (cachedInvoices == null || selectedRow >= cachedInvoices.size()) return;

        String displayId = (String) view.getTable().getValueAt(selectedRow, 0);
        int invoiceId = 0;
        try {
            invoiceId = Integer.parseInt(displayId.replace("HD", ""));
        } catch (Exception e) { return; }

        Invoice detail = null;
        for (Invoice inv : cachedInvoices) {
            if (inv.getInvoiceId() == invoiceId) { detail = inv; break; }
        }
        if (detail == null) return;

        final Invoice finalDetail = detail;
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(view);
        ViewInvoiceDetailDialog dialog = new ViewInvoiceDetailDialog(
            parent,
            "HD" + String.format("%03d", finalDetail.getInvoiceId()),
            finalDetail.getRoomId(),
            finalDetail.getTenantName(),
            DateFormatter.toDisplay(finalDetail.getIssueDate()),
            DateFormatter.toDisplay(finalDetail.getDueDate()),
            finalDetail.getStatus(),
            DateFormatter.toDisplay(finalDetail.getPaymentDate()),
            (long) finalDetail.getRent(),
            finalDetail.getElecUsage(),
            finalDetail.getWaterUsage(),
            (long) finalDetail.getOtherFee(),
            prices[0],
            prices[1]
        );

        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                reloadCallback.run();
            }
        });

        dialog.setVisible(true);
    }

    public static void handleExportPdf(InvoicesController controller, InvoicesPanel view, List<Invoice> cachedInvoices, long[] prices) {
        int selectedRow = view.getTable().getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn một hóa đơn từ danh sách để xuất PDF!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (cachedInvoices == null || selectedRow >= cachedInvoices.size()) return;

        String displayId = (String) view.getTable().getValueAt(selectedRow, 0);
        int invoiceId = 0;
        try {
            invoiceId = Integer.parseInt(displayId.replace("HD", ""));
        } catch (Exception e) { return; }

        Invoice detail = null;
        for (Invoice inv : cachedInvoices) {
            if (inv.getInvoiceId() == invoiceId) { detail = inv; break; }
        }
        if (detail == null) return;

        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Lưu Hóa Đơn PDF");
            fileChooser.setSelectedFile(new File("HoaDon_" + detail.getRoomId() + "_" + System.currentTimeMillis() + ".pdf"));
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Documents (*.pdf)", "pdf"));
            
            if (fileChooser.showSaveDialog(view) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            
            File fileToSave = fileChooser.getSelectedFile();
            String fileName = fileToSave.getAbsolutePath();
            if (!fileName.toLowerCase().endsWith(".pdf")) {
                fileName += ".pdf";
            }
            client.util.InvoicePdfExporter.exportInvoiceToPdf(detail, fileName, prices[0], prices[1]);

            JOptionPane.showMessageDialog(view, "Đã xuất hóa đơn thành công:\n" + fileName, "Thành công", JOptionPane.INFORMATION_MESSAGE);
            java.awt.Desktop.getDesktop().open(new File(fileName));

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Có lỗi xảy ra khi xuất PDF: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void openAddInvoiceDialog(InvoicesController controller, InvoicesPanel view, Map<String, String> roomTenantMap, Map<String, Double> roomPriceMap, Runnable reloadCallback) {
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(view);
        AddInvoiceDialog dialog = new AddInvoiceDialog(parent, roomTenantMap, roomPriceMap);

        dialog.getBtnCancel().addActionListener(e -> dialog.dispose());
        dialog.getBtnSave().addActionListener(e -> {
            String room       = dialog.getRoom();
            String tenantName = dialog.getTenantName();
            String dateStr    = dialog.getDate();
            String rentStr    = dialog.getRent();
            String elecStr    = dialog.getElectricity();
            String waterStr   = dialog.getWater();
            String otherStr   = dialog.getOther();

            if (room == null || room.trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn phòng!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (tenantName.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập tên khách thuê!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (rentStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập tiền thuê phòng!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double rent = 0, elec = 0, water = 0, other = 0;
            try {
                rent = controller.parseAmount(rentStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Tiền thuê phòng không hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!elecStr.isEmpty()) {
                try { elec = Double.parseDouble(elecStr.replace(",", ".")); } catch (Exception ex) { return; }
            }
            if (!waterStr.isEmpty()) {
                try { water = Double.parseDouble(waterStr.replace(",", ".")); } catch (Exception ex) { return; }
            }
            if (!otherStr.isEmpty()) {
                try { other = controller.parseAmount(otherStr); } catch (Exception ex) { return; }
            }

            if (dateStr.isEmpty()) {
                dateStr = new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());
            }

            dialog.getBtnSave().setEnabled(false);
            dialog.getBtnSave().setText("Đang tạo...");
            
            final String finalDate   = dateStr;
            final double finalRent   = rent;
            final double finalElec   = elec;
            final double finalWater  = water;
            final double finalOther  = other;

            new SwingWorker<String, Void>() {
                @Override
                protected String doInBackground() {
                    return InvoiceService.addInvoice(room, tenantName, finalDate, finalRent, finalElec, finalWater, finalOther);
                }

                @Override
                protected void done() {
                    dialog.getBtnSave().setEnabled(true);
                    dialog.getBtnSave().setText("Tạo Hóa Đơn");
                    try {
                        String result = get();
                        if ("SUCCESS".equals(result)) {
                            JOptionPane.showMessageDialog(dialog, "✅ Đã tạo hóa đơn thành công cho phòng " + room + "!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                            dialog.dispose();
                            reloadCallback.run();
                        } else {
                            JOptionPane.showMessageDialog(dialog, "❌ " + result, "Lỗi khi tạo hóa đơn", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, "Lỗi hệ thống: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });

        dialog.setVisible(true);
    }
}
