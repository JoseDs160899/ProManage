package com.example.promanage.controller;

import com.example.promanage.model.Usuario;
import com.example.promanage.repository.UsuarioRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Expone el listado de operadores para que el ADMIN los asigne a las tareas.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

	private final UsuarioRepository usuarioRepository;

	public UsuarioController(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	@GetMapping("/operadores")
	public List<Usuario> listarOperadores() {
		return usuarioRepository.listarOperadores();
	}
}
