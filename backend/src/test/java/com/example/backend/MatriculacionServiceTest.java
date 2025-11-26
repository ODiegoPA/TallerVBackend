package com.example.backend;

import com.example.backend.dto.materia.MateriaLiteDto;
import com.example.backend.dto.matriculacion.MatriculacionRequestDto;
import com.example.backend.dto.matriculacion.MatriculacionResponseDto;
import com.example.backend.dto.nota.ValorRequestDto;
import com.example.backend.dto.user.UserLiteDto;
import com.example.backend.models.*;
import com.example.backend.repository.*;
import com.example.backend.service.MatriculacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatriculacionServiceTest {

    @Mock private MatriculacionRepository matriculacionRepository;
    @Mock private UserRepository userRepository;
    @Mock private SemestreMateriaRepository semestreMateriaRepository;
    @Mock private EvaluacionRepository evaluacionRepository;
    @Mock private NotaRepository notaRepository;

    @InjectMocks
    private MatriculacionService service;

    private User alumno;
    private User docente;
    private Materia materia;
    private Modalidad modalidad;
    private SemestreMateria semestreMateria;
    private Matriculacion matriculacion;

    @BeforeEach
    void setup() {
        alumno = new User();
        alumno.setId(1L);
        alumno.setNombre("Juan");
        alumno.setApellido("Perez");
        alumno.setEmail("juan@example.com");

        docente = new User();
        docente.setId(2L);
        docente.setNombre("Carlos");
        docente.setApellido("Lopez");

        modalidad = new Modalidad();
        modalidad.setId(1L);
        modalidad.setFaltas_permitidas(5);

        materia = new Materia();
        materia.setId(10L);
        materia.setNombre("Matemáticas");

        semestreMateria = new SemestreMateria();
        semestreMateria.setId(100L);
        semestreMateria.setMateria(materia);
        semestreMateria.setDocente(docente);
        semestreMateria.setModalidad(modalidad);

        matriculacion = new Matriculacion();
        matriculacion.setId(50L);
        matriculacion.setAlumno(alumno);
        matriculacion.setSemestreMateria(semestreMateria);
        matriculacion.setFaltas(0);
        matriculacion.setNotaFinal(0);
        matriculacion.setEstaAprobado(false);
        matriculacion.setEstaConsolidado(false);
    }

    // -------------------------------------------------------------
    // GET MATRICULACION
    // -------------------------------------------------------------
    @Test
    void getMatriculacion_retornaDTO_siExiste() {
        when(matriculacionRepository.findById(50L)).thenReturn(Optional.of(matriculacion));

        MatriculacionResponseDto dto = service.getMatriculacion(50L);

        assertThat(dto.id()).isEqualTo(50L);
        assertThat(dto.alumno().nombre()).isEqualTo("Juan");
        assertThat(dto.docente().nombre()).isEqualTo("Carlos");
        assertThat(dto.materia().nombre()).isEqualTo("Matemáticas");
    }

    @Test
    void getMatriculacion_lanzaExcepcion_siNoExiste() {
        when(matriculacionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getMatriculacion(1L))
                .isInstanceOf(Exception.class);
    }

    // -------------------------------------------------------------
    // GET BY SEMESTRE MATERIA
    // -------------------------------------------------------------
    @Test
    void getBySemestreMateriaId_retornaListaVacia() {
        when(matriculacionRepository.findBySemestreMateria_Id(100L)).thenReturn(List.of());

        List<MatriculacionResponseDto> lista = service.getBySemestreMateriaId(100L);

        assertThat(lista).isEmpty();
    }

    @Test
    void getBySemestreMateriaId_retornaListaDeDTOs() {
        when(matriculacionRepository.findBySemestreMateria_Id(100L))
                .thenReturn(List.of(matriculacion));

        List<MatriculacionResponseDto> lista = service.getBySemestreMateriaId(100L);

        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).alumno().nombre()).isEqualTo("Juan");
    }

    // -------------------------------------------------------------
    // GET BY ALUMNO (AUTENTICACIÓN)
    // -------------------------------------------------------------
    @Test
    void getByAlumno_lanzaExcepcion_siAuthEsNull() {
        assertThatThrownBy(() -> service.getByAlumno(null))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void getByAlumno_lanzaExcepcion_siNoAutenticado() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(false);

        assertThatThrownBy(() -> service.getByAlumno(auth))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void getByAlumno_retornaListaDeDTOs() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("juan@example.com");

        when(userRepository.findByEmail("juan@example.com"))
                .thenReturn(Optional.of(alumno));

        when(matriculacionRepository.findByAlumno_Id(1L))
                .thenReturn(List.of(matriculacion));

        List<MatriculacionResponseDto> lista = service.getByAlumno(auth);

        assertThat(lista).hasSize(1);
        assertThat(lista.get(0).alumno().nombre()).isEqualTo("Juan");
    }

    // -------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------
    @Test
    void create_creaMatriculacionCorrectamente() {
        MatriculacionRequestDto dto = new MatriculacionRequestDto(1L, 100L);

        when(matriculacionRepository.findByAlumnoIdAndSemestreMateriaId(1L, 100L))
                .thenReturn(Optional.empty());

        when(userRepository.findById(1L)).thenReturn(Optional.of(alumno));
        when(semestreMateriaRepository.findById(100L)).thenReturn(Optional.of(semestreMateria));

        for (int i = 1; i <= 5; i++) {
            Evaluacion e = new Evaluacion();
            e.setId(i);
            e.setNombre("Eval " + i);
            when(evaluacionRepository.findById((long) i)).thenReturn(Optional.of(e));
        }

        when(matriculacionRepository.save(any(Matriculacion.class)))
                .thenAnswer(inv -> {
                    Matriculacion m = inv.getArgument(0);
                    m.setId(999L);
                    return m;
                });

        when(matriculacionRepository.findById(999L))
                .thenReturn(Optional.of(matriculacion));

        service.create(dto);

        verify(matriculacionRepository, times(1)).save(any());
    }

    @Test
    void create_lanzaExcepcion_siAlumnoYaMatriculado() {
        MatriculacionRequestDto dto = new MatriculacionRequestDto(1L, 100L);

        when(matriculacionRepository.findByAlumnoIdAndSemestreMateriaId(1L, 100L))
                .thenReturn(Optional.of(matriculacion));

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void create_lanzaExcepcion_siAlumnoNoExiste() {
        MatriculacionRequestDto dto = new MatriculacionRequestDto(1L, 100L);

        when(matriculacionRepository.findByAlumnoIdAndSemestreMateriaId(1L, 100L))
                .thenReturn(Optional.empty());

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(Exception.class);
    }

    // -------------------------------------------------------------
    // CALCULAR NOTAS
    // -------------------------------------------------------------
    @Test
    void calcularNotas_sumaCorrectamente() {
        Nota n1 = new Nota(); n1.setCalificacion(10.0);
        Nota n2 = new Nota(); n2.setCalificacion(30.0);
        Nota n3 = new Nota(); n3.setCalificacion(20.0);

        matriculacion.setNotas(List.of(n1, n2, n3));

        when(matriculacionRepository.findById(50L)).thenReturn(Optional.of(matriculacion));

        String msg = service.calcularNotas(50L);

        assertThat(matriculacion.getNotaFinal()).isEqualTo(60);
        assertThat(msg).contains("60");
    }

    @Test
    void calcularNotas_apruebaSiNotaMayor51() {
        Nota n1 = new Nota(); n1.setCalificacion(60.0);
        matriculacion.setNotas(List.of(n1));

        when(matriculacionRepository.findById(50L)).thenReturn(Optional.of(matriculacion));

        service.calcularNotas(50L);

        assertThat(matriculacion.getEstaAprobado()).isTrue();
    }

    // -------------------------------------------------------------
    // SUBIR FALTAS
    // -------------------------------------------------------------
    @Test
    void subirFaltas_sumaCorrectamente() {
        ValorRequestDto dto = new ValorRequestDto(3);

        when(matriculacionRepository.findById(50L))
                .thenReturn(Optional.of(matriculacion));

        String msg = service.subirFaltas(50L, dto);

        assertThat(matriculacion.getFaltas()).isEqualTo(3);
        assertThat(msg).contains("3");
    }

    @Test
    void subirFaltas_repruebaSiExcedeLimite() {
        ValorRequestDto dto = new ValorRequestDto(10);
        when(matriculacionRepository.findById(50L))
                .thenReturn(Optional.of(matriculacion));

        String msg = service.subirFaltas(50L, dto);

        assertThat(matriculacion.getEstaAprobado()).isFalse();
        assertThat(msg).contains("excedido");
    }

    // -------------------------------------------------------------
    // CONSOLIDAR TODOS
    // -------------------------------------------------------------
    @Test
    void consolidarNotasATodos_consolidaCorrectamente() {
        Matriculacion m1 = new Matriculacion();
        m1.setSemestreMateria(semestreMateria);
        m1.setFaltas(0);
        m1.setNotaFinal(80);

        Matriculacion m2 = new Matriculacion();
        m2.setSemestreMateria(semestreMateria);
        m2.setFaltas(10);

        when(matriculacionRepository.findBySemestreMateria_Id(100L))
                .thenReturn(List.of(m1, m2));

        service.consolidarNotasATodos(100L);

        assertThat(m1.getEstaConsolidado()).isTrue();
        assertThat(m2.getEstaConsolidado()).isTrue();
    }

    // -------------------------------------------------------------
    // CONSOLIDAR INDIVIDUAL
    // -------------------------------------------------------------
    @Test
    void consolidarNotasIndividual_consolidaAprobado() {
        matriculacion.setNotas(List.of(new Nota(){{
            setCalificacion(60.0);
        }}));

        when(matriculacionRepository.findById(50L))
                .thenReturn(Optional.of(matriculacion));

        String msg = service.consolidarNotasIndividual(50L);

        assertThat(msg).contains("aprobado");
        assertThat(matriculacion.getEstaConsolidado()).isTrue();
    }

    // -------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------
    @Test
    void delete_eliminaCorrectamente() {
        when(matriculacionRepository.findById(50L))
                .thenReturn(Optional.of(matriculacion));

        service.delete(50L);

        verify(matriculacionRepository).delete(matriculacion);
    }

    @Test
    void delete_lanzaExcepcion_siNoExiste() {
        when(matriculacionRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(1L))
                .isInstanceOf(Exception.class);
    }
}
