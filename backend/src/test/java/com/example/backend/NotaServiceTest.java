package com.example.backend;

import com.example.backend.dto.nota.NotaRequestDto;
import com.example.backend.dto.nota.ValorRequestDto;
import com.example.backend.models.*;
import com.example.backend.repository.EvaluacionRepository;
import com.example.backend.repository.NotaRepository;
import com.example.backend.service.NotaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotaServiceTest {

    @Mock private NotaRepository notaRepository;
    @Mock private EvaluacionRepository evaluacionRepository; // no usado directamente pero requerido por ctor

    @InjectMocks private NotaService notaService;

    private Nota nota;
    private Evaluacion evaluacion;
    private Matriculacion matriculacion;
    private SemestreMateria semestreMateria;

    @BeforeEach
    void setup() {
        evaluacion = new Evaluacion();
        evaluacion.setId(10L);
        evaluacion.setNombre("Parcial 1");

        semestreMateria = new SemestreMateria();
        semestreMateria.setEstaActiva(true);

        matriculacion = new Matriculacion();
        matriculacion.setSemestreMateria(semestreMateria);

        nota = new Nota();
        nota.setId(1L);
        nota.setEvaluacion(evaluacion);
        nota.setMatriculacion(matriculacion);
        nota.setPonderacion(30);
        nota.setCalificacion(0.0);
    }

    @Test
    void getNota_ok() {
        when(notaRepository.findById(1L)).thenReturn(Optional.of(nota));
        var dto = notaService.getNota(1L);
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.evaluacion()).isEqualTo("Parcial 1");
    }

    @Test
    void getNota_noExiste() {
        when(notaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> notaService.getNota(1L)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void findByMatriculacionId_listaVacia() {
        when(notaRepository.findByMatriculacion_Id(99L)).thenReturn(List.of());
        assertThat(notaService.findByMatriculacionId(99L)).isEmpty();
    }

    @Test
    void findByMatriculacionId_conResultados() {
        when(notaRepository.findByMatriculacion_Id(50L)).thenReturn(List.of(nota));
        var lista = notaService.findByMatriculacionId(50L);
        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).ponderacion()).isEqualTo(30);
    }

    @Test
    void update_ok() {
        when(notaRepository.findById(1L)).thenReturn(Optional.of(nota));
        when(notaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        NotaRequestDto req = new NotaRequestDto(1L, 10L, 40, 50.0);
        var dto = notaService.update(1L, req);
        assertThat(dto.ponderacion()).isEqualTo(40);
        assertThat(dto.calificacion()).isEqualTo(50.0);
    }

    @Test
    void update_noExiste() {
        when(notaRepository.findById(1L)).thenReturn(Optional.empty());
        NotaRequestDto req = new NotaRequestDto(1L, 10L, 40, 50.0);
        assertThatThrownBy(() -> notaService.update(1L, req)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void cambiarPonderacion_okMateriaActiva() {
        when(notaRepository.findById(1L)).thenReturn(Optional.of(nota));
        when(notaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        String msg = notaService.cambiarPonderacion(1L, new ValorRequestDto(45));
        assertThat(msg).contains("actualizada");
        assertThat(nota.getPonderacion()).isEqualTo(45);
    }

    @Test
    void cambiarPonderacion_materiaInactiva() {
        semestreMateria.setEstaActiva(false);
        when(notaRepository.findById(1L)).thenReturn(Optional.of(nota));
        String msg = notaService.cambiarPonderacion(1L, new ValorRequestDto(45));
        assertThat(msg).contains("inactiva");
    }

    @Test
    void subirCalificacion_materiaInactiva() {
        semestreMateria.setEstaActiva(false);
        when(notaRepository.findById(1L)).thenReturn(Optional.of(nota));
        String msg = notaService.subirCalificacion(1L, new ValorRequestDto(80));
        assertThat(msg).contains("inactiva");
    }

    @Test
    void subirCalificacion_fueraDeRango() {
        when(notaRepository.findById(1L)).thenReturn(Optional.of(nota));
        String msg = notaService.subirCalificacion(1L, new ValorRequestDto(101));
        assertThat(msg).contains("entre 0 y 100");
    }

    @Test
    void subirCalificacion_okAplicandoPonderacion() {
        when(notaRepository.findById(1L)).thenReturn(Optional.of(nota));
        when(notaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        String msg = notaService.subirCalificacion(1L, new ValorRequestDto(80));
        // Calificación ponderada = 80 * 30 / 100 = 24.0
        assertThat(nota.getCalificacion()).isEqualTo(24.0);
        assertThat(msg).contains("actualizada");
    }
}
