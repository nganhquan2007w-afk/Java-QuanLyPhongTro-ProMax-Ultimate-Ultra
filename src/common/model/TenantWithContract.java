package common.model;

import java.io.Serializable;

/**
 * Data Transfer Object (DTO) chứa thông tin của khách thuê
 * kết hợp với thông tin hợp đồng và phòng đang thuê.
 */
public class TenantWithContract implements Serializable {
    private static final long serialVersionUID = 1L;

    private Tenant tenant;
    private String roomId;
    private String startDate;
    private String endDate;
    private double deposit;
    private String roomStatus;

    public TenantWithContract(Tenant tenant, String roomId, String startDate, String endDate, double deposit, String roomStatus) {
        this.tenant = tenant;
        this.roomId = roomId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.deposit = deposit;
        this.roomStatus = roomStatus;
    }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public double getDeposit() { return deposit; }
    public void setDeposit(double deposit) { this.deposit = deposit; }

    public String getRoomStatus() { return roomStatus; }
    public void setRoomStatus(String roomStatus) { this.roomStatus = roomStatus; }
}
