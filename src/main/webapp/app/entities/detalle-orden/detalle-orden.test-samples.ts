import { IDetalleOrden, NewDetalleOrden } from './detalle-orden.model';

export const sampleWithRequiredData: IDetalleOrden = {
  id: 32270,
  descripcion: 'insidious cinema electrify',
  cantidad: 16634,
  precio: 22416.68,
};

export const sampleWithPartialData: IDetalleOrden = {
  id: 3557,
  descripcion: 'rotating a roughly',
  cantidad: 10940,
  precio: 22583.92,
};

export const sampleWithFullData: IDetalleOrden = {
  id: 32649,
  descripcion: 'trusty yippee',
  cantidad: 9860,
  precio: 25263.78,
};

export const sampleWithNewData: NewDetalleOrden = {
  descripcion: 'even',
  cantidad: 31436,
  precio: 20647.44,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
