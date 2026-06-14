package server.protocol;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * Request — JSON request từ Client gửi Server.
 * Dùng Gson để serialize/deserialize.
 *
 * Format: {"action":"LOGIN","params":{"username":"admin","password":"123"}}
 */
public class Request {

    private static final Gson GSON = new Gson();

    private String action;
    private Map<String, String> params;

    public Request() {
        this.params = new HashMap<>();
    }

    public Request(String action) {
        this.action = action;
        this.params = new HashMap<>();
    }

    // ---- Getters / Setters ----

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public Map<String, String> getParams() { return params; }
    public void setParams(Map<String, String> params) { this.params = params; }

    /** Lấy param theo key, "" nếu không có */
    public String get(String key) {
        String v = params != null ? params.get(key) : null;
        return v != null ? v : "";
    }

    /** Lấy param dạng int */
    public int getInt(String key, int defaultVal) {
        try { return Integer.parseInt(get(key).trim()); }
        catch (NumberFormatException e) { return defaultVal; }
    }

    /** Lấy param dạng double */
    public double getDouble(String key, double defaultVal) {
        try { return Double.parseDouble(get(key).trim()); }
        catch (NumberFormatException e) { return defaultVal; }
    }

    // ---- Serialization ----

    public String toJson() {
        return GSON.toJson(this);
    }

    public static Request fromJson(String json) {
        if (json == null || json.trim().isEmpty()) return new Request();
        try {
            Request req = GSON.fromJson(json, Request.class);
            return req != null ? req : new Request();
        } catch (Exception e) {
            server.util.ServerLogger.error("Loi parse Request JSON: " + e.getMessage());
            return new Request();
        }
    }

    @Override
    public String toString() {
        return "Request{action='" + action + "', params=" + params + "}";
    }
}
