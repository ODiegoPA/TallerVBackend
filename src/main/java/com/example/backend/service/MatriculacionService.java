package com.example.backend.service;

import com.example.backend.dto.materia.MateriaLiteDto;
import com.example.backend.dto.matriculacion.MatriculacionRequestDto;
import com.example.backend.dto.matriculacion.MatriculacionResponseDto;
import com.example.backend.dto.nota.ValorRequestDto;
import com.example.backend.dto.user.UserLiteDto;
import com.example.backend.models.*;
import com.example.backend.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MatriculacionService {
    private MatriculacionRepository matriculacionRepository;
    private UserRepository userRepository;
    private SemestreMateriaRepository semestreMateriaRepository;
    private EvaluacionRepository evaluacionRepository;
    private NotaRepository notaRepository;


    public MatriculacionService(MatriculacionRepository matriculacionRepository, UserRepository userRepository, SemestreMateriaRepository semestreMateriaRepository, EvaluacionRepository evaluacionRepository, NotaRepository notaRepository) {
        this.matriculacionRepository = matriculacionRepository;
        this.userRepository = userRepository;
        this.semestreMateriaRepository = semestreMateriaRepository;
        this.evaluacionRepository = evaluacionRepository;
        this.notaRepository = notaRepository;
    }

    public MatriculacionResponseDto getMatriculacion(Long id) {
        Matriculacion matriculacion = matriculacionRepository.findById(id).orElseThrow();
        User a = matriculacion.getAlumno();
        User d = matriculacion.getSemestreMateria().getDocente();
        Materia m = matriculacion.getSemestreMateria().getMateria();

        UserLiteDto aLite = new UserLiteDto(a.getId(), a.getNombre(), a.getApellido());
        UserLiteDto dLite = new UserLiteDto(d.getId(), d.getNombre(), d.getApellido());
        MateriaLiteDto mLite = new MateriaLiteDto(m.getId(), m.getNombre());

        return new MatriculacionResponseDto(
                matriculacion.getId(),
                aLite,
                dLite,
                mLite,
                matriculacion.getFaltas(),
                matriculacion.getEstaAprobado(),
                matriculacion.getNotaFinal(),
                matriculacion.getEstaConsolidado(),
                matriculacion.getSemestreMateria().getId()
        );
    }

    public List<MatriculacionResponseDto> getBySemestreMateriaId(Long semestreMateriaId) {
        List<Matriculacion> matriculaciones = matriculacionRepository.findBySemestreMateria_Id(semestreMateriaId);
        return matriculaciones.stream().map(this::toDto).toList();
    }

    public List<MatriculacionResponseDto> getByAlumno(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("No autenticado");
        }
        String email = auth.getName();
        User alumno = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        List<Matriculacion> matriculaciones = matriculacionRepository.findByAlumno_Id(alumno.getId());
        return matriculaciones.stream().map(this::toDto).toList();
    }

    public MatriculacionResponseDto create(MatriculacionRequestDto dto){
        Optional<Matriculacion> existingMatricula = matriculacionRepository.findByAlumnoIdAndSemestreMateriaId(
                dto.alumnoId(),
                dto.semestreMateriaId()
        );

        if (existingMatricula.isPresent()) {
            throw new IllegalStateException("El alumno ya se encuentra matriculado en esta materia.");
        }
        User alumno = userRepository.findById(dto.alumnoId()).orElseThrow();
        SemestreMateria semestreMateria = semestreMateriaRepository.findById(dto.semestreMateriaId()).orElseThrow();

        Matriculacion matriculacion = new Matriculacion();
        matriculacion.setAlumno(alumno);
        matriculacion.setSemestreMateria(semestreMateria);
        matriculacion.setFaltas(0);
        matriculacion.setEstaAprobado(false);
        matriculacion.setEstaConsolidado(false);
        matriculacion.setNotaFinal(0);

        for (int i = 0; i < 5; i++) {
            Nota nota = new Nota();
            nota.setMatriculacion(matriculacion);
            nota.setEvaluacion(evaluacionRepository.findById((long)(i+1)).orElseThrow());
            nota.setPonderacion(20);
            nota.setCalificacion(0.0);
            matriculacion.getNotas().add(nota);
        }
        matriculacionRepository.save(matriculacion);
        return toDto(matriculacionRepository.findById(matriculacion.getId()).orElse(matriculacion));
    }

    public String calcularNotas (Long id){
        Matriculacion matriculacion = matriculacionRepository.findById(id).orElseThrow();
        List<Nota> notas = matriculacion.getNotas();
        double suma = 0.0;
        for (Nota nota : notas) {
            suma += nota.getCalificacion();
        }
        matriculacion.setNotaFinal((int) Math.round(suma));
        matriculacion.setEstaAprobado(matriculacion.getNotaFinal() >= 51);
        matriculacionRepository.save(matriculacion);
        return "Notas calculadas correctamente, el total es: " + matriculacion.getNotaFinal();
    }

    public String subirFaltas(Long id, ValorRequestDto dto){
        Integer faltasASumar = dto.nuevoValor();
        Matriculacion matriculacion = matriculacionRepository.findById(id).orElseThrow();
        SemestreMateria semestreMateria = matriculacion.getSemestreMateria();
        Integer faltasActuales = matriculacion.getFaltas();
        Integer faltasPermitidas = semestreMateria.getModalidad().getFaltas_permitidas();

        Integer nuevasFaltas = faltasActuales + faltasASumar;
        if (nuevasFaltas > faltasPermitidas) {
            matriculacion.setEstaAprobado(false);
            matriculacionRepository.save(matriculacion);
            return "El alumno ha excedido el limite de faltas permitidas y ha sido reprobado.";
        }

        matriculacion.setFaltas(nuevasFaltas);
        matriculacionRepository.save(matriculacion);
        return "Faltas actualizadas correctamente. Total de faltas: " + nuevasFaltas;
    }
    public String consolidarNotasATodos(Long id) {
        List<Matriculacion> matriculaciones = matriculacionRepository.findBySemestreMateria_Id(id);
        StringBuilder resultadoFinal = new StringBuilder();

        for (Matriculacion matriculacion : matriculaciones) {
            int faltas = matriculacion.getFaltas();
            int faltasPermitidas = matriculacion.getSemestreMateria().getModalidad().getFaltas_permitidas();

            if (faltas > faltasPermitidas) {
                matriculacion.setEstaAprobado(false);
            } else if (matriculacion.getNotaFinal() >= 51) {
                matriculacion.setEstaAprobado(true);
            } else {
                matriculacion.setEstaAprobado(false);
            }
            matriculacion.setEstaConsolidado(true);
        }
        matriculacionRepository.saveAll(matriculaciones);
        return resultadoFinal.length() > 0 ? resultadoFinal.toString() : "Notas y asistencias consolidadas para todos los alumnos.";
    }

    public String consolidarNotasIndividual(Long id) {
        Matriculacion matriculacion = matriculacionRepository.findById(id).orElseThrow();
        String resultadoNotas = calcularNotas(id);
        int faltas = matriculacion.getFaltas();
        int faltasPermitidas = matriculacion.getSemestreMateria().getModalidad().getFaltas_permitidas();

        if (faltas > faltasPermitidas) {
            matriculacion.setEstaAprobado(false);
            matriculacion.setEstaConsolidado(true);
            matriculacionRepository.save(matriculacion);
            return resultadoNotas + " El alumno ha excedido el límite de faltas permitidas y ha sido reprobado.";
        } else if (matriculacion.getNotaFinal() >= 51) {
            matriculacion.setEstaAprobado(true);
            matriculacion.setEstaConsolidado(true);
            matriculacionRepository.save(matriculacion);
            return resultadoNotas + " El alumno ha aprobado la materia.";
        } else {
            matriculacion.setEstaAprobado(false);
            matriculacion.setEstaConsolidado(true);
            matriculacionRepository.save(matriculacion);
            return resultadoNotas + " El alumno ha reprobado la materia.";
        }
    }

    public void delete(Long id){
        Matriculacion matriculacion = matriculacionRepository.findById(id).orElseThrow();
        matriculacionRepository.delete(matriculacion);
    }

    public MatriculacionResponseDto toDto(Matriculacion matriculacion){
        User a = matriculacion.getAlumno();
        User d = matriculacion.getSemestreMateria().getDocente();
        Materia m = matriculacion.getSemestreMateria().getMateria();

        UserLiteDto aLite = new UserLiteDto(a.getId(), a.getNombre(), a.getApellido());
        UserLiteDto dLite = new UserLiteDto(d.getId(), d.getNombre(), d.getApellido());
        MateriaLiteDto mLite = new MateriaLiteDto(m.getId(), m.getNombre());

        return new MatriculacionResponseDto(
                matriculacion.getId(),
                aLite,
                dLite,
                mLite,
                matriculacion.getFaltas(),
                matriculacion.getEstaAprobado(),
                matriculacion.getNotaFinal(),
                matriculacion.getEstaConsolidado(),
                matriculacion.getSemestreMateria().getId()
        );
    }
}
