package com.example.backend.dto.gestion;

import com.example.backend.dto.modalidad.ModalidadLiteDto;
import com.example.backend.dto.semestre.SemestreLiteDto;

import java.util.List;

public record GestionResponseDto(Long id, Integer ano, List<SemestreLiteDto> semestres, List<ModalidadLiteDto> modalidades) {}
