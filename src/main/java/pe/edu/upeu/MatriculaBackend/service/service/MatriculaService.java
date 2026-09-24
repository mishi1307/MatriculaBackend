package pe.edu.upeu.MatriculaBackend.service.service;

import java.util.List;
import pe.edu.upeu.MatriculaBackend.dto.MatriculaRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.MatriculaResponseDTO;

public interface MatriculaService {

    MatriculaResponseDTO registrar(MatriculaRequestDTO request);

    MatriculaResponseDTO buscar(Long id);

    List<MatriculaResponseDTO> listar();

    MatriculaResponseDTO anular(Long id);
}
