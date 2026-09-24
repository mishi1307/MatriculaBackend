package pe.edu.upeu.MatriculaBackend.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import pe.edu.upeu.MatriculaBackend.exception.dto.ErrorResponseDTO;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> handleNoEncontrado(
            RecursoNoEncontradoException ex,
            HttpServletRequest request
    ) {
        return construir(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponseDTO> handleReglaNegocio(
            ReglaNegocioException ex,
            HttpServletRequest request
    ) {
        return construir(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidacion(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        Map<String, String> errores = new HashMap<>();

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errores.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return construir(
                HttpStatus.BAD_REQUEST,
                "Existen errores de validación",
                request,
                errores
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleTipoInvalido(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {
        String mensaje = "El parámetro '" + ex.getName() + "' tiene un formato inválido";
        return construir(HttpStatus.BAD_REQUEST, mensaje, request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleErrorInterno(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Error no controlado en la solicitud {}", request.getRequestURI(), ex);
        return construir(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor",
                request,
                null
        );
    }

    private ResponseEntity<ErrorResponseDTO> construir(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            Map<String, String> errores
    ) {
        ErrorResponseDTO body = new ErrorResponseDTO(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI(),
                errores
        );

        return ResponseEntity.status(status).body(body);
    }
}
