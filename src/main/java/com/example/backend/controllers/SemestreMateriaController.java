package com.example.backend.controllers;

import com.example.backend.dto.semestreMateria.SemestreMateriaRequestDTO;
import com.example.backend.dto.semestreMateria.SemestreMateriaResponseDto;
import com.example.backend.service.SemestreMateriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/semestre-materia")
public class SemestreMateriaController {
    private final SemestreMateriaService semestreMateriaService;

    public SemestreMateriaController(SemestreMateriaService semestreMateriaService) {
        this.semestreMateriaService = semestreMateriaService;
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public SemestreMateriaResponseDto create(@RequestBody SemestreMateriaRequestDTO dto) {
        return semestreMateriaService.create(dto);
    }

    @GetMapping("/")
    public List<SemestreMateriaResponseDto> getAll() {
        return semestreMateriaService.getAll();
    }

    @GetMapping("/{id}")
    public SemestreMateriaResponseDto getSemestreMateria(@PathVariable Long id) {
        return semestreMateriaService.getSemestreMateria(id);
    }

    @GetMapping("/docente/materias")
    public List<SemestreMateriaResponseDto> getByDocente(Authentication authentication) {
        return semestreMateriaService.getByDocente(authentication);
    }

    @PutMapping("/{id}")
    public SemestreMateriaResponseDto update(@PathVariable Long id, @RequestBody SemestreMateriaRequestDTO dto) {
        return semestreMateriaService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        semestreMateriaService.delete(id);
    }

    @PatchMapping("/cerrar/{id}")
    public ResponseEntity<String> cerrarMateria(@PathVariable Long id) {
        String mensaje = semestreMateriaService.cerrarMateria(id);
        return ResponseEntity.ok(mensaje);
    }
}
