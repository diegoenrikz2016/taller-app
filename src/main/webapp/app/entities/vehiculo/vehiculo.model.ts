import { ICliente } from 'app/entities/cliente/cliente.model';

export interface IVehiculo {
  id: number;
  placa?: string | null;
  marca?: string | null;
  modelo?: string | null;
  color?: string | null;
  cliente?: ICliente | null;
}

export type NewVehiculo = Omit<IVehiculo, 'id'> & { id: null };
