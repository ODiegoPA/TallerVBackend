package com.example.backend;

import com.example.backend.dto.semestre.SemestreRequestDto;
import com.example.backend.dto.semestre.SemestreResponseDto;
import com.example.backend.models.Gestion;
import com.example.backend.models.Semestre;
import com.example.backend.repository.GestionRepository;
import com.example.backend.repository.SemestreRepository;
import com.example.backend.service.SemestreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SemestreServiceTest {

    @Mock private SemestreRepository semestreRepository;
    @Mock private GestionRepository gestionRepository;

    @InjectMocks private SemestreService service;

    private Gestion gestion;
    private Semestre semestre;

    @BeforeEach
    void setup() {
        gestion = new Gestion();
        gestion.id = 1L; gestion.ano = 2025;
        semestre = new Semestre();
        semestre.id = 10L; semestre.nombre = "2025-I"; semestre.gestion = gestion;
        semestre.fechaInicio = LocalDate.of(2025,1,10);
        semestre.fechaFin = LocalDate.of(2025,6,10);
    }

    @Test
    void getSemestre_ok() {
        when(semestreRepository.findById(10L)).thenReturn(Optional.of(semestre));
        SemestreResponseDto dto = service.getSemestre(10L);
        assertThat(dto.id()).isEqualTo(10L);
        assertThat(dto.gestion().ano()).isEqualTo(2025);
    }

    @Test
    void getSemestre_noExiste() {
        when(semestreRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getSemestre(99L)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void getAll_vacio() {
        when(semestreRepository.findAll()).thenReturn(List.of());
        assertThat(service.getAll()).isEmpty();
    }

    @Test
    void getAll_conElementos() {
        when(semestreRepository.findAll()).thenReturn(List.of(semestre));
        assertThat(service.getAll()).hasSize(1);
    }

    @Test
    void create_ok() {
        SemestreRequestDto req = new SemestreRequestDto("2025-II", 1L, LocalDate.of(2025,7,1), LocalDate.of(2025,12,1));
        when(gestionRepository.findById(1L)).thenReturn(Optional.of(gestion));
        when(semestreRepository.save(any())).thenAnswer(inv -> { Semestre s = inv.getArgument(0); s.id = 20L; return s; });
        when(semestreRepository.findById(20L)).thenReturn(Optional.of(semestre));
        SemestreResponseDto dto = service.create(req);
        assertThat(dto.id()).isEqualTo(10L); // retorna semestre original en findById mock
    }

    @Test
    void create_gestionNoExiste() {
        SemestreRequestDto req = new SemestreRequestDto("2025-II", 1L, LocalDate.of(2025,7,1), LocalDate.of(2025,12,1));
        when(gestionRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.create(req)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void update_ok() {
        SemestreRequestDto req = new SemestreRequestDto("2025-II", 1L, LocalDate.of(2025,7,1), LocalDate.of(2025,12,1));
        when(semestreRepository.findById(10L)).thenReturn(Optional.of(semestre));
        when(gestionRepository.findById(1L)).thenReturn(Optional.of(gestion));
        when(semestreRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(semestreRepository.findById(10L)).thenReturn(Optional.of(semestre));
        SemestreResponseDto dto = service.update(10L, req);
        assertThat(dto.nombre()).isEqualTo("2025-II");
    }

    @Test
    void update_semestreNoExiste() {
        SemestreRequestDto req = new SemestreRequestDto("2025-II", 1L, LocalDate.of(2025,7,1), LocalDate.of(2025,12,1));
        when(semestreRepository.findById(10L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(10L, req)).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void update_gestionNoExiste() {
        SemestreRequestDto req = new SemestreRequestDto("2025-II", 2L, LocalDate.of(2025,7,1), LocalDate.of(2025,12,1));
        when(semestreRepository.findById(10L)).thenReturn(Optional.of(semestre));
        when(gestionRepository.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(10L, req)).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void delete_ok() {
        when(semestreRepository.findById(10L)).thenReturn(Optional.of(semestre));
        service.delete(10L);
        verify(semestreRepository).delete(semestre);
    }

    @Test
    void delete_noExiste() {
        when(semestreRepository.findById(10L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.delete(10L)).isInstanceOf(ResponseStatusException.class);
    }
}

