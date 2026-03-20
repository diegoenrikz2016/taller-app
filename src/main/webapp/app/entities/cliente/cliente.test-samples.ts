import { ICliente, NewCliente } from './cliente.model';

export const sampleWithRequiredData: ICliente = {
  id: 2897,
  nombre: 'wherever alongside per',
};

export const sampleWithPartialData: ICliente = {
  id: 28503,
  nombre: 'boo bob',
  telefono: 'veg whoa',
  direccion: 'ugh nearly',
};

export const sampleWithFullData: ICliente = {
  id: 17129,
  nombre: 'circumference',
  cedula: 'categorise council',
  telefono: 'blah reluctantly than',
  direccion: 'afore consequently',
};

export const sampleWithNewData: NewCliente = {
  nombre: 'under',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
