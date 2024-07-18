package com.example.demo.hybrid.until.constant;

/**
 * Định nghĩa các hằng số thông báo log.
 * Các thông điệp này được sử dụng để ghi log cho các ngoại lệ khác nhau trong ứng dụng.
 */
public class LogMessageConstants {

    // Thông điệp log cho ngoại lệ validation
    public static final String VALIDATION_EXCEPTION = "Validation error for field '{}': {}";

    // Thông điệp log cho ngoại lệ runtime
    public static final String RUNTIME_EXCEPTION = "Runtime exception: {}";

    // Thông điệp log cho ngoại lệ NotNull
    public static final String NOT_NULL_EXCEPTION = "NotNull exception: {}";

    // Thông điệp log cho ngoại lệ NotFound
    public static final String NOT_FOUND_EXCEPTION = "NotFound exception: {}";

}
