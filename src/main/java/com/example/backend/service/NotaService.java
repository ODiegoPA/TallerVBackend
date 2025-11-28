package com.example.backend.service;

import com.example.backend.dto.nota.NotaRequestDto;
import com.example.backend.dto.nota.NotaResponseDto;
import com.example.backend.dto.nota.ValorRequestDto;
import com.example.backend.models.Evaluacion;
import com.example.backend.models.Matriculacion;
import com.example.backend.models.Nota;
import com.example.backend.models.SemestreMateria;
import com.example.backend.repository.EvaluacionRepository;
import com.example.backend.repository.NotaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotaService {
    private final NotaRepository notaRepository;
    private final EvaluacionRepository evaluacionRepository;

    public NotaService(NotaRepository notaRepository, EvaluacionRepository evaluacionRepository) {
        this.notaRepository = notaRepository;
        this.evaluacionRepository = evaluacionRepository;
    }

    public NotaResponseDto getNota(Long id){
        Nota nota = notaRepository.findById(id).orElseThrow();
        Evaluacion e = nota.getEvaluacion();

        return new NotaResponseDto(
                nota.getId(),
                e.getNombre(),
                nota.getPonderacion(),
                nota.getCalificacion()
        );
    }

    public List<NotaResponseDto> findByMatriculacionId(Long id){
        List<Nota> notas = notaRepository.findByMatriculacion_Id(id);
        return notas.stream().map(this::toDto).toList();
    }

    public NotaResponseDto update(Long id, NotaRequestDto dto){
        Nota nota = notaRepository.findById(id).orElseThrow();
        nota.setPonderacion(dto.ponderacion());
        nota.setCalificacion(dto.calificacion());
        notaRepository.save(nota);
        return toDto(notaRepository.findById(id).orElse(nota));
    }

    public String cambiarPonderacion(Long id, ValorRequestDto dto){
        Integer nuevaPonderacion = dto.nuevoValor();
        Nota nota = notaRepository.findById(id).orElseThrow();
        Matriculacion matriculacion = nota.getMatriculacion();
        SemestreMateria semestreMateria = matriculacion.getSemestreMateria();
        if (!semestreMateria.isEstaActiva()){
            return "No se puede cambiar la ponderacion de una materia inactiva";
        }
        nota.setPonderacion(nuevaPonderacion);
        notaRepository.save(nota);
        return "Ponderacion actualizada correctamente";
    }

    public String subirCalificacion(Long id, ValorRequestDto dto){
        Double nuevaCalificacion = dto.nuevoValor().doubleValue();
        Nota nota = notaRepository.findById(id).orElseThrow();
        Matriculacion matriculacion = nota.getMatriculacion();
        SemestreMateria semestreMateria = matriculacion.getSemestreMateria();
        if (!semestreMateria.isEstaActiva()){
            return "No se puede subir la nota de una materia inactiva";
        }
        if (nuevaCalificacion < 0 || nuevaCalificacion > 100){
            return "La calificacion debe estar entre 0 y 100";
        }
        nuevaCalificacion = (nuevaCalificacion * nota.getPonderacion()) / 100;
        nota.setCalificacion(nuevaCalificacion);
        notaRepository.save(nota);
        return "Calificacion actualizada correctamente";
    }


    private NotaResponseDto toDto(Nota nota){
        Evaluacion e = nota.getEvaluacion();
        return new NotaResponseDto(
                nota.getId(),
                e.getNombre(),
                nota.getPonderacion(),
                nota.getCalificacion()
        );
    }
}
