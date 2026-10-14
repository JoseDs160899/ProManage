package com.example.promanage.controller;

import com.example.promanage.model.Proyecto;
import com.example.promanage.service.ProyectoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/projects")
public class ProyectoController {

	private final ProyectoService proyectoService;

	public ProyectoController(ProyectoService proyectoService) {
		this.proyectoService = proyectoService;
	}

	@GetMapping
	public List<Proyecto> listar() {
		return proyectoService.listar();
	}

	@GetMapping("/{id}")
	public ResponseEntity<Proyecto> obtener(@PathVariable Long id) {
		return proyectoService.obtenerPorId(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	/** Consulta JPQL: búsqueda por título (parcial, sin distinguir mayúsculas). */
	@GetMapping("/buscar")
	public List<Proyecto> buscar(@RequestParam("texto") String texto) {
		return proyectoService.buscarPorTitulo(texto);
	}

	/** Consulta JPQL: filtra por estado. */
	@GetMapping("/estado/{estado}")
	public List<Proyecto> porEstado(@PathVariable String estado) {
		return proyectoService.buscarPorEstado(estado);
	}

	/** Consulta JPQL de agregación: cuenta proyectos por estado. */
	@GetMapping("/contar/{estado}")
	public Map<String, Object> contar(@PathVariable String estado) {
		return Map.of("estado", estado, "total", proyectoService.contarPorEstado(estado));
	}

	@PostMapping
	public ResponseEntity<Proyecto> crear(@Valid @RequestBody Proyecto proyecto) {
		Proyecto creado = proyectoService.crear(proyecto);
		return ResponseEntity.status(HttpStatus.CREATED).body(creado);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Proyecto> actualizar(@PathVariable Long id, @Valid @RequestBody Proyecto proyecto) {
		return proyectoService.actualizar(id, proyecto)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	/** Operación transaccional: completa el proyecto y todas sus tareas. */
	@PatchMapping("/{id}/completar")
	public ResponseEntity<Proyecto> completar(@PathVariable Long id) {
		return proyectoService.completarProyecto(id)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		boolean eliminado = proyectoService.eliminar(id);
		return eliminado
				? ResponseEntity.noContent().build()
				: ResponseEntity.notFound().build();
	}
}
