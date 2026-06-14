package server.controller;

import server.dao.ContractDAO;
import server.exception.ServiceException;
import server.protocol.Request;
import server.protocol.Response;
import server.service.ContractService;
import server.service.TenantService;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ContractController — nhận request, gọi service, trả response.
 * Không có date parsing, validation, hay business logic.
 */
public class ContractController {

    private final ContractService contractService = new ContractService();
    private final TenantService   tenantService   = new TenantService();

    /** GET_CONTRACTS */
    public Response handleGetContracts(Request req) {
        List<ContractDAO.ContractWithTenantName> contracts = contractService.getAllContracts();
        if (contracts.isEmpty()) return Response.success("EMPTY");

        List<Map<String, Object>> list = new ArrayList<>();
        for (ContractDAO.ContractWithTenantName item : contracts) {
            Date endDate = item.contract.getEndDate();
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("contractId", item.contract.getContractId());
            m.put("roomId",     item.contract.getRoomId());
            m.put("tenantName", item.tenantName != null ? item.tenantName : "");
            m.put("startDate",  item.contract.getStartDate() != null ? item.contract.getStartDate().toString() : "");
            m.put("endDate",    endDate != null ? endDate.toString() : "");
            m.put("deposit",    item.contract.getDeposit());
            m.put("status",     ContractService.calcStatus(endDate));
            list.add(m);
        }
        return Response.successList("OK", list);
    }

    /** ADD_CONTRACT */
    public Response handleAddContract(Request req) {
        try {
            int tenantId = tenantService.getOrCreateTenant(req.get("tenantName"));
            contractService.createContract(
                req.get("roomId"),
                tenantId,
                req.get("startDate"),
                req.get("endDate"),
                req.getDouble("deposit", 0.0)
            );
            return Response.success("Tạo hợp đồng thành công!");
        } catch (ServiceException e) {
            return Response.fail(e.getMessage());
        }
    }
}
