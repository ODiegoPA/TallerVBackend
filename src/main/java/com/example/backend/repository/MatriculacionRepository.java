package com.example.backend.repository;

import com.example.backend.models.Matriculacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatriculacionRepository extends JpaRepository<Matriculacion, Long> {
    List<Matriculacion> findBySemestreMateria_Id(Long semestreMateriaId);
    Optional<Matriculacion> findById(Long id);
    List<Matriculacion> findByAlumno_Id(Long alumnoId);
    Optional<Matriculacion> findByAlumnoIdAndSemestreMateriaId(Long alumnoId, Long semestreMateriaId);
}
