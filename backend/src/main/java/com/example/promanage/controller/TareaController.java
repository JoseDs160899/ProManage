package com.example.promanage.controller;

import com.example.promanage.model.Tarea;
import com.example.promanage.service.TareaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

	private final TareaService tareaService;

	public TareaController(TareaService tareaService) {
		this.tareaService = tareaService;
	}

	/** Lista las tareas de un proyecto (consulta JPQL con JOIN). */
	@GetMapping("/proyecto/{proyectoId}")
	public List<Tarea> listarPorProyecto(@PathVariable Long proyectoId) {
		return tareaService.listarPorProyecto(proyectoId);
	}

	/** Lista solo las tareas pendientes de un proyecto. */
	@GetMapping("/proyecto/{proyectoId}/pendientes")
	public List<Tarea> listarPendientes(@PathVariable Long proyectoId) {
		return tareaService.listarPendientesPorProyecto(proyectoId);
	}

	@PostMapping("/proyecto/{proyectoId}")
	public ResponseEntity<Tarea> crear(@PathVariable Long proyectoId, @Valid @RequestBody Tarea tarea) {
		return tareaService.crear(proyectoId, tarea)
				.map(creada -> ResponseEntity.status(HttpStatus.CREATED).body(creada))
				.orElse(ResponseEntity.notFound().build());
	}

	/**
	 * Marca/desmarca una tarea. ADMIN puede completar y deshacer.
	 * OPERADOR solo puede completar (valor=true); desmarcar → 403.
	 */
	@PatchMapping("/{id}/completar")
	public ResponseEntity<Tarea> completar(
			@PathVariable Long id,
			@RequestParam(defaultValue = "true") boolean valor,
			Authentication authentication) {

		boolean esAdmin = authentication.getAuthorities().stream()
				.anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

		if (!valor && !esAdmin) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		return tareaService.cambiarEstado(id, valor)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		return tareaService.eliminar(id)
				? ResponseEntity.noContent().build()
				: ResponseEntity.notFound().build();
	}
}
