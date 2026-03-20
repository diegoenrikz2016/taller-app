import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ICliente, NewCliente } from '../cliente.model';

export type PartialUpdateCliente = Partial<ICliente> & Pick<ICliente, 'id'>;

@Injectable()
export class ClientesService {
  readonly clientesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly clientesResource = httpResource<ICliente[]>(() => {
    const params = this.clientesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of cliente that have been fetched. It is updated when the clientesResource emits a new value.
   * In case of error while fetching the clientes, the signal is set to an empty array.
   */
  readonly clientes = computed(() => (this.clientesResource.hasValue() ? this.clientesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/clientes');
}

@Injectable({ providedIn: 'root' })
export class ClienteService extends ClientesService {
  protected readonly http = inject(HttpClient);

  create(cliente: NewCliente): Observable<ICliente> {
    return this.http.post<ICliente>(this.resourceUrl, cliente);
  }

  update(cliente: ICliente): Observable<ICliente> {
    return this.http.put<ICliente>(`${this.resourceUrl}/${encodeURIComponent(this.getClienteIdentifier(cliente))}`, cliente);
  }

  partialUpdate(cliente: PartialUpdateCliente): Observable<ICliente> {
    return this.http.patch<ICliente>(`${this.resourceUrl}/${encodeURIComponent(this.getClienteIdentifier(cliente))}`, cliente);
  }

  find(id: number): Observable<ICliente> {
    return this.http.get<ICliente>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ICliente[]>> {
    const options = createRequestOption(req);
    return this.http.get<ICliente[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getClienteIdentifier(cliente: Pick<ICliente, 'id'>): number {
    return cliente.id;
  }

  compareCliente(o1: Pick<ICliente, 'id'> | null, o2: Pick<ICliente, 'id'> | null): boolean {
    return o1 && o2 ? this.getClienteIdentifier(o1) === this.getClienteIdentifier(o2) : o1 === o2;
  }

  addClienteToCollectionIfMissing<Type extends Pick<ICliente, 'id'>>(
    clienteCollection: Type[],
    ...clientesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const clientes: Type[] = clientesToCheck.filter(isPresent);
    if (clientes.length > 0) {
      const clienteCollectionIdentifiers = clienteCollection.map(clienteItem => this.getClienteIdentifier(clienteItem));
      const clientesToAdd = clientes.filter(clienteItem => {
        const clienteIdentifier = this.getClienteIdentifier(clienteItem);
        if (clienteCollectionIdentifiers.includes(clienteIdentifier)) {
          return false;
        }
        clienteCollectionIdentifiers.push(clienteIdentifier);
        return true;
      });
      return [...clientesToAdd, ...clienteCollection];
    }
    return clienteCollection;
  }
}
