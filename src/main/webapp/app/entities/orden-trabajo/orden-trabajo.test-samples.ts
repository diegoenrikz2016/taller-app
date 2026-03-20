import dayjs from 'dayjs/esm';

import { IOrdenTrabajo, NewOrdenTrabajo } from './orden-trabajo.model';

export const sampleWithRequiredData: IOrdenTrabajo = {
  id: 5347,
  fecha: dayjs('2026-03-19'),
  estado: 'EN_PROCESO',
};

export const sampleWithPartialData: IOrdenTrabajo = {
  id: 31006,
  fecha: dayjs('2026-03-19'),
  estado: 'EN_PROCESO',
  manoObra: 16787.93,
  subtotal: 24300.68,
  total: 21424.58,
};

export const sampleWithFullData: IOrdenTrabajo = {
  id: 11309,
  fecha: dayjs('2026-03-19'),
  estado: 'FINALIZADO',
  observaciones: 'inasmuch who',
  mecanico: 'place boo',
  manoObra: 23804.45,
  subtotal: 3943.31,
  total: 11737.94,
};

export const sampleWithNewData: NewOrdenTrabajo = {
  fecha: dayjs('2026-03-19'),
  estado: 'EN_PROCESO',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
