import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../vehiculo.test-samples';

import { VehiculoFormService } from './vehiculo-form.service';

describe('Vehiculo Form Service', () => {
  let service: VehiculoFormService;

  beforeEach(() => {
    service = TestBed.inject(VehiculoFormService);
  });

  describe('Service methods', () => {
    describe('createVehiculoFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createVehiculoFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            placa: expect.any(Object),
            marca: expect.any(Object),
            modelo: expect.any(Object),
            color: expect.any(Object),
            cliente: expect.any(Object),
          }),
        );
      });

      it('passing IVehiculo should create a new form with FormGroup', () => {
        const formGroup = service.createVehiculoFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            placa: expect.any(Object),
            marca: expect.any(Object),
            modelo: expect.any(Object),
            color: expect.any(Object),
            cliente: expect.any(Object),
          }),
        );
      });
    });

    describe('getVehiculo', () => {
      it('should return NewVehiculo for default Vehiculo initial value', () => {
        const formGroup = service.createVehiculoFormGroup(sampleWithNewData);

        const vehiculo = service.getVehiculo(formGroup);

        expect(vehiculo).toMatchObject(sampleWithNewData);
      });

      it('should return NewVehiculo for empty Vehiculo initial value', () => {
        const formGroup = service.createVehiculoFormGroup();

        const vehiculo = service.getVehiculo(formGroup);

        expect(vehiculo).toMatchObject({});
      });

      it('should return IVehiculo', () => {
        const formGroup = service.createVehiculoFormGroup(sampleWithRequiredData);

        const vehiculo = service.getVehiculo(formGroup);

        expect(vehiculo).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IVehiculo should not enable id FormControl', () => {
        const formGroup = service.createVehiculoFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewVehiculo should disable id FormControl', () => {
        const formGroup = service.createVehiculoFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
