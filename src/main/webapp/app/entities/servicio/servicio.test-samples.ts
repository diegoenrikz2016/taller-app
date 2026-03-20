import { IServicio, NewServicio } from './servicio.model';

export const sampleWithRequiredData: IServicio = {
  id: 27960,
  nombre: 'besides that reckon',
  precio: 30600.77,
};

export const sampleWithPartialData: IServicio = {
  id: 31565,
  nombre: 'incandescence rival',
  precio: 3393.44,
};

export const sampleWithFullData: IServicio = {
  id: 15992,
  nombre: 'gaseous total',
  precio: 29449.67,
};

export const sampleWithNewData: NewServicio = {
  nombre: 'absent',
  precio: 27735.81,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
