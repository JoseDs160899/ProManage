package com.example.promanage.dto;

import jakarta.validation.constraints.NotBlank;

/** Credenciales recibidas en POST /api/auth/login. */
public record LoginRequest(
		@NotBlank String username,
		@NotBlank String password) {
}
