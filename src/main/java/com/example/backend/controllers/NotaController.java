package com.example.backend.controllers;

import com.example.backend.dto.nota.NotaResponseDto;
import com.example.backend.dto.nota.ValorRequestDto;
import com.example.backend.service.NotaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/nota")
public class NotaController {

    private final NotaService notaService;
    public NotaController(NotaService notaService) {
        this.notaService = notaService;
    }

    @GetMapping("/matriculacion/{id}")
    public List<NotaResponseDto> getByMatriculacionId(@PathVariable Long id){
        return notaService.findByMatriculacionId(id);
    }
    @GetMapping("/{id}")
    public NotaResponseDto get(@PathVariable Long id) {
        return notaService.getNota(id);
    }
    @PatchMapping("/{id}/cambiar-ponderacion")
    public String cambiarPonderacion(@PathVariable Long id, @RequestBody ValorRequestDto dto) {
        return notaService.cambiarPonderacion(id, dto);
    }
    @PatchMapping("/{id}/subir-calificacion")
    public String subirCalificacion(@PathVariable Long id, @RequestBody ValorRequestDto dto) {
        return notaService.subirCalificacion(id, dto);
    }
}
