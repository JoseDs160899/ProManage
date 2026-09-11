package com.example.promanage.service;

import com.example.promanage.model.Proyecto;
import com.example.promanage.repository.ProyectoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProyectoService {

	private final ProyectoRepository proyectoRepository;

	public ProyectoService(ProyectoRepository proyectoRepository) {
		this.proyectoRepository = proyectoRepository;
	}

	public List<Proyecto> listar() {
		return proyectoRepository.findAll();
	}

	public Optional<Proyecto> obtenerPorId(Long id) {
		return proyectoRepository.findById(id);
	}

	public Proyecto crear(Proyecto proyecto) {
		proyecto.setId(null);
		if (proyecto.getEstado() == null || proyecto.getEstado().isBlank()) {
			proyecto.setEstado("PENDIENTE");
		}
		return proyectoRepository.save(proyecto);
	}

	public Optional<Proyecto> actualizar(Long id, Proyecto datos) {
		return proyectoRepository.findById(id).map(existente -> {
			datos.setId(id);
			return proyectoRepository.save(datos);
		});
	}

	public boolean eliminar(Long id) {
		return proyectoRepository.deleteById(id);
	}
}
