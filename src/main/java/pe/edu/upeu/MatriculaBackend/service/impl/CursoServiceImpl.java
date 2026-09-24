package pe.edu.upeu.MatriculaBackend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.MatriculaBackend.dto.CursoRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.CursoResponseDTO;
import pe.edu.upeu.MatriculaBackend.entity.Carrera;
import pe.edu.upeu.MatriculaBackend.entity.Curso;
import pe.edu.upeu.MatriculaBackend.exception.RecursoNoEncontradoException;
import pe.edu.upeu.MatriculaBackend.exception.ReglaNegocioException;
import pe.edu.upeu.MatriculaBackend.repository.CarreraRepository;
import pe.edu.upeu.MatriculaBackend.repository.CursoRepository;
import pe.edu.upeu.MatriculaBackend.repository.MatriculaRepository;
import pe.edu.upeu.MatriculaBackend.service.service.CursoService;

import java.util.List;
import java.util.Set;

@Service
public class CursoServiceImpl implements CursoService {

    private static final Logger log = LoggerFactory.getLogger(CursoServiceImpl.class);
    private static final Set<String> CAMPOS_ORDENABLES = Set.of("nombre", "creditos", "vacantes");

    private final CursoRepository cursoRepository;
    private final CarreraRepository carreraRepository;
    private final MatriculaRepository matriculaRepository;

    public CursoServiceImpl(
            CursoRepository cursoRepository,
            CarreraRepository carreraRepository,
            MatriculaRepository matriculaRepository
    ) {
        this.cursoRepository = cursoRepository;
        this.carreraRepository = carreraRepository;
        this.matriculaRepository = matriculaRepository;
    }

    @Override
    @Transactional
    public CursoResponseDTO create(CursoRequestDTO request) {
        String codigo = request.getCodigo().trim();
        if (cursoRepository.existsByCodigo(codigo)) {
            throw new ReglaNegocioException("Ya existe un curso con ese código: " + codigo);
        }

        Curso curso = new Curso();
        actualizarDatos(curso, request, codigo);

        Curso creado = cursoRepository.save(curso);
        log.info("Curso creado con id={}, código={}", creado.getId(), creado.getCodigo());
        return convertirResponse(creado);
    }

    @Override
    @Transactional(readOnly = true)
    public CursoResponseDTO read(Long id) {
        return convertirResponse(obtenerCurso(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Iterable<CursoResponseDTO> readAll() {
        return cursoRepository.findAll()
                .stream()
                .map(this::convertirResponse)
                .toList();
    }

    @Override
    @Transactional
    public CursoResponseDTO update(Long id, CursoRequestDTO request) {
        Curso curso = obtenerCurso(id);
        String codigo = request.getCodigo().trim();

        if (cursoRepository.existsByCodigoAndIdNot(codigo, id)) {
            throw new ReglaNegocioException("Ya existe otro curso con ese código: " + codigo);
        }

        actualizarDatos(curso, request, codigo);
        Curso actualizado = cursoRepository.save(curso);
        log.info("Curso id={} actualizado", id);
        return convertirResponse(actualizado);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Curso curso = obtenerCurso(id);
        if (matriculaRepository.existsByDetallesCursoId(id)) {
            throw new ReglaNegocioException("No se puede eliminar un curso con matrículas asociadas");
        }

        cursoRepository.delete(curso);
        log.info("Curso id={} eliminado", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> cursosCarrera(Long carreraId) {
        obtenerCarrera(carreraId);
        return cursoRepository.findByCarreraId(carreraId)
                .stream()
                .map(this::convertirResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponseDTO> buscar(
            String nombre,
            Long carreraId,
            Integer ciclo,
            Boolean conVacantes,
            String orden,
            String direccion
    ) {
        String campoOrden = orden == null ? "nombre" : orden;
        String direccionOrden = direccion == null ? "asc" : direccion.toLowerCase();

        if (!CAMPOS_ORDENABLES.contains(campoOrden)) {
            throw new ReglaNegocioException("Campo de orden inválido: " + campoOrden);
        }
        if (!Set.of("asc", "desc").contains(direccionOrden)) {
            throw new ReglaNegocioException("La dirección debe ser asc o desc");
        }

        Specification<Curso> filtros = (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        if (nombre != null && !nombre.isBlank()) {
            String nombreNormalizado = "%" + nombre.trim().toLowerCase() + "%";
            filtros = filtros.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("nombre")), nombreNormalizado));
        }
        if (carreraId != null) {
            filtros = filtros.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("carrera").get("id"), carreraId));
        }
        if (ciclo != null) {
            filtros = filtros.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("ciclo"), ciclo));
        }
        if (Boolean.TRUE.equals(conVacantes)) {
            filtros = filtros.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThan(root.get("vacantes"), 0));
        }

        Sort.Direction sortDirection = "desc".equals(direccionOrden)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return cursoRepository.findAll(filtros, Sort.by(sortDirection, campoOrden))
                .stream()
                .map(this::convertirResponse)
                .toList();
    }

    private void actualizarDatos(Curso curso, CursoRequestDTO request, String codigo) {
        Carrera carrera = obtenerCarrera(request.getCarreraId());
        curso.setCodigo(codigo);
        curso.setNombre(request.getNombre().trim());
        curso.setCreditos(request.getCreditos());
        curso.setCiclo(request.getCiclo());
        curso.setVacantes(request.getVacantes());
        curso.setEstado(request.getEstado() == null || request.getEstado());
        curso.setCarrera(carrera);
    }

    private Curso obtenerCurso(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Curso no encontrado con id: " + id
                ));
    }

    private Carrera obtenerCarrera(Long id) {
        return carreraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Carrera no encontrada con id: " + id
                ));
    }

    private CursoResponseDTO convertirResponse(Curso curso) {
        return new CursoResponseDTO(
                curso.getId(),
                curso.getCodigo(),
                curso.getNombre(),
                curso.getCreditos(),
                curso.getCiclo(),
                curso.getVacantes(),
                curso.getEstado(),
                curso.getCarrera().getId(),
                curso.getCarrera().getNombre(),
                curso.getFechaCreacion(),
                curso.getFechaModificacion()
        );
    }
}
