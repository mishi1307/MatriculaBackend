package pe.edu.upeu.MatriculaBackend.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.upeu.MatriculaBackend.dto.MatriculaRequestDTO;
import pe.edu.upeu.MatriculaBackend.dto.MatriculaResponseDTO;
import pe.edu.upeu.MatriculaBackend.service.service.MatriculaService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/matriculas")
public class MatriculaController {

    private final MatriculaService matriculaService;

    public MatriculaController(MatriculaService matriculaService) {
        this.matriculaService = matriculaService;
    }

    @GetMapping
    public ResponseEntity<List<MatriculaResponseDTO>> findAll() {
        return ResponseEntity.ok(matriculaService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatriculaResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(matriculaService.buscar(id));
    }

    @PostMapping
    public ResponseEntity<MatriculaResponseDTO> create(
            @Valid @RequestBody MatriculaRequestDTO request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(matriculaService.registrar(request));
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<MatriculaResponseDTO> anular(@PathVariable Long id) {
        return ResponseEntity.ok(matriculaService.anular(id));
    }
}
