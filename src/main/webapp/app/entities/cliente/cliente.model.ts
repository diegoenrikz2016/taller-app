export interface ICliente {
  id: number;
  nombre?: string | null;
  cedula?: string | null;
  telefono?: string | null;
  direccion?: string | null;
  email?: string | null;
}

export type NewCliente = Omit<ICliente, 'id'> & { id: null };
