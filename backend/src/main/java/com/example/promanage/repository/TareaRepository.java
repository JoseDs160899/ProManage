package com.example.promanage.repository;

import com.example.promanage.model.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {

	/** Consulta JPQL con JOIN: tareas de un proyecto e incluye el operador asignado. */
	@Query("""
			SELECT t FROM Tarea t
			LEFT JOIN FETCH t.operador
			WHERE t.proyecto.id = :proyectoId
			ORDER BY t.id ASC
			""")
	List<Tarea> buscarPorProyecto(@Param("proyectoId") Long proyectoId);

	/** Consulta JPQL: tareas pendientes (no completadas) de un proyecto. */
	@Query("""
			SELECT t FROM Tarea t
			LEFT JOIN FETCH t.operador
			WHERE t.proyecto.id = :proyectoId AND t.completada = false
			ORDER BY t.id ASC
			""")
	List<Tarea> buscarPendientesPorProyecto(@Param("proyectoId") Long proyectoId);
}
