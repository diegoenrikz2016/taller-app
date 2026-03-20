import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IOrdenTrabajo, NewOrdenTrabajo } from '../orden-trabajo.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IOrdenTrabajo for edit and NewOrdenTrabajoFormGroupInput for create.
 */
type OrdenTrabajoFormGroupInput = IOrdenTrabajo | PartialWithRequiredKeyOf<NewOrdenTrabajo>;

type OrdenTrabajoFormDefaults = Pick<NewOrdenTrabajo, 'id'>;

type OrdenTrabajoFormGroupContent = {
  id: FormControl<IOrdenTrabajo['id'] | NewOrdenTrabajo['id']>;
  fecha: FormControl<IOrdenTrabajo['fecha']>;
  estado: FormControl<IOrdenTrabajo['estado']>;
  observaciones: FormControl<IOrdenTrabajo['observaciones']>;
  mecanico: FormControl<IOrdenTrabajo['mecanico']>;
  manoObra: FormControl<IOrdenTrabajo['manoObra']>;
  subtotal: FormControl<IOrdenTrabajo['subtotal']>;
  total: FormControl<IOrdenTrabajo['total']>;
  vehiculo: FormControl<IOrdenTrabajo['vehiculo']>;
};

export type OrdenTrabajoFormGroup = FormGroup<OrdenTrabajoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class OrdenTrabajoFormService {
  createOrdenTrabajoFormGroup(ordenTrabajo?: OrdenTrabajoFormGroupInput): OrdenTrabajoFormGroup {
    const ordenTrabajoRawValue = {
      ...this.getFormDefaults(),
      ...(ordenTrabajo ?? { id: null }),
    };
    return new FormGroup<OrdenTrabajoFormGroupContent>({
      id: new FormControl(
        { value: ordenTrabajoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      fecha: new FormControl(ordenTrabajoRawValue.fecha, {
        validators: [Validators.required],
      }),
      estado: new FormControl(ordenTrabajoRawValue.estado, {
        validators: [Validators.required],
      }),
      observaciones: new FormControl(ordenTrabajoRawValue.observaciones),
      mecanico: new FormControl(ordenTrabajoRawValue.mecanico),
      manoObra: new FormControl(ordenTrabajoRawValue.manoObra),
      subtotal: new FormControl(ordenTrabajoRawValue.subtotal),
      total: new FormControl(ordenTrabajoRawValue.total),
      vehiculo: new FormControl(ordenTrabajoRawValue.vehiculo),
    });
  }

  getOrdenTrabajo(form: OrdenTrabajoFormGroup): IOrdenTrabajo | NewOrdenTrabajo {
    return form.getRawValue() as IOrdenTrabajo | NewOrdenTrabajo;
  }

  resetForm(form: OrdenTrabajoFormGroup, ordenTrabajo: OrdenTrabajoFormGroupInput): void {
    const ordenTrabajoRawValue = { ...this.getFormDefaults(), ...ordenTrabajo };
    form.reset({
      ...ordenTrabajoRawValue,
      id: { value: ordenTrabajoRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): OrdenTrabajoFormDefaults {
    return {
      id: null,
    };
  }
}
