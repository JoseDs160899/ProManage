package com.example.promanage.controller;

import com.example.promanage.dto.LoginRequest;
import com.example.promanage.dto.LoginResponse;
import com.example.promanage.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	/**
	 * Valida las credenciales con el AuthenticationManager (que a su vez usa
	 * CustomUserDetailsService + BCrypt). Si son correctas, genera un JWT.
	 * Credenciales inválidas -> 401 (AuthenticationException).
	 */
	@PostMapping("/login")
	public LoginResponse login(@Valid @RequestBody LoginRequest request) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.username(), request.password()));

		String token = jwtService.generarToken(authentication.getName());
		String rol = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.map(a -> a.replace("ROLE_", ""))
				.findFirst()
				.orElse("");

		return new LoginResponse(token, authentication.getName(), rol);
	}
}
