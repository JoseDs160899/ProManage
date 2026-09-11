package com.example.promanage.service;

import com.example.promanage.model.Proyecto;
import com.example.promanage.repository.ProyectoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProyectoServiceTest {

	@Mock
	ProyectoRepository proyectoRepository;

	@InjectMocks
	ProyectoService proyectoService;

	@Test
	void listar_debeDelegarEnRepositorio() {
		when(proyectoRepository.findAll()).thenReturn(List.of(proyecto(1L, "A")));

		List<Proyecto> resultado = proyectoService.listar();

		assertThat(resultado).hasSize(1);
		verify(proyectoRepository).findAll();
	}

	@Test
	void crear_sinEstado_debeAsignarPendiente() {
		Proyecto entrada = new Proyecto(null, "Nuevo", "Desc",
				LocalDate.now().plusDays(7), "ALTA", null);
		when(proyectoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		Proyecto creado = proyectoService.crear(entrada);

		ArgumentCaptor<Proyecto> captor = ArgumentCaptor.forClass(Proyecto.class);
		verify(proyectoRepository).save(captor.capture());
		assertThat(captor.getValue().getId()).isNull();
		assertThat(creado.getEstado()).isEqualTo("PENDIENTE");
	}

	@Test
	void crear_conEstadoEnBlanco_debeAsignarPendiente() {
		Proyecto entrada = new Proyecto(99L, "Nuevo", "Desc",
				LocalDate.now(), "MEDIA", "   ");
		when(proyectoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		Proyecto creado = proyectoService.crear(entrada);

		assertThat(creado.getEstado()).isEqualTo("PENDIENTE");
		assertThat(creado.getId()).isNull();
	}

	@Test
	void crear_conEstado_debeConservarlo() {
		Proyecto entrada = new Proyecto(null, "Nuevo", "Desc",
				LocalDate.now(), "BAJA", "EN_PROGRESO");
		when(proyectoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		assertThat(proyectoService.crear(entrada).getEstado()).isEqualTo("EN_PROGRESO");
	}

	@Test
	void actualizar_siExiste_debeGuardarConIdDeRuta() {
		when(proyectoRepository.findById(1L)).thenReturn(Optional.of(proyecto(1L, "Viejo")));
		when(proyectoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

		Proyecto datos = new Proyecto(null, "Actualizado", "Nueva",
				LocalDate.now(), "ALTA", "COMPLETADO");

		Optional<Proyecto> resultado = proyectoService.actualizar(1L, datos);

		assertThat(resultado).isPresent();
		assertThat(resultado.get().getId()).isEqualTo(1L);
		assertThat(resultado.get().getTitulo()).isEqualTo("Actualizado");
	}

	@Test
	void actualizar_siNoExiste_debeRetornarEmpty() {
		when(proyectoRepository.findById(99L)).thenReturn(Optional.empty());

		assertThat(proyectoService.actualizar(99L, proyecto(null, "X"))).isEmpty();
		verify(proyectoRepository, never()).save(any());
	}

	@Test
	void eliminar_debeRetornarResultadoDelRepositorio() {
		when(proyectoRepository.deleteById(1L)).thenReturn(true);
		when(proyectoRepository.deleteById(99L)).thenReturn(false);

		assertThat(proyectoService.eliminar(1L)).isTrue();
		assertThat(proyectoService.eliminar(99L)).isFalse();
	}

	private static Proyecto proyecto(Long id, String titulo) {
		return new Proyecto(id, titulo, "d", LocalDate.now(), "MEDIA", "PENDIENTE");
	}
}
