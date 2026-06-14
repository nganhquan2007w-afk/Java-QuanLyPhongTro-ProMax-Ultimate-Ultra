package client.util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.Base64;

/**
 * Tiện ích hỗ trợ Xuất và Nhập file CSV, dùng chung cho tất cả các Controller.
 * Giúp giảm thiểu code trùng lặp và "giảm cân" cho Controller.
 */
public class CSVHelper {

    /**
     * Mở hộp thoại lưu file và xuất dữ liệu từ JTable ra file CSV.
     * @param parent Component cha (để hiển thị hộp thoại)
     * @param table  JTable chứa dữ liệu cần xuất
     * @param defaultFileName Tên file mặc định (VD: "DanhSachPhong.csv")
     */
    public static void exportTableToCSV(JComponent parent, JTable table, String defaultFileName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn vị trí lưu file CSV");
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        int userSelection = fileChooser.showSaveDialog(parent);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".csv");
            }
            
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileToSave), "UTF-8"))) {
                // Ghi BOM để Excel đọc tiếng Việt UTF-8 chuẩn
                pw.write('\ufeff');
                
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                int colCount = model.getColumnCount();
                
                // Ghi header
                for (int i = 0; i < colCount; i++) {
                    pw.print("\"" + model.getColumnName(i).replace("\"", "\"\"") + "\"");
                    if (i < colCount - 1) pw.print(",");
                }
                pw.println();
                
                // Ghi data
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < colCount; j++) {
                        Object val = model.getValueAt(i, j);
                        String strVal = (val == null) ? "" : val.toString();
                        pw.print("\"" + strVal.replace("\"", "\"\"") + "\"");
                        if (j < colCount - 1) pw.print(",");
                    }
                    pw.println();
                }
                
                JOptionPane.showMessageDialog(parent, "Xuất CSV thành công:\n" + fileToSave.getAbsolutePath(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Lỗi khi lưu file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Mở hộp thoại chọn file CSV, đọc nội dung (bỏ qua header) và mã hóa sang chuỗi Base64.
     * Cần thiết để truyền dữ liệu văn bản thuần tùy ý qua Socket an toàn.
     * @param parent Component cha
     * @return Chuỗi mã hóa Base64 của file CSV (không chứa header), hoặc null nếu hủy bỏ hoặc file trống.
     */
    public static String readCSVToBase64(JComponent parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file CSV để nhập");
        
        int userSelection = fileChooser.showOpenDialog(parent);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();
            
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileToOpen), "UTF-8"))) {
                String line;
                boolean isFirstLine = true;
                StringBuilder csvContent = new StringBuilder();
                
                while ((line = br.readLine()) != null) {
                    if (isFirstLine) {
                        isFirstLine = false;
                        // Bỏ qua BOM nếu có
                        if (line.startsWith("\uFEFF")) {
                            line = line.substring(1);
                        }
                        // Bỏ qua dòng Header
                        continue;
                    }
                    if (line.trim().isEmpty()) continue;
                    csvContent.append(line).append("\n");
                }
                
                if (csvContent.length() == 0) {
                    JOptionPane.showMessageDialog(parent, "File CSV trống hoặc sai định dạng!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                    return null;
                }

                return Base64.getEncoder().encodeToString(csvContent.toString().getBytes("UTF-8"));
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Lỗi khi đọc file CSV: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
        return null; // Bị hủy bỏ
    }
}
