package com.bootcamp19.userservice.exception;

import com.bootcamp19.userservice.dto.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Menangkap exception dari SEMUA controller dan mengubahnya
 * menjadi Base Response yang konsisten.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // dilempar otomatis oleh @Valid kalau validasi request body gagal
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handleValidation(MethodArgumentNotValidException exception) {
        List<String> errorMessages = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .toList();
        return build(HttpStatus.BAD_REQUEST, "Validation Error!", errorMessages);
    }

    // contoh: GET /users/me dipanggil langsung tanpa lewat gateway (tidak ada X-User-Id)
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<BaseResponse<Object>> handleMissingHeader(MissingRequestHeaderException exception) {
        return build(HttpStatus.BAD_REQUEST, "Header " + exception.getHeaderName() + " wajib dikirim", null);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<BaseResponse<Object>> handleBadRequest(BadRequestException exception) {
        return build(HttpStatus.BAD_REQUEST, exception.getMessage(), null);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<BaseResponse<Object>> handleDataNotFound(DataNotFoundException exception) {
        return build(HttpStatus.NOT_FOUND, exception.getMessage(), null);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<BaseResponse<Object>> handleUnauthorized(UnauthorizedException exception) {
        return build(HttpStatus.UNAUTHORIZED, exception.getMessage(), null);
    }

    private ResponseEntity<BaseResponse<Object>> build(HttpStatus status, String message, Object data) {
        BaseResponse<Object> response = BaseResponse.builder()
                .status("F")
                .message(message)
                .data(data)
                .build();
        return ResponseEntity.status(status).body(response);
    }
}
