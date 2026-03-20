import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IDetalleOrden, NewDetalleOrden } from '../detalle-orden.model';

export type PartialUpdateDetalleOrden = Partial<IDetalleOrden> & Pick<IDetalleOrden, 'id'>;

@Injectable()
export class DetalleOrdensService {
  readonly detalleOrdensParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly detalleOrdensResource = httpResource<IDetalleOrden[]>(() => {
    const params = this.detalleOrdensParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of detalleOrden that have been fetched. It is updated when the detalleOrdensResource emits a new value.
   * In case of error while fetching the detalleOrdens, the signal is set to an empty array.
   */
  readonly detalleOrdens = computed(() => (this.detalleOrdensResource.hasValue() ? this.detalleOrdensResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/detalle-ordens');
}

@Injectable({ providedIn: 'root' })
export class DetalleOrdenService extends DetalleOrdensService {
  protected readonly http = inject(HttpClient);

  create(detalleOrden: NewDetalleOrden): Observable<IDetalleOrden> {
    return this.http.post<IDetalleOrden>(this.resourceUrl, detalleOrden);
  }

  update(detalleOrden: IDetalleOrden): Observable<IDetalleOrden> {
    return this.http.put<IDetalleOrden>(
      `${this.resourceUrl}/${encodeURIComponent(this.getDetalleOrdenIdentifier(detalleOrden))}`,
      detalleOrden,
    );
  }

  partialUpdate(detalleOrden: PartialUpdateDetalleOrden): Observable<IDetalleOrden> {
    return this.http.patch<IDetalleOrden>(
      `${this.resourceUrl}/${encodeURIComponent(this.getDetalleOrdenIdentifier(detalleOrden))}`,
      detalleOrden,
    );
  }

  find(id: number): Observable<IDetalleOrden> {
    return this.http.get<IDetalleOrden>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IDetalleOrden[]>> {
    const options = createRequestOption(req);
    return this.http.get<IDetalleOrden[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getDetalleOrdenIdentifier(detalleOrden: Pick<IDetalleOrden, 'id'>): number {
    return detalleOrden.id;
  }

  compareDetalleOrden(o1: Pick<IDetalleOrden, 'id'> | null, o2: Pick<IDetalleOrden, 'id'> | null): boolean {
    return o1 && o2 ? this.getDetalleOrdenIdentifier(o1) === this.getDetalleOrdenIdentifier(o2) : o1 === o2;
  }

  addDetalleOrdenToCollectionIfMissing<Type extends Pick<IDetalleOrden, 'id'>>(
    detalleOrdenCollection: Type[],
    ...detalleOrdensToCheck: (Type | null | undefined)[]
  ): Type[] {
    const detalleOrdens: Type[] = detalleOrdensToCheck.filter(isPresent);
    if (detalleOrdens.length > 0) {
      const detalleOrdenCollectionIdentifiers = detalleOrdenCollection.map(detalleOrdenItem =>
        this.getDetalleOrdenIdentifier(detalleOrdenItem),
      );
      const detalleOrdensToAdd = detalleOrdens.filter(detalleOrdenItem => {
        const detalleOrdenIdentifier = this.getDetalleOrdenIdentifier(detalleOrdenItem);
        if (detalleOrdenCollectionIdentifiers.includes(detalleOrdenIdentifier)) {
          return false;
        }
        detalleOrdenCollectionIdentifiers.push(detalleOrdenIdentifier);
        return true;
      });
      return [...detalleOrdensToAdd, ...detalleOrdenCollection];
    }
    return detalleOrdenCollection;
  }
}
