package pe.edu.upeu.MatriculaBackend.dto;

import jakarta.validation.constraints.Email;
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
public class EstudianteRequestDTO {
    @NotBlank(message = "El código es obligatorio")
    @Pattern(regexp = "^\\d{9}$", message = "El código debe contener 9 dígitos")
    private String codigo;
    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^\\d{8}$", message = "El DNI debe contener 8 dígitos")
    private String dni;
    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 100)
    private String nombres;
    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100)
    private String apellidos;
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico no tiene un formato válido")
    @Size(max = 150)
    private String email;
    private Boolean estado;
    @NotNull
    @Positive(message = "La carrera debe tener un identificador válido")
    private Long carreraId;
}
