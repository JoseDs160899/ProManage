package com.example.promanage.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Usuario del sistema para autenticación. El rol define el nivel de autorización
 * (ADMIN u OPERADOR). La contraseña se almacena cifrada con BCrypt.
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 50)
	private String username;

	@JsonIgnore
	@Column(nullable = false)
	private String password;

	/** Rol sin el prefijo ROLE_: "ADMIN" u "OPERADOR". */
	@Column(nullable = false, length = 20)
	private String rol;

	public Usuario() {
	}

	public Usuario(Long id, String username, String password, String rol) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.rol = rol;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}
}
