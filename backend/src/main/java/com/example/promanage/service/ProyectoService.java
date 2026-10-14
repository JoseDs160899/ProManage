package com.example.promanage.service;

import com.example.promanage.model.Proyecto;
import com.example.promanage.model.Tarea;
import com.example.promanage.repository.ProyectoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProyectoService {

	private final ProyectoRepository proyectoRepository;

	public ProyectoService(ProyectoRepository proyectoRepository) {
		this.proyectoRepository = proyectoRepository;
	}

	@Transactional(readOnly = true)
	public List<Proyecto> listar() {
		return proyectoRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Optional<Proyecto> obtenerPorId(Long id) {
		return proyectoRepository.findById(id);
	}

	@Transactional(readOnly = true)
	public List<Proyecto> buscarPorTitulo(String texto) {
		return proyectoRepository.buscarPorTitulo(texto);
	}

	@Transactional(readOnly = true)
	public List<Proyecto> buscarPorEstado(String estado) {
		return proyectoRepository.buscarPorEstado(estado);
	}

	@Transactional(readOnly = true)
	public long contarPorEstado(String estado) {
		return proyectoRepository.contarPorEstado(estado);
	}

	@Transactional
	public Proyecto crear(Proyecto proyecto) {
		proyecto.setId(null);
		if (proyecto.getEstado() == null || proyecto.getEstado().isBlank()) {
			proyecto.setEstado("PENDIENTE");
		}
		return proyectoRepository.save(proyecto);
	}

	@Transactional
	public Optional<Proyecto> actualizar(Long id, Proyecto datos) {
		return proyectoRepository.findById(id).map(existente -> {
			existente.setTitulo(datos.getTitulo());
			existente.setDescripcion(datos.getDescripcion());
			existente.setFechaVencimiento(datos.getFechaVencimiento());
			existente.setPrioridad(datos.getPrioridad());
			if (datos.getEstado() != null && !datos.getEstado().isBlank()) {
				existente.setEstado(datos.getEstado());
			}
			return proyectoRepository.save(existente);
		});
	}

	@Transactional
	public boolean eliminar(Long id) {
		if (!proyectoRepository.existsById(id)) {
			return false;
		}
		proyectoRepository.deleteById(id);
		return true;
	}

	/**
	 * Operación transaccional: marca el proyecto como COMPLETADA y, en la misma
	 * transacción, completa todas sus tareas. Si algo falla, se revierte todo
	 * (consistencia). Devuelve el proyecto actualizado o vacío si no existe.
	 */
	@Transactional
	public Optional<Proyecto> completarProyecto(Long id) {
		return proyectoRepository.findById(id).map(proyecto -> {
			proyecto.setEstado("COMPLETADA");
			for (Tarea tarea : proyecto.getTareas()) {
				tarea.setCompletada(true);
			}
			return proyectoRepository.save(proyecto);
		});
	}
}
