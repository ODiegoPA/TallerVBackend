package com.example.backend.service;

import com.example.backend.dto.evaluacion.EvaluacionRequestDto;
import com.example.backend.dto.evaluacion.EvaluacionResponseDto;
import com.example.backend.models.Evaluacion;
import com.example.backend.repository.EvaluacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class EvaluacionService {
    private final EvaluacionRepository evaluacionRepository;

    public EvaluacionService(EvaluacionRepository evaluacionRepository) {
        this.evaluacionRepository = evaluacionRepository;
    }

    public EvaluacionResponseDto getEvaluacion(Long id){
        Evaluacion m = evaluacionRepository.findById(id).orElseThrow();
        return new EvaluacionResponseDto(m.getId(), m.getNombre());
    }

    public List<EvaluacionResponseDto> getAll() {
        List<Evaluacion> evaluaciones = evaluacionRepository.findAll();
        return evaluaciones.stream().map(this::toDto).toList();
    }

    public EvaluacionResponseDto create(EvaluacionRequestDto dto) {
        Objects.requireNonNull(dto, "dto no puede ser nulo");
        Evaluacion m = new Evaluacion();
        m.setNombre(dto.nombre());
        Evaluacion persisted = evaluacionRepository.save(m);
        Evaluacion effective = persisted != null ? persisted : m;
        return toDto(effective);
    }

    public EvaluacionResponseDto update(Long id, EvaluacionRequestDto dto) {
        Objects.requireNonNull(dto, "dto no puede ser nulo");
        Evaluacion m = evaluacionRepository.findById(id).orElseThrow();
        m.setNombre(dto.nombre());
        Evaluacion persisted = evaluacionRepository.save(m);
        Evaluacion effective = persisted != null ? persisted : m;
        return toDto(effective);
    }

    public void delete(Long id){
        Evaluacion m = evaluacionRepository.findById(id).orElseThrow();
        evaluacionRepository.delete(m);
    }

    private EvaluacionResponseDto toDto(Evaluacion m) {
        return new EvaluacionResponseDto(m.getId(), m.getNombre());
    }
}
