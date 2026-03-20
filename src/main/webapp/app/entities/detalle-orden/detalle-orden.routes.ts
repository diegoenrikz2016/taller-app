import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import DetalleOrdenResolve from './route/detalle-orden-routing-resolve.service';

const detalleOrdenRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/detalle-orden').then(m => m.DetalleOrden),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/detalle-orden-detail').then(m => m.DetalleOrdenDetail),
    resolve: {
      detalleOrden: DetalleOrdenResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/detalle-orden-update').then(m => m.DetalleOrdenUpdate),
    resolve: {
      detalleOrden: DetalleOrdenResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/detalle-orden-update').then(m => m.DetalleOrdenUpdate),
    resolve: {
      detalleOrden: DetalleOrdenResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default detalleOrdenRoute;
