import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IDetalleOrden, NewDetalleOrden } from '../detalle-orden.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IDetalleOrden for edit and NewDetalleOrdenFormGroupInput for create.
 */
type DetalleOrdenFormGroupInput = IDetalleOrden | PartialWithRequiredKeyOf<NewDetalleOrden>;

type DetalleOrdenFormDefaults = Pick<NewDetalleOrden, 'id'>;

type DetalleOrdenFormGroupContent = {
  id: FormControl<IDetalleOrden['id'] | NewDetalleOrden['id']>;
  descripcion: FormControl<IDetalleOrden['descripcion']>;
  cantidad: FormControl<IDetalleOrden['cantidad']>;
  precio: FormControl<IDetalleOrden['precio']>;
  ordenTrabajo: FormControl<IDetalleOrden['ordenTrabajo']>;
};

export type DetalleOrdenFormGroup = FormGroup<DetalleOrdenFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class DetalleOrdenFormService {
  createDetalleOrdenFormGroup(detalleOrden?: DetalleOrdenFormGroupInput): DetalleOrdenFormGroup {
    const detalleOrdenRawValue = {
      ...this.getFormDefaults(),
      ...(detalleOrden ?? { id: null }),
    };
    return new FormGroup<DetalleOrdenFormGroupContent>({
      id: new FormControl(
        { value: detalleOrdenRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      descripcion: new FormControl(detalleOrdenRawValue.descripcion, {
        validators: [Validators.required],
      }),
      cantidad: new FormControl(detalleOrdenRawValue.cantidad, {
        validators: [Validators.required],
      }),
      precio: new FormControl(detalleOrdenRawValue.precio, {
        validators: [Validators.required],
      }),
      ordenTrabajo: new FormControl(detalleOrdenRawValue.ordenTrabajo),
    });
  }

  getDetalleOrden(form: DetalleOrdenFormGroup): IDetalleOrden | NewDetalleOrden {
    return form.getRawValue() as IDetalleOrden | NewDetalleOrden;
  }

  resetForm(form: DetalleOrdenFormGroup, detalleOrden: DetalleOrdenFormGroupInput): void {
    const detalleOrdenRawValue = { ...this.getFormDefaults(), ...detalleOrden };
    form.reset({
      ...detalleOrdenRawValue,
      id: { value: detalleOrdenRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): DetalleOrdenFormDefaults {
    return {
      id: null,
    };
  }
}
