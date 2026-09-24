package pe.edu.upeu.MatriculaBackend.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DetalleMatriculaResponseDTO {
    private Long cursoId;
    private String cursoCodigo;
    private String cursoNombre;
    private Integer creditos;
    private BigDecimal costo;
}
