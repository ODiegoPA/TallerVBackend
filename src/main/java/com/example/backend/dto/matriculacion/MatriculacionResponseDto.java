package com.example.backend.dto.matriculacion;

import com.example.backend.dto.materia.MateriaLiteDto;
import com.example.backend.dto.user.UserLiteDto;

public record MatriculacionResponseDto(Long id, UserLiteDto alumno, UserLiteDto docente, MateriaLiteDto materia, Integer faltas, Boolean estaAprobado, Integer notaFinal, Boolean estaConsolidado, Long semestreMateriaId) {
}
