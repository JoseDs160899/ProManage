package com.example.promanage.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

/**
 * Tarea perteneciente a un {@link Proyecto}. Puede asignarse a un operador.
 */
@Entity
@Table(name = "tareas")
public class Tarea {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(nullable = false, length = 150)
	private String titulo;

	@Column(nullable = false)
	private boolean completada;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "proyecto_id", nullable = false)
	@JsonBackReference
	private Proyecto proyecto;

	/** Operador responsable de ejecutar la tarea (asignado por ADMIN). */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "operador_id")
	@JsonIgnoreProperties({"password", "hibernateLazyInitializer", "handler"})
	private Usuario operador;

	public Tarea() {
	}

	public Tarea(Long id, String titulo, boolean completada, Proyecto proyecto) {
		this(id, titulo, completada, proyecto, null);
	}

	public Tarea(Long id, String titulo, boolean completada, Proyecto proyecto, Usuario operador) {
		this.id = id;
		this.titulo = titulo;
		this.completada = completada;
		this.proyecto = proyecto;
		this.operador = operador;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public boolean isCompletada() {
		return completada;
	}

	public void setCompletada(boolean completada) {
		this.completada = completada;
	}

	public Proyecto getProyecto() {
		return proyecto;
	}

	public void setProyecto(Proyecto proyecto) {
		this.proyecto = proyecto;
	}

	public Usuario getOperador() {
		return operador;
	}

	public void setOperador(Usuario operador) {
		this.operador = operador;
	}
}
