package common.model;

import java.io.Serializable;

/**
 * Model đại diện cho thông tin tài khoản người dùng trong hệ thống
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String username;
    private String password;
    private String role;
    private String fullName;
    private String phone;
    /** Liên kết với bảng tenants (null nếu là admin/staff, có giá trị nếu là khách thuê) */
    private Integer tenantId;

    public User() {}

    /** Constructor tương thích ngược — không yêu cầu tenantId */
    public User(int id, String username, String password, String role, String fullName, String phone) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.phone = phone;
        this.tenantId = null;
    }

    /** Constructor đầy đủ bao gồm tenantId */
    public User(int id, String username, String password, String role, String fullName, String phone, Integer tenantId) {
        this(id, username, password, role, fullName, phone);
        this.tenantId = tenantId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Integer getTenantId() { return tenantId; }
    public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }
}
