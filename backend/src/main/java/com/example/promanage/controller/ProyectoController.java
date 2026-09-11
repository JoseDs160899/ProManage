package com.example.promanage.controller;

import com.example.promanage.model.Proyecto;
import com.example.promanage.service.ProyectoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

	@PostMapping
	public ResponseEntity<Proyecto> crear(@RequestBody Proyecto proyecto) {
		Proyecto creado = proyectoService.crear(proyecto);
		return ResponseEntity.status(HttpStatus.CREATED).body(creado);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Proyecto> actualizar(@PathVariable Long id, @RequestBody Proyecto proyecto) {
		return proyectoService.actualizar(id, proyecto)
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
