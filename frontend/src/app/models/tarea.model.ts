import { Usuario } from './usuario.model';

export interface Tarea {
  id?: number;
  titulo: string;
  completada: boolean;
  operador?: Usuario | null;
}
