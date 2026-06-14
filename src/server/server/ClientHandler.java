package server.server;

import server.controller.*;
import server.protocol.Request;
import server.protocol.Response;
import server.util.ServerLogger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * ClientHandler — Router: parse JSON Request → dispatch Controller → gửi JSON Response.
 *
 * Controllers là static (stateless) — khởi tạo 1 lần, dùng chung mọi connection.
 * Không chứa business logic — chỉ: đọc, định tuyến, gửi.
 */
public class ClientHandler implements Runnable {

    // Static: khởi tạo 1 lần cho toàn server, không cấp phát lại mỗi connection
    private static final AuthController         AUTH         = new AuthController();
    private static final RoomController         ROOM         = new RoomController();
    private static final TenantController       TENANT       = new TenantController();
    private static final ContractController     CONTRACT     = new ContractController();
    private static final InvoiceController      INVOICE      = new InvoiceController();
    private static final MaintenanceController  MAINTENANCE  = new MaintenanceController();
    private static final NotificationController NOTIFICATION = new NotificationController();
    private static final ServicePricingController SERVICE_PRICING = new ServicePricingController();
    private static final ServiceSubscriptionController SERVICE_SUBSCRIPTION = new ServiceSubscriptionController();
    private static final FeedbackController FEEDBACK = new FeedbackController();

    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        String ip = clientSocket.getInetAddress().getHostAddress();
        ServerLogger.info("Client connect: " + ip);

        try (
            BufferedReader in  = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
            PrintWriter    out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String line;
            while ((line = in.readLine()) != null) {
                Request  req  = Request.fromJson(line);
                ServerLogger.info("[" + ip + "] action=" + req.getAction());
                out.println(dispatch(req).toJson());
            }
        } catch (Exception e) {
            ServerLogger.error("Loi giao tiep [" + ip + "]: " + e.getMessage());
        } finally {
            try { clientSocket.close(); } catch (Exception ignored) {}
            ServerLogger.info("Client disconnect: " + ip);
        }
    }

    /** Định tuyến theo action — không chứa logic */
    private static Response dispatch(Request req) {
        String action = req.getAction();
        if (action == null || action.trim().isEmpty())
            return Response.fail("Action trong request bi trong!");

        switch (action) {
            // Auth
            case "LOGIN":           return AUTH.handleLogin(req);
            case "LOGOUT":          return AUTH.handleLogout(req);
            case "CHANGE_PASSWORD": return AUTH.handleChangePassword(req);
            case "UPDATE_PROFILE":  return AUTH.handleUpdateProfile(req);

            // Room
            case "GET_ROOMS":    return ROOM.handleGetRooms(req);
            case "ADD_ROOM":     return ROOM.handleAddRoom(req);
            case "UPDATE_ROOM":  return ROOM.handleUpdateRoom(req);
            case "DELETE_ROOM":  return ROOM.handleDeleteRoom(req);
            case "IMPORT_ROOMS": return ROOM.handleImportRooms(req);

            // Tenant
            case "GET_TENANTS":      return TENANT.handleGetTenants(req);
            case "ADD_TENANT":       return TENANT.handleAddTenant(req);
            case "UPDATE_TENANT":    return TENANT.handleUpdateTenant(req);
            case "DELETE_TENANT":    return TENANT.handleDeleteTenant(req);
            case "GET_ROOM_BY_USER": return TENANT.handleGetRoomByUser(req);

            // Contract
            case "GET_CONTRACTS": return CONTRACT.handleGetContracts(req);
            case "ADD_CONTRACT":  return CONTRACT.handleAddContract(req);

            // Invoice
            case "GET_INVOICES":          return INVOICE.handleGetInvoices(req);
            case "ADD_INVOICE":           return INVOICE.handleAddInvoice(req);
            case "UPDATE_INVOICE_STATUS": return INVOICE.handleUpdateInvoiceStatus(req);

            // Maintenance
            case "GET_MAINTENANCE":           return MAINTENANCE.handleGetMaintenance(req);
            case "ADD_MAINTENANCE":           return MAINTENANCE.handleAddMaintenance(req);
            case "UPDATE_MAINTENANCE_STATUS": return MAINTENANCE.handleUpdateMaintenanceStatus(req);

            // Notification
            case "GET_NOTIFICATIONS": return NOTIFICATION.handleGetNotifications(req);
            case "SEND_NOTIFICATION": return NOTIFICATION.handleSendNotification(req);

            // Service Pricing
            case "GET_SERVICES":      return SERVICE_PRICING.handleGetServices(req);
            case "UPDATE_SERVICE":    return SERVICE_PRICING.handleUpdateService(req);
            case "ADD_SERVICE":       return SERVICE_PRICING.handleAddService(req);
            case "EDIT_SINGLE_SERVICE": return SERVICE_PRICING.handleEditSingleService(req);
            case "DELETE_SERVICE":    return SERVICE_PRICING.handleDeleteService(req);

            // Service Subscriptions
            case "ADD_SUBSCRIPTION":  return SERVICE_SUBSCRIPTION.handleAddSubscription(req);
            case "GET_SUBSCRIPTIONS": return SERVICE_SUBSCRIPTION.handleGetSubscriptions(req);
            case "GET_ALL_SUBSCRIPTIONS": return SERVICE_SUBSCRIPTION.handleGetAllSubscriptions(req);
            case "UPDATE_SUBSCRIPTION_STATUS": return SERVICE_SUBSCRIPTION.handleUpdateSubscriptionStatus(req);

            // Feedbacks
            case "ADD_FEEDBACK":      return FEEDBACK.handleAddFeedback(req);
            case "GET_MY_FEEDBACKS":  return FEEDBACK.handleGetMyFeedbacks(req);
            case "GET_ALL_FEEDBACKS": return FEEDBACK.handleGetAllFeedbacks(req);
            case "UPDATE_FEEDBACK_STATUS": return FEEDBACK.handleUpdateFeedbackStatus(req);

            default: return Response.fail("Action khong ho tro: " + action);
        }
    }
}
