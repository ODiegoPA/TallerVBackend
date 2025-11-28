package com.example.backend.repository;

import com.example.backend.models.Evaluacion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class EvaluacionRepositoryIntegrationTest {

    @Autowired
    private EvaluacionRepository evaluacionRepository;

    @Test
    void saveYFindById_debePersistirYRecuperar() {
        Evaluacion e = new Evaluacion();
        e.setNombre("Examen Parcial");
        Evaluacion saved = evaluacionRepository.save(e);

        assertThat(saved.getId()).isNotZero();

        Optional<Evaluacion> found = evaluacionRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getNombre()).isEqualTo("Examen Parcial");
    }

    @Test
    void findById_debeRetornarEmpty_siNoExiste() {
        Optional<Evaluacion> notFound = evaluacionRepository.findById(999L);
        assertThat(notFound).isEmpty();
    }
}

