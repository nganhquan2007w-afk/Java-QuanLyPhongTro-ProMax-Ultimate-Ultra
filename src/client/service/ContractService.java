package client.service;

import client.socket.SocketClient;
import server.protocol.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ContractService — Client-side service quản lý hợp đồng.
 * Giao tiếp với server qua JSON protocol.
 */
public class ContractService {

    /**
     * Lấy danh sách hợp đồng.
     * Mỗi phần tử là String[7]: [contractId, roomId, tenantName, startDate, endDate, deposit, status]
     */
    public static List<String[]> getContracts() {
        List<String[]> list = new ArrayList<>();
        Response resp = SocketClient.send("GET_CONTRACTS");

        if (!resp.isSuccess() || "EMPTY".equals(resp.getMessage())) return list;

        List<Map<String, Object>> items = resp.getList();
        if (items == null) return list;

        for (Map<String, Object> item : items) {
            list.add(new String[]{
                str(item, "contractId"),
                str(item, "roomId"),
                str(item, "tenantName"),
                str(item, "startDate"),
                str(item, "endDate"),
                str(item, "deposit"),
                str(item, "status")
            });
        }
        return list;
    }

    /**
     * Tạo hợp đồng mới.
     * @return "SUCCESS" hoặc thông báo lỗi.
     */
    public static String addContract(String roomId, String tenantName,
                                      String startDate, String endDate, double deposit) {
        Response resp = SocketClient.send("ADD_CONTRACT",
            "roomId",     roomId     != null ? roomId     : "",
            "tenantName", tenantName != null ? tenantName : "",
            "startDate",  startDate  != null ? startDate  : "",
            "endDate",    endDate    != null ? endDate    : "",
            "deposit",    String.valueOf(deposit)
        );
        return resp.isSuccess() ? "SUCCESS" : resp.getMessage();
    }

    private static String str(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString() : "";
    }
}
