import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { filter, finalize, map } from 'rxjs/operators';

import { IOrdenTrabajo } from 'app/entities/orden-trabajo/orden-trabajo.model';
import { OrdenTrabajoService } from 'app/entities/orden-trabajo/service/orden-trabajo.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IDetalleOrden } from '../detalle-orden.model';
import { DetalleOrdenService } from '../service/detalle-orden.service';

import { DetalleOrdenFormGroup, DetalleOrdenFormService } from './detalle-orden-form.service';

import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { debounceTime, switchMap } from 'rxjs/operators';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'jhi-detalle-orden-update',
  templateUrl: './detalle-orden-update.html',
  imports: [
    TranslateDirective,
    TranslateModule,
    FontAwesomeModule,
    AlertError,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatAutocompleteModule,
    CommonModule,
  ],
})
export class DetalleOrdenUpdate implements OnInit {
  readonly isSaving = signal(false);
  detalleOrden: IDetalleOrden | null = null;

  ordenTrabajosSharedCollection = signal<IOrdenTrabajo[]>([]);

  ordenesSharedCollection = signal<IOrdenTrabajo[]>([]);

  protected detalleOrdenService = inject(DetalleOrdenService);
  protected detalleOrdenFormService = inject(DetalleOrdenFormService);
  protected ordenTrabajoService = inject(OrdenTrabajoService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: DetalleOrdenFormGroup = this.detalleOrdenFormService.createDetalleOrdenFormGroup();

  compareOrdenTrabajo = (o1: IOrdenTrabajo | null, o2: IOrdenTrabajo | null): boolean =>
    this.ordenTrabajoService.compareOrdenTrabajo(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ detalleOrden }) => {
      this.detalleOrden = detalleOrden;
      if (detalleOrden) {
        this.updateForm(detalleOrden);
      }

      this.loadRelationshipsOptions();

      this.editForm.controls.ordenTrabajo.valueChanges
        .pipe(
          debounceTime(300),
          filter(value => typeof value === 'string'),
          switchMap(value => this.ordenTrabajoService.search(value)),
        )
        .subscribe((ordenes: IOrdenTrabajo[]) => {
          this.ordenesSharedCollection.set(ordenes);
        });
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const detalleOrden = this.detalleOrdenFormService.getDetalleOrden(this.editForm);
    if (detalleOrden.id === null) {
      this.subscribeToSaveResponse(this.detalleOrdenService.create(detalleOrden));
    } else {
      this.subscribeToSaveResponse(this.detalleOrdenService.update(detalleOrden));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IDetalleOrden | null>): void {
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

  protected updateForm(detalleOrden: IDetalleOrden): void {
    this.detalleOrden = detalleOrden;
    this.detalleOrdenFormService.resetForm(this.editForm, detalleOrden);

    this.ordenTrabajosSharedCollection.update(ordenTrabajos =>
      this.ordenTrabajoService.addOrdenTrabajoToCollectionIfMissing<IOrdenTrabajo>(ordenTrabajos, detalleOrden.ordenTrabajo),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.ordenTrabajoService
      .query()
      .pipe(map((res: HttpResponse<IOrdenTrabajo[]>) => res.body ?? []))
      .pipe(
        map((ordenTrabajos: IOrdenTrabajo[]) =>
          this.ordenTrabajoService.addOrdenTrabajoToCollectionIfMissing<IOrdenTrabajo>(ordenTrabajos, this.detalleOrden?.ordenTrabajo),
        ),
      )
      .subscribe((ordenTrabajos: IOrdenTrabajo[]) => this.ordenTrabajosSharedCollection.set(ordenTrabajos));
  }

  displayOrden(orden: IOrdenTrabajo): string {
    return orden ? `#${orden.id} - ${orden.vehiculo?.placa ?? ''}` : '';
  }
}
