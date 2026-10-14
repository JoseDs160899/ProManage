package com.example.promanage.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Endpoint público de prueba: responde 200 sin necesidad de token.
 * Sirve como evidencia de "recurso público → 200 sin autenticación".
 */
@RestController
@RequestMapping("/api/publico")
public class PublicoController {

	@GetMapping("/saludo")
	public Map<String, String> saludo() {
		return Map.of(
				"mensaje", "API ProManage operativa",
				"estado", "publico");
	}
}
