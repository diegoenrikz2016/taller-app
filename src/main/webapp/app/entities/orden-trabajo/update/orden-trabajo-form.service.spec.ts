import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../orden-trabajo.test-samples';

import { OrdenTrabajoFormService } from './orden-trabajo-form.service';

describe('OrdenTrabajo Form Service', () => {
  let service: OrdenTrabajoFormService;

  beforeEach(() => {
    service = TestBed.inject(OrdenTrabajoFormService);
  });

  describe('Service methods', () => {
    describe('createOrdenTrabajoFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createOrdenTrabajoFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fecha: expect.any(Object),
            estado: expect.any(Object),
            observaciones: expect.any(Object),
            mecanico: expect.any(Object),
            manoObra: expect.any(Object),
            subtotal: expect.any(Object),
            total: expect.any(Object),
            vehiculo: expect.any(Object),
          }),
        );
      });

      it('passing IOrdenTrabajo should create a new form with FormGroup', () => {
        const formGroup = service.createOrdenTrabajoFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fecha: expect.any(Object),
            estado: expect.any(Object),
            observaciones: expect.any(Object),
            mecanico: expect.any(Object),
            manoObra: expect.any(Object),
            subtotal: expect.any(Object),
            total: expect.any(Object),
            vehiculo: expect.any(Object),
          }),
        );
      });
    });

    describe('getOrdenTrabajo', () => {
      it('should return NewOrdenTrabajo for default OrdenTrabajo initial value', () => {
        const formGroup = service.createOrdenTrabajoFormGroup(sampleWithNewData);

        const ordenTrabajo = service.getOrdenTrabajo(formGroup);

        expect(ordenTrabajo).toMatchObject(sampleWithNewData);
      });

      it('should return NewOrdenTrabajo for empty OrdenTrabajo initial value', () => {
        const formGroup = service.createOrdenTrabajoFormGroup();

        const ordenTrabajo = service.getOrdenTrabajo(formGroup);

        expect(ordenTrabajo).toMatchObject({});
      });

      it('should return IOrdenTrabajo', () => {
        const formGroup = service.createOrdenTrabajoFormGroup(sampleWithRequiredData);

        const ordenTrabajo = service.getOrdenTrabajo(formGroup);

        expect(ordenTrabajo).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IOrdenTrabajo should not enable id FormControl', () => {
        const formGroup = service.createOrdenTrabajoFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewOrdenTrabajo should disable id FormControl', () => {
        const formGroup = service.createOrdenTrabajoFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
