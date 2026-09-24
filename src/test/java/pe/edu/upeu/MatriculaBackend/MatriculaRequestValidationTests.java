package pe.edu.upeu.MatriculaBackend;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pe.edu.upeu.MatriculaBackend.dto.CursoRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.MatriculaRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.DetalleMatriculaRequestDTO;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;
class MatriculaRequestValidationTests {
    private static Validator validator;
    private static ValidatorFactory factory;
    @BeforeAll static void setUp() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    @AfterAll static void tearDown() {
        factory.close();
    }
    @Test void rejectsCourseFieldsOutsideContract() {
        var request = new CursoRequestDTO("is-40", "X", 0, 11, -1, true, 99L);
        var fields = validator.validate(request).stream().map(v -> v.getPropertyPath().toString()).toList();
        assertTrue(fields.containsAll(List.of("codigo", "nombre", "creditos", "ciclo", "vacantes")));
    }
    @Test void rejectsInvalidEnrollmentPeriodAndEmptyDetails() {
        var request = new MatriculaRequestDTO(1L, "2026-3", List.of());
        var fields = validator.validate(request).stream().map(v -> v.getPropertyPath().toString()).toList();
        assertTrue(fields.containsAll(List.of("periodo", "detalles")));
    }
}
