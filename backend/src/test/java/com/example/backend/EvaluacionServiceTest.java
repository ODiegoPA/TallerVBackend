package com.example.backend; // revertido al paquete que coincide con la ruta

import com.example.backend.dto.evaluacion.EvaluacionRequestDto;
import com.example.backend.dto.evaluacion.EvaluacionResponseDto;
import com.example.backend.models.Evaluacion;
import com.example.backend.repository.EvaluacionRepository;
import com.example.backend.service.EvaluacionService; // agregado
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
class EvaluacionServiceTest {

    @Mock
    private EvaluacionRepository evaluacionRepository;

    @InjectMocks
    private EvaluacionService evaluacionService;

    private Evaluacion evaluacion;

    @BeforeEach
    void setUp() {
        evaluacion = new Evaluacion();
        evaluacion.setId(1L);
        evaluacion.setNombre("Prueba");
    }

    @Test
    void getEvaluacion_debeRetornarDTO_siExiste() {
        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(evaluacion));

        EvaluacionResponseDto dto = evaluacionService.getEvaluacion(1L);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.nombre()).isEqualTo("Prueba");
        verify(evaluacionRepository, times(1)).findById(1L);
    }

    @Test
    void getEvaluacion_debeLanzarExcepcion_siNoExiste() {
        when(evaluacionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> evaluacionService.getEvaluacion(1L))
                .isInstanceOf(NoSuchElementException.class);
    }


    @Test
    void getAll_debeRetornarListaVacia_siNoHayDatos() {
        when(evaluacionRepository.findAll()).thenReturn(List.of());

        List<EvaluacionResponseDto> result = evaluacionService.getAll();

        assertThat(result).isEmpty();
        verify(evaluacionRepository, times(1)).findAll();
    }

    @Test
    void getAll_debeRetornarListaDeDTOs() {
        Evaluacion e2 = new Evaluacion();
        e2.setId(2L);
        e2.setNombre("Otra");

        when(evaluacionRepository.findAll()).thenReturn(List.of(evaluacion, e2));

        List<EvaluacionResponseDto> result = evaluacionService.getAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting("nombre").containsExactly("Prueba", "Otra");
    }

    @Test
    void create_debeCrearYRetornarDTO() {
        EvaluacionRequestDto dto = new EvaluacionRequestDto("Nueva");

        Evaluacion saved = new Evaluacion();
        saved.setId(10L);
        saved.setNombre("Nueva");

        when(evaluacionRepository.save(any(Evaluacion.class))).thenReturn(saved);

        EvaluacionResponseDto result = evaluacionService.create(dto);

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.nombre()).isEqualTo("Nueva");

        ArgumentCaptor<Evaluacion> captor = ArgumentCaptor.forClass(Evaluacion.class);
        verify(evaluacionRepository).save(captor.capture());

        assertThat(captor.getValue().getNombre()).isEqualTo("Nueva");
    }

    @Test
    void create_siFindByIdVuelveEmpty_retornaEntidadCreada() {
        EvaluacionRequestDto dto = new EvaluacionRequestDto("Nueva");

        Evaluacion saved = new Evaluacion();
        saved.setId(10L);
        saved.setNombre("Nueva");

        when(evaluacionRepository.save(any(Evaluacion.class))).thenReturn(saved);

        EvaluacionResponseDto result = evaluacionService.create(dto);

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.nombre()).isEqualTo("Nueva");
    }

    @Test
    void create_debeFallarSiDtoEsNulo() {
        assertThatThrownBy(() -> evaluacionService.create(null))
                .isInstanceOf(NullPointerException.class);
    }


    @Test
    void update_debeActualizarYRetornarDTO() {
        EvaluacionRequestDto dto = new EvaluacionRequestDto("Modificada");

        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(evaluacion));
        when(evaluacionRepository.save(any(Evaluacion.class))).thenReturn(evaluacion);

        EvaluacionResponseDto result = evaluacionService.update(1L, dto);

        assertThat(result.nombre()).isEqualTo("Modificada");
    }

    @Test
    void update_siEntidadNoExiste_debeLanzarExcepcion() {
        when(evaluacionRepository.findById(1L)).thenReturn(Optional.empty());
        EvaluacionRequestDto dto = new EvaluacionRequestDto("Modificada");

        assertThatThrownBy(() -> evaluacionService.update(1L, dto))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void update_siFindByIdPostSaveEsEmpty_retornaEntidadActualizada() {
        EvaluacionRequestDto dto = new EvaluacionRequestDto("Modificada");

        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(evaluacion));
        when(evaluacionRepository.save(any(Evaluacion.class))).thenReturn(evaluacion);

        EvaluacionResponseDto result = evaluacionService.update(1L, dto);

        assertThat(result.nombre()).isEqualTo("Modificada");
    }

    @Test
    void update_debeFallarSiDtoEsNulo() {
        assertThatThrownBy(() -> evaluacionService.update(1L, null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void delete_debeEliminarEntidad_siExiste() {
        when(evaluacionRepository.findById(1L)).thenReturn(Optional.of(evaluacion));

        evaluacionService.delete(1L);

        verify(evaluacionRepository, times(1)).delete(evaluacion);
    }

    @Test
    void delete_debeFallarSiEntidadNoExiste() {
        when(evaluacionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> evaluacionService.delete(1L))
                .isInstanceOf(NoSuchElementException.class);

        verify(evaluacionRepository, never()).delete(any());
    }
}
