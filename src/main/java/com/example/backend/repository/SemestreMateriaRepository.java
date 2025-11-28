package com.example.backend.repository;

import com.example.backend.models.SemestreMateria;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SemestreMateriaRepository extends JpaRepository<SemestreMateria, Long> {
    @EntityGraph(attributePaths = {"materia", "docente", "modalidad"})
    Optional<SemestreMateria> findById(Long id);
    List<SemestreMateria> findByDocenteId(Long docenteId);
}
