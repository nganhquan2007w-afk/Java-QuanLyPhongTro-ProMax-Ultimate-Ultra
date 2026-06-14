package server.service;

import server.dao.InvoiceDAO;
import server.exception.ServiceException;
import common.model.Invoice;
import server.util.DateUtils;
import server.util.ServerLogger;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;

/**
 * InvoiceService — Validate, business logic, gọi DAO.
 * Throw ServiceException khi lỗi.
 */
public class InvoiceService {

    private static final int    DUE_DAYS_OFFSET    = 5;
    public  static final double DEFAULT_ELEC_UNIT  = 3500.0;
    public  static final double DEFAULT_WATER_UNIT = 10000.0;

    private final InvoiceDAO invoiceDAO;

    public InvoiceService() {
        this.invoiceDAO = new InvoiceDAO();
    }

    /** Lấy tất cả hóa đơn (Admin) */
    public List<Invoice> getAllInvoices() {
        return invoiceDAO.getAllInvoices();
    }

    /** Lấy hóa đơn theo phòng (Tenant) */
    public List<Invoice> getInvoicesByRoom(String roomId) {
        return invoiceDAO.getInvoicesByRoom(roomId);
    }

    /**
     * Tạo hóa đơn mới.
     * Business rule: due_date = issue_date + 5 ngày.
     * @throws ServiceException nếu validate lỗi hoặc DB thất bại
     */
    public int createInvoice(String roomId, String tenantName, String issueDate,
                              double rent, double elecUsage,
                              double waterUsage, double otherFee)
            throws ServiceException {
        // Validate
        if (roomId == null || roomId.trim().isEmpty())
            throw new ServiceException("Mã phòng không được để trống!");
        if (tenantName == null || tenantName.trim().isEmpty())
            throw new ServiceException("Tên khách thuê không được để trống!");
        if (rent < 0)
            throw new ServiceException("Tiền thuê không được âm!");
        if (elecUsage < 0)
            throw new ServiceException("Chỉ số điện không được âm!");
        if (waterUsage < 0)
            throw new ServiceException("Chỉ số nước không được âm!");
        if (otherFee < 0)
            throw new ServiceException("Phí khác không được âm!");

        Date sqlIssue = DateUtils.parse(issueDate);
        Calendar cal  = Calendar.getInstance();
        cal.setTime(sqlIssue);
        cal.add(Calendar.DAY_OF_MONTH, DUE_DAYS_OFFSET);
        Date sqlDue = new Date(cal.getTimeInMillis());

        Invoice inv = new Invoice();
        inv.setRoomId(roomId.trim());
        inv.setTenantName(tenantName.trim());
        inv.setIssueDate(sqlIssue);
        inv.setDueDate(sqlDue);
        inv.setRent(rent);
        inv.setElecUsage(elecUsage);
        inv.setWaterUsage(waterUsage);
        inv.setOtherFee(otherFee);
        inv.setStatus("Chưa thanh toán");

        int id = invoiceDAO.addInvoice(inv);
        if (id <= 0)
            throw new ServiceException("Không thể tạo hóa đơn trong cơ sở dữ liệu!");

        ServerLogger.info("Tao hoa don thanh cong #" + id + " phong " + roomId);
        
        try {
            server.dao.NotificationDAO notiDao = new server.dao.NotificationDAO();
            String title = "Hóa đơn mới: Phòng " + roomId;
            
            double elecUnit = DEFAULT_ELEC_UNIT;
            double waterUnit = DEFAULT_WATER_UNIT;
            try {
                server.dao.ServiceDAO svcDao = new server.dao.ServiceDAO();
                List<common.model.Service> svcs = svcDao.getAllServices();
                if (svcs != null) {
                    for (common.model.Service s : svcs) {
                        if (s.getServiceName().toLowerCase().contains("điện")) elecUnit = s.getUnitPrice();
                        if (s.getServiceName().toLowerCase().contains("nước")) waterUnit = s.getUnitPrice();
                    }
                }
            } catch (Exception ignored) {}

            double total = rent + elecUsage * elecUnit + waterUsage * waterUnit + otherFee;
            String content = "Bạn có một hóa đơn mới. Tổng tiền thuê: " + String.format("%,.0f", total) + " VND. Vui lòng kiểm tra hóa đơn và thanh toán trước hạn nộp.";
            notiDao.addNotification(title, content, roomId);
        } catch (Exception e) {
            ServerLogger.error("Lỗi khi gửi thông báo hóa đơn: " + e.getMessage());
        }

        return id;
    }

    /**
     * Cập nhật trạng thái thanh toán.
     * Business rule: khi "Đã thanh toán" mà không có paymentDate → dùng ngày hôm nay.
     * @throws ServiceException nếu validate lỗi hoặc DB thất bại
     */
    public void updatePaymentStatus(int invoiceId, String status, String paymentDate)
            throws ServiceException {
        if (invoiceId <= 0)
            throw new ServiceException("Mã hóa đơn không hợp lệ!");
        if (status == null || status.trim().isEmpty())
            throw new ServiceException("Trạng thái không được để trống!");

        Date payDate = null;
        if ("Đã thanh toán".equals(status)) {
            payDate = (paymentDate == null || paymentDate.trim().isEmpty() || "-".equals(paymentDate.trim()))
                    ? new Date(System.currentTimeMillis())
                    : DateUtils.parse(paymentDate);
        }

        if (!invoiceDAO.updatePaymentStatus(invoiceId, status, payDate))
            throw new ServiceException("Không thể cập nhật trạng thái hóa đơn #" + invoiceId);

        ServerLogger.info("Cap nhat trang thai hoa don #" + invoiceId + " -> " + status);
    }

    /** Tính tổng tiền hóa đơn với đơn giá tùy chỉnh */
    public static double calcTotal(Invoice inv, double elecUnit, double waterUnit) {
        double eu = elecUnit  <= 0 ? DEFAULT_ELEC_UNIT  : elecUnit;
        double wu = waterUnit <= 0 ? DEFAULT_WATER_UNIT : waterUnit;
        return inv.getRent() + inv.getElecUsage() * eu + inv.getWaterUsage() * wu + inv.getOtherFee();
    }
}
