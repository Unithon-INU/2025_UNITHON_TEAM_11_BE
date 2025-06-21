package Uniton.Fring.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponseEntity> handleCustomException(CustomException ex) {
        return ErrorResponseEntity.toResponseEntity(ex.getErrorCode());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseEntity> handleAuthenticationException(AuthenticationException ex) {
        ErrorCode errorCode = ErrorCode.JWT_ENTRY_POINT;
        return ErrorResponseEntity.toResponseEntity(errorCode);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseEntity> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ErrorCode errorCode = ErrorCode.VALIDATION_FAILED;
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        return ErrorResponseEntity.toResponseEntity(errorCode,"요청한 값이 올바르지 않습니다.", errors);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponseEntity> handleMissingPart(MissingServletRequestPartException ex) {
        ErrorCode errorCode = ErrorCode.MISSING_PART;

        String message = "요청에 필요한 부분이 없습니다: " + ex.getRequestPartName();

        return ErrorResponseEntity.toResponseEntity(errorCode, message, null);
    }
}
