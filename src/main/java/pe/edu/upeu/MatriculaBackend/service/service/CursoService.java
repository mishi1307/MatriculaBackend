package pe.edu.upeu.MatriculaBackend.service.service;

import java.util.List;
import pe.edu.upeu.MatriculaBackend.dto.CursoRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.CursoResponseDTO;
import pe.edu.upeu.MatriculaBackend.service.generic.CrudService;

public interface CursoService
        extends CrudService<CursoRequestDTO, CursoResponseDTO, Long> {

    List<CursoResponseDTO> cursosCarrera(Long carreraId);

    List<CursoResponseDTO> buscar(
            String nombre,
            Long carreraId,
            Integer ciclo,
            Boolean conVacantes,
            String orden,
            String direccion
    );
}
