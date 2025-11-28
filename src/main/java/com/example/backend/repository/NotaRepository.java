package com.example.backend.repository;

import com.example.backend.models.Nota;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotaRepository extends JpaRepository<Nota, Long> {
    List<Nota> findByMatriculacion_Id(Long matriculacionId);
    Optional<Nota> findById(Long id);
}
