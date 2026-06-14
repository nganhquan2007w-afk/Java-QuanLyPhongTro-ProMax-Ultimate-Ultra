package client.util;

import java.text.SimpleDateFormat;

/**
 * DateFormatter — Utility định dạng ngày tháng cho phía Client (hiển thị UI).
 * Tách ra khỏi Model để tuân thủ nguyên tắc Model chỉ là data holder.
 */
public final class DateFormatter {

    private DateFormatter() {}

    /**
     * Format ngày từ yyyy-MM-dd → dd/MM/yyyy để hiển thị trên giao diện.
     * @return chuỗi đã format, hoặc "-" nếu input rỗng/null
     */
    public static String toDisplay(String raw) {
        if (raw == null || raw.trim().isEmpty() || "-".equals(raw.trim())) return "-";
        try {
            SimpleDateFormat from = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat to   = new SimpleDateFormat("dd/MM/yyyy");
            return to.format(from.parse(raw.trim()));
        } catch (Exception e) {
            return raw; // trả về nguyên bản nếu không parse được
        }
    }

    /**
     * Format ngày từ Date object → dd/MM/yyyy để hiển thị trên giao diện.
     */
    public static String toDisplay(java.util.Date date) {
        if (date == null) return "-";
        try {
            SimpleDateFormat to = new SimpleDateFormat("dd/MM/yyyy");
            return to.format(date);
        } catch (Exception e) {
            return "-";
        }
    }

    /**
     * Format ngày từ dd/MM/yyyy → yyyy-MM-dd để gửi lên server.
     */
    public static String toServerFormat(String display) {
        if (display == null || display.trim().isEmpty() || "-".equals(display.trim())) return "";
        try {
            SimpleDateFormat from = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat to   = new SimpleDateFormat("yyyy-MM-dd");
            return to.format(from.parse(display.trim()));
        } catch (Exception e) {
            return display;
        }
    }

    /**
     * Tính toán thời hạn (1 tháng, 1 năm...) giữa 2 khoảng thời gian (dùng cho Contract/Tenant)
     */
    public static String formatDuration(String startStr, String endStr) {
        if (startStr == null || endStr == null || startStr.trim().isEmpty() || endStr.trim().isEmpty()) {
            return "1 năm";
        }
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.util.Date start = sdf.parse(startStr);
            java.util.Date end = sdf.parse(endStr);
            long diff = end.getTime() - start.getTime();
            long days = diff / (24L * 60 * 60 * 1000);
            if (days >= 360) {
                return Math.round(days / 365.0) + " năm";
            } else {
                return Math.round(days / 30.0) + " tháng";
            }
        } catch (Exception e) {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                java.util.Date start = sdf.parse(startStr);
                java.util.Date end = sdf.parse(endStr);
                long diff = end.getTime() - start.getTime();
                long days = diff / (24L * 60 * 60 * 1000);
                if (days >= 360) {
                    return Math.round(days / 365.0) + " năm";
                } else {
                    return Math.round(days / 30.0) + " tháng";
                }
            } catch (Exception ex) {
                return "1 năm";
            }
        }
    }
}
