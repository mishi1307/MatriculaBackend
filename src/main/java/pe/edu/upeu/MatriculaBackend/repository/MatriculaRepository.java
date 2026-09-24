package pe.edu.upeu.MatriculaBackend.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.MatriculaBackend.entity.Matricula;
import pe.edu.upeu.MatriculaBackend.enums.EstadoMatricula;

import java.util.List;
import java.util.Optional;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    boolean existsByEstudianteIdAndPeriodoAndEstado(
            Long estudianteId,
            String periodo,
            EstadoMatricula estado
    );

    boolean existsByEstudianteIdAndEstado(Long estudianteId, EstadoMatricula estado);

    boolean existsByEstudianteId(Long estudianteId);

    boolean existsByDetallesCursoId(Long cursoId);

    @Query("select distinct m from Matricula m "
            + "join fetch m.estudiante "
            + "left join fetch m.detalles d "
            + "left join fetch d.curso")
    List<Matricula> findAllWithDetails();

    @Query("select distinct m from Matricula m "
            + "join fetch m.estudiante "
            + "left join fetch m.detalles d "
            + "left join fetch d.curso "
            + "where m.id = :id")
    Optional<Matricula> findOneWithDetails(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Matricula m where m.id = :id")
    Optional<Matricula> findLockedById(@Param("id") Long id);
}
