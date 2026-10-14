package com.example.promanage.config;

import com.example.promanage.model.Proyecto;
import com.example.promanage.model.Tarea;
import com.example.promanage.model.Usuario;
import com.example.promanage.repository.ProyectoRepository;
import com.example.promanage.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

/**
 * Carga datos iniciales la primera vez que arranca la aplicación:
 * - Usuarios de prueba (admin / operador) con contraseña cifrada con BCrypt.
 * - Proyectos de ejemplo con sus tareas.
 * Solo inserta si las tablas están vacías (idempotente).
 */
@Configuration
public class DataInitializer {

	@Bean
	public CommandLineRunner cargarDatos(
			UsuarioRepository usuarioRepository,
			ProyectoRepository proyectoRepository,
			PasswordEncoder passwordEncoder) {

		return args -> {
			if (usuarioRepository.count() == 0) {
				usuarioRepository.save(new Usuario(null, "admin",
						passwordEncoder.encode("Admin123"), "ADMIN"));
				usuarioRepository.save(new Usuario(null, "operador",
						passwordEncoder.encode("Operador123"), "OPERADOR"));
			}

			if (proyectoRepository.count() == 0) {
				Proyecto p1 = new Proyecto(null, "Sitio web institucional",
						"Rediseño del portal corporativo",
						LocalDate.now().plusDays(30), "ALTA", "EN_PROGRESO");
				p1.getTareas().add(new Tarea(null, "Definir wireframes", true, p1));
				p1.getTareas().add(new Tarea(null, "Maquetar home", false, p1));
				p1.getTareas().add(new Tarea(null, "Integrar CMS", false, p1));

				Proyecto p2 = new Proyecto(null, "App móvil de ventas",
						"MVP para catálogo y pedidos",
						LocalDate.now().plusDays(60), "MEDIA", "PENDIENTE");
				p2.getTareas().add(new Tarea(null, "Modelo de datos", false, p2));
				p2.getTareas().add(new Tarea(null, "Pantalla de catálogo", false, p2));

				proyectoRepository.save(p1);
				proyectoRepository.save(p2);
			}
		};
	}
}
