import { Routes } from '@angular/router';
import { LayoutComponent } from './layout/layout.component';
import { ProjectListComponent } from './projects/project-list.component';
import { ProjectFormComponent } from './projects/project-form.component';
import { ProjectDeleteComponent } from './projects/project-delete.component';

export const routes: Routes = [
  {
    path: '',
    component: LayoutComponent,
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'projects' },
      { path: 'projects', component: ProjectListComponent },
      { path: 'projects/nuevo', component: ProjectFormComponent },
      { path: 'projects/:id/editar', component: ProjectFormComponent },
      { path: 'projects/:id/eliminar', component: ProjectDeleteComponent }
    ]
  },
  { path: '**', redirectTo: 'projects' }
];
