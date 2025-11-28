package com.example.backend.service;

import com.example.backend.dto.materia.MateriaLiteDto;
import com.example.backend.dto.modalidad.ModalidadLiteDto;
import com.example.backend.dto.semestre.SemestreLiteDto;
import com.example.backend.dto.semestreMateria.SemestreMateriaRequestDTO;
import com.example.backend.dto.semestreMateria.SemestreMateriaResponseDto;
import com.example.backend.dto.user.UserLiteDto;
import com.example.backend.models.*;
import com.example.backend.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SemestreMateriaService {
    private final SemestreMateriaRepository semestreMateriaRepository;
    private final MateriaRepository materiaRepository;
    private final SemestreRepository semestreRepository;
    private final UserRepository userRepository;
    private final ModalidadRepository modalidadRepository;

    public SemestreMateriaService(SemestreMateriaRepository semestreMateriaRepository, MateriaRepository materiaRepository, SemestreRepository semestreRepository, UserRepository userRepository, ModalidadRepository modalidadRepository) {
        this.semestreMateriaRepository = semestreMateriaRepository;
        this.materiaRepository = materiaRepository;
        this.semestreRepository = semestreRepository;
        this.userRepository = userRepository;
        this.modalidadRepository = modalidadRepository;
    }

    public SemestreMateriaResponseDto getSemestreMateria(Long id){
        SemestreMateria sm = semestreMateriaRepository.findById(id).orElseThrow();
        Materia m = sm.getMateria();
        Semestre s = sm.getSemestre();
        User u = sm.getDocente();
        Modalidad d = sm.getModalidad();

        MateriaLiteDto mLite = new MateriaLiteDto(m.getId(), m.getNombre());
        SemestreLiteDto sLite = new SemestreLiteDto(s.getId(), s.getNombre(), s.getFechaInicio(), s.getFechaFin());
        UserLiteDto uLite = new UserLiteDto(u.getId(), u.getNombre(), u.getApellido());
        ModalidadLiteDto dLite = new ModalidadLiteDto(d.getId(), d.getNombre(), d.getFaltas_permitidas());

        return new SemestreMateriaResponseDto(sm.getId(), mLite, sLite, uLite, dLite, sm.getCupos(), sm.isEstaActiva());
    }

    public List<SemestreMateriaResponseDto> getAll(){
        List<SemestreMateria> semestreMaterias = semestreMateriaRepository.findAll();
        return semestreMaterias.stream().map(this::toDto).toList();
    }

    public List<SemestreMateriaResponseDto> getByDocente(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("No autenticado");
        }
        String email = auth.getName();
        User docente = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        List<SemestreMateria> semestreMaterias = semestreMateriaRepository.findByDocenteId(docente.getId());
        return semestreMaterias.stream().map(this::toDto).toList();
    }

    public SemestreMateriaResponseDto create(SemestreMateriaRequestDTO dto){
        Materia m = materiaRepository.findById(dto.materiaId()).orElseThrow();
        Semestre s = semestreRepository.findById(dto.semestreId()).orElseThrow();
        User u = userRepository.findById(dto.docenteId()).orElseThrow();
        Modalidad d = modalidadRepository.findById(dto.modalidadId()).orElseThrow();

        SemestreMateria sm = new SemestreMateria();
        sm.setMateria(m);
        sm.setSemestre(s);
        sm.setDocente(u);
        sm.setModalidad(d);
        sm.setCupos(dto.cupos());
        sm.setEstaActiva(true);
        semestreMateriaRepository.save(sm);
        return toDto(semestreMateriaRepository.findById(sm.getId()).orElse(sm));
    }

    public SemestreMateriaResponseDto update(Long id, SemestreMateriaRequestDTO dto){
        SemestreMateria sm = semestreMateriaRepository.findById(id).orElseThrow();
        Materia m = materiaRepository.findById(dto.materiaId()).orElseThrow();
        Semestre s = semestreRepository.findById(dto.semestreId()).orElseThrow();
        User u = userRepository.findById(dto.docenteId()).orElseThrow();
        Modalidad d = modalidadRepository.findById(dto.modalidadId()).orElseThrow();

        sm.setMateria(m);
        sm.setSemestre(s);
        sm.setDocente(u);
        sm.setModalidad(d);
        sm.setCupos(dto.cupos());
        semestreMateriaRepository.save(sm);
        return toDto(semestreMateriaRepository.findById(sm.getId()).orElse(sm));
    }

    public String cerrarMateria(Long id){
        SemestreMateria sm = semestreMateriaRepository.findById(id).orElseThrow();
        sm.setEstaActiva(false);
        semestreMateriaRepository.save(sm);
        return "La materia ha sido cerrada exitosamente.";
    }

    public void delete(Long id){
        SemestreMateria sm = semestreMateriaRepository.findById(id).orElseThrow();
        semestreMateriaRepository.delete(sm);
    }

    private SemestreMateriaResponseDto toDto(SemestreMateria sm) {
        Materia m = sm.getMateria();
        Semestre s = sm.getSemestre();
        User u = sm.getDocente();
        Modalidad d = sm.getModalidad();

        MateriaLiteDto mLite = new MateriaLiteDto(m.getId(), m.getNombre());
        SemestreLiteDto sLite = new SemestreLiteDto(s.getId(), s.getNombre(), s.getFechaInicio(), s.getFechaFin());
        UserLiteDto uLite = new UserLiteDto(u.getId(), u.getNombre(), u.getApellido());
        ModalidadLiteDto dLite = new ModalidadLiteDto(d.getId(), d.getNombre(), d.getFaltas_permitidas());

        return new SemestreMateriaResponseDto(sm.getId(), mLite, sLite, uLite, dLite, sm.getCupos(), sm.isEstaActiva());
    }
}
