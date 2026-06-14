package server.protocol;

import com.google.gson.Gson;

import java.util.List;
import java.util.Map;

/**
 * Response — JSON response từ Server trả Client.
 * Dùng Gson để serialize/deserialize.
 *
 * Format: {"status":"SUCCESS","message":"...","data":{...},"list":[...]}
 */
public class Response {

    private static final Gson GSON = new Gson();

    public static final String SUCCESS = "SUCCESS";
    public static final String FAIL    = "FAIL";

    private String status;
    private String message;
    private Map<String, Object> data;
    private List<Map<String, Object>> list;

    // ---- Factory methods ----

    public static Response success(String message) {
        Response r = new Response();
        r.status  = SUCCESS;
        r.message = message;
        return r;
    }

    public static Response success(String message, Map<String, Object> data) {
        Response r = success(message);
        r.data = data;
        return r;
    }

    public static Response successList(String message, List<Map<String, Object>> list) {
        Response r = success(message);
        r.list = list;
        return r;
    }

    public static Response fail(String message) {
        Response r = new Response();
        r.status  = FAIL;
        r.message = message != null ? message : "Loi khong xac dinh";
        return r;
    }

    // ---- Getters ----

    public String  getStatus()  { return status; }
    public String  getMessage() { return message; }
    public Map<String, Object> getData() { return data; }
    public List<Map<String, Object>> getList() { return list; }
    public boolean isSuccess()  { return SUCCESS.equals(status); }

    public String getDataString(String key) {
        if (data == null) return "";
        Object v = data.get(key);
        return v != null ? v.toString() : "";
    }

    // ---- Serialization ----

    public String toJson() {
        return GSON.toJson(this);
    }

    public static Response fromJson(String json) {
        if (json == null || json.trim().isEmpty())
            return fail("Phan hoi rong tu may chu");
        try {
            Response resp = GSON.fromJson(json, Response.class);
            return resp != null ? resp : fail("Phan hoi khong hop le");
        } catch (Exception e) {
            server.util.ServerLogger.error("Loi parse Response JSON: " + e.getMessage());
            return fail("Loi parse phan hoi: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "Response{status='" + status + "', message='" + message + "'}";
    }
}
