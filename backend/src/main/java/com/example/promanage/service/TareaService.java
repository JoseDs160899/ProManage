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

	/** Crea una tarea asociada a un proyecto existente. */
	@Transactional
	public Optional<Tarea> crear(Long proyectoId, Tarea tarea) {
		return proyectoRepository.findById(proyectoId).map(proyecto -> {
			tarea.setId(null);
			tarea.setProyecto(proyecto);
			return tareaRepository.save(tarea);
		});
	}

	@Transactional
	public Optional<Tarea> cambiarEstado(Long tareaId, boolean completada) {
		return tareaRepository.findById(tareaId).map(tarea -> {
			tarea.setCompletada(completada);
			return tareaRepository.save(tarea);
		});
	}

	@Transactional
	public boolean eliminar(Long tareaId) {
		if (!tareaRepository.existsById(tareaId)) {
			return false;
		}
		tareaRepository.deleteById(tareaId);
		return true;
	}
}
