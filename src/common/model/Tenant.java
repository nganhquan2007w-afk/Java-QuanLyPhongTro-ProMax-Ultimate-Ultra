package common.model;

import java.io.Serializable;
import java.sql.Date;

public class Tenant implements Serializable {
    private static final long serialVersionUID = 1L;

    private int tenantId;
    private String fullName;
    private String phone;
    private String cccd;
    private String gender;
    private Date birthDate;
    private String address;

    public Tenant() {}

    public Tenant(int tenantId, String fullName, String phone, String cccd, String gender, Date birthDate, String address) {
        this.tenantId = tenantId;
        this.fullName = fullName;
        this.phone = phone;
        this.cccd = cccd;
        this.gender = gender;
        this.birthDate = birthDate;
        this.address = address;
    }

    public int getTenantId() { return tenantId; }
    public void setTenantId(int tenantId) { this.tenantId = tenantId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public Date getBirthDate() { return birthDate; }
    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
