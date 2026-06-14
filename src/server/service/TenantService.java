package server.service;

import server.dao.TenantDAO;
import server.dao.ContractDAO;
import server.dao.RoomDAO;
import server.exception.ServiceException;
import common.model.Contract;
import common.model.Tenant;
import server.util.DateUtils;
import server.util.ServerLogger;
import server.dao.UserDAO;
import common.model.User;
import server.security.PasswordUtil;

import java.sql.Date;
import java.util.List;

/**
 * TenantService — Validate, business logic, gọi DAO.
 * Throw ServiceException khi lỗi để Controller không cần xử lý logic.
 */
public class TenantService {

    private final TenantDAO   tenantDAO;
    private final ContractDAO contractDAO;
    private final RoomDAO roomDAO;

    public TenantService() {
        this.tenantDAO   = new TenantDAO();
        this.contractDAO = new ContractDAO();
        this.roomDAO     = new RoomDAO();
    }

    /** Lấy danh sách khách thuê kèm thông tin hợp đồng */
    public List<common.model.TenantWithContract> getTenantsWithContracts() {
        return tenantDAO.getTenantsWithContracts();
    }

    private void validateBirthDate(String birthDate) throws ServiceException {
        if (birthDate == null || birthDate.trim().isEmpty())
            throw new ServiceException("Ngày sinh không được để trống!");
        
        String bd = birthDate.trim();
        if (!bd.matches("^\\d{1,2}[/-]\\d{1,2}[/-]\\d{4}$") && !bd.matches("^\\d{4}[/-]\\d{1,2}[/-]\\d{1,2}$")) {
            throw new ServiceException("Ngày sinh không hợp lệ. Vui lòng nhập đủ ngày, tháng và 4 số năm (VD: 01/12/1990)!");
        }
        
        java.sql.Date d = DateUtils.parse(bd);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(d);
        int year = cal.get(java.util.Calendar.YEAR);
        if (year < 1900 || year > java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)) {
            throw new ServiceException("Năm sinh không hợp lệ (phải từ năm 1900 đến hiện tại)!");
        }
    }

    /**
     * Thêm khách thuê mới kèm hợp đồng.
     * @throws ServiceException nếu validate lỗi hoặc DB thất bại
     */
    public void addTenantWithContract(String fullName, String phone, String cccd,
                                      String gender, String birthDate, String address,
                                      String roomId, String startDate, String duration)
            throws ServiceException {
        // Validate
        if (fullName == null || fullName.trim().isEmpty())
            throw new ServiceException("Họ tên không được để trống!");
        if (fullName.trim().length() < 2)
            throw new ServiceException("Họ tên phải có ít nhất 2 ký tự!");
        if (phone == null || phone.trim().isEmpty())
            throw new ServiceException("Số điện thoại không được để trống!");
        if (!phone.trim().matches("^[0-9]{9,11}$"))
            throw new ServiceException("Số điện thoại không hợp lệ (9–11 chữ số)!");
        if (roomId == null || roomId.trim().isEmpty())
            throw new ServiceException("Mã phòng không được để trống!");

        validateBirthDate(birthDate);

        // Validate số lượng người trong phòng
        common.model.Room room = roomDAO.getRoomById(roomId.trim());
        if (room == null) {
            throw new ServiceException("Mã phòng không tồn tại!");
        }
        int capacity = room.getCapacity();
        if (capacity > 0) {
            int currentTenants = contractDAO.countTenantsInRoom(roomId.trim());
            if (currentTenants >= capacity) {
                throw new ServiceException("Phòng " + roomId.trim() + " đã đạt giới hạn tối đa (" + capacity + " người)!");
            }
        }

        // Validate số điện thoại duy nhất
        UserDAO userDAO = new UserDAO();
        if (userDAO.getUserByUsername(phone.trim()) != null) {
            throw new ServiceException("Số điện thoại này đã được đăng ký tài khoản trong hệ thống!");
        }
        List<Tenant> allTenants = tenantDAO.getAllTenants();
        for (Tenant t : allTenants) {
            if (phone.trim().equals(t.getPhone())) {
                throw new ServiceException("Số điện thoại này đã thuộc về một khách thuê khác!");
            }
        }

        Tenant tenant = new Tenant();
        tenant.setFullName(fullName.trim());
        tenant.setPhone(phone.trim());
        tenant.setCccd(cccd != null ? cccd.trim() : "");
        tenant.setGender(gender != null && !gender.trim().isEmpty() ? gender.trim() : "Nam");
        tenant.setBirthDate(DateUtils.parse(birthDate));
        tenant.setAddress(address != null ? address.trim() : "");

        int tenantId = tenantDAO.addTenant(tenant);
        if (tenantId == -1)
            throw new ServiceException("Không thể tạo hồ sơ khách thuê trong cơ sở dữ liệu!");

        Date sqlStart = DateUtils.parse(startDate);
        Date sqlEnd   = DateUtils.computeEndDate(sqlStart, duration);

        Contract contract = new Contract();
        contract.setRoomId(roomId.trim());
        contract.setTenantId(tenantId);
        contract.setStartDate(sqlStart);
        contract.setEndDate(sqlEnd);
        contract.setDeposit(0); // sẽ được tính ở ContractService.createContract()

        int contractId = contractDAO.addContract(contract);
        if (contractId == -1)
            throw new ServiceException("Không thể tạo hợp đồng sau khi đăng ký khách thuê!");

        // Cập nhật trạng thái phòng thành Đã thuê
        roomDAO.updateRoomStatus(roomId.trim(), "Đã thuê");

        // Tự động cấp tài khoản cho khách thuê
        // Username = SĐT, Mật khẩu = 6 chữ số ngẫu nhiên
        if (userDAO.getUserByUsername(phone.trim()) == null) {
            String plainPassword = String.format("%06d",
                    new java.security.SecureRandom().nextInt(1_000_000));
            User newUser = new User();
            newUser.setUsername(phone.trim());
            newUser.setPassword(PasswordUtil.hashSHA256(plainPassword));
            newUser.setRole("USER");
            newUser.setFullName(fullName.trim());
            newUser.setPhone(phone.trim());
            newUser.setTenantId(tenantId); // Liên kết FK với bảng tenants
            userDAO.addUser(newUser);
            ServerLogger.info("Đã cấp tài khoản cho khách thuê: " + phone.trim() + " (mật khẩu đã hash)");
            throw new _SuccessWithPassword(plainPassword); // truyền mật khẩu về controller
        }

        ServerLogger.info("Đăng ký khách thuê thành công: " + fullName + " → phòng " + roomId);
    }

    /**
     * Exception nội bộ — chỉ dùng để truyền mật khẩu plain-text ngược lên controller.
     * Không phải lỗi thật — controller xử lý thành SUCCESS kèm password.
     */
    public static class _SuccessWithPassword extends RuntimeException {
        public final String plainPassword;
        public _SuccessWithPassword(String pw) { super(); this.plainPassword = pw; }
    }

    /**
     * Tìm hoặc tạo khách thuê theo tên.
     * @return tenantId
     * @throws ServiceException nếu không tạo được
     */
    public int getOrCreateTenant(String name) throws ServiceException {
        if (name == null || name.trim().isEmpty())
            throw new ServiceException("Tên khách thuê không được để trống!");

        Tenant existing = tenantDAO.getTenantByName(name.trim());
        if (existing != null) return existing.getTenantId();

        Tenant newTenant = new Tenant();
        newTenant.setFullName(name.trim());
        newTenant.setPhone("");
        newTenant.setCccd("");
        newTenant.setGender("Nam");
        newTenant.setBirthDate(new Date(System.currentTimeMillis()));
        newTenant.setAddress("");

        int id = tenantDAO.addTenant(newTenant);
        if (id == -1)
            throw new ServiceException("Không thể tạo khách thuê mới: " + name);
        return id;
    }

    /**
     * Lấy mã phòng đang thuê theo tên đăng nhập.
     * @throws ServiceException nếu không tìm thấy
     */
    public String getRoomIdByUsername(String username) throws ServiceException {
        if (username == null || username.trim().isEmpty())
            throw new ServiceException("Tên đăng nhập không hợp lệ!");
        String roomId = contractDAO.getRoomIdByUsername(username.trim());
        if (roomId == null)
            throw new ServiceException("Không tìm thấy phòng đang thuê!");
        return roomId;
    }

    /** Cập nhật thông tin khách thuê và mã phòng */
    public void updateTenant(int tenantId, String fullName, String phone, String cccd,
                             String gender, String birthDate, String address, String roomId) throws ServiceException {
        if (tenantId <= 0)
            throw new ServiceException("ID khách thuê không hợp lệ!");
        if (fullName == null || fullName.trim().isEmpty())
            throw new ServiceException("Họ tên không được để trống!");
        if (phone != null && !phone.trim().isEmpty() && !phone.trim().matches("^[0-9]{9,11}$"))
            throw new ServiceException("Số điện thoại không hợp lệ (9–11 chữ số)!");

        validateBirthDate(birthDate);

        Tenant tenant = new Tenant();
        tenant.setTenantId(tenantId);
        tenant.setFullName(fullName.trim());
        tenant.setPhone(phone != null ? phone.trim() : "");
        tenant.setCccd(cccd != null ? cccd.trim() : "");
        tenant.setGender(gender != null && !gender.trim().isEmpty() ? gender.trim() : "Nam");
        tenant.setBirthDate(DateUtils.parse(birthDate));
        tenant.setAddress(address != null ? address.trim() : "");

        if (!tenantDAO.updateTenant(tenant)) {
            throw new ServiceException("Không thể cập nhật thông tin khách thuê trong cơ sở dữ liệu!");
        }
        
        // Handle room change if roomId is provided and not empty
        if (roomId != null && !roomId.trim().isEmpty()) {
            String newRoom = roomId.trim();
            String oldRoom = contractDAO.getRoomIdByTenantId(tenantId);
            
            if (oldRoom != null && !oldRoom.equals(newRoom)) {
                // Validate số lượng người trong phòng mới
                common.model.Room room = roomDAO.getRoomById(newRoom);
                if (room == null) {
                    throw new ServiceException("Mã phòng chuyển đến không tồn tại!");
                }
                int capacity = room.getCapacity();
                if (capacity > 0) {
                    int currentTenants = contractDAO.countTenantsInRoom(newRoom);
                    if (currentTenants >= capacity) {
                        throw new ServiceException("Phòng " + newRoom + " đã đạt giới hạn tối đa (" + capacity + " người)! Không thể chuyển đến.");
                    }
                }

                // Update room statuses
                roomDAO.updateRoomStatus(oldRoom, "Còn trống");
                roomDAO.updateRoomStatus(newRoom, "Đã thuê");
                
                // Update contract to new room
                contractDAO.updateContractRoom(tenantId, newRoom);
                ServerLogger.info("Đổi phòng cho khách thuê " + tenantId + " từ " + oldRoom + " sang " + newRoom);
            }
        }
        
        ServerLogger.info("Cập nhật khách thuê thành công: ID " + tenantId);
    }

    /** Xoá khách thuê */
    public void deleteTenant(int tenantId) throws ServiceException {
        if (tenantId <= 0)
            throw new ServiceException("ID khách thuê không hợp lệ!");
        
        // Lấy phone trước khi xóa
        String phone = "";
        List<Tenant> all = tenantDAO.getAllTenants();
        for (Tenant t : all) {
            if (t.getTenantId() == tenantId) { phone = t.getPhone(); break; }
        }

        // Lấy mã phòng trước khi xóa (vì xóa tenant sẽ xóa luôn contract theo CASCADE)
        String roomId = contractDAO.getRoomIdByTenantId(tenantId);
        
        if (!tenantDAO.deleteTenant(tenantId)) {
            throw new ServiceException("Không thể xóa khách thuê! (Có thể do còn ràng buộc hợp đồng)");
        }
        
        // Xóa tài khoản người dùng
        if (!phone.isEmpty()) {
            UserDAO userDAO = new UserDAO();
            userDAO.deleteUser(phone);
        }
        
        // Giải phóng phòng
        if (roomId != null && !roomId.isEmpty()) {
            roomDAO.updateRoomStatus(roomId, "Còn trống");
        }
        
        ServerLogger.info("Xóa khách thuê thành công: ID " + tenantId);
    }
}
