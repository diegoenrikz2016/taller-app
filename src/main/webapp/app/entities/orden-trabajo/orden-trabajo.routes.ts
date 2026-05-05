import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import OrdenTrabajoResolve from './route/orden-trabajo-routing-resolve.service';

const ordenTrabajoRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/orden-trabajo').then(m => m.OrdenTrabajo),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'pdfs',
    loadComponent: () => import('./pdf/orden-trabajo-pdf-historial').then(m => m.OrdenTrabajoPdfHistorial),
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/orden-trabajo-update').then(m => m.OrdenTrabajoUpdate),
    resolve: {
      ordenTrabajo: OrdenTrabajoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/orden-trabajo-detail').then(m => m.OrdenTrabajoDetail),
    resolve: {
      ordenTrabajo: OrdenTrabajoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/orden-trabajo-update').then(m => m.OrdenTrabajoUpdate),
    resolve: {
      ordenTrabajo: OrdenTrabajoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default ordenTrabajoRoute;
