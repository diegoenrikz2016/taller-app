import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ICliente, NewCliente } from '../cliente.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICliente for edit and NewClienteFormGroupInput for create.
 */
type ClienteFormGroupInput = ICliente | PartialWithRequiredKeyOf<NewCliente>;

type ClienteFormDefaults = Pick<NewCliente, 'id'>;

type ClienteFormGroupContent = {
  id: FormControl<ICliente['id'] | NewCliente['id']>;
  nombre: FormControl<ICliente['nombre']>;
  cedula: FormControl<ICliente['cedula']>;
  telefono: FormControl<ICliente['telefono']>;
  direccion: FormControl<ICliente['direccion']>;
};

export type ClienteFormGroup = FormGroup<ClienteFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ClienteFormService {
  createClienteFormGroup(cliente?: ClienteFormGroupInput): ClienteFormGroup {
    const clienteRawValue = {
      ...this.getFormDefaults(),
      ...(cliente ?? { id: null }),
    };
    return new FormGroup<ClienteFormGroupContent>({
      id: new FormControl(
        { value: clienteRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nombre: new FormControl(clienteRawValue.nombre, {
        validators: [Validators.required],
      }),
      cedula: new FormControl(clienteRawValue.cedula),
      telefono: new FormControl(clienteRawValue.telefono),
      direccion: new FormControl(clienteRawValue.direccion),
    });
  }

  getCliente(form: ClienteFormGroup): ICliente | NewCliente {
    return form.getRawValue() as ICliente | NewCliente;
  }

  resetForm(form: ClienteFormGroup, cliente: ClienteFormGroupInput): void {
    const clienteRawValue = { ...this.getFormDefaults(), ...cliente };
    form.reset({
      ...clienteRawValue,
      id: { value: clienteRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ClienteFormDefaults {
    return {
      id: null,
    };
  }
}
