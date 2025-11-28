package com.example.backend.service;

import com.example.backend.dto.gestion.GestionRequestDto;
import com.example.backend.dto.gestion.GestionResponseDto;
import com.example.backend.dto.modalidad.ModalidadLiteDto;
import com.example.backend.dto.semestre.SemestreLiteDto;
import com.example.backend.models.Gestion;
import com.example.backend.models.Modalidad;
import com.example.backend.models.Semestre;
import com.example.backend.repository.GestionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class GestionService {
    private final GestionRepository gestionRepository;

    public GestionService(GestionRepository gestionRepository) {
        this.gestionRepository = gestionRepository;
    }

    public GestionResponseDto getGestion(Long id){
        Gestion g = gestionRepository.findById(id).orElseThrow();
        List<SemestreLiteDto> semestres = g.getSemestres().stream().map
                (s -> new SemestreLiteDto(s.getId(), s.getNombre(), s.getFechaInicio(), s.getFechaFin())).toList();
        List<ModalidadLiteDto> modalidades = g.getModalidades().stream().map
                (m -> new ModalidadLiteDto(m.getId(), m.getNombre(), m.getFaltas_permitidas())).toList();
        return new GestionResponseDto(g.getId(), g.getAno(), semestres, modalidades);
    }

    public List<GestionResponseDto> getAll() {
        List<Gestion> gestiones = gestionRepository.findAll();
        return gestiones.stream().map(this::toDto).toList();
    }

    public GestionResponseDto create(GestionRequestDto dto) {
        if (dto == null) throw new NullPointerException("dto es nulo");
        Gestion g = new Gestion();
        g.setAno(dto.ano());
        Modalidad m1 = new Modalidad();
        m1.nombre = "Presencial";     m1.faltas_permitidas = 5; m1.gestion = g;
        Modalidad m2 = new Modalidad();
        m2.nombre = "Semipresencial"; m2.faltas_permitidas = 3; m2.gestion = g;
        g.getModalidades().add(m1);
        g.getModalidades().add(m2);
        Gestion persisted = gestionRepository.save(g);
        if (persisted != null) {
            g.setId(persisted.getId()); // asegurar id asignado
        }
        return toDto(g);
    }

    public GestionResponseDto update(Long id, GestionRequestDto dto) {
        if (dto == null) throw new NullPointerException("dto es nulo");
        Gestion g = gestionRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gesti\u00f3n " + id + " no encontrada")
        );
        g.setAno(dto.ano());
        Gestion persisted = gestionRepository.save(g);
        if (persisted != null) {
            g.setId(persisted.getId()); // mantener id (normalmente igual) por consistencia
        }
        return toDto(g);
    }

    public void delete(Long id) {
        Gestion g = gestionRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gestión " + id + " no encontrada")
        );

        if (g.getSemestres() != null && !g.getSemestres().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede eliminar la gestión porque tiene semestres asociados.");
        }

        gestionRepository.delete(g);
    }
    private GestionResponseDto toDto(Gestion g) {
        List<SemestreLiteDto> semestres = g.getSemestres() == null ? List.of()
                : g.getSemestres().stream().map(this::toLite).toList();
        List<ModalidadLiteDto> modalidades = g.getModalidades() == null ? List.of()
                : g.getModalidades().stream()
                .map(m -> new ModalidadLiteDto(m.getId(), m.getNombre(), m.getFaltas_permitidas()))
                .toList();
        return new GestionResponseDto(g.getId(), g.getAno(), semestres, modalidades);
    }

    private SemestreLiteDto toLite(Semestre s) {
        return new SemestreLiteDto(s.getId(), s.getNombre(), s.getFechaInicio(), s.getFechaFin());
    }
}
