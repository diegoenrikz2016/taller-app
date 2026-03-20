import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../detalle-orden.test-samples';

import { DetalleOrdenFormService } from './detalle-orden-form.service';

describe('DetalleOrden Form Service', () => {
  let service: DetalleOrdenFormService;

  beforeEach(() => {
    service = TestBed.inject(DetalleOrdenFormService);
  });

  describe('Service methods', () => {
    describe('createDetalleOrdenFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDetalleOrdenFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            descripcion: expect.any(Object),
            cantidad: expect.any(Object),
            precio: expect.any(Object),
            ordenTrabajo: expect.any(Object),
          }),
        );
      });

      it('passing IDetalleOrden should create a new form with FormGroup', () => {
        const formGroup = service.createDetalleOrdenFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            descripcion: expect.any(Object),
            cantidad: expect.any(Object),
            precio: expect.any(Object),
            ordenTrabajo: expect.any(Object),
          }),
        );
      });
    });

    describe('getDetalleOrden', () => {
      it('should return NewDetalleOrden for default DetalleOrden initial value', () => {
        const formGroup = service.createDetalleOrdenFormGroup(sampleWithNewData);

        const detalleOrden = service.getDetalleOrden(formGroup);

        expect(detalleOrden).toMatchObject(sampleWithNewData);
      });

      it('should return NewDetalleOrden for empty DetalleOrden initial value', () => {
        const formGroup = service.createDetalleOrdenFormGroup();

        const detalleOrden = service.getDetalleOrden(formGroup);

        expect(detalleOrden).toMatchObject({});
      });

      it('should return IDetalleOrden', () => {
        const formGroup = service.createDetalleOrdenFormGroup(sampleWithRequiredData);

        const detalleOrden = service.getDetalleOrden(formGroup);

        expect(detalleOrden).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDetalleOrden should not enable id FormControl', () => {
        const formGroup = service.createDetalleOrdenFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDetalleOrden should disable id FormControl', () => {
        const formGroup = service.createDetalleOrdenFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
