package pe.edu.upeu.MatriculaBackend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.MatriculaBackend.dto.EstudianteRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.EstudianteResponseDTO;
import pe.edu.upeu.MatriculaBackend.entity.Carrera;
import pe.edu.upeu.MatriculaBackend.entity.Estudiante;
import pe.edu.upeu.MatriculaBackend.exception.RecursoNoEncontradoException;
import pe.edu.upeu.MatriculaBackend.exception.ReglaNegocioException;
import pe.edu.upeu.MatriculaBackend.repository.CarreraRepository;
import pe.edu.upeu.MatriculaBackend.repository.EstudianteRepository;
import pe.edu.upeu.MatriculaBackend.repository.MatriculaRepository;
import pe.edu.upeu.MatriculaBackend.service.service.EstudianteService;

import java.util.List;

@Service
public class EstudianteServiceImpl implements EstudianteService {

    private static final Logger log = LoggerFactory.getLogger(EstudianteServiceImpl.class);

    private final EstudianteRepository estudianteRepository;
    private final CarreraRepository carreraRepository;
    private final MatriculaRepository matriculaRepository;

    public EstudianteServiceImpl(
            EstudianteRepository estudianteRepository,
            CarreraRepository carreraRepository,
            MatriculaRepository matriculaRepository
    ) {
        this.estudianteRepository = estudianteRepository;
        this.carreraRepository = carreraRepository;
        this.matriculaRepository = matriculaRepository;
    }

    @Override
    @Transactional
    public EstudianteResponseDTO create(EstudianteRequestDTO request) {
        validarUnicidad(request, null);

        Estudiante estudiante = new Estudiante();
        actualizarDatos(estudiante, request);

        Estudiante creado = estudianteRepository.save(estudiante);
        log.info("Estudiante creado con id={}", creado.getId());
        return convertirResponse(creado);
    }

    @Override
    @Transactional(readOnly = true)
    public EstudianteResponseDTO read(Long id) {
        return convertirResponse(obtenerEstudiante(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<EstudianteResponseDTO> readAll() {
        return estudianteRepository.findAll()
                .stream()
                .map(this::convertirResponse)
                .toList();
    }

    @Override
    @Transactional
    public EstudianteResponseDTO update(Long id, EstudianteRequestDTO request) {
        Estudiante estudiante = obtenerEstudiante(id);
        validarUnicidad(request, id);
        actualizarDatos(estudiante, request);

        Estudiante actualizado = estudianteRepository.save(estudiante);
        log.info("Estudiante id={} actualizado", id);
        return convertirResponse(actualizado);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Estudiante estudiante = obtenerEstudiante(id);

        if (matriculaRepository.existsByEstudianteId(id)) {
            throw new ReglaNegocioException(
                    "No se puede eliminar un estudiante con matrículas asociadas"
            );
        }

        estudianteRepository.delete(estudiante);
        log.info("Estudiante id={} eliminado", id);
    }

    private void validarUnicidad(EstudianteRequestDTO request, Long id) {
        boolean codigoDuplicado = id == null
                ? estudianteRepository.existsByCodigo(request.getCodigo())
                : estudianteRepository.existsByCodigoAndIdNot(request.getCodigo(), id);
        if (codigoDuplicado) {
            throw new ReglaNegocioException("Ya existe un estudiante con ese código");
        }

        boolean dniDuplicado = id == null
                ? estudianteRepository.existsByDni(request.getDni())
                : estudianteRepository.existsByDniAndIdNot(request.getDni(), id);
        if (dniDuplicado) {
            throw new ReglaNegocioException("Ya existe un estudiante con ese DNI");
        }
    }

    private void actualizarDatos(Estudiante estudiante, EstudianteRequestDTO request) {
        Carrera carrera = carreraRepository.findById(request.getCarreraId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Carrera no encontrada con id: " + request.getCarreraId()
                ));

        estudiante.setCodigo(request.getCodigo().trim());
        estudiante.setDni(request.getDni().trim());
        estudiante.setNombres(request.getNombres().trim());
        estudiante.setApellidos(request.getApellidos().trim());
        estudiante.setEmail(request.getEmail().trim());
        estudiante.setEstado(request.getEstado() == null || request.getEstado());
        estudiante.setCarrera(carrera);
    }

    private Estudiante obtenerEstudiante(Long id) {
        return estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Estudiante no encontrado con id: " + id
                ));
    }

    private EstudianteResponseDTO convertirResponse(Estudiante estudiante) {
        return new EstudianteResponseDTO(
                estudiante.getId(),
                estudiante.getCodigo(),
                estudiante.getDni(),
                estudiante.getNombres(),
                estudiante.getApellidos(),
                estudiante.getEmail(),
                estudiante.getEstado(),
                estudiante.getCarrera().getId(),
                estudiante.getCarrera().getNombre(),
                estudiante.getFechaCreacion(),
                estudiante.getFechaModificacion()
        );
    }
}
