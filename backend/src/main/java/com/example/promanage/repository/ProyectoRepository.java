package com.example.promanage.repository;

import com.example.promanage.model.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

	/**
	 * Consulta JPQL: busca proyectos cuyo título contenga el texto (sin distinguir
	 * mayúsculas/minúsculas). Resuelve la búsqueda parcial por nombre.
	 */
	@Query("SELECT p FROM Proyecto p WHERE LOWER(p.titulo) LIKE LOWER(CONCAT('%', :texto, '%'))")
	List<Proyecto> buscarPorTitulo(@Param("texto") String texto);

	/** Consulta JPQL: lista los proyectos filtrados por estado. */
	@Query("SELECT p FROM Proyecto p WHERE p.estado = :estado ORDER BY p.fechaVencimiento ASC")
	List<Proyecto> buscarPorEstado(@Param("estado") String estado);

	/** Consulta JPQL de agregación: cuenta cuántos proyectos hay en un estado. */
	@Query("SELECT COUNT(p) FROM Proyecto p WHERE p.estado = :estado")
	long contarPorEstado(@Param("estado") String estado);
}
