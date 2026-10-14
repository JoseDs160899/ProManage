package com.example.promanage.service;

import com.example.promanage.model.Usuario;
import com.example.promanage.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Recupera el usuario desde MySQL y lo adapta al modelo UserDetails de Spring
 * Security. El rol almacenado (ADMIN/OPERADOR) se expone como authority ROLE_*.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UsuarioRepository usuarioRepository;

	public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

		return User.builder()
				.username(usuario.getUsername())
				.password(usuario.getPassword())
				.roles(usuario.getRol())
				.build();
	}
}
