package com.example.backend.controllers;

import com.example.backend.dto.semestre.SemestreRequestDto;
import com.example.backend.dto.semestre.SemestreResponseDto;
import com.example.backend.service.SemestreService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/semestre")
public class SemestreController {
    private final SemestreService semestreService;

    public SemestreController(SemestreService semestreService) {
        this.semestreService = semestreService;
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public SemestreResponseDto create(@RequestBody SemestreRequestDto dto){
        return semestreService.create(dto);
    }

    @GetMapping("/")
    public List<SemestreResponseDto> getAll(){
        return semestreService.getAll();
    }

    @GetMapping("/{id}")
    public SemestreResponseDto getSemestre(@PathVariable Long id){
        return semestreService.getSemestre(id);
    }
    @PutMapping("/{id}")
    public SemestreResponseDto update(@PathVariable Long id, @RequestBody SemestreRequestDto dto) {
        return semestreService.update(id, dto);
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        semestreService.delete(id);
    }
}
