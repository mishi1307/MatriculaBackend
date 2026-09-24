package pe.edu.upeu.MatriculaBackend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.edu.upeu.MatriculaBackend.enums.EstadoMatricula;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MatriculaResponseDTO {
    private Long id;
    private LocalDateTime fecha;
    private String periodo;
    private Long estudianteId;
    private String estudianteCodigo;
    private String estudianteNombre;
    private EstadoMatricula estado;
    private Integer totalCreditos;
    private BigDecimal montoTotal;
    private List<DetalleMatriculaResponseDTO> detalles;
}
