import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbInputDatepicker } from '@ng-bootstrap/ng-bootstrap/datepicker';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter, finalize, map } from 'rxjs/operators';

import { EstadoOrden } from 'app/entities/enumerations/estado-orden.model';
import { VehiculoService } from 'app/entities/vehiculo/service/vehiculo.service';
import { IVehiculo } from 'app/entities/vehiculo/vehiculo.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IOrdenTrabajo } from '../orden-trabajo.model';
import { OrdenTrabajoService } from '../service/orden-trabajo.service';

import { OrdenTrabajoFormGroup, OrdenTrabajoFormService } from './orden-trabajo-form.service';

import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { debounceTime, switchMap } from 'rxjs/operators';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'jhi-orden-trabajo-update',
  templateUrl: './orden-trabajo-update.html',
  imports: [
    TranslateDirective,
    TranslateModule,
    FontAwesomeModule,
    AlertError,
    ReactiveFormsModule,
    NgbInputDatepicker,
    MatFormFieldModule,
    MatInputModule,
    MatAutocompleteModule,
    CommonModule,
  ],
})
export class OrdenTrabajoUpdate implements OnInit {
  readonly isSaving = signal(false);
  ordenTrabajo: IOrdenTrabajo | null = null;
  estadoOrdenValues = Object.keys(EstadoOrden);

  vehiculosSharedCollection = signal<IVehiculo[]>([]);

  protected ordenTrabajoService = inject(OrdenTrabajoService);
  protected ordenTrabajoFormService = inject(OrdenTrabajoFormService);
  protected vehiculoService = inject(VehiculoService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: OrdenTrabajoFormGroup = this.ordenTrabajoFormService.createOrdenTrabajoFormGroup();

  compareVehiculo = (o1: IVehiculo | null, o2: IVehiculo | null): boolean => this.vehiculoService.compareVehiculo(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ordenTrabajo }) => {
      this.ordenTrabajo = ordenTrabajo;
      if (ordenTrabajo) {
        this.updateForm(ordenTrabajo);
      }

      this.loadRelationshipsOptions();

      this.editForm.controls.vehiculo.valueChanges
        .pipe(
          debounceTime(300),
          filter(value => typeof value === 'string'),
          switchMap(value => this.vehiculoService.search(value)),
        )
        .subscribe((vehiculos: IVehiculo[]) => {
          this.vehiculosSharedCollection.set(vehiculos);
        });
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const ordenTrabajo = this.ordenTrabajoFormService.getOrdenTrabajo(this.editForm);
    if (ordenTrabajo.id === null) {
      this.subscribeToSaveResponse(this.ordenTrabajoService.create(ordenTrabajo));
    } else {
      this.subscribeToSaveResponse(this.ordenTrabajoService.update(ordenTrabajo));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IOrdenTrabajo | null>): void {
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

  protected updateForm(ordenTrabajo: IOrdenTrabajo): void {
    this.ordenTrabajo = ordenTrabajo;
    this.ordenTrabajoFormService.resetForm(this.editForm, ordenTrabajo);

    this.vehiculosSharedCollection.update(vehiculos =>
      this.vehiculoService.addVehiculoToCollectionIfMissing<IVehiculo>(vehiculos, ordenTrabajo.vehiculo),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.vehiculoService
      .query()
      .pipe(map((res: HttpResponse<IVehiculo[]>) => res.body ?? []))
      .pipe(
        map((vehiculos: IVehiculo[]) =>
          this.vehiculoService.addVehiculoToCollectionIfMissing<IVehiculo>(vehiculos, this.ordenTrabajo?.vehiculo),
        ),
      )
      .subscribe((vehiculos: IVehiculo[]) => this.vehiculosSharedCollection.set(vehiculos));
  }

  displayVehiculo(vehiculo: IVehiculo): string {
    return vehiculo ? `${vehiculo.marca} ${vehiculo.modelo} ${vehiculo.placa}` : '';
  }

  calcularSaldo(): void {
    const vp = parseFloat(String(this.editForm.get('valorPactado')?.value ?? 0)) || 0;
    const ab = parseFloat(String(this.editForm.get('abono')?.value ?? 0)) || 0;
    this.editForm.get('saldo')?.setValue(parseFloat((vp - ab).toFixed(2)));
  }
}
