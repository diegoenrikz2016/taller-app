import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IVehiculo, NewVehiculo } from '../vehiculo.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IVehiculo for edit and NewVehiculoFormGroupInput for create.
 */
type VehiculoFormGroupInput = IVehiculo | PartialWithRequiredKeyOf<NewVehiculo>;

type VehiculoFormDefaults = Pick<NewVehiculo, 'id'>;

type VehiculoFormGroupContent = {
  id: FormControl<IVehiculo['id'] | NewVehiculo['id']>;
  placa: FormControl<IVehiculo['placa']>;
  marca: FormControl<IVehiculo['marca']>;
  modelo: FormControl<IVehiculo['modelo']>;
  color: FormControl<IVehiculo['color']>;
  cliente: FormControl<IVehiculo['cliente']>;
};

export type VehiculoFormGroup = FormGroup<VehiculoFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class VehiculoFormService {
  createVehiculoFormGroup(vehiculo?: VehiculoFormGroupInput): VehiculoFormGroup {
    const vehiculoRawValue = {
      ...this.getFormDefaults(),
      ...(vehiculo ?? { id: null }),
    };
    return new FormGroup<VehiculoFormGroupContent>({
      id: new FormControl(
        { value: vehiculoRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      placa: new FormControl(vehiculoRawValue.placa, {
        validators: [Validators.required],
      }),
      marca: new FormControl(vehiculoRawValue.marca),
      modelo: new FormControl(vehiculoRawValue.modelo),
      color: new FormControl(vehiculoRawValue.color),
      cliente: new FormControl(vehiculoRawValue.cliente),
    });
  }

  getVehiculo(form: VehiculoFormGroup): IVehiculo | NewVehiculo {
    return form.getRawValue() as IVehiculo | NewVehiculo;
  }

  resetForm(form: VehiculoFormGroup, vehiculo: VehiculoFormGroupInput): void {
    const vehiculoRawValue = { ...this.getFormDefaults(), ...vehiculo };
    form.reset({
      ...vehiculoRawValue,
      id: { value: vehiculoRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): VehiculoFormDefaults {
    return {
      id: null,
    };
  }
}
