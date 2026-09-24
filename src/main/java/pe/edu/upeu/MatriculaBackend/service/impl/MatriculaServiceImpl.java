package pe.edu.upeu.MatriculaBackend.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.upeu.MatriculaBackend.dto.DetalleMatriculaRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.DetalleMatriculaResponseDTO;
import pe.edu.upeu.MatriculaBackend.dto.MatriculaRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.MatriculaResponseDTO;
import pe.edu.upeu.MatriculaBackend.entity.Curso;
import pe.edu.upeu.MatriculaBackend.entity.DetalleMatricula;
import pe.edu.upeu.MatriculaBackend.entity.Estudiante;
import pe.edu.upeu.MatriculaBackend.entity.Matricula;
import pe.edu.upeu.MatriculaBackend.enums.EstadoMatricula;
import pe.edu.upeu.MatriculaBackend.exception.RecursoNoEncontradoException;
import pe.edu.upeu.MatriculaBackend.exception.ReglaNegocioException;
import pe.edu.upeu.MatriculaBackend.repository.CursoRepository;
import pe.edu.upeu.MatriculaBackend.repository.EstudianteRepository;
import pe.edu.upeu.MatriculaBackend.repository.MatriculaRepository;
import pe.edu.upeu.MatriculaBackend.service.service.MatriculaService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MatriculaServiceImpl implements MatriculaService {

    private static final Logger log = LoggerFactory.getLogger(MatriculaServiceImpl.class);

    private final EstudianteRepository estudianteRepository;
    private final CursoRepository cursoRepository;
    private final MatriculaRepository matriculaRepository;
    private final BigDecimal costoCredito;

    public MatriculaServiceImpl(
            EstudianteRepository estudianteRepository,
            CursoRepository cursoRepository,
            MatriculaRepository matriculaRepository,
            @Value("${matricula.costo-credito:120.00}") BigDecimal costoCredito
    ) {
        this.estudianteRepository = estudianteRepository;
        this.cursoRepository = cursoRepository;
        this.matriculaRepository = matriculaRepository;
        this.costoCredito = costoCredito;
    }

    @Override
    @Transactional
    public MatriculaResponseDTO registrar(MatriculaRequestDTO request) {
        Estudiante estudiante = obtenerEstudianteBloqueado(request.getEstudianteId());
        validarEstudianteActivo(estudiante);
        validarMatriculaUnica(estudiante.getId(), request.getPeriodo());

        List<Long> cursoIds = request.getDetalles()
                .stream()
                .map(DetalleMatriculaRequestDTO::getCursoId)
                .sorted()
                .toList();
        validarCursosDuplicados(cursoIds);

        Matricula matricula = new Matricula();
        matricula.setFecha(LocalDateTime.now());
        matricula.setPeriodo(request.getPeriodo());
        matricula.setEstudiante(estudiante);
        matricula.setEstado(EstadoMatricula.REGISTRADA);

        int totalCreditos = 0;
        BigDecimal montoTotal = BigDecimal.ZERO;

        for (Long cursoId : cursoIds) {
            Curso curso = obtenerCursoBloqueado(cursoId);
            validarCursoParaEstudiante(curso, estudiante);

            int creditos = curso.getCreditos();
            BigDecimal costo = costoCredito
                    .multiply(BigDecimal.valueOf(creditos))
                    .setScale(2, RoundingMode.HALF_UP);

            DetalleMatricula detalle = new DetalleMatricula();
            detalle.setCurso(curso);
            detalle.setCreditos(creditos);
            detalle.setCosto(costo);
            matricula.agregarDetalle(detalle);

            totalCreditos += creditos;
            montoTotal = montoTotal.add(costo);
        }

        if (totalCreditos > 20) {
            throw new ReglaNegocioException(
                    "RN-04: la matrícula no puede superar 20 créditos"
            );
        }

        matricula.setTotalCreditos(totalCreditos);
        matricula.setMontoTotal(montoTotal.setScale(2, RoundingMode.HALF_UP));

        for (DetalleMatricula detalle : matricula.getDetalles()) {
            Curso curso = detalle.getCurso();
            curso.setVacantes(curso.getVacantes() - 1);
        }

        Matricula guardada = matriculaRepository.save(matricula);
        log.info("Matrícula id={} creada para estudiante={}", guardada.getId(), estudiante.getId());
        return convertirResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public MatriculaResponseDTO buscar(Long id) {
        Matricula matricula = matriculaRepository.findOneWithDetails(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Matrícula no encontrada con id: " + id
                ));
        return convertirResponse(matricula);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatriculaResponseDTO> listar() {
        return matriculaRepository.findAllWithDetails()
                .stream()
                .map(this::convertirResponse)
                .toList();
    }

    @Override
    @Transactional
    public MatriculaResponseDTO anular(Long id) {
        Matricula matricula = matriculaRepository.findLockedById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Matrícula no encontrada con id: " + id
                ));

        if (matricula.getEstado() != EstadoMatricula.REGISTRADA) {
            throw new ReglaNegocioException("La matrícula ya fue anulada");
        }

        List<Long> cursoIds = matricula.getDetalles()
                .stream()
                .map(detalle -> detalle.getCurso().getId())
                .sorted()
                .toList();

        for (Long cursoId : cursoIds) {
            Curso curso = obtenerCursoBloqueado(cursoId);
            curso.setVacantes(curso.getVacantes() + 1);
        }

        matricula.setEstado(EstadoMatricula.ANULADA);
        log.info("Matrícula id={} anulada", id);
        return convertirResponse(matricula);
    }

    private Estudiante obtenerEstudianteBloqueado(Long id) {
        return estudianteRepository.findLockedById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Estudiante no encontrado con id: " + id
                ));
    }

    private Curso obtenerCursoBloqueado(Long id) {
        return cursoRepository.findLockedById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Curso no encontrado con id: " + id
                ));
    }

    private void validarEstudianteActivo(Estudiante estudiante) {
        if (!Boolean.TRUE.equals(estudiante.getEstado())) {
            throw new ReglaNegocioException("RN-01: el estudiante debe estar activo");
        }
    }

    private void validarMatriculaUnica(Long estudianteId, String periodo) {
        boolean yaMatriculado = matriculaRepository.existsByEstudianteIdAndPeriodoAndEstado(
                estudianteId,
                periodo,
                EstadoMatricula.REGISTRADA
        );
        if (yaMatriculado) {
            throw new ReglaNegocioException(
                    "RN-03: el estudiante ya tiene matrícula registrada en el periodo"
            );
        }
    }

    private void validarCursosDuplicados(List<Long> cursoIds) {
        Set<Long> idsUnicos = new HashSet<>(cursoIds);
        if (idsUnicos.size() != cursoIds.size()) {
            throw new ReglaNegocioException("No se puede incluir el mismo curso más de una vez");
        }
    }

    private void validarCursoParaEstudiante(Curso curso, Estudiante estudiante) {
        if (!Boolean.TRUE.equals(curso.getEstado())
                || !curso.getCarrera().getId().equals(estudiante.getCarrera().getId())) {
            throw new ReglaNegocioException(
                    "RN-01: el curso debe estar activo y pertenecer a la carrera del estudiante"
            );
        }
        if (curso.getVacantes() <= 0) {
            throw new ReglaNegocioException(
                    "RN-02: el curso no tiene vacantes: " + curso.getCodigo()
            );
        }
    }

    private MatriculaResponseDTO convertirResponse(Matricula matricula) {
        List<DetalleMatriculaResponseDTO> detalles = matricula.getDetalles()
                .stream()
                .map(detalle -> new DetalleMatriculaResponseDTO(
                        detalle.getCurso().getId(),
                        detalle.getCurso().getCodigo(),
                        detalle.getCurso().getNombre(),
                        detalle.getCreditos(),
                        detalle.getCosto()
                ))
                .toList();

        String nombreEstudiante = matricula.getEstudiante().getNombres()
                + " "
                + matricula.getEstudiante().getApellidos();

        return new MatriculaResponseDTO(
                matricula.getId(),
                matricula.getFecha(),
                matricula.getPeriodo(),
                matricula.getEstudiante().getId(),
                matricula.getEstudiante().getCodigo(),
                nombreEstudiante,
                matricula.getEstado(),
                matricula.getTotalCreditos(),
                matricula.getMontoTotal(),
                detalles
        );
    }
}
