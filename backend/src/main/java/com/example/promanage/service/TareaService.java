package com.example.promanage.service;

import com.example.promanage.model.Proyecto;
import com.example.promanage.model.Tarea;
import com.example.promanage.repository.ProyectoRepository;
import com.example.promanage.repository.TareaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TareaService {

	private final TareaRepository tareaRepository;
	private final ProyectoRepository proyectoRepository;

	public TareaService(TareaRepository tareaRepository, ProyectoRepository proyectoRepository) {
		this.tareaRepository = tareaRepository;
		this.proyectoRepository = proyectoRepository;
	}

	@Transactional(readOnly = true)
	public List<Tarea> listarPorProyecto(Long proyectoId) {
		return tareaRepository.buscarPorProyecto(proyectoId);
	}

	@Transactional(readOnly = true)
	public List<Tarea> listarPendientesPorProyecto(Long proyectoId) {
		return tareaRepository.buscarPendientesPorProyecto(proyectoId);
	}

	/** Crea una tarea asociada a un proyecto existente y recalcula el estado del proyecto. */
	@Transactional
	public Optional<Tarea> crear(Long proyectoId, Tarea tarea) {
		return proyectoRepository.findById(proyectoId).map(proyecto -> {
			tarea.setId(null);
			tarea.setProyecto(proyecto);
			Tarea guardada = tareaRepository.save(tarea);
			actualizarEstadoProyecto(proyecto);
			return guardada;
		});
	}

	/**
	 * Cambia el estado de la tarea y sincroniza el estado del proyecto:
	 * - ninguna hecha → PENDIENTE
	 * - alguna hecha (no todas) → EN_PROGRESO
	 * - todas hechas → COMPLETADA
	 */
	@Transactional
	public Optional<Tarea> cambiarEstado(Long tareaId, boolean completada) {
		return tareaRepository.findById(tareaId).map(tarea -> {
			tarea.setCompletada(completada);
			Tarea guardada = tareaRepository.save(tarea);
			actualizarEstadoProyecto(tarea.getProyecto());
			return guardada;
		});
	}

	@Transactional
	public boolean eliminar(Long tareaId) {
		Optional<Tarea> opt = tareaRepository.findById(tareaId);
		if (opt.isEmpty()) {
			return false;
		}
		Long proyectoId = opt.get().getProyecto().getId();
		tareaRepository.deleteById(tareaId);
		proyectoRepository.findById(proyectoId).ifPresent(this::actualizarEstadoProyecto);
		return true;
	}

	/**
	 * Regla de negocio: el avance de las tareas determina el estado del proyecto.
	 */
	private void actualizarEstadoProyecto(Proyecto proyecto) {
		List<Tarea> tareas = tareaRepository.buscarPorProyecto(proyecto.getId());
		if (tareas.isEmpty()) {
			proyecto.setEstado("PENDIENTE");
		} else {
			long hechas = tareas.stream().filter(Tarea::isCompletada).count();
			if (hechas == 0) {
				proyecto.setEstado("PENDIENTE");
			} else if (hechas == tareas.size()) {
				proyecto.setEstado("COMPLETADA");
			} else {
				proyecto.setEstado("EN_PROGRESO");
			}
		}
		proyectoRepository.save(proyecto);
	}
}
