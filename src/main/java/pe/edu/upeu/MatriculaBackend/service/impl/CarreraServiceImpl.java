package pe.edu.upeu.MatriculaBackend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.MatriculaBackend.dto.CarreraRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.CarreraResponseDTO;
import pe.edu.upeu.MatriculaBackend.entity.Carrera;
import pe.edu.upeu.MatriculaBackend.exception.RecursoNoEncontradoException;
import pe.edu.upeu.MatriculaBackend.exception.ReglaNegocioException;
import pe.edu.upeu.MatriculaBackend.repository.CarreraRepository;
import pe.edu.upeu.MatriculaBackend.repository.CursoRepository;
import pe.edu.upeu.MatriculaBackend.repository.EstudianteRepository;
import pe.edu.upeu.MatriculaBackend.service.service.CarreraService;

import java.util.List;

@Service
public class CarreraServiceImpl implements CarreraService {

    private static final Logger log = LoggerFactory.getLogger(CarreraServiceImpl.class);

    private final CarreraRepository carreraRepository;
    private final CursoRepository cursoRepository;
    private final EstudianteRepository estudianteRepository;

    public CarreraServiceImpl(
            CarreraRepository carreraRepository,
            CursoRepository cursoRepository,
            EstudianteRepository estudianteRepository
    ) {
        this.carreraRepository = carreraRepository;
        this.cursoRepository = cursoRepository;
        this.estudianteRepository = estudianteRepository;
    }

    @Override
    @Transactional
    public CarreraResponseDTO create(CarreraRequestDTO request) {
        String nombre = request.getNombre().trim();
        if (carreraRepository.nombreExiste(nombre)) {
            throw new ReglaNegocioException("Ya existe una carrera con ese nombre");
        }

        Carrera carrera = new Carrera();
        carrera.setNombre(nombre);
        carrera.setDescripcion(request.getDescripcion());
        carrera.setEstado(request.getEstado() == null || request.getEstado());

        Carrera creada = carreraRepository.save(carrera);
        log.info("Carrera creada con id={}", creada.getId());
        return convertirResponse(creada);
    }

    @Override
    @Transactional(readOnly = true)
    public CarreraResponseDTO read(Long id) {
        return convertirResponse(obtenerCarrera(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<CarreraResponseDTO> readAll() {
        return carreraRepository.findAll()
                .stream()
                .map(this::convertirResponse)
                .toList();
    }

    @Override
    @Transactional
    public CarreraResponseDTO update(Long id, CarreraRequestDTO request) {
        Carrera carrera = obtenerCarrera(id);
        String nombre = request.getNombre().trim();

        if (carreraRepository.otroNombre(nombre, id)) {
            throw new ReglaNegocioException("Ya existe otra carrera con ese nombre");
        }

        carrera.setNombre(nombre);
        carrera.setDescripcion(request.getDescripcion());
        carrera.setEstado(request.getEstado() == null || request.getEstado());

        Carrera actualizada = carreraRepository.save(carrera);
        log.info("Carrera id={} actualizada", id);
        return convertirResponse(actualizada);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Carrera carrera = obtenerCarrera(id);

        if (cursoRepository.existsByCarreraId(id)
                || estudianteRepository.existsByCarreraId(id)) {
            throw new ReglaNegocioException(
                    "No se puede eliminar una carrera con cursos o estudiantes asociados"
            );
        }

        carreraRepository.delete(carrera);
        log.info("Carrera id={} eliminada", id);
    }

    private Carrera obtenerCarrera(Long id) {
        return carreraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Carrera no encontrada con id: " + id
                ));
    }

    private CarreraResponseDTO convertirResponse(Carrera carrera) {
        return new CarreraResponseDTO(
                carrera.getId(),
                carrera.getNombre(),
                carrera.getDescripcion(),
                carrera.getEstado(),
                carrera.getFechaCreacion(),
                carrera.getFechaModificacion()
        );
    }
}
