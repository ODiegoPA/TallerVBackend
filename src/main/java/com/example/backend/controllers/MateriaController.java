package com.example.backend.controllers;

import com.example.backend.dto.materia.MateriaRequestDto;
import com.example.backend.dto.materia.MateriaResponseDto;
import com.example.backend.service.MateriaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/materia")
public class MateriaController {

    private final MateriaService materiaService;
    public MateriaController(MateriaService materiaService) {
        this.materiaService = materiaService;
    }

    @GetMapping("/")
    public List<MateriaResponseDto> getAll(){
        return materiaService.getAll();
    }

    @GetMapping("/{id}")
    public MateriaResponseDto get(@PathVariable Long id){
        return materiaService.getMateria(id);
    }

    @PostMapping("/")
    public MateriaResponseDto create(@RequestBody MateriaRequestDto dto){
        return materiaService.create(dto);
    }
    @PutMapping("/{id}")
    public MateriaResponseDto update(@PathVariable Long id, @RequestBody MateriaRequestDto dto){
        return materiaService.update(id, dto);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        materiaService.delete(id);
    }
}
