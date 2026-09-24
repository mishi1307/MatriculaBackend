package pe.edu.upeu.MatriculaBackend.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.MatriculaBackend.entity.Estudiante;

import java.util.Optional;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    boolean existsByDni(String dni);

    boolean existsByDniAndIdNot(String dni, Long id);

    boolean existsByCarreraId(Long carreraId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Estudiante e where e.id = :id")
    Optional<Estudiante> findLockedById(@Param("id") Long id);
}
