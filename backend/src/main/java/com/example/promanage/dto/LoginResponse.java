package com.example.promanage.dto;

/**
 * Respuesta del login. Incluye el token JWT y, por comodidad para el cliente
 * (Angular), el username y el rol autenticado.
 */
public record LoginResponse(
		String token,
		String username,
		String rol) {
}
