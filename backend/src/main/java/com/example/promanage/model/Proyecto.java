package com.example.promanage.model;

import java.time.LocalDate;

public class Proyecto {

	private Long id;
	private String titulo;
	private String descripcion;
	private LocalDate fechaVencimiento;
	private String prioridad;
	private String estado;

	public Proyecto() {
	}

	public Proyecto(Long id, String titulo, String descripcion, LocalDate fechaVencimiento,
			String prioridad, String estado) {
		this.id = id;
		this.titulo = titulo;
		this.descripcion = descripcion;
		this.fechaVencimiento = fechaVencimiento;
		this.prioridad = prioridad;
		this.estado = estado;
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

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public LocalDate getFechaVencimiento() {
		return fechaVencimiento;
	}

	public void setFechaVencimiento(LocalDate fechaVencimiento) {
		this.fechaVencimiento = fechaVencimiento;
	}

	public String getPrioridad() {
		return prioridad;
	}

	public void setPrioridad(String prioridad) {
		this.prioridad = prioridad;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
}
