import { Routes } from '@angular/router';
import { LayoutComponent } from './layout/layout.component';
import { LoginComponent } from './auth/login.component';
import { ProjectListComponent } from './projects/project-list.component';
import { ProjectFormComponent } from './projects/project-form.component';
import { ProjectDeleteComponent } from './projects/project-delete.component';
import { ProjectTasksComponent } from './projects/project-tasks.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  {
    path: '',
    component: LayoutComponent,
    canActivate: [authGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'projects' },
      { path: 'projects', component: ProjectListComponent },
      { path: 'projects/nuevo', component: ProjectFormComponent },
      { path: 'projects/:id/tareas', component: ProjectTasksComponent },
      { path: 'projects/:id/editar', component: ProjectFormComponent },
      { path: 'projects/:id/eliminar', component: ProjectDeleteComponent }
    ]
  },
  { path: '**', redirectTo: 'projects' }
];
