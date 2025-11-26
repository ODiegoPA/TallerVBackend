package com.example.backend;

import com.example.backend.dto.materia.MateriaRequestDto;
import com.example.backend.dto.materia.MateriaResponseDto;
import com.example.backend.models.Materia;
import com.example.backend.repository.MateriaRepository;
import com.example.backend.service.MateriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MateriaServiceTest {

    @Mock
    private MateriaRepository materiaRepository;

    @InjectMocks
    private MateriaService materiaService;

    private Materia materia;

    @BeforeEach
    void setUp() {
        materia = new Materia();
        materia.id = 1L;
        materia.nombre = "Matemáticas";
    }


    @Test
    void getMateria_retornaDTO_siExisteMateria() {
        when(materiaRepository.findById(1L)).thenReturn(Optional.of(materia));

        MateriaResponseDto dto = materiaService.getMateria(1L);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.nombre()).isEqualTo("Matemáticas");
    }

    @Test
    void getMateria_mapeaCorrectamenteLosCampos() {
        when(materiaRepository.findById(1L)).thenReturn(Optional.of(materia));

        MateriaResponseDto dto = materiaService.getMateria(1L);

        assertThat(dto.nombre()).isEqualTo("Matemáticas");
    }

    @Test
    void getMateria_lanzaExcepcion_siNoExisteMateria() {
        when(materiaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> materiaService.getMateria(1L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void getMateria_llamaFindByIdUnaVez() {
        when(materiaRepository.findById(1L)).thenReturn(Optional.of(materia));

        materiaService.getMateria(1L);

        verify(materiaRepository, times(1)).findById(1L);
    }


    @Test
    void getAll_retornaListaVacia_siNoHayMaterias() {
        when(materiaRepository.findAll()).thenReturn(List.of());

        List<MateriaResponseDto> lista = materiaService.getAll();

        assertThat(lista).isEmpty();
    }

    @Test
    void getAll_retornaListaDeDTOs_siHayMaterias() {
        Materia m2 = new Materia();
        m2.id = 2L;
        m2.nombre = "Física";

        when(materiaRepository.findAll()).thenReturn(List.of(materia, m2));

        List<MateriaResponseDto> lista = materiaService.getAll();

        assertThat(lista).hasSize(2);
    }

    @Test
    void getAll_mapeaCorrectamenteCadaMateria() {
        when(materiaRepository.findAll()).thenReturn(List.of(materia));

        List<MateriaResponseDto> lista = materiaService.getAll();

        assertThat(lista.get(0).nombre()).isEqualTo("Matemáticas");
    }

    @Test
    void getAll_llamaFindAllUnaVez() {
        when(materiaRepository.findAll()).thenReturn(List.of());

        materiaService.getAll();

        verify(materiaRepository, times(1)).findAll();
    }


    @Test
    void create_guardaMateriaCorrectamente() {
        MateriaRequestDto dto = new MateriaRequestDto("Programación");

        Materia saved = new Materia();
        saved.id = 10L;
        saved.nombre = "Programación";

        when(materiaRepository.save(any(Materia.class))).thenReturn(saved);
        when(materiaRepository.findById(10L)).thenReturn(Optional.of(saved));

        MateriaResponseDto res = materiaService.create(dto);

        assertThat(res.id()).isEqualTo(10L);
        assertThat(res.nombre()).isEqualTo("Programación");
    }

    @Test
    void create_retornaDTODespuesDeGuardar() {
        MateriaRequestDto dto = new MateriaRequestDto("Lenguaje");

        Materia saved = new Materia();
        saved.id = 15L;
        saved.nombre = "Lenguaje";

        when(materiaRepository.save(any(Materia.class))).thenReturn(saved);
        when(materiaRepository.findById(15L)).thenReturn(Optional.of(saved));

        MateriaResponseDto res = materiaService.create(dto);

        assertThat(res.nombre()).isEqualTo("Lenguaje");
    }

    @Test
    void create_siFindByIdVuelveEmptyRetornaEntidadCreada() {
        MateriaRequestDto dto = new MateriaRequestDto("Arte");

        Materia saved = new Materia();
        saved.id = 20L;
        saved.nombre = "Arte";

        when(materiaRepository.save(any(Materia.class))).thenReturn(saved);
        when(materiaRepository.findById(20L)).thenReturn(Optional.empty());

        MateriaResponseDto res = materiaService.create(dto);

        assertThat(res.id()).isEqualTo(20L);
    }

    @Test
    void create_lanzaExcepcion_siDtoEsNulo() {
        assertThatThrownBy(() -> materiaService.create(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void create_llamaSaveUnaVez() {
        MateriaRequestDto dto = new MateriaRequestDto("Historia");

        Materia saved = new Materia();
        saved.id = 8L;

        when(materiaRepository.save(any(Materia.class))).thenReturn(saved);
        when(materiaRepository.findById(8L)).thenReturn(Optional.of(saved));

        materiaService.create(dto);

        verify(materiaRepository, times(1)).save(any(Materia.class));
    }

    @Test
    void create_llamaFindByIdUnaVez() {
        MateriaRequestDto dto = new MateriaRequestDto("Biología");

        Materia saved = new Materia();
        saved.id = 9L;

        when(materiaRepository.save(any(Materia.class))).thenReturn(saved);
        when(materiaRepository.findById(9L)).thenReturn(Optional.of(saved));

        materiaService.create(dto);

        verify(materiaRepository, times(1)).findById(9L);
    }


    @Test
    void update_actualizaNombreCorrectamente() {
        MateriaRequestDto dto = new MateriaRequestDto("Álgebra");
        when(materiaRepository.findById(1L)).thenReturn(Optional.of(materia));
        when(materiaRepository.save(any(Materia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MateriaResponseDto res = materiaService.update(1L, dto);
        assertThat(res.nombre()).isEqualTo("Álgebra");
    }

    @Test
    void update_retornaDTOActualizadoCorrectamente() {
        MateriaRequestDto dto = new MateriaRequestDto("Cálculo");
        when(materiaRepository.findById(1L))
                .thenReturn(Optional.of(materia))
                .thenReturn(Optional.of(materia));
        when(materiaRepository.save(any(Materia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MateriaResponseDto res = materiaService.update(1L, dto);
        assertThat(res.nombre()).isEqualTo("Cálculo");
    }

    @Test
    void update_lanzaExcepcion_siMateriaNoExiste() {
        when(materiaRepository.findById(1L)).thenReturn(Optional.empty());

        MateriaRequestDto dto = new MateriaRequestDto("Taller");

        assertThatThrownBy(() -> materiaService.update(1L, dto))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void update_lanzaExcepcion_siDtoEsNulo() {
        assertThatThrownBy(() -> materiaService.update(1L, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void update_llamaFindByIdAntesDeActualizar() {
        MateriaRequestDto dto = new MateriaRequestDto("Geometría");
        when(materiaRepository.findById(1L)).thenReturn(Optional.of(materia));
        when(materiaRepository.save(any(Materia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        materiaService.update(1L, dto);
        verify(materiaRepository, atLeastOnce()).findById(1L);
    }

    @Test
    void update_llamaSaveUnaVez() {
        MateriaRequestDto dto = new MateriaRequestDto("Trigonometría");
        when(materiaRepository.findById(1L)).thenReturn(Optional.of(materia));
        when(materiaRepository.save(any(Materia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        materiaService.update(1L, dto);
        verify(materiaRepository, times(1)).save(materia);
    }

    @Test
    void update_llamaFindByIdDespuesDeGuardar() {
        MateriaRequestDto dto = new MateriaRequestDto("Geografía");
        when(materiaRepository.findById(1L))
                .thenReturn(Optional.of(materia))
                .thenReturn(Optional.of(materia));
        when(materiaRepository.save(any(Materia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        materiaService.update(1L, dto);
        verify(materiaRepository, times(2)).findById(1L);
    }


    @Test
    void delete_eliminaMateria_siExiste() {
        when(materiaRepository.findById(1L)).thenReturn(Optional.of(materia));

        materiaService.delete(1L);

        verify(materiaRepository, times(1)).delete(materia);
    }

    @Test
    void delete_lanzaExcepcion_siMateriaNoExiste() {
        when(materiaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> materiaService.delete(1L))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void delete_noLlamaDelete_siNoExiste() {
        when(materiaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> materiaService.delete(1L))
                .isInstanceOf(NoSuchElementException.class);

        verify(materiaRepository, never()).delete(any());
    }
}
