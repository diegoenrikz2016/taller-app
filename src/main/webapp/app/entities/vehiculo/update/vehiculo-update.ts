import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICliente } from 'app/entities/cliente/cliente.model';
import { ClienteService } from 'app/entities/cliente/service/cliente.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { VehiculoService } from '../service/vehiculo.service';
import { IVehiculo } from '../vehiculo.model';

import { VehiculoFormGroup, VehiculoFormService } from './vehiculo-form.service';

@Component({
  selector: 'jhi-vehiculo-update',
  templateUrl: './vehiculo-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class VehiculoUpdate implements OnInit {
  readonly isSaving = signal(false);
  vehiculo: IVehiculo | null = null;

  clientesSharedCollection = signal<ICliente[]>([]);

  protected vehiculoService = inject(VehiculoService);
  protected vehiculoFormService = inject(VehiculoFormService);
  protected clienteService = inject(ClienteService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: VehiculoFormGroup = this.vehiculoFormService.createVehiculoFormGroup();

  compareCliente = (o1: ICliente | null, o2: ICliente | null): boolean => this.clienteService.compareCliente(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ vehiculo }) => {
      this.vehiculo = vehiculo;
      if (vehiculo) {
        this.updateForm(vehiculo);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const vehiculo = this.vehiculoFormService.getVehiculo(this.editForm);
    if (vehiculo.id === null) {
      this.subscribeToSaveResponse(this.vehiculoService.create(vehiculo));
    } else {
      this.subscribeToSaveResponse(this.vehiculoService.update(vehiculo));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IVehiculo | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(vehiculo: IVehiculo): void {
    this.vehiculo = vehiculo;
    this.vehiculoFormService.resetForm(this.editForm, vehiculo);

    this.clientesSharedCollection.update(clientes =>
      this.clienteService.addClienteToCollectionIfMissing<ICliente>(clientes, vehiculo.cliente),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.clienteService
      .query()
      .pipe(map((res: HttpResponse<ICliente[]>) => res.body ?? []))
      .pipe(map((clientes: ICliente[]) => this.clienteService.addClienteToCollectionIfMissing<ICliente>(clientes, this.vehiculo?.cliente)))
      .subscribe((clientes: ICliente[]) => this.clientesSharedCollection.set(clientes));
  }
}
