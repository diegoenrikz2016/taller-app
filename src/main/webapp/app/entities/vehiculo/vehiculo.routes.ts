import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import VehiculoResolve from './route/vehiculo-routing-resolve.service';

const vehiculoRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/vehiculo').then(m => m.Vehiculo),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/vehiculo-detail').then(m => m.VehiculoDetail),
    resolve: {
      vehiculo: VehiculoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/vehiculo-update').then(m => m.VehiculoUpdate),
    resolve: {
      vehiculo: VehiculoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/vehiculo-update').then(m => m.VehiculoUpdate),
    resolve: {
      vehiculo: VehiculoResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default vehiculoRoute;
