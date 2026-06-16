package gn.uganc.gestiongarage.exception;


import gn.uganc.gestiongarage.shared.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //    exceptions
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex, HttpServletRequest req) {
        log.warn("404: {}", ex.getMessage());
        var resp = ApiResponse.<Void>error(404, ex.getMessage());
        resp.setPath(req.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resp);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusiness(BusinessException ex, HttpServletRequest req) {
        log.warn("400: {}", ex.getMessage());
        var resp = ApiResponse.<Void>error(400, ex.getMessage());
        resp.setPath(req.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }

    @ExceptionHandler(WorkflowException.class)
    public ResponseEntity<ApiResponse<Void>> handleWorkflow(WorkflowException ex, HttpServletRequest req) {
        log.warn("409: {}", ex.getMessage());
        var resp = ApiResponse.<Void>error(409, ex.getMessage());
        resp.setPath(req.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(resp);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        var resp = ApiResponse.<Void>error(422, "Erreur de validation", errors);
        resp.setPath(req.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(resp);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArg(IllegalArgumentException ex, HttpServletRequest req) {
        var resp = ApiResponse.<Void>error(400, ex.getMessage());
        resp.setPath(req.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resp);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex, HttpServletRequest req) {
        log.warn("401: {}", ex.getMessage());
        var resp = ApiResponse.<Void>error(401, "Identifiants incorrects.");
        resp.setPath(req.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resp);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex, HttpServletRequest req) {
        log.error("500:", ex);
        var resp = ApiResponse.<Void>error(500, "Erreur interne du serveur");
        resp.setPath(req.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
    }
}
