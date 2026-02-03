package pl.smyk.authservice.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private T data;
    private String message;
    private int status;

    public static <T> ApiResponse<T> of(String message, int status) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setMessage(message);
        response.setStatus(status);
        return response;
    }

    public static <T> ApiResponse<T> of(String message, int status, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setMessage(message);
        response.setStatus(status);
        response.setData(data);
        return response;
    }
}