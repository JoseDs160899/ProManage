import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { Proyecto } from '../models/proyecto.model';
import { Tarea } from '../models/tarea.model';
import { Usuario } from '../models/usuario.model';
import { ProyectoService } from '../services/proyecto.service';
import { TareaService } from '../services/tarea.service';
import { UsuarioService } from '../services/usuario.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-project-tasks',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './project-tasks.component.html',
  styleUrl: './project-tasks.component.css'
})
export class ProjectTasksComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly proyectoService = inject(ProyectoService);
  private readonly tareaService = inject(TareaService);
  private readonly usuarioService = inject(UsuarioService);
  private readonly authService = inject(AuthService);

  proyecto?: Proyecto;
  tareas: Tarea[] = [];
  operadores: Usuario[] = [];
  proyectoId = 0;
  cargando = true;
  error = '';
  nuevaTarea = '';
  operadorSeleccionado: number | null = null;
  enviando = false;
  soloPendientes = false;

  get esAdmin(): boolean {
    return this.authService.esAdmin;
  }

  get esOperador(): boolean {
    return this.authService.rol === 'OPERADOR';
  }

  get total(): number {
    return this.tareas.length;
  }

  get completadas(): number {
    return this.tareas.filter((t) => t.completada).length;
  }

  ngOnInit(): void {
    this.proyectoId = Number(this.route.snapshot.paramMap.get('id'));
    this.cargarProyecto();
    this.cargarTareas();
    this.cargarOperadores();
  }

  /** ADMIN siempre puede cambiar; OPERADOR solo puede marcar pendientes (no desmarcar). */
  puedeInteractuar(tarea: Tarea): boolean {
    if (this.esAdmin) {
      return true;
    }
    return this.esOperador && !tarea.completada;
  }

  cargarProyecto(): void {
    this.proyectoService.obtener(this.proyectoId).subscribe({
      next: (p) => (this.proyecto = p),
      error: () => (this.error = 'No se pudo cargar el proyecto.')
    });
  }

  cargarOperadores(): void {
    this.usuarioService.listarOperadores().subscribe({
      next: (ops) => {
        this.operadores = ops;
        if (ops.length > 0 && this.operadorSeleccionado == null) {
          this.operadorSeleccionado = ops[0].id;
        }
      },
      error: () => (this.error = 'No se pudieron cargar los operadores.')
    });
  }

  cargarTareas(): void {
    this.cargando = true;
    this.error = '';
    const req$ = this.soloPendientes
      ? this.tareaService.listarPendientes(this.proyectoId)
      : this.tareaService.listarPorProyecto(this.proyectoId);

    req$.subscribe({
      next: (data) => {
        this.tareas = data;
        this.cargando = false;
      },
      error: () => {
        this.error = 'No se pudieron cargar las tareas.';
        this.cargando = false;
      }
    });
  }

  cambiarFiltro(soloPendientes: boolean): void {
    this.soloPendientes = soloPendientes;
    this.cargarTareas();
  }

  agregar(): void {
    const titulo = this.nuevaTarea.trim();
    if (!titulo || !this.esAdmin) {
      return;
    }
    this.enviando = true;
    this.tareaService.crear(this.proyectoId, titulo, this.operadorSeleccionado).subscribe({
      next: () => {
        this.nuevaTarea = '';
        this.enviando = false;
        this.cargarTareas();
        this.cargarProyecto();
      },
      error: () => {
        this.error = 'No se pudo crear la tarea.';
        this.enviando = false;
      }
    });
  }

  cambiarOperador(tarea: Tarea, operadorId: string): void {
    if (!this.esAdmin || !tarea.id) {
      return;
    }
    const id = operadorId === '' ? null : Number(operadorId);
    this.tareaService.asignarOperador(tarea.id, id).subscribe({
      next: (actualizada) => {
        tarea.operador = actualizada.operador;
      },
      error: () => (this.error = 'No se pudo asignar el operador.')
    });
  }

  toggleCompletada(tarea: Tarea): void {
    if (!tarea.id || !this.puedeInteractuar(tarea)) {
      return;
    }

    const nuevoValor = !tarea.completada;
    if (this.esOperador && !nuevoValor) {
      return;
    }

    this.tareaService.completar(tarea.id, nuevoValor).subscribe({
      next: (actualizada) => {
        tarea.completada = actualizada.completada;
        this.cargarProyecto();
        if (this.soloPendientes) {
          this.cargarTareas();
        }
      },
      error: (err) => {
        this.error = err.status === 403
          ? 'No tienes permiso para desmarcar esta tarea.'
          : 'No se pudo actualizar la tarea.';
      }
    });
  }

  eliminar(tarea: Tarea): void {
    if (!this.esAdmin || !tarea.id) {
      return;
    }
    if (!confirm(`¿Eliminar la tarea "${tarea.titulo}"?`)) {
      return;
    }
    this.tareaService.eliminar(tarea.id).subscribe({
      next: () => {
        this.cargarTareas();
        this.cargarProyecto();
      },
      error: () => (this.error = 'No se pudo eliminar la tarea.')
    });
  }

  /** Transacción: marca el proyecto y todas sus tareas como completadas. */
  completarProyecto(): void {
    if (!this.esAdmin) {
      return;
    }
    if (!confirm('¿Completar el proyecto y todas sus tareas?')) {
      return;
    }
    this.proyectoService.completar(this.proyectoId).subscribe({
      next: (p) => {
        this.proyecto = p;
        this.cargarTareas();
      },
      error: () => (this.error = 'No se pudo completar el proyecto.')
    });
  }

  etiquetaEstado(estado?: string): string {
    switch (estado) {
      case 'EN_PROGRESO':
        return 'En progreso';
      case 'COMPLETADA':
        return 'Completada';
      default:
        return 'Pendiente';
    }
  }

  estadoBadge(estado?: string): string {
    switch (estado) {
      case 'EN_PROGRESO':
        return 'text-bg-primary';
      case 'COMPLETADA':
        return 'text-bg-success';
      default:
        return 'text-bg-secondary';
    }
  }
}
