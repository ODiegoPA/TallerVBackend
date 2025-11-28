package com.example.backend.service;

import com.example.backend.dto.gestion.GestionLiteDto;
import com.example.backend.dto.modalidad.ModalidadRequestDto;
import com.example.backend.dto.modalidad.ModalidadResponseDto;
import com.example.backend.models.Gestion;
import com.example.backend.models.Modalidad;
import com.example.backend.repository.GestionRepository;
import com.example.backend.repository.ModalidadRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ModalidadService {
    private final ModalidadRepository modalidadRepository;
    private final GestionRepository gestionRepository;

    public ModalidadService(ModalidadRepository modalidadRepository, GestionRepository gestionRepository) {
        this.modalidadRepository = modalidadRepository;
        this.gestionRepository = gestionRepository;
    }

    public ModalidadResponseDto getModalidad(Long id){
        Modalidad d = modalidadRepository.findById(id).orElseThrow();
        Gestion g = d.getGestion();

        GestionLiteDto gLite = new GestionLiteDto(g.getId(), g.getAno());
        return new ModalidadResponseDto(d.getId(), d.getNombre(), d.getFaltas_permitidas(), gLite);
    }

    public List<ModalidadResponseDto> getAll() {
        List<Modalidad> modalidades = modalidadRepository.findAll();
        return modalidades.stream().map(this::toDto).toList();
    }
    public List<ModalidadResponseDto> getByGestion(Long gestionId) {
        Gestion g = gestionRepository.findById(gestionId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gestion " + gestionId + " no encontrada"));
        List<Modalidad> modalidades = modalidadRepository.findByGestionId(g.id);
        return modalidades.stream().map(this::toDto).toList();
    }

    public ModalidadResponseDto create(ModalidadRequestDto dto){
        Gestion g = gestionRepository.findById(dto.gestionId()).orElseThrow();
        Modalidad d = new Modalidad();
        d.setNombre(dto.nombre());
        d.setFaltas_permitidas(dto.faltasPermitidas());
        d.setGestion(g);
        modalidadRepository.save(d);
        return toDto(modalidadRepository.findById(d.getId()).orElse(d));
    }

    public ModalidadResponseDto update(Long id, ModalidadRequestDto dto){
        Modalidad d = modalidadRepository.findById(id).orElseThrow();
        d.setNombre(dto.nombre());
        d.setFaltas_permitidas(dto.faltasPermitidas());
        Gestion g = gestionRepository.findById(dto.gestionId()).orElseThrow();
        d.setGestion(g);
        modalidadRepository.save(d);
        return toDto(modalidadRepository.findById(d.getId()).orElse(d));
    }

    public void delete(Long id){
        Modalidad m = modalidadRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Modalidad " + id + " no encontrada"));
        modalidadRepository.delete(m);
    }


    private ModalidadResponseDto toDto(Modalidad d) {
        Gestion g = d.getGestion();
        GestionLiteDto gLite = new GestionLiteDto(g.getId(), g.getAno());
        return new ModalidadResponseDto(d.getId(), d.getNombre(), d.getFaltas_permitidas(), gLite);
    }
}
