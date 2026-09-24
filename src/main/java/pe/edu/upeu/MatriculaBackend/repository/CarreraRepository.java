package pe.edu.upeu.MatriculaBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.upeu.MatriculaBackend.entity.Carrera;

public interface CarreraRepository extends JpaRepository<Carrera, Long> {

    @Query("select count(c) > 0 from Carrera c "
            + "where lower(trim(c.nombre)) = lower(trim(:nombre))")
    boolean nombreExiste(@Param("nombre") String nombre);

    @Query("select count(c) > 0 from Carrera c "
            + "where lower(trim(c.nombre)) = lower(trim(:nombre)) and c.id <> :id")
    boolean otroNombre(
            @Param("nombre") String nombre,
            @Param("id") Long id
    );
}
