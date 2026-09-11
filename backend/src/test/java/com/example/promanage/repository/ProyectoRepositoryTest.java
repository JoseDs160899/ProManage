package com.example.promanage.repository;

import com.example.promanage.model.Proyecto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ProyectoRepositoryTest {

	ProyectoRepository repository;

	@BeforeEach
	void setUp() {
		repository = new ProyectoRepository();
	}

	@Test
	void findAll_debeIncluirDatosSemilla() {
		assertThat(repository.findAll()).hasSize(2);
	}

	@Test
	void save_sinId_debeAsignarIdIncremental() {
		Proyecto nuevo = new Proyecto(null, "T3", "d", LocalDate.now(), "BAJA", "PENDIENTE");

		Proyecto guardado = repository.save(nuevo);

		assertThat(guardado.getId()).isEqualTo(3L);
		assertThat(repository.findAll()).hasSize(3);
	}

	@Test
	void save_conIdExistente_debeActualizarCampos() {
		Proyecto cambios = new Proyecto(1L, "Nuevo título", "Nueva desc",
				LocalDate.of(2030, 1, 1), "BAJA", "COMPLETADO");

		Proyecto actualizado = repository.save(cambios);

		assertThat(actualizado.getTitulo()).isEqualTo("Nuevo título");
		assertThat(repository.findById(1L)).get()
				.extracting(Proyecto::getEstado)
				.isEqualTo("COMPLETADO");
		assertThat(repository.findAll()).hasSize(2);
	}

	@Test
	void deleteById_existente_debeRetornarTrue() {
		assertThat(repository.deleteById(1L)).isTrue();
		assertThat(repository.findById(1L)).isEmpty();
	}

	@Test
	void deleteById_inexistente_debeRetornarFalse() {
		assertThat(repository.deleteById(999L)).isFalse();
	}
}
