import { IOrdenTrabajo } from 'app/entities/orden-trabajo/orden-trabajo.model';

export interface IDetalleOrden {
  id: number;
  descripcion?: string | null;
  cantidad?: number | null;
  precio?: number | null;
  ordenTrabajo?: IOrdenTrabajo | null;
}

export type NewDetalleOrden = Omit<IDetalleOrden, 'id'> & { id: null };
