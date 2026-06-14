package common.model;

import java.io.Serializable;

public class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    private String roomId;
    private String status;
    private double price;
    private double area;
    private String zone;
    private String type;
    private int capacity;
    private String description;

    public Room() {}

    public Room(String roomId, String status, double price, double area, String zone, String type, int capacity, String description) {
        this.roomId = roomId;
        this.status = status;
        this.price = price;
        this.area = area;
        this.zone = zone;
        this.type = type;
        this.capacity = capacity;
        this.description = description;
    }
    
    // For backward compatibility before full refactoring
    public Room(String roomId, String status, double price, double area, String description) {
        this.roomId = roomId;
        this.status = status;
        this.price = price;
        this.area = area;
        this.zone = "Khu nhà A";
        this.type = "Tiêu chuẩn";
        this.capacity = 2;
        this.description = description != null ? description : "";
    }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getArea() { return area; }
    public void setArea(double area) { this.area = area; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
