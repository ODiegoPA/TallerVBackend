package com.example.backend;

import com.example.backend.dto.semestreMateria.SemestreMateriaRequestDTO;
import com.example.backend.models.*;
import com.example.backend.repository.*;
import com.example.backend.service.SemestreMateriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SemestreMateriaServiceTest {

    @Mock private SemestreMateriaRepository semestreMateriaRepository;
    @Mock private MateriaRepository materiaRepository;
    @Mock private SemestreRepository semestreRepository;
    @Mock private UserRepository userRepository;
    @Mock private ModalidadRepository modalidadRepository;

    @InjectMocks private SemestreMateriaService service;

    private Materia materia;
    private Semestre semestre;
    private User docente;
    private Modalidad modalidad;
    private SemestreMateria sm;

    @BeforeEach
    void setup() {
        materia = new Materia();
        materia.setId(11L); materia.setNombre("Matemáticas");
        semestre = new Semestre();
        semestre.setId(22L); semestre.setNombre("2025-I");
        semestre.setFechaInicio(LocalDate.now());
        semestre.setFechaFin(LocalDate.now().plusMonths(4));
        docente = new User();
        docente.setId(33L); docente.setEmail("docente@example.com"); docente.setNombre("Carlos"); docente.setApellido("Lopez");
        modalidad = new Modalidad();
        modalidad.setId(44L); modalidad.setNombre("Presencial"); modalidad.setFaltas_permitidas(5);
        sm = new SemestreMateria();
        sm.setId(55L);
        sm.setMateria(materia);
        sm.setSemestre(semestre);
        sm.setDocente(docente);
        sm.setModalidad(modalidad);
        sm.setCupos(40);
        sm.setEstaActiva(true);
    }

    @Test
    void getSemestreMateria_ok() {
        when(semestreMateriaRepository.findById(55L)).thenReturn(Optional.of(sm));
        var dto = service.getSemestreMateria(55L);
        assertThat(dto.id()).isEqualTo(55L);
        assertThat(dto.materia().nombre()).isEqualTo("Matemáticas");
        assertThat(dto.docente().nombre()).isEqualTo("Carlos");
    }

    @Test
    void getSemestreMateria_noExiste() {
        when(semestreMateriaRepository.findById(1L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getSemestreMateria(1L)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void getAll_vacio() {
        when(semestreMateriaRepository.findAll()).thenReturn(List.of());
        assertThat(service.getAll()).isEmpty();
    }

    @Test
    void getAll_conElementos() {
        when(semestreMateriaRepository.findAll()).thenReturn(List.of(sm));
        assertThat(service.getAll()).hasSize(1);
    }

    @Test
    void getByDocente_authNull() {
        assertThatThrownBy(() -> service.getByDocente(null)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void getByDocente_noAutenticado() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(false);
        assertThatThrownBy(() -> service.getByDocente(auth)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void getByDocente_usuarioNoEncontrado() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("docente@example.com");
        when(userRepository.findByEmail("docente@example.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getByDocente(auth)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void getByDocente_ok() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("docente@example.com");
        when(userRepository.findByEmail("docente@example.com")).thenReturn(Optional.of(docente));
        when(semestreMateriaRepository.findByDocenteId(33L)).thenReturn(List.of(sm));
        var lista = service.getByDocente(auth);
        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).docente().apellido()).isEqualTo("Lopez");
    }

    @Test
    void create_ok() {
        SemestreMateriaRequestDTO req = new SemestreMateriaRequestDTO(11L, 22L, 33L, 44L, 50);
        when(materiaRepository.findById(11L)).thenReturn(Optional.of(materia));
        when(semestreRepository.findById(22L)).thenReturn(Optional.of(semestre));
        when(userRepository.findById(33L)).thenReturn(Optional.of(docente));
        when(modalidadRepository.findById(44L)).thenReturn(Optional.of(modalidad));
        when(semestreMateriaRepository.save(any())).thenAnswer(inv -> { SemestreMateria x = inv.getArgument(0); x.setId(77L); return x;});
        when(semestreMateriaRepository.findById(77L)).thenAnswer(inv -> Optional.of(sm));
        var dto = service.create(req);
        assertThat(dto.id()).isEqualTo(55L); // porque devolvemos sm al findById
    }

    @Test
    void create_fallaMateriaNoExiste() {
        SemestreMateriaRequestDTO req = new SemestreMateriaRequestDTO(11L, 22L, 33L, 44L, 50);
        when(materiaRepository.findById(11L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.create(req)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void create_fallaSemestreNoExiste() {
        SemestreMateriaRequestDTO req = new SemestreMateriaRequestDTO(11L, 22L, 33L, 44L, 50);
        when(materiaRepository.findById(11L)).thenReturn(Optional.of(materia));
        when(semestreRepository.findById(22L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.create(req)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void create_fallaDocenteNoExiste() {
        SemestreMateriaRequestDTO req = new SemestreMateriaRequestDTO(11L, 22L, 33L, 44L, 50);
        when(materiaRepository.findById(11L)).thenReturn(Optional.of(materia));
        when(semestreRepository.findById(22L)).thenReturn(Optional.of(semestre));
        when(userRepository.findById(33L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.create(req)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void create_fallaModalidadNoExiste() {
        SemestreMateriaRequestDTO req = new SemestreMateriaRequestDTO(11L, 22L, 33L, 44L, 50);
        when(materiaRepository.findById(11L)).thenReturn(Optional.of(materia));
        when(semestreRepository.findById(22L)).thenReturn(Optional.of(semestre));
        when(userRepository.findById(33L)).thenReturn(Optional.of(docente));
        when(modalidadRepository.findById(44L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.create(req)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void update_ok() {
        SemestreMateriaRequestDTO req = new SemestreMateriaRequestDTO(11L, 22L, 33L, 44L, 60);
        when(semestreMateriaRepository.findById(55L)).thenReturn(Optional.of(sm));
        when(materiaRepository.findById(11L)).thenReturn(Optional.of(materia));
        when(semestreRepository.findById(22L)).thenReturn(Optional.of(semestre));
        when(userRepository.findById(33L)).thenReturn(Optional.of(docente));
        when(modalidadRepository.findById(44L)).thenReturn(Optional.of(modalidad));
        when(semestreMateriaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(semestreMateriaRepository.findById(55L)).thenReturn(Optional.of(sm));
        var dto = service.update(55L, req);
        assertThat(dto.cupos()).isEqualTo(60);
    }

    @Test
    void update_noExisteSemestreMateria() {
        SemestreMateriaRequestDTO req = new SemestreMateriaRequestDTO(11L, 22L, 33L, 44L, 60);
        when(semestreMateriaRepository.findById(55L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update(55L, req)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void cerrarMateria_ok() {
        when(semestreMateriaRepository.findById(55L)).thenReturn(Optional.of(sm));
        when(semestreMateriaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        String msg = service.cerrarMateria(55L);
        assertThat(msg).contains("cerrada");
        assertThat(sm.isEstaActiva()).isFalse();
    }

    @Test
    void delete_ok() {
        when(semestreMateriaRepository.findById(55L)).thenReturn(Optional.of(sm));
        service.delete(55L);
        verify(semestreMateriaRepository).delete(sm);
    }

    @Test
    void delete_noExiste() {
        when(semestreMateriaRepository.findById(55L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.delete(55L)).isInstanceOf(NoSuchElementException.class);
    }
}
