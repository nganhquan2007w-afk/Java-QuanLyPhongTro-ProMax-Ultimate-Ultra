package common.model;

import java.io.Serializable;
import java.sql.Date;

public class Contract implements Serializable {
    private static final long serialVersionUID = 1L;

    private int contractId;
    private String roomId;
    private int tenantId;
    private Date startDate;
    private Date endDate;
    private double deposit;

    public Contract() {}

    public Contract(int contractId, String roomId, int tenantId, Date startDate, Date endDate, double deposit) {
        this.contractId = contractId;
        this.roomId = roomId;
        this.tenantId = tenantId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.deposit = deposit;
    }

    public int getContractId() { return contractId; }
    public void setContractId(int contractId) { this.contractId = contractId; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public int getTenantId() { return tenantId; }
    public void setTenantId(int tenantId) { this.tenantId = tenantId; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public double getDeposit() { return deposit; }
    public void setDeposit(double deposit) { this.deposit = deposit; }
}
