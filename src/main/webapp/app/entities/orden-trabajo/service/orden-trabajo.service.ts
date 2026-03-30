import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IOrdenTrabajo, NewOrdenTrabajo } from '../orden-trabajo.model';

export type PartialUpdateOrdenTrabajo = Partial<IOrdenTrabajo> & Pick<IOrdenTrabajo, 'id'>;

type RestOf<T extends IOrdenTrabajo | NewOrdenTrabajo> = Omit<T, 'fecha'> & {
  fecha?: string | null;
};

export type RestOrdenTrabajo = RestOf<IOrdenTrabajo>;

export type NewRestOrdenTrabajo = RestOf<NewOrdenTrabajo>;

export type PartialUpdateRestOrdenTrabajo = RestOf<PartialUpdateOrdenTrabajo>;

@Injectable()
export class OrdenTrabajosService {
  readonly ordenTrabajosParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly ordenTrabajosResource = httpResource<RestOrdenTrabajo[]>(() => {
    const params = this.ordenTrabajosParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of ordenTrabajo that have been fetched. It is updated when the ordenTrabajosResource emits a new value.
   * In case of error while fetching the ordenTrabajos, the signal is set to an empty array.
   */
  readonly ordenTrabajos = computed(() =>
    (this.ordenTrabajosResource.hasValue() ? this.ordenTrabajosResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/orden-trabajos');

  protected convertValueFromServer(restOrdenTrabajo: RestOrdenTrabajo): IOrdenTrabajo {
    return {
      ...restOrdenTrabajo,
      fecha: restOrdenTrabajo.fecha ? dayjs(restOrdenTrabajo.fecha) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class OrdenTrabajoService extends OrdenTrabajosService {
  protected readonly http = inject(HttpClient);

  create(ordenTrabajo: NewOrdenTrabajo): Observable<IOrdenTrabajo> {
    const copy = this.convertValueFromClient(ordenTrabajo);
    return this.http.post<RestOrdenTrabajo>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(ordenTrabajo: IOrdenTrabajo): Observable<IOrdenTrabajo> {
    const copy = this.convertValueFromClient(ordenTrabajo);
    return this.http
      .put<RestOrdenTrabajo>(`${this.resourceUrl}/${encodeURIComponent(this.getOrdenTrabajoIdentifier(ordenTrabajo))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(ordenTrabajo: PartialUpdateOrdenTrabajo): Observable<IOrdenTrabajo> {
    const copy = this.convertValueFromClient(ordenTrabajo);
    return this.http
      .patch<RestOrdenTrabajo>(`${this.resourceUrl}/${encodeURIComponent(this.getOrdenTrabajoIdentifier(ordenTrabajo))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IOrdenTrabajo> {
    return this.http
      .get<RestOrdenTrabajo>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IOrdenTrabajo[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestOrdenTrabajo[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getOrdenTrabajoIdentifier(ordenTrabajo: Pick<IOrdenTrabajo, 'id'>): number {
    return ordenTrabajo.id;
  }

  compareOrdenTrabajo(o1: Pick<IOrdenTrabajo, 'id'> | null, o2: Pick<IOrdenTrabajo, 'id'> | null): boolean {
    return o1 && o2 ? this.getOrdenTrabajoIdentifier(o1) === this.getOrdenTrabajoIdentifier(o2) : o1 === o2;
  }

  addOrdenTrabajoToCollectionIfMissing<Type extends Pick<IOrdenTrabajo, 'id'>>(
    ordenTrabajoCollection: Type[],
    ...ordenTrabajosToCheck: (Type | null | undefined)[]
  ): Type[] {
    const ordenTrabajos: Type[] = ordenTrabajosToCheck.filter(isPresent);
    if (ordenTrabajos.length > 0) {
      const ordenTrabajoCollectionIdentifiers = ordenTrabajoCollection.map(ordenTrabajoItem =>
        this.getOrdenTrabajoIdentifier(ordenTrabajoItem),
      );
      const ordenTrabajosToAdd = ordenTrabajos.filter(ordenTrabajoItem => {
        const ordenTrabajoIdentifier = this.getOrdenTrabajoIdentifier(ordenTrabajoItem);
        if (ordenTrabajoCollectionIdentifiers.includes(ordenTrabajoIdentifier)) {
          return false;
        }
        ordenTrabajoCollectionIdentifiers.push(ordenTrabajoIdentifier);
        return true;
      });
      return [...ordenTrabajosToAdd, ...ordenTrabajoCollection];
    }
    return ordenTrabajoCollection;
  }

  protected convertValueFromClient<T extends IOrdenTrabajo | NewOrdenTrabajo | PartialUpdateOrdenTrabajo>(ordenTrabajo: T): RestOf<T> {
    return {
      ...ordenTrabajo,
      fecha: ordenTrabajo.fecha?.format(DATE_FORMAT) ?? null,
    };
  }

  protected convertResponseFromServer(res: RestOrdenTrabajo): IOrdenTrabajo {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestOrdenTrabajo[]): IOrdenTrabajo[] {
    return res.map(item => this.convertValueFromServer(item));
  }

  search(query: string): Observable<IOrdenTrabajo[]> {
    return this.http.get<IOrdenTrabajo[]>(`${this.resourceUrl}/search`, {
      params: { query },
    });
  }
}
