package com.example.backend.repository;

import com.example.backend.models.Modalidad;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ModalidadRepository extends JpaRepository<Modalidad, Long> {
    @EntityGraph(attributePaths = "gestion")
    @Query("select m from Modalidad m join fetch m.gestion where m.id = :id")
    Optional<Modalidad> findById(@Param("id") Long id);
    List<Modalidad> findByGestionId(Long gestionId);

}
