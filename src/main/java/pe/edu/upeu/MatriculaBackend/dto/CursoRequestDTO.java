package pe.edu.upeu.MatriculaBackend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CursoRequestDTO {
    @NotBlank(message = "El código es obligatorio")
    @Pattern(regexp = "^[A-Z]{2}\\d{3}$", message = "El código debe cumplir el patrón AA999")
    private String codigo;
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 150, message = "El nombre debe tener entre 3 y 150 caracteres")
    private String nombre;
    @NotNull
    @Min(value = 1, message = "Los créditos deben ser como mínimo 1")
    @Max(value = 6, message = "Los créditos no pueden superar 6")
    private Integer creditos;
    @NotNull
    @Min(value = 1, message = "El ciclo debe ser como mínimo 1")
    @Max(value = 10, message = "El ciclo no puede superar 10")
    private Integer ciclo;
    @NotNull
    @Min(value = 0, message = "Las vacantes no pueden ser negativas")
    private Integer vacantes;
    private Boolean estado;
    @NotNull
    @Positive(message = "La carrera debe tener un identificador válido")
    private Long carreraId;
}
