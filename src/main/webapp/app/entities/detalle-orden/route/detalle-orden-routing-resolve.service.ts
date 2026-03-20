import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { IDetalleOrden } from '../detalle-orden.model';
import { DetalleOrdenService } from '../service/detalle-orden.service';

const detalleOrdenResolve = (route: ActivatedRouteSnapshot): Observable<null | IDetalleOrden> => {
  const id = route.params.id;
  if (id) {
    const router = inject(Router);
    const service = inject(DetalleOrdenService);
    return service.find(id).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 404) {
          router.navigate(['404']);
        } else {
          router.navigate(['error']);
        }
        return EMPTY;
      }),
    );
  }

  return of(null);
};

export default detalleOrdenResolve;
