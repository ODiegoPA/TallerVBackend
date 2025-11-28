package com.example.backend.repository;

import com.example.backend.models.Evaluacion;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {
    Optional<Evaluacion> findById(@Param("id") Long id);
}
