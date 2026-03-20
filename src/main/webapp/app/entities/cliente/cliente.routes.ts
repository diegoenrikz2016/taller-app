import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import ClienteResolve from './route/cliente-routing-resolve.service';

const clienteRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/cliente').then(m => m.Cliente),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/cliente-detail').then(m => m.ClienteDetail),
    resolve: {
      cliente: ClienteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/cliente-update').then(m => m.ClienteUpdate),
    resolve: {
      cliente: ClienteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/cliente-update').then(m => m.ClienteUpdate),
    resolve: {
      cliente: ClienteResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default clienteRoute;
