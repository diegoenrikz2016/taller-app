import { Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'tallerApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'user-management',
    data: { pageTitle: 'userManagement.home.title' },
    loadChildren: () => import('./admin/user-management/user-management.routes'),
  },
  {
    path: 'cliente',
    data: { pageTitle: 'tallerApp.cliente.home.title' },
    loadChildren: () => import('./cliente/cliente.routes'),
  },
  {
    path: 'vehiculo',
    data: { pageTitle: 'tallerApp.vehiculo.home.title' },
    loadChildren: () => import('./vehiculo/vehiculo.routes'),
  },
  {
    path: 'orden-trabajo',
    data: { pageTitle: 'tallerApp.ordenTrabajo.home.title' },
    loadChildren: () => import('./orden-trabajo/orden-trabajo.routes'),
  },
  {
    path: 'detalle-orden',
    data: { pageTitle: 'tallerApp.detalleOrden.home.title' },
    loadChildren: () => import('./detalle-orden/detalle-orden.routes'),
  },
  {
    path: 'taller/nueva-orden',
    loadComponent: () => import('../taller/nueva-orden/nueva-orden-wizard').then(m => m.NuevaOrdenWizard),
    canActivate: [UserRouteAccessService],
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
