package com.example.backend.dto.modalidad;

import com.example.backend.dto.gestion.GestionLiteDto;

public record ModalidadResponseDto (Long id, String nombre, Integer faltasPermitidas, GestionLiteDto gestion) {
}
