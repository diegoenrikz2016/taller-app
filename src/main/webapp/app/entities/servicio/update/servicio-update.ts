import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ServicioService } from '../service/servicio.service';
import { IServicio } from '../servicio.model';

import { ServicioFormGroup, ServicioFormService } from './servicio-form.service';

@Component({
  selector: 'jhi-servicio-update',
  templateUrl: './servicio-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ServicioUpdate implements OnInit {
  readonly isSaving = signal(false);
  servicio: IServicio | null = null;

  protected servicioService = inject(ServicioService);
  protected servicioFormService = inject(ServicioFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ServicioFormGroup = this.servicioFormService.createServicioFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ servicio }) => {
      this.servicio = servicio;
      if (servicio) {
        this.updateForm(servicio);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const servicio = this.servicioFormService.getServicio(this.editForm);
    if (servicio.id === null) {
      this.subscribeToSaveResponse(this.servicioService.create(servicio));
    } else {
      this.subscribeToSaveResponse(this.servicioService.update(servicio));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IServicio | null>): void {
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

  protected updateForm(servicio: IServicio): void {
    this.servicio = servicio;
    this.servicioFormService.resetForm(this.editForm, servicio);
  }
}
