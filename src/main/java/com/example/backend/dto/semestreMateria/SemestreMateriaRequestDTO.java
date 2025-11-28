package com.example.backend.dto.semestreMateria;

public record SemestreMateriaRequestDTO (Long materiaId, Long semestreId, Long docenteId, Long modalidadId, Integer cupos) {}
