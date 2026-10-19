package pedidos360.admin.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejador global de excepciones para el microservicio administrador.
 * Garantiza respuestas de error estructuradas y claras cuando ocurren
 * fallos de validación en los DTOs o en la lógica de negocio (Criterio 8).
 */
@RestControllerAdvice
public class GlobalAdminExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalAdminExceptionHandler.class);

    /**
     * Captura errores de validación de Bean Validation (@Valid en los DTOs)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> erroresCampos = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            erroresCampos.put(error.getField(), error.getDefaultMessage());
        }

        log.warn("[VALIDATION-ERROR] Petición inválida detectada: {}", erroresCampos);

        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("codigo", HttpStatus.BAD_REQUEST.value());
        body.put("mensaje", "La petición contiene parámetros inválidos o campos requeridos vacíos");
        body.put("errores", erroresCampos);
        body.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Captura errores de JSON malformado o campos con tipos incompatibles
     */
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleMessageNotReadable(org.springframework.http.converter.HttpMessageNotReadableException ex) {
        log.warn("[JSON-PARSE-ERROR] Error al deserializar payload JSON: {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("codigo", HttpStatus.BAD_REQUEST.value());
        body.put("mensaje", "El formato del cuerpo de la petición (JSON) es inválido o contiene tipos incompatibles.");
        body.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Captura argumentos o estados ilegales
     */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, Object>> handleIllegalArguments(RuntimeException ex) {
        log.warn("[CLIENT-ERROR] Operación rechazada: {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("codigo", HttpStatus.BAD_REQUEST.value());
        body.put("mensaje", ex.getMessage());
        body.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Captura cualquier otra excepción no controlada
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("[SERVER-ERROR] Error interno en microservicio administrador RabbitMQ", ex);

        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("codigo", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("mensaje", "Error interno al interactuar con el broker RabbitMQ: " + ex.getMessage());
        body.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
