package pe.edu.upeu.MatriculaBackend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MatriculaRequestDTO {
    @NotNull(message = "El estudiante es obligatorio")
    @Positive(message = "El estudiante debe tener un identificador válido")
    private Long estudianteId;
    @NotBlank(message = "El periodo es obligatorio")
    @Pattern(regexp = "^\\d{4}-[12]$", message = "El periodo debe tener el formato AAAA-1 o AAAA-2")
    private String periodo;
    @NotEmpty(message = "La matrícula debe incluir al menos un curso")
    private List<@Valid DetalleMatriculaRequestDTO> detalles;
}
