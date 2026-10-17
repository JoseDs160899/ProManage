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
import java.util.List;

/**
 * Carga datos iniciales al arrancar:
 * - Usuarios (admin + varios operadores) con contraseña BCrypt.
 * - Proyectos de ejemplo con tareas asignadas a operadores.
 */
@Configuration
public class DataInitializer {

	@Bean
	public CommandLineRunner cargarDatos(
			UsuarioRepository usuarioRepository,
			ProyectoRepository proyectoRepository,
			PasswordEncoder passwordEncoder) {

		return args -> {
			asegurarUsuario(usuarioRepository, passwordEncoder, "admin", "Admin123", "ADMIN");
			asegurarUsuario(usuarioRepository, passwordEncoder, "operador", "Operador123", "OPERADOR");
			asegurarUsuario(usuarioRepository, passwordEncoder, "operador2", "Operador123", "OPERADOR");
			asegurarUsuario(usuarioRepository, passwordEncoder, "maria", "Operador123", "OPERADOR");
			asegurarUsuario(usuarioRepository, passwordEncoder, "carlos", "Operador123", "OPERADOR");

			Usuario op1 = usuarioRepository.findByUsername("operador").orElseThrow();
			Usuario op2 = usuarioRepository.findByUsername("operador2").orElseThrow();
			Usuario maria = usuarioRepository.findByUsername("maria").orElseThrow();
			Usuario carlos = usuarioRepository.findByUsername("carlos").orElseThrow();

			for (Proyecto ejemplo : proyectosEjemplo(op1, op2, maria, carlos)) {
				boolean existe = proyectoRepository.findAll().stream()
						.anyMatch(p -> ejemplo.getTitulo().equalsIgnoreCase(p.getTitulo()));
				if (!existe) {
					proyectoRepository.save(ejemplo);
				}
			}
		};
	}

	private void asegurarUsuario(
			UsuarioRepository repo,
			PasswordEncoder encoder,
			String username,
			String password,
			String rol) {
		if (!repo.existsByUsername(username)) {
			repo.save(new Usuario(null, username, encoder.encode(password), rol));
		}
	}

	private List<Proyecto> proyectosEjemplo(Usuario op1, Usuario op2, Usuario maria, Usuario carlos) {
		Proyecto p1 = proyecto("Sitio web institucional",
				"Rediseño del portal corporativo",
				30, "ALTA", "EN_PROGRESO");
		p1.getTareas().add(new Tarea(null, "Definir wireframes", true, p1, maria));
		p1.getTareas().add(new Tarea(null, "Maquetar home", false, p1, op1));
		p1.getTareas().add(new Tarea(null, "Integrar CMS", false, p1, carlos));

		Proyecto p2 = proyecto("App móvil de ventas",
				"MVP para catálogo y pedidos",
				60, "MEDIA", "PENDIENTE");
		p2.getTareas().add(new Tarea(null, "Modelo de datos", false, p2, op2));
		p2.getTareas().add(new Tarea(null, "Pantalla de catálogo", false, p2, maria));

		Proyecto p3 = proyecto("Migración a la nube",
				"Pasar servidores on-premise a AWS",
				45, "ALTA", "COMPLETADA");
		p3.getTareas().add(new Tarea(null, "Inventario de servidores", true, p3, carlos));
		p3.getTareas().add(new Tarea(null, "Configurar VPC", true, p3, carlos));
		p3.getTareas().add(new Tarea(null, "Migrar bases de datos", true, p3, op1));

		Proyecto p4 = proyecto("Capacitación del equipo",
				"Curso interno de Spring Boot y Angular",
				20, "BAJA", "PENDIENTE");
		p4.getTareas().add(new Tarea(null, "Definir temario", false, p4, maria));
		p4.getTareas().add(new Tarea(null, "Preparar material", false, p4, op2));

		Proyecto p5 = proyecto("API de reportes",
				"Endpoints de indicadores para gerencia",
				35, "MEDIA", "EN_PROGRESO");
		p5.getTareas().add(new Tarea(null, "Diseñar DTOs", true, p5, op1));
		p5.getTareas().add(new Tarea(null, "Consultas JPQL", true, p5, op2));
		p5.getTareas().add(new Tarea(null, "Pruebas de integración", false, p5, maria));

		Proyecto p6 = proyecto("Auditoría de seguridad",
				"Revisión de roles, JWT y accesos",
				15, "ALTA", "COMPLETADA");
		p6.getTareas().add(new Tarea(null, "Mapear endpoints", true, p6, carlos));
		p6.getTareas().add(new Tarea(null, "Probar 401 y 403", true, p6, op1));

		Proyecto p7 = proyecto("Mejora de UX del dashboard",
				"Ajustes visuales y accesibilidad",
				50, "BAJA", "EN_PROGRESO");
		p7.getTareas().add(new Tarea(null, "Paleta de colores", true, p7, maria));
		p7.getTareas().add(new Tarea(null, "Responsive mobile", false, p7, op2));
		p7.getTareas().add(new Tarea(null, "Prueba con usuarios", false, p7, carlos));

		return List.of(p1, p2, p3, p4, p5, p6, p7);
	}

	private Proyecto proyecto(String titulo, String descripcion, int dias,
			String prioridad, String estado) {
		return new Proyecto(null, titulo, descripcion,
				LocalDate.now().plusDays(dias), prioridad, estado);
	}
}
