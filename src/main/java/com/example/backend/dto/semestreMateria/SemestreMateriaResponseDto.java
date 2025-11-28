package com.example.backend.dto.semestreMateria;

import com.example.backend.dto.materia.MateriaLiteDto;
import com.example.backend.dto.modalidad.ModalidadLiteDto;
import com.example.backend.dto.semestre.SemestreLiteDto;
import com.example.backend.dto.user.UserLiteDto;

public record SemestreMateriaResponseDto (Long id, MateriaLiteDto materia, SemestreLiteDto semestre, UserLiteDto docente, ModalidadLiteDto modalidad, Integer cupos, Boolean estaActivo) {}
