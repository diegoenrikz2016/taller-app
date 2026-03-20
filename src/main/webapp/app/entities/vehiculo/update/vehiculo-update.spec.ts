import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICliente } from 'app/entities/cliente/cliente.model';
import { ClienteService } from 'app/entities/cliente/service/cliente.service';
import { VehiculoService } from '../service/vehiculo.service';
import { IVehiculo } from '../vehiculo.model';

import { VehiculoFormService } from './vehiculo-form.service';
import { VehiculoUpdate } from './vehiculo-update';

describe('Vehiculo Management Update Component', () => {
  let comp: VehiculoUpdate;
  let fixture: ComponentFixture<VehiculoUpdate>;
  let activatedRoute: ActivatedRoute;
  let vehiculoFormService: VehiculoFormService;
  let vehiculoService: VehiculoService;
  let clienteService: ClienteService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(VehiculoUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    vehiculoFormService = TestBed.inject(VehiculoFormService);
    vehiculoService = TestBed.inject(VehiculoService);
    clienteService = TestBed.inject(ClienteService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Cliente query and add missing value', () => {
      const vehiculo: IVehiculo = { id: 25020 };
      const cliente: ICliente = { id: 13484 };
      vehiculo.cliente = cliente;

      const clienteCollection: ICliente[] = [{ id: 13484 }];
      vitest.spyOn(clienteService, 'query').mockReturnValue(of(new HttpResponse({ body: clienteCollection })));
      const additionalClientes = [cliente];
      const expectedCollection: ICliente[] = [...additionalClientes, ...clienteCollection];
      vitest.spyOn(clienteService, 'addClienteToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ vehiculo });
      comp.ngOnInit();

      expect(clienteService.query).toHaveBeenCalled();
      expect(clienteService.addClienteToCollectionIfMissing).toHaveBeenCalledWith(
        clienteCollection,
        ...additionalClientes.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.clientesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const vehiculo: IVehiculo = { id: 25020 };
      const cliente: ICliente = { id: 13484 };
      vehiculo.cliente = cliente;

      activatedRoute.data = of({ vehiculo });
      comp.ngOnInit();

      expect(comp.clientesSharedCollection()).toContainEqual(cliente);
      expect(comp.vehiculo).toEqual(vehiculo);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IVehiculo>();
      const vehiculo = { id: 32266 };
      vitest.spyOn(vehiculoFormService, 'getVehiculo').mockReturnValue(vehiculo);
      vitest.spyOn(vehiculoService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ vehiculo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(vehiculo);
      saveSubject.complete();

      // THEN
      expect(vehiculoFormService.getVehiculo).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(vehiculoService.update).toHaveBeenCalledWith(expect.objectContaining(vehiculo));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IVehiculo>();
      const vehiculo = { id: 32266 };
      vitest.spyOn(vehiculoFormService, 'getVehiculo').mockReturnValue({ id: null });
      vitest.spyOn(vehiculoService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ vehiculo: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(vehiculo);
      saveSubject.complete();

      // THEN
      expect(vehiculoFormService.getVehiculo).toHaveBeenCalled();
      expect(vehiculoService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IVehiculo>();
      const vehiculo = { id: 32266 };
      vitest.spyOn(vehiculoService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ vehiculo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(vehiculoService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCliente', () => {
      it('should forward to clienteService', () => {
        const entity = { id: 13484 };
        const entity2 = { id: 20795 };
        vitest.spyOn(clienteService, 'compareCliente');
        comp.compareCliente(entity, entity2);
        expect(clienteService.compareCliente).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
