package common.model;

import java.sql.Date;

/**
 * Model đại diện cho một Hóa Đơn trong hệ thống quản lý phòng trọ.
 */
public class Invoice {

    private int    invoiceId;
    private String roomId;
    private String tenantName;
    private Date   issueDate;
    private Date   dueDate;
    private double rent;
    private double elecUsage;    // Số kWh điện tiêu thụ
    private double waterUsage;   // Số m3 nước tiêu thụ
    private double otherFee;     // Phụ phí khác
    private String status;       // "Chưa thanh toán" | "Đã thanh toán"
    private Date   paymentDate;  // null nếu chưa thanh toán

    public Invoice() {}

    public Invoice(int invoiceId, String roomId, String tenantName,
                   Date issueDate, Date dueDate,
                   double rent, double elecUsage, double waterUsage, double otherFee,
                   String status, Date paymentDate) {
        this.invoiceId   = invoiceId;
        this.roomId      = roomId;
        this.tenantName  = tenantName;
        this.issueDate   = issueDate;
        this.dueDate     = dueDate;
        this.rent        = rent;
        this.elecUsage   = elecUsage;
        this.waterUsage  = waterUsage;
        this.otherFee    = otherFee;
        this.status      = status;
        this.paymentDate = paymentDate;
    }

    // Getters & Setters
    public int    getInvoiceId()               { return invoiceId; }
    public void   setInvoiceId(int invoiceId)  { this.invoiceId = invoiceId; }

    public String getRoomId()                  { return roomId; }
    public void   setRoomId(String roomId)     { this.roomId = roomId; }

    public String getTenantName()                     { return tenantName; }
    public void   setTenantName(String tenantName)    { this.tenantName = tenantName; }

    public Date   getIssueDate()               { return issueDate; }
    public void   setIssueDate(Date issueDate) { this.issueDate = issueDate; }

    public Date   getDueDate()                 { return dueDate; }
    public void   setDueDate(Date dueDate)     { this.dueDate = dueDate; }

    public double getRent()                    { return rent; }
    public void   setRent(double rent)         { this.rent = rent; }

    public double getElecUsage()               { return elecUsage; }
    public void   setElecUsage(double v)       { this.elecUsage = v; }

    public double getWaterUsage()              { return waterUsage; }
    public void   setWaterUsage(double v)      { this.waterUsage = v; }

    public double getOtherFee()                { return otherFee; }
    public void   setOtherFee(double v)        { this.otherFee = v; }

    public String getStatus()                  { return status; }
    public void   setStatus(String status)     { this.status = status; }

    public Date   getPaymentDate()                    { return paymentDate; }
    public void   setPaymentDate(Date paymentDate)    { this.paymentDate = paymentDate; }

    /** Tính tổng tiền hóa đơn (tiền phòng + điện + nước + phụ phí) */
    public double getTotal(double elecUnitPrice, double waterUnitPrice) {
        return rent + (elecUsage * elecUnitPrice) + (waterUsage * waterUnitPrice) + otherFee;
    }
}
