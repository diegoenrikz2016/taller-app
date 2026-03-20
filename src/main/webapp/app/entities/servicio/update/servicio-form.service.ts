import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IServicio, NewServicio } from '../servicio.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IServicio for edit and NewServicioFormGroupInput for create.
 */
type ServicioFormGroupInput = IServicio | PartialWithRequiredKeyOf<NewServicio>;

type ServicioFormDefaults = Pick<NewServicio, 'id'>;

type ServicioFormGroupContent = {
  id: FormControl<IServicio['id'] | NewServicio['id']>;
  nombre: FormControl<IServicio['nombre']>;
  precio: FormControl<IServicio['precio']>;
};

export type ServicioFormGroup = FormGroup<ServicioFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ServicioFormService {
  createServicioFormGroup(servicio?: ServicioFormGroupInput): ServicioFormGroup {
    const servicioRawValue = {
      ...this.getFormDefaults(),
      ...(servicio ?? { id: null }),
    };
    return new FormGroup<ServicioFormGroupContent>({
      id: new FormControl(
        { value: servicioRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nombre: new FormControl(servicioRawValue.nombre, {
        validators: [Validators.required],
      }),
      precio: new FormControl(servicioRawValue.precio, {
        validators: [Validators.required],
      }),
    });
  }

  getServicio(form: ServicioFormGroup): IServicio | NewServicio {
    return form.getRawValue() as IServicio | NewServicio;
  }

  resetForm(form: ServicioFormGroup, servicio: ServicioFormGroupInput): void {
    const servicioRawValue = { ...this.getFormDefaults(), ...servicio };
    form.reset({
      ...servicioRawValue,
      id: { value: servicioRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ServicioFormDefaults {
    return {
      id: null,
    };
  }
}
