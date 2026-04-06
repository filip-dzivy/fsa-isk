package sk.posam.fsa.isk.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import sk.posam.fsa.isk.domain.shared.DomainException;
import sk.posam.fsa.isk.rest.dto.ErrorResponseDto;

import java.time.OffsetDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponseDto> handleDomainException(
            DomainException ex, WebRequest request) {
        return new ResponseEntity<>(
                createError(resolveCode(ex), ex.getMessage(), null, request),
                resolveStatus(ex));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDto> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, WebRequest request){
        return new ResponseEntity<>(
                createError("VALIDATION_ERROR", "Incorrect request format.",
                        List.of("Please check your request"), request),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(
            MethodArgumentNotValidException ex, WebRequest request) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + " " + e.getDefaultMessage())
                .toList();
        return new ResponseEntity<>(
                createError("VALIDATION_ERROR", "Invalid request body", details, request),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponseDto> handleThrowable(
            Throwable ex, WebRequest request) {
        return new ResponseEntity<>(
                createError("INTERNAL_ERROR", "Unexpected internal error",
                        List.of("Please contact support"), request),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {
        return new ResponseEntity<>(
                createError("VALIDATION_ERROR", ex.getMessage(), null, request),
                HttpStatus.BAD_REQUEST);
    }

    private ErrorResponseDto createError(String code, String message,
                                         List<String> details, WebRequest request) {
        return new ErrorResponseDto()
                .code(code)
                .message(message)
                .details(details == null ? List.of() : details)
                .timestamp(OffsetDateTime.now())
                .path(resolvePath(request));
    }

    private String resolvePath(WebRequest request) {
        if (request instanceof ServletWebRequest servletWebRequest) {
            return servletWebRequest.getRequest().getRequestURI();
        }
        return "";
    }

    private HttpStatus resolveStatus(DomainException ex) {
        return switch (ex.getType()) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case UNAUTHORIZED -> HttpStatus.UNAUTHORIZED;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
            case CONFLICT -> HttpStatus.CONFLICT;
            case VALIDATION -> HttpStatus.BAD_REQUEST;
        };
    }

    private String resolveCode(DomainException ex) {
        return switch (ex.getType()) {
            case NOT_FOUND -> "NOT_FOUND";
            case UNAUTHORIZED -> "UNAUTHORIZED";
            case FORBIDDEN -> "FORBIDDEN";
            case CONFLICT -> "CONFLICT";
            case VALIDATION -> "VALIDATION_ERROR";
        };
    }
}