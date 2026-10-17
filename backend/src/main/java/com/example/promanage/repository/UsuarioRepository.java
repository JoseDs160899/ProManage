package com.example.promanage.repository;

import com.example.promanage.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	Optional<Usuario> findByUsername(String username);

	boolean existsByUsername(String username);

	@Query("SELECT u FROM Usuario u WHERE u.rol = 'OPERADOR' ORDER BY u.username ASC")
	List<Usuario> listarOperadores();
}
