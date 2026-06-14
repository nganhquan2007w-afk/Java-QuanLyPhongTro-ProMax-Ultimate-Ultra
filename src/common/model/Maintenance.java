package common.model;

import java.io.Serializable;
import java.sql.Date;

/**
 * Model biểu diễn một yêu cầu bảo trì/sửa chữa.
 */
public class Maintenance implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String roomId;
    private String description;
    private String priority;
    private Date reportDate;
    private String status;

    public Maintenance(int id, String roomId, String description, String priority, Date reportDate, String status) {
        this.id = id;
        this.roomId = roomId;
        this.description = description;
        this.priority = priority;
        this.reportDate = reportDate;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public Date getReportDate() { return reportDate; }
    public void setReportDate(Date reportDate) { this.reportDate = reportDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
