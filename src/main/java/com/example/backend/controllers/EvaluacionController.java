package com.example.backend.controllers;

import com.example.backend.dto.evaluacion.EvaluacionRequestDto;
import com.example.backend.dto.evaluacion.EvaluacionResponseDto;
import com.example.backend.service.EvaluacionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evaluacion")
public class EvaluacionController {

    private final EvaluacionService evaluacionService;
    public EvaluacionController(EvaluacionService evaluacionService) {
        this.evaluacionService = evaluacionService;
    }

    @GetMapping("/")
    public List<EvaluacionResponseDto> getAll() {
        return evaluacionService.getAll();
    }

    @GetMapping("/{id}")
    public EvaluacionResponseDto get(@PathVariable Long id) {
        return evaluacionService.getEvaluacion(id);
    }

    @PostMapping("/")
    public EvaluacionResponseDto create(@RequestBody EvaluacionRequestDto dto) {
        return evaluacionService.create(dto);
    }

    @PutMapping("/{id}")
    public EvaluacionResponseDto update(@PathVariable Long id, @RequestBody EvaluacionRequestDto dto) {
        return evaluacionService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        evaluacionService.delete(id);
    }
}
