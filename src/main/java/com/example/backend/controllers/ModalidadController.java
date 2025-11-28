package com.example.backend.controllers;

import com.example.backend.dto.modalidad.ModalidadRequestDto;
import com.example.backend.dto.modalidad.ModalidadResponseDto;
import com.example.backend.service.ModalidadService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/modalidad")
public class ModalidadController {
    private final ModalidadService modalidadService;

    public ModalidadController(ModalidadService modalidadService) {
        this.modalidadService = modalidadService;
    }

    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public ModalidadResponseDto create(@RequestBody ModalidadRequestDto dto){
        return modalidadService.create(dto);
    }

    @GetMapping("/")
    public List<ModalidadResponseDto> getAll() {
        return modalidadService.getAll();
    }

    @GetMapping("/{id}")
    public ModalidadResponseDto getModalidad(@PathVariable Long id){
        return modalidadService.getModalidad(id);
    }

    @PutMapping("/{id}")
    public ModalidadResponseDto update(@PathVariable Long id, @RequestBody ModalidadRequestDto dto) {
        return modalidadService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        modalidadService.delete(id);
    }

    @GetMapping("/gestion/{gestionId}")
    public java.util.List<ModalidadResponseDto> getByGestion(@PathVariable Long gestionId) {
        return modalidadService.getByGestion(gestionId);
    }
}
