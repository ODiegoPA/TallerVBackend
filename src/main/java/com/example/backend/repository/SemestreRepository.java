package com.example.backend.repository;

import com.example.backend.models.Semestre;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.util.Optional;

public interface SemestreRepository extends JpaRepository<Semestre, Long> {
    @EntityGraph(attributePaths = "gestion")
    Optional<Semestre> findById(@Param("id") Long id);
}
