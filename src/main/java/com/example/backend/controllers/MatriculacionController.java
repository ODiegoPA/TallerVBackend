package com.example.backend.controllers;

import com.example.backend.dto.matriculacion.MatriculacionRequestDto;
import com.example.backend.dto.matriculacion.MatriculacionResponseDto;
import com.example.backend.dto.nota.ValorRequestDto;
import com.example.backend.service.MatriculacionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/matriculacion")
public class MatriculacionController {

    private final MatriculacionService matriculacionService;
    public MatriculacionController(MatriculacionService matriculacionService) {
        this.matriculacionService = matriculacionService;
    }

    @GetMapping("/{id}")
    public MatriculacionResponseDto get(@PathVariable Long id) {
        return matriculacionService.getMatriculacion(id);
    }

    @GetMapping("/semestre-materia/{semestreMateriaId}")
    public List<MatriculacionResponseDto> getBySemestreMateriaId(@PathVariable Long semestreMateriaId) {
        return matriculacionService.getBySemestreMateriaId(semestreMateriaId);
    }

    @GetMapping("/alumno")
    public List<MatriculacionResponseDto> getByAlumnoId(Authentication authentication) {
        return matriculacionService.getByAlumno(authentication);
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public MatriculacionResponseDto create(@RequestBody MatriculacionRequestDto dto) {
        return matriculacionService.create(dto);
    }

    @PatchMapping("/calcular-nota/{id}")
    public String calcularNotaFinal(@PathVariable Long id) {
        return matriculacionService.calcularNotas(id);
    }

    @PatchMapping("/subir-faltas/{id}")
    public String subirFaltas(@PathVariable Long id, @RequestBody ValorRequestDto dto) {
        return matriculacionService.subirFaltas(id, dto);
    }

    @PatchMapping("consolidar-todos/{semestreMateriaId}")
    public String consolidarTodos(@PathVariable Long semestreMateriaId) {
        return matriculacionService.consolidarNotasATodos(semestreMateriaId);
    }

    @PatchMapping("consolidar-alumno/{matriculacionId}")
    public String consolidarAlumno(@PathVariable Long matriculacionId) {
        return matriculacionService.consolidarNotasIndividual(matriculacionId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        matriculacionService.delete(id);
    }
}
