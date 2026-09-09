package com.example.promanage.repository;

import com.example.promanage.model.Proyecto;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ProyectoRepository {

	private final List<Proyecto> proyectos = new ArrayList<>();
	private final AtomicLong secuencia = new AtomicLong(1);

	public ProyectoRepository() {
		proyectos.add(new Proyecto(
				secuencia.getAndIncrement(),
				"Sitio web institucional",
				"Rediseño del portal corporativo",
				LocalDate.now().plusDays(30),
				"ALTA",
				"EN_PROGRESO"));
		proyectos.add(new Proyecto(
				secuencia.getAndIncrement(),
				"App móvil de ventas",
				"MVP para catálogo y pedidos",
				LocalDate.now().plusDays(60),
				"MEDIA",
				"PENDIENTE"));
	}

	public List<Proyecto> findAll() {
		return new ArrayList<>(proyectos);
	}

	public Optional<Proyecto> findById(Long id) {
		return proyectos.stream()
				.filter(p -> p.getId().equals(id))
				.findFirst();
	}

	public Proyecto save(Proyecto proyecto) {
		if (proyecto.getId() == null) {
			proyecto.setId(secuencia.getAndIncrement());
			proyectos.add(proyecto);
			return proyecto;
		}

		return findById(proyecto.getId())
				.map(existente -> {
					existente.setTitulo(proyecto.getTitulo());
					existente.setDescripcion(proyecto.getDescripcion());
					existente.setFechaVencimiento(proyecto.getFechaVencimiento());
					existente.setPrioridad(proyecto.getPrioridad());
					existente.setEstado(proyecto.getEstado());
					return existente;
				})
				.orElseGet(() -> {
					proyectos.add(proyecto);
					return proyecto;
				});
	}

	public boolean deleteById(Long id) {
		return proyectos.removeIf(p -> p.getId().equals(id));
	}
}
