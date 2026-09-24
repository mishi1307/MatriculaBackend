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
import pe.edu.upeu.MatriculaBackend.dto.EstudianteRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.EstudianteResponseDTO;
import pe.edu.upeu.MatriculaBackend.service.service.EstudianteService;

@RestController
@RequestMapping("/api/v1/estudiantes")
public class EstudianteController {

    private final EstudianteService estudianteService;

    public EstudianteController(EstudianteService estudianteService) {
        this.estudianteService = estudianteService;
    }

    @GetMapping
    public ResponseEntity<Iterable<EstudianteResponseDTO>> findAll() {
        return ResponseEntity.ok(estudianteService.readAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstudianteResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(estudianteService.read(id));
    }

    @PostMapping
    public ResponseEntity<EstudianteResponseDTO> create(
            @Valid @RequestBody EstudianteRequestDTO request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(estudianteService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstudianteResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody EstudianteRequestDTO request
    ) {
        return ResponseEntity.ok(
                estudianteService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        estudianteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
