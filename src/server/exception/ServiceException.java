package server.exception;

/**
 * ServiceException — Ném khi có lỗi nghiệp vụ (validation, business rule).
 * Controller bắt exception này và trả về Response.fail(message).
 *
 * Giúp controller thuần túy: chỉ nhận request, gọi service, trả response.
 */
public class ServiceException extends Exception {

    public ServiceException(String message) {
        super(message);
    }
}
