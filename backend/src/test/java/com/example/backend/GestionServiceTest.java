package com.example.backend; // revertido

import com.example.backend.dto.gestion.GestionRequestDto;
import com.example.backend.dto.gestion.GestionResponseDto;
import com.example.backend.models.Gestion;
import com.example.backend.models.Modalidad;
import com.example.backend.models.Semestre;
import com.example.backend.repository.GestionRepository;
import com.example.backend.service.GestionService; // agregado
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestionServiceTest {

    @Mock
    private GestionRepository gestionRepository;

    @InjectMocks
    private GestionService gestionService;

    private Gestion gestion;

    @BeforeEach
    void setUp() {
        gestion = new Gestion();
        gestion.id = 1L;
        gestion.ano = 2024;
        gestion.getSemestres().clear();
        gestion.getModalidades().clear();
    }


    @Test
    void getGestion_retornaDTO_siExisteGestion() {
        when(gestionRepository.findById(1L)).thenReturn(Optional.of(gestion));
        GestionResponseDto dto = gestionService.getGestion(1L);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.ano()).isEqualTo(2024);
    }

    @Test
    void getGestion_mapeaSemestresCorrectamente() {
        Semestre s = new Semestre();
        s.id = 10L;
        s.nombre = "Primer Semestre";
        s.fechaInicio = LocalDate.now();
        s.fechaFin = LocalDate.now().plusMonths(4);
        gestion.getSemestres().add(s);

        when(gestionRepository.findById(1L)).thenReturn(Optional.of(gestion));
        GestionResponseDto dto = gestionService.getGestion(1L);

        assertThat(dto.semestres()).hasSize(1);
        assertThat(dto.semestres().get(0).id()).isEqualTo(10L);
    }

    @Test
    void getGestion_mapeaModalidadesCorrectamente() {
        Modalidad m = new Modalidad();
        m.id = 5L;
        m.nombre = "Presencial";
        m.faltas_permitidas = 5;
        gestion.getModalidades().add(m);

        when(gestionRepository.findById(1L)).thenReturn(Optional.of(gestion));
        GestionResponseDto dto = gestionService.getGestion(1L);

        assertThat(dto.modalidades()).hasSize(1);
        assertThat(dto.modalidades().get(0).nombre()).isEqualTo("Presencial");
    }

    @Test
    void getGestion_lanzaExcepcion_siNoExisteGestion() {
        when(gestionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gestionService.getGestion(1L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void getAll_retornaListaVacia_siNoHayGestiones() {
        when(gestionRepository.findAll()).thenReturn(List.of());
        List<GestionResponseDto> lista = gestionService.getAll();
        assertThat(lista).isEmpty();
    }

    @Test
    void getAll_retornaListaConDTOs_siHayGestiones() {
        Gestion g2 = new Gestion();
        g2.id = 2L;
        g2.ano = 2025;

        when(gestionRepository.findAll()).thenReturn(List.of(gestion, g2));

        List<GestionResponseDto> lista = gestionService.getAll();
        assertThat(lista).hasSize(2);
    }

    @Test
    void create_guardaGestionCorrectamente() {
        GestionRequestDto dto = new GestionRequestDto(2024);

        Gestion saved = new Gestion();
        saved.id = 10L;
        saved.ano = 2024;
        saved.modalidades = gestion.modalidades;

        when(gestionRepository.save(any(Gestion.class))).thenReturn(saved);

        GestionResponseDto result = gestionService.create(dto);

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.ano()).isEqualTo(2024);

        verify(gestionRepository, times(1)).save(any(Gestion.class));
    }

    @Test
    void create_creaModalidadesPorDefecto() {
        GestionRequestDto dto = new GestionRequestDto(2024);

        Gestion saved = new Gestion();
        saved.id = 15L;
        saved.ano = 2024;

        when(gestionRepository.save(any(Gestion.class))).thenReturn(saved);

        GestionResponseDto result = gestionService.create(dto);

        ArgumentCaptor<Gestion> captor = ArgumentCaptor.forClass(Gestion.class);
        verify(gestionRepository).save(captor.capture());

        Gestion g = captor.getValue();
        assertThat(g.getModalidades()).hasSize(2);
        assertThat(result.id()).isEqualTo(15L); // nueva aserción usando result
    }

    @Test
    void create_lanzaExcepcion_siDtoEsNulo() {
        assertThatThrownBy(() -> gestionService.create(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void update_actualizaAnoCorrectamente() {
        GestionRequestDto dto = new GestionRequestDto(2030);

        when(gestionRepository.findById(1L))
                .thenReturn(Optional.of(gestion))
                .thenReturn(Optional.of(gestion));

        GestionResponseDto result = gestionService.update(1L, dto);

        assertThat(result.ano()).isEqualTo(2030);
    }

    @Test
    void update_lanzaNotFound_siGestionNoExiste() {
        GestionRequestDto dto = new GestionRequestDto(2030);

        when(gestionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gestionService.update(1L, dto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("no encontrada");
    }

    @Test
    void update_lanzaExcepcion_siDtoEsNulo() {
        assertThatThrownBy(() -> gestionService.update(1L, null))
                .isInstanceOf(NullPointerException.class);
    }


    @Test
    void delete_eliminaGestion_siNoTieneSemestres() {
        when(gestionRepository.findById(1L)).thenReturn(Optional.of(gestion));

        gestionService.delete(1L);

        verify(gestionRepository, times(1)).delete(gestion);
    }

    @Test
    void delete_lanzaNotFound_siGestionNoExiste() {
        when(gestionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gestionService.delete(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("no encontrada");
    }

    @Test
    void delete_lanzaConflict_siTieneSemestres() {
        Semestre s = new Semestre();
        s.id = 20L;
        s.nombre = "Semestre X";
        gestion.getSemestres().add(s);

        when(gestionRepository.findById(1L)).thenReturn(Optional.of(gestion));

        assertThatThrownBy(() -> gestionService.delete(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("tiene semestres asociados");

        verify(gestionRepository, never()).delete(any());
    }
}
