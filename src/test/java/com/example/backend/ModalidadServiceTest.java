package com.example.backend; // revertido

import com.example.backend.dto.modalidad.ModalidadRequestDto;
import com.example.backend.dto.modalidad.ModalidadResponseDto;
import com.example.backend.models.Gestion;
import com.example.backend.models.Modalidad;
import com.example.backend.repository.GestionRepository;
import com.example.backend.repository.ModalidadRepository;
import com.example.backend.service.ModalidadService; // agregado

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ModalidadServiceTest {

    @Mock
    private ModalidadRepository modalidadRepository;

    @Mock
    private GestionRepository gestionRepository;

    @InjectMocks
    private ModalidadService modalidadService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }




    @Test
    void getModalidad_retornaDTO_siExiste() {
        Gestion g = new Gestion();
        g.setId(1L);
        g.setAno(2024);

        Modalidad m = new Modalidad();
        m.setId(10L);
        m.setNombre("Presencial");
        m.setFaltas_permitidas(5);
        m.setGestion(g);

        when(modalidadRepository.findById(10L)).thenReturn(Optional.of(m));

        ModalidadResponseDto dto = modalidadService.getModalidad(10L);

        assertEquals(10L, dto.id());
        assertEquals("Presencial", dto.nombre());
    }

    @Test
    void getModalidad_mapeaGestionCorrectamente() {
        Gestion g = new Gestion();
        g.setId(1L);
        g.setAno(2024);

        Modalidad m = new Modalidad();
        m.setId(10L);
        m.setGestion(g);

        when(modalidadRepository.findById(10L)).thenReturn(Optional.of(m));

        ModalidadResponseDto dto = modalidadService.getModalidad(10L);

        assertEquals(1L, dto.gestion().id());
        assertEquals(2024, dto.gestion().ano());
    }

    @Test
    void getModalidad_lanzaExcepcion_siNoExiste() {
        when(modalidadRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> modalidadService.getModalidad(999L));
    }




    @Test
    void getAll_retornaListaVacia_siNoHayDatos() {
        when(modalidadRepository.findAll()).thenReturn(List.of());

        List<ModalidadResponseDto> result = modalidadService.getAll();

        assertTrue(result.isEmpty());
    }

    @Test
    void getAll_retornaListaDeDTOs() {
        Gestion g = new Gestion();
        g.setId(1L); g.setAno(2024);

        Modalidad m = new Modalidad();
        m.setId(10L);
        m.setGestion(g);

        when(modalidadRepository.findAll()).thenReturn(List.of(m));

        List<ModalidadResponseDto> result = modalidadService.getAll();

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).id());
    }

    @Test
    void getAll_llamaFindAllUnaVez() {
        modalidadService.getAll();
        verify(modalidadRepository, times(1)).findAll();
    }



    @Test
    void getByGestion_retornaDTOs_siGestionExiste() {
        Gestion g = new Gestion();
        g.setId(1L);

        Modalidad m = new Modalidad();
        m.setId(10L);
        m.setGestion(g);

        when(gestionRepository.findById(1L)).thenReturn(Optional.of(g));
        when(modalidadRepository.findByGestionId(1L)).thenReturn(List.of(m));

        List<ModalidadResponseDto> result = modalidadService.getByGestion(1L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).id());
    }

    @Test
    void getByGestion_retornaListaVacia_siNoHayModalidades() {
        Gestion g = new Gestion();
        g.setId(1L);

        when(gestionRepository.findById(1L)).thenReturn(Optional.of(g));
        when(modalidadRepository.findByGestionId(1L)).thenReturn(List.of());

        List<ModalidadResponseDto> result = modalidadService.getByGestion(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getByGestion_lanzaExcepcion_siGestionNoExiste() {
        when(gestionRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> modalidadService.getByGestion(1L));
    }

    @Test
    void getByGestion_llamaFindByGestionIdUnaVez() {
        Gestion g = new Gestion();
        g.setId(1L);

        when(gestionRepository.findById(1L)).thenReturn(Optional.of(g));
        when(modalidadRepository.findByGestionId(1L)).thenReturn(List.of());

        modalidadService.getByGestion(1L);

        verify(modalidadRepository, times(1)).findByGestionId(1L);
    }



    @Test
    void create_creaModalidadCorrectamente() {
        Gestion g = new Gestion();
        g.setId(1L);

        ModalidadRequestDto dto = new ModalidadRequestDto("Presencial", 1L, 5); // corregido orden

        Modalidad saved = new Modalidad();
        saved.setId(10L);
        saved.setNombre("Presencial");
        saved.setFaltas_permitidas(5);
        saved.setGestion(g);

        when(gestionRepository.findById(1L)).thenReturn(Optional.of(g));
        when(modalidadRepository.findById(anyLong())).thenReturn(Optional.of(saved));

        ModalidadResponseDto result = modalidadService.create(dto);

        assertEquals("Presencial", result.nombre());
        assertEquals(5, result.faltasPermitidas());
    }

    @Test
    void create_lanzaExcepcion_siGestionNoExiste() {
        ModalidadRequestDto dto = new ModalidadRequestDto("X", 1L, 5); // corregido orden
        when(gestionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> modalidadService.create(dto));
    }

    @Test
    void create_llamaSaveUnaVez() {
        Gestion g = new Gestion();
        g.setId(1L);

        ModalidadRequestDto dto = new ModalidadRequestDto("Presencial", 1L, 5); // corregido orden

        when(gestionRepository.findById(1L)).thenReturn(Optional.of(g));
        when(modalidadRepository.findById(anyLong())).thenReturn(Optional.empty());

        modalidadService.create(dto);

        verify(modalidadRepository, times(1)).save(any(Modalidad.class));
    }




    @Test
    void update_actualizaNombreCorrectamente() {
        Gestion g = new Gestion(); g.setId(1L);

        Modalidad m = new Modalidad();
        m.setId(10L);
        m.setNombre("Viejo");

        ModalidadRequestDto dto = new ModalidadRequestDto("Nuevo", 1L, 10); // corregido orden

        when(modalidadRepository.findById(10L)).thenReturn(Optional.of(m));
        when(gestionRepository.findById(1L)).thenReturn(Optional.of(g));
        when(modalidadRepository.findById(10L)).thenReturn(Optional.of(m));

        ModalidadResponseDto result = modalidadService.update(10L, dto);

        assertEquals("Nuevo", result.nombre());
    }

    @Test
    void update_lanzaExcepcion_siModalidadNoExiste() {
        when(modalidadRepository.findById(20L)).thenReturn(Optional.empty());
        ModalidadRequestDto dto = new ModalidadRequestDto("X", 1L, 5); // corregido orden

        assertThrows(NoSuchElementException.class, () -> modalidadService.update(20L, dto));
    }

    @Test
    void update_lanzaExcepcion_siGestionNoExiste() {
        Modalidad m = new Modalidad();
        m.setId(10L);

        when(modalidadRepository.findById(10L)).thenReturn(Optional.of(m));
        when(gestionRepository.findById(1L)).thenReturn(Optional.empty());

        ModalidadRequestDto dto = new ModalidadRequestDto("X", 1L, 5); // corregido orden

        assertThrows(NoSuchElementException.class, () -> modalidadService.update(10L, dto));
    }

    @Test
    void update_llamaSaveUnaVez() {
        Gestion g = new Gestion(); g.setId(1L);
        Modalidad m = new Modalidad(); m.setId(10L);

        ModalidadRequestDto dto = new ModalidadRequestDto("Nuevo", 1L, 10); // corregido orden

        when(modalidadRepository.findById(10L)).thenReturn(Optional.of(m));
        when(gestionRepository.findById(1L)).thenReturn(Optional.of(g));

        modalidadService.update(10L, dto);

        verify(modalidadRepository, times(1)).save(m);
    }



    @Test
    void delete_eliminaModalidad_siExiste() {
        Modalidad m = new Modalidad();
        m.setId(10L);

        when(modalidadRepository.findById(10L)).thenReturn(Optional.of(m));

        modalidadService.delete(10L);

        verify(modalidadRepository, times(1)).delete(m);
    }

    @Test
    void delete_lanzaExcepcion_siNoExiste() {
        when(modalidadRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> modalidadService.delete(10L));
    }
}
