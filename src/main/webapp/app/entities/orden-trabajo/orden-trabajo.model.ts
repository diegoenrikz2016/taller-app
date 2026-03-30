import dayjs from 'dayjs/esm';

import { EstadoOrden } from 'app/entities/enumerations/estado-orden.model';
import { IVehiculo } from 'app/entities/vehiculo/vehiculo.model';

export interface IOrdenTrabajo {
  id: number;
  fecha?: dayjs.Dayjs | null;
  estado?: keyof typeof EstadoOrden | null;
  observaciones?: string | null;
  mecanico?: string | null;
  manoObra?: number | null;
  subtotal?: number | null;
  total?: number | null;
  vehiculo?: IVehiculo | null;
}

export type NewOrdenTrabajo = Omit<IOrdenTrabajo, 'id'> & { id: null };
