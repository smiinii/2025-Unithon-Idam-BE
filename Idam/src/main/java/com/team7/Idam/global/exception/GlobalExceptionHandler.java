package com.team7.Idam.global.exception;

import com.team7.Idam.global.util.SlackNotifier;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RequiredArgsConstructor // ✅ SlackNotifier 주입을 위한 어노테이션
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final SlackNotifier slackNotifier; // ✅ 슬랙 노티파이어 주입

    // IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, HttpServletRequest request) {
        String uri = request.getRequestURI();
        slackNotifier.sendMessage("❗ IllegalArgumentException 발생\n경로: " + uri + "\n메시지: " + ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // Validation 실패 처리 (@Valid 실패)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String uri = request.getRequestURI();
        String errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();

        slackNotifier.sendMessage("⚠️ Validation 실패\n경로: " + uri + "\n오류: " + errorMessage);

        ErrorResponse errorResponse = new ErrorResponse(
                errorMessage,
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    // 그 외 모든 RuntimeException 처리
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        String uri = request.getRequestURI();
        slackNotifier.sendMessage("🚨 RuntimeException 발생\n경로: " + uri + "\n예외: " + ex.getClass().getSimpleName() +
                "\n메시지: " + ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
