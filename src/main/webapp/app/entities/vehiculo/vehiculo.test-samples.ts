import { IVehiculo, NewVehiculo } from './vehiculo.model';

export const sampleWithRequiredData: IVehiculo = {
  id: 24629,
  placa: 'honored',
};

export const sampleWithPartialData: IVehiculo = {
  id: 11344,
  placa: 'sequester devoted without',
  marca: 'bolster',
  color: 'maroon',
};

export const sampleWithFullData: IVehiculo = {
  id: 29796,
  placa: 'gee',
  marca: 'optimistic ack',
  modelo: 'innovation',
  color: 'grey',
};

export const sampleWithNewData: NewVehiculo = {
  placa: 'yearly',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
