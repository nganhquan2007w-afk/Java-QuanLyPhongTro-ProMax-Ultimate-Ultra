package server.service;

import server.dao.ContractDAO;
import server.dao.RoomDAO;
import server.exception.ServiceException;
import common.model.Contract;
import server.util.DateUtils;
import server.util.ServerLogger;

import java.sql.Date;
import java.util.List;

/**
 * ContractService — Validate, business logic, gọi DAO.
 * Throw ServiceException khi lỗi.
 */
public class ContractService {

    private final ContractDAO contractDAO;
    private final RoomDAO     roomDAO;

    public ContractService() {
        this.contractDAO = new ContractDAO();
        this.roomDAO     = new RoomDAO();
    }

    public List<ContractDAO.ContractWithTenantName> getAllContracts() {
        return contractDAO.getAllContracts();
    }

    /** Lấy hợp đồng theo phòng */
    public List<ContractDAO.ContractWithTenantName> getContractsByRoom(String roomId) {
        return contractDAO.getContractsByRoom(roomId);
    }

    /**
     * Tạo hợp đồng mới.
     * Business rule: sau khi tạo contract → phòng chuyển "Đã thuê".
     * @throws ServiceException nếu validate lỗi hoặc DB thất bại
     */
    public int createContract(String roomId, int tenantId,
                               String startDate, String endDate, double deposit)
            throws ServiceException {
        if (roomId == null || roomId.trim().isEmpty())
            throw new ServiceException("Mã phòng không được để trống!");
        if (tenantId <= 0)
            throw new ServiceException("Khách thuê không hợp lệ!");
        if (deposit < 0)
            throw new ServiceException("Tiền đặt cọc không được âm!");

        Contract contract = new Contract();
        contract.setRoomId(roomId.trim());
        contract.setTenantId(tenantId);
        contract.setStartDate(DateUtils.parse(startDate));
        contract.setEndDate(DateUtils.parse(endDate));
        contract.setDeposit(deposit);

        int contractId = contractDAO.addContract(contract);
        if (contractId <= 0)
            throw new ServiceException("Không thể tạo hợp đồng trong cơ sở dữ liệu!");

        // Business rule: phòng → "Đã thuê" sau khi có hợp đồng
        if (!roomDAO.updateRoomStatus(roomId.trim(), "Đã thuê")) {
            ServerLogger.info("CANH BAO: Tao hop dong #" + contractId
                + " thanh cong nhung khong cap nhat duoc trang thai phong " + roomId);
        }
        ServerLogger.info("Tao hop dong thanh cong #" + contractId + " cho phong " + roomId);
        return contractId;
    }

    /**
     * Tính trạng thái hiệu lực của hợp đồng theo ngày kết thúc.
     */
    public static String calcStatus(Date endDate) {
        if (endDate == null) return "Còn hiệu lực";
        long diff = endDate.getTime() - System.currentTimeMillis();
        if (diff < 0) return "Hết hiệu lực";
        if (diff < 30L * 24 * 60 * 60 * 1000) return "Sắp hết hạn";
        return "Còn hiệu lực";
    }
}
