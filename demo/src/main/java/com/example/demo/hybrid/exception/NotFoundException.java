package com.example.demo.hybrid.exception;

/**
 * Ngoại lệ được ném khi không tìm thấy một đối tượng trong hệ thống.
 */
public class NotFoundException extends RuntimeException {

    /**
     * Constructor khởi tạo một ngoại lệ mới với thông báo chi tiết cụ thể.
     *
     * @param message Thông báo chi tiết về lỗi.
     * Thông báo này có thể được lấy lại sau này bằng phương thức {@link #getMessage()}.
     */
    public NotFoundException(String message) {
        super(message);
    }
}
