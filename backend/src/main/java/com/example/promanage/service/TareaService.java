package com.example.promanage.service;

import com.example.promanage.model.Proyecto;
import com.example.promanage.model.Tarea;
import com.example.promanage.model.Usuario;
import com.example.promanage.repository.ProyectoRepository;
import com.example.promanage.repository.TareaRepository;
import com.example.promanage.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class TareaService {

	private final TareaRepository tareaRepository;
	private final ProyectoRepository proyectoRepository;
	private final UsuarioRepository usuarioRepository;

	public TareaService(
			TareaRepository tareaRepository,
			ProyectoRepository proyectoRepository,
			UsuarioRepository usuarioRepository) {
		this.tareaRepository = tareaRepository;
		this.proyectoRepository = proyectoRepository;
		this.usuarioRepository = usuarioRepository;
	}

	@Transactional(readOnly = true)
	public List<Tarea> listarPorProyecto(Long proyectoId) {
		return tareaRepository.buscarPorProyecto(proyectoId);
	}

	@Transactional(readOnly = true)
	public List<Tarea> listarPendientesPorProyecto(Long proyectoId) {
		return tareaRepository.buscarPendientesPorProyecto(proyectoId);
	}

	/** Crea una tarea asociada a un proyecto y opcionalmente a un operador. */
	@Transactional
	public Optional<Tarea> crear(Long proyectoId, String titulo, Long operadorId) {
		return proyectoRepository.findById(proyectoId).map(proyecto -> {
			Tarea tarea = new Tarea();
			tarea.setTitulo(titulo);
			tarea.setCompletada(false);
			tarea.setProyecto(proyecto);
			tarea.setOperador(resolverOperador(operadorId));
			Tarea guardada = tareaRepository.save(tarea);
			actualizarEstadoProyecto(proyecto);
			return guardada;
		});
	}

	/** Reasigna el operador de una tarea (solo ADMIN). */
	@Transactional
	public Optional<Tarea> asignarOperador(Long tareaId, Long operadorId) {
		return tareaRepository.findById(tareaId).map(tarea -> {
			tarea.setOperador(resolverOperador(operadorId));
			return tareaRepository.save(tarea);
		});
	}

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

	private Usuario resolverOperador(Long operadorId) {
		if (operadorId == null) {
			return null;
		}
		Usuario usuario = usuarioRepository.findById(operadorId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operador no encontrado"));
		if (!"OPERADOR".equals(usuario.getRol())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario seleccionado no es OPERADOR");
		}
		return usuario;
	}

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
