package pe.edu.upeu.MatriculaBackend.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CursoResponseDTO {
    private Long id;
    private String codigo;
    private String nombre;
    private Integer creditos;
    private Integer ciclo;
    private Integer vacantes;
    private Boolean estado;
    private Long carreraId;
    private String carreraNombre;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
}
