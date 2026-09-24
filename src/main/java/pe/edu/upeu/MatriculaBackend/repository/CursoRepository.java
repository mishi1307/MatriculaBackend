package pe.edu.upeu.MatriculaBackend.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.MatriculaBackend.entity.Curso;

import java.util.List;
import java.util.Optional;

public interface CursoRepository
        extends JpaRepository<Curso, Long>, JpaSpecificationExecutor<Curso> {

    boolean existsByCodigo(String codigo);

    boolean existsByCodigoAndIdNot(String codigo, Long id);

    boolean existsByCarreraId(Long carreraId);

    List<Curso> findByCarreraId(Long carreraId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Curso c where c.id = :id")
    Optional<Curso> findLockedById(@Param("id") Long id);
}
