import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ICliente } from '../cliente.model';
import { ClienteService } from '../service/cliente.service';

import { ClienteFormGroup, ClienteFormService } from './cliente-form.service';

@Component({
  selector: 'jhi-cliente-update',
  templateUrl: './cliente-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ClienteUpdate implements OnInit {
  readonly isSaving = signal(false);
  cliente: ICliente | null = null;

  protected clienteService = inject(ClienteService);
  protected clienteFormService = inject(ClienteFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ClienteFormGroup = this.clienteFormService.createClienteFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cliente }) => {
      this.cliente = cliente;
      if (cliente) {
        this.updateForm(cliente);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const cliente = this.clienteFormService.getCliente(this.editForm);
    if (cliente.id === null) {
      this.subscribeToSaveResponse(this.clienteService.create(cliente));
    } else {
      this.subscribeToSaveResponse(this.clienteService.update(cliente));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ICliente | null>): void {
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

  protected updateForm(cliente: ICliente): void {
    this.cliente = cliente;
    this.clienteFormService.resetForm(this.editForm, cliente);
  }
}
