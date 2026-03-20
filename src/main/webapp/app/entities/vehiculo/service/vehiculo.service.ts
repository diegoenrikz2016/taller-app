import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IVehiculo, NewVehiculo } from '../vehiculo.model';

export type PartialUpdateVehiculo = Partial<IVehiculo> & Pick<IVehiculo, 'id'>;

@Injectable()
export class VehiculosService {
  readonly vehiculosParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly vehiculosResource = httpResource<IVehiculo[]>(() => {
    const params = this.vehiculosParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of vehiculo that have been fetched. It is updated when the vehiculosResource emits a new value.
   * In case of error while fetching the vehiculos, the signal is set to an empty array.
   */
  readonly vehiculos = computed(() => (this.vehiculosResource.hasValue() ? this.vehiculosResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/vehiculos');
}

@Injectable({ providedIn: 'root' })
export class VehiculoService extends VehiculosService {
  protected readonly http = inject(HttpClient);

  create(vehiculo: NewVehiculo): Observable<IVehiculo> {
    return this.http.post<IVehiculo>(this.resourceUrl, vehiculo);
  }

  update(vehiculo: IVehiculo): Observable<IVehiculo> {
    return this.http.put<IVehiculo>(`${this.resourceUrl}/${encodeURIComponent(this.getVehiculoIdentifier(vehiculo))}`, vehiculo);
  }

  partialUpdate(vehiculo: PartialUpdateVehiculo): Observable<IVehiculo> {
    return this.http.patch<IVehiculo>(`${this.resourceUrl}/${encodeURIComponent(this.getVehiculoIdentifier(vehiculo))}`, vehiculo);
  }

  find(id: number): Observable<IVehiculo> {
    return this.http.get<IVehiculo>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IVehiculo[]>> {
    const options = createRequestOption(req);
    return this.http.get<IVehiculo[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getVehiculoIdentifier(vehiculo: Pick<IVehiculo, 'id'>): number {
    return vehiculo.id;
  }

  compareVehiculo(o1: Pick<IVehiculo, 'id'> | null, o2: Pick<IVehiculo, 'id'> | null): boolean {
    return o1 && o2 ? this.getVehiculoIdentifier(o1) === this.getVehiculoIdentifier(o2) : o1 === o2;
  }

  addVehiculoToCollectionIfMissing<Type extends Pick<IVehiculo, 'id'>>(
    vehiculoCollection: Type[],
    ...vehiculosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const vehiculos: Type[] = vehiculosToCheck.filter(isPresent);
    if (vehiculos.length > 0) {
      const vehiculoCollectionIdentifiers = vehiculoCollection.map(vehiculoItem => this.getVehiculoIdentifier(vehiculoItem));
      const vehiculosToAdd = vehiculos.filter(vehiculoItem => {
        const vehiculoIdentifier = this.getVehiculoIdentifier(vehiculoItem);
        if (vehiculoCollectionIdentifiers.includes(vehiculoIdentifier)) {
          return false;
        }
        vehiculoCollectionIdentifiers.push(vehiculoIdentifier);
        return true;
      });
      return [...vehiculosToAdd, ...vehiculoCollection];
    }
    return vehiculoCollection;
  }
}
