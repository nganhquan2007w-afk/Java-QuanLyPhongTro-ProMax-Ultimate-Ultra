package server.util;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DateUtils — Tập trung toàn bộ date parsing/computation.
 * Loại bỏ helper methods trùng lặp trong nhiều Controllers.
 */
public final class DateUtils {

    private static final String[] FORMATS = {"dd/MM/yyyy", "yyyy-MM-dd", "dd-MM-yyyy"};

    private DateUtils() {}

    /**
     * Parse chuỗi ngày theo nhiều format phổ biến.
     * @return java.sql.Date, hoặc ngày hôm nay nếu parse thất bại
     */
    public static Date parse(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty())
            return new Date(System.currentTimeMillis());

        for (String fmt : FORMATS) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(fmt);
                sdf.setLenient(false);
                return new Date(sdf.parse(dateStr.trim()).getTime());
            } catch (Exception ignored) {}
        }
        return new Date(System.currentTimeMillis());
    }

    /**
     * Tính ngày kết thúc hợp đồng dựa theo chuỗi thời hạn.
     * Ví dụ: "12 tháng", "1 năm", "6 month" → tính theo Calendar.
     * Mặc định: 12 tháng nếu không parse được.
     */
    public static Date computeEndDate(Date startDate, String duration) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);

        String norm = (duration == null) ? "" : duration.toLowerCase().trim();
        int amount = 1;
        Matcher m = Pattern.compile("\\d+").matcher(norm);
        if (m.find()) amount = Integer.parseInt(m.group());

        if (norm.contains("nam") || norm.contains("year")) {
            cal.add(Calendar.YEAR, amount);
        } else if (norm.contains("thang") || norm.contains("month")
                || norm.contains("tháng") || norm.contains("năm")) {
            // Handle UTF-8 or ASCII versions
            if (norm.contains("nam") || norm.contains("năm") || norm.contains("year")) {
                cal.add(Calendar.YEAR, amount);
            } else {
                cal.add(Calendar.MONTH, amount);
            }
        } else {
            cal.add(Calendar.MONTH, 12); // mặc định 12 tháng
        }
        return new Date(cal.getTimeInMillis());
    }
}
