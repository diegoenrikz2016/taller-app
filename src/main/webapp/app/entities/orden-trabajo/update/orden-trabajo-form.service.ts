import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IOrdenTrabajo, NewOrdenTrabajo } from '../orden-trabajo.model';

type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };
type OrdenTrabajoFormGroupInput = IOrdenTrabajo | PartialWithRequiredKeyOf<NewOrdenTrabajo>;
type OrdenTrabajoFormDefaults = Pick<NewOrdenTrabajo, 'id'>;

type OrdenTrabajoFormGroupContent = {
  id: FormControl<IOrdenTrabajo['id'] | NewOrdenTrabajo['id']>;
  fecha: FormControl<IOrdenTrabajo['fecha']>;
  estado: FormControl<IOrdenTrabajo['estado']>;
  observaciones: FormControl<IOrdenTrabajo['observaciones']>;
  mecanico: FormControl<IOrdenTrabajo['mecanico']>;
  valorPactado: FormControl<IOrdenTrabajo['valorPactado']>;
  abono: FormControl<IOrdenTrabajo['abono']>;
  saldo: FormControl<IOrdenTrabajo['saldo']>;
  trabajosExtras: FormControl<IOrdenTrabajo['trabajosExtras']>;
  vehiculo: FormControl<IOrdenTrabajo['vehiculo']>;
};

export type OrdenTrabajoFormGroup = FormGroup<OrdenTrabajoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class OrdenTrabajoFormService {
  createOrdenTrabajoFormGroup(ordenTrabajo?: OrdenTrabajoFormGroupInput): OrdenTrabajoFormGroup {
    const raw = { ...this.getFormDefaults(), ...(ordenTrabajo ?? { id: null }) };
    return new FormGroup<OrdenTrabajoFormGroupContent>({
      id: new FormControl({ value: raw.id, disabled: true }, { nonNullable: true, validators: [Validators.required] }),
      fecha: new FormControl(raw.fecha, { validators: [Validators.required] }),
      estado: new FormControl(raw.estado, { validators: [Validators.required] }),
      observaciones: new FormControl(raw.observaciones),
      mecanico: new FormControl(raw.mecanico),
      valorPactado: new FormControl(raw.valorPactado),
      abono: new FormControl(raw.abono),
      saldo: new FormControl({ value: raw.saldo, disabled: true }), // calculado
      trabajosExtras: new FormControl(raw.trabajosExtras),
      vehiculo: new FormControl(raw.vehiculo),
    });
  }

  getOrdenTrabajo(form: OrdenTrabajoFormGroup): IOrdenTrabajo | NewOrdenTrabajo {
    return form.getRawValue() as IOrdenTrabajo | NewOrdenTrabajo;
  }

  resetForm(form: OrdenTrabajoFormGroup, ordenTrabajo: OrdenTrabajoFormGroupInput): void {
    const raw = { ...this.getFormDefaults(), ...ordenTrabajo };
    form.reset({ ...raw, id: { value: raw.id, disabled: true } });
  }

  private getFormDefaults(): OrdenTrabajoFormDefaults {
    return { id: null };
  }
}
