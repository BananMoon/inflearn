package sample.cafekiosk.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@Slf4j
@RestControllerAdvice
public class ApiControllerAdvice {

    // validation 관련 예외 발생시 BindException.class이 발생함.
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Object> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder errorMessage = new StringBuilder();
        for (ObjectError error : e.getBindingResult().getAllErrors()) {
            errorMessage.append(error.getDefaultMessage()).append("/");
        }
        log.error("Error Message : {}", errorMessage);
        return ApiResponse.of(HttpStatus.BAD_REQUEST, errorMessage.toString());
    }
}
