import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IDetalleOrden } from '../detalle-orden.model';

@Component({
  selector: 'jhi-detalle-orden-detail',
  templateUrl: './detalle-orden-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink],
})
export class DetalleOrdenDetail {
  readonly detalleOrden = input<IDetalleOrden | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
