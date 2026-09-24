package pe.edu.upeu.MatriculaBackend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.MatriculaBackend.dto.CarreraRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.CarreraResponseDTO;
import pe.edu.upeu.MatriculaBackend.dto.CursoResponseDTO;
import pe.edu.upeu.MatriculaBackend.service.service.CarreraService;
import pe.edu.upeu.MatriculaBackend.service.service.CursoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carreras")
public class CarreraController {

    private final CarreraService carreraService;
    private final CursoService cursoService;

    public CarreraController(CarreraService carreraService, CursoService cursoService) {
        this.carreraService = carreraService;
        this.cursoService = cursoService;
    }

    @GetMapping
    public ResponseEntity<Iterable<CarreraResponseDTO>> findAll() {
        return ResponseEntity.ok(carreraService.readAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarreraResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(carreraService.read(id));
    }

    @PostMapping
    public ResponseEntity<CarreraResponseDTO> create(
            @Valid @RequestBody CarreraRequestDTO request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(carreraService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CarreraResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CarreraRequestDTO request
    ) {
        return ResponseEntity.ok(
                carreraService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        carreraService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/cursos")
    public ResponseEntity<List<CursoResponseDTO>> findCursosByCarrera(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(cursoService.cursosCarrera(id));
    }
}
