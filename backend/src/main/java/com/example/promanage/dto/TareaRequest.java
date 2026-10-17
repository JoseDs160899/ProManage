package com.example.promanage.dto;

import jakarta.validation.constraints.NotBlank;

/** Cuerpo para crear o reasignar una tarea. */
public record TareaRequest(
		@NotBlank String titulo,
		Long operadorId) {
}
