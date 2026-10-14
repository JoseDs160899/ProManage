package com.example.promanage.repository;

import com.example.promanage.model.Proyecto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas de persistencia reales contra una base H2 en memoria (@DataJpaTest).
 * Verifican las consultas JPQL del repositorio.
 */
@DataJpaTest
class ProyectoRepositoryTest {

	@Autowired
	ProyectoRepository repository;

	@BeforeEach
	void setUp() {
		repository.deleteAll();
		repository.save(new Proyecto(null, "Portal web", "d", LocalDate.now(), "ALTA", "EN_PROGRESO"));
		repository.save(new Proyecto(null, "App web ventas", "d", LocalDate.now(), "MEDIA", "PENDIENTE"));
		repository.save(new Proyecto(null, "Migración datos", "d", LocalDate.now(), "BAJA", "PENDIENTE"));
	}

	@Test
	void buscarPorTitulo_debeFiltrarSinDistinguirMayusculas() {
		List<Proyecto> resultado = repository.buscarPorTitulo("web");

		assertThat(resultado).hasSize(2)
				.extracting(Proyecto::getTitulo)
				.containsExactlyInAnyOrder("Portal web", "App web ventas");
	}

	@Test
	void buscarPorEstado_debeDevolverSoloEseEstado() {
		List<Proyecto> pendientes = repository.buscarPorEstado("PENDIENTE");

		assertThat(pendientes).hasSize(2)
				.allMatch(p -> p.getEstado().equals("PENDIENTE"));
	}

	@Test
	void contarPorEstado_debeContarCorrectamente() {
		assertThat(repository.contarPorEstado("PENDIENTE")).isEqualTo(2);
		assertThat(repository.contarPorEstado("EN_PROGRESO")).isEqualTo(1);
		assertThat(repository.contarPorEstado("COMPLETADA")).isZero();
	}

	@Test
	void save_debePersistirYGenerarId() {
		Proyecto guardado = repository.save(
				new Proyecto(null, "Nuevo", "d", LocalDate.now(), "ALTA", "PENDIENTE"));

		assertThat(guardado.getId()).isNotNull();
		assertThat(repository.findById(guardado.getId())).isPresent();
	}
}
