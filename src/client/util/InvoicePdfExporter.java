package client.util;

import common.model.Invoice;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;

public class InvoicePdfExporter {

    public static void exportInvoiceToPdf(Invoice detail, String fileName, long elecPrice, long waterPrice) throws Exception {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(fileName));
        document.open();

        // Cấu hình Font tiếng Việt
        BaseFont bf = null;
        try {
            // Sử dụng duy nhất font Roboto tải cục bộ trong dự án
            bf = BaseFont.createFont("assets/fonts/Roboto-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (Exception e) {
            // Fallback font chuẩn nếu thiếu file (sẽ mất dấu tiếng Việt)
            bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        }

        Font titleFont = new Font(bf, 18, Font.BOLD);
        Font headerFont = new Font(bf, 14, Font.BOLD);
        Font normalFont = new Font(bf, 12, Font.NORMAL);
        Font boldFont = new Font(bf, 12, Font.BOLD);

        Paragraph title = new Paragraph("HOA DON TIEN NHA P." + detail.getRoomId().toUpperCase(), titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        document.add(new Paragraph("Tên Khách Thuê: " + (detail.getTenantName() != null ? detail.getTenantName() : ""), normalFont));
        document.add(new Paragraph("Ngày Lập: " + formatDate(detail.getIssueDate()), normalFont));
        document.add(new Paragraph("Hạn Nộp: " + formatDate(detail.getDueDate()), normalFont));
        document.add(new Paragraph("Trạng Thái: " + detail.getStatus(), normalFont));
        document.add(new Paragraph(" "));

        long total = 0;

        document.add(new Paragraph("CHI TIẾT THANH TOÁN:", boldFont));
        
        total += detail.getRent();
        document.add(new Paragraph("1. Tiền thuê phòng: " + formatCurrency((long)detail.getRent()) + " VND", normalFont));
        
        long elecTotal = (long)(detail.getElecUsage() * elecPrice);
        total += elecTotal;
        document.add(new Paragraph("2. Tiền điện: " + detail.getElecUsage() + " x " + formatCurrency(elecPrice) + " = " + formatCurrency(elecTotal) + " VND", normalFont));
        
        long waterTotal = (long)(detail.getWaterUsage() * waterPrice);
        total += waterTotal;
        document.add(new Paragraph("3. Tiền nước: " + detail.getWaterUsage() + " x " + formatCurrency(waterPrice) + " = " + formatCurrency(waterTotal) + " VND", normalFont));
        
        long otherTotal = (long)detail.getOtherFee();
        total += otherTotal;
        document.add(new Paragraph("4. Phụ phí khác: " + formatCurrency(otherTotal) + " VND", normalFont));
        
        document.add(new Paragraph(" "));
        Paragraph totalP = new Paragraph("TỔNG CỘNG: " + formatCurrency(total) + " VND", headerFont);
        totalP.setAlignment(Element.ALIGN_RIGHT);
        document.add(totalP);

        document.close();
    }

    private static String formatCurrency(long val) {
        return String.format("%,d", val).replace(',', '.');
    }

    private static String formatDate(java.sql.Date sqlDate) {
        if (sqlDate == null) return "-";
        return new SimpleDateFormat("dd/MM/yyyy").format(sqlDate);
    }
}
