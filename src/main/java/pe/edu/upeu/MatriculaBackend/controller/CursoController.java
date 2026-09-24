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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.MatriculaBackend.dto.CursoRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.CursoResponseDTO;
import pe.edu.upeu.MatriculaBackend.service.service.CursoService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cursos")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @GetMapping
    public ResponseEntity<Iterable<CursoResponseDTO>> findAll() {
        return ResponseEntity.ok(cursoService.readAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CursoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(cursoService.read(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<CursoResponseDTO>> buscar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Long carreraId,
            @RequestParam(required = false) Integer ciclo,
            @RequestParam(required = false) Boolean conVacantes,
            @RequestParam(defaultValue = "nombre") String orden,
            @RequestParam(defaultValue = "asc") String dir
    ) {
        return ResponseEntity.ok(
                cursoService.buscar(nombre, carreraId, ciclo, conVacantes, orden, dir)
        );
    }

    @PostMapping
    public ResponseEntity<CursoResponseDTO> create(
            @Valid @RequestBody CursoRequestDTO request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cursoService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CursoResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CursoRequestDTO request
    ) {
        return ResponseEntity.ok(
                cursoService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cursoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
