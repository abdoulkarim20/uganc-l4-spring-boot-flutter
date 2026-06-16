package gn.uganc.gestiongarage.shared;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Object errors;
    private int status;
    private String path;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder().success(true).message("OK").data(data).status(200).build();
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder().success(true).message(message).data(data).status(200).build();
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        return ApiResponse.<T>builder().success(true).message(message).data(data).status(201).build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder().success(true).message(message).data(data).status(200).build();
    }

    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder().success(true).message(message).status(200).build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder().success(false).message(message).status(400).build();
    }

    public static <T> ApiResponse<T> error(String message, T data) {
        return ApiResponse.<T>builder().success(false).message(message).data(data).status(400).build();
    }

    public static <T> ApiResponse<T> error(int status, String message) {
        return ApiResponse.<T>builder().success(false).message(message).status(status).build();
    }

    public static <T> ApiResponse<T> error(int status, String message, Object errors) {
        return ApiResponse.<T>builder().success(false).message(message).errors(errors).status(status).build();
    }
}
