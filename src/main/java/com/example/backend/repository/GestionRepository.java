package com.example.backend.repository;

import com.example.backend.models.Gestion;
import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GestionRepository extends JpaRepository<Gestion, Long> {
    @EntityGraph(attributePaths = {"semestres", "modalidades"})
    Optional<Gestion> findById(Long id);

    @EntityGraph(attributePaths = {"semestres", "modalidades"})
    List<Gestion> findAll();
}
