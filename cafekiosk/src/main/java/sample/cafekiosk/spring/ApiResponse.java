package sample.cafekiosk.spring;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
@Getter      // getter 기반으로 json 생성함.
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse<T> {
    private int code;
    private HttpStatus status;
    private String message;
    private T data;

    public static <T> ApiResponse<T> of(HttpStatus status, String message, T data) {
        return new ApiResponse<>(status.value(), status, message, data);
    }

    public static <T> ApiResponse<T> of(HttpStatus status, String message) {
        return of(status, message, null);
    }
    public static <T> ApiResponse<T> ok(T data) {
        return of(HttpStatus.OK, "success", data);
    }
}
