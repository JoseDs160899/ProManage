package com.example.promanage.controller;

import com.example.promanage.model.Proyecto;
import com.example.promanage.security.JwtService;
import com.example.promanage.service.CustomUserDetailsService;
import com.example.promanage.service.ProyectoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba la capa web del controlador. Se desactivan los filtros de seguridad
 * (addFilters = false) para probar el comportamiento HTTP del endpoint de forma
 * aislada; la autorización por roles se valida en pruebas de integración/manuales.
 */
@WebMvcTest(ProyectoController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProyectoControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockitoBean
	ProyectoService proyectoService;

	// Necesarios para que el JwtAuthenticationFilter (bean Filter) pueda construirse.
	@MockitoBean
	JwtService jwtService;

	@MockitoBean
	CustomUserDetailsService customUserDetailsService;

	@Test
	void listar_debeRetornar200YJson() throws Exception {
		when(proyectoService.listar()).thenReturn(List.of(
				new Proyecto(1L, "A", "d", LocalDate.now(), "ALTA", "PENDIENTE")));

		mockMvc.perform(get("/api/projects"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].titulo").value("A"));
	}

	@Test
	void obtener_inexistente_debeRetornar404() throws Exception {
		when(proyectoService.obtenerPorId(99L)).thenReturn(Optional.empty());

		mockMvc.perform(get("/api/projects/99"))
				.andExpect(status().isNotFound());
	}

	@Test
	void crear_debeRetornar201() throws Exception {
		Proyecto body = new Proyecto(null, "Nuevo", "d", LocalDate.now(), "MEDIA", null);
		when(proyectoService.crear(any())).thenReturn(
				new Proyecto(3L, "Nuevo", "d", LocalDate.now(), "MEDIA", "PENDIENTE"));

		mockMvc.perform(post("/api/projects")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(body)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(3))
				.andExpect(jsonPath("$.estado").value("PENDIENTE"));
	}

	@Test
	void actualizar_inexistente_debeRetornar404() throws Exception {
		Proyecto body = new Proyecto(null, "Valido", "d", LocalDate.now(), "ALTA", "PENDIENTE");
		when(proyectoService.actualizar(eq(99L), any())).thenReturn(Optional.empty());

		mockMvc.perform(put("/api/projects/99")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(body)))
				.andExpect(status().isNotFound());
	}

	@Test
	void completar_existente_debeRetornar200() throws Exception {
		when(proyectoService.completarProyecto(1L)).thenReturn(Optional.of(
				new Proyecto(1L, "A", "d", LocalDate.now(), "ALTA", "COMPLETADA")));

		mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
						.patch("/api/projects/1/completar"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.estado").value("COMPLETADA"));
	}

	@Test
	void eliminar_existente_debeRetornar204() throws Exception {
		when(proyectoService.eliminar(1L)).thenReturn(true);

		mockMvc.perform(delete("/api/projects/1"))
				.andExpect(status().isNoContent());
	}
}
