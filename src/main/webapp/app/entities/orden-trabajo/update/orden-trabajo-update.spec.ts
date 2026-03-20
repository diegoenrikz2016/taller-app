import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { VehiculoService } from 'app/entities/vehiculo/service/vehiculo.service';
import { IVehiculo } from 'app/entities/vehiculo/vehiculo.model';
import { IOrdenTrabajo } from '../orden-trabajo.model';
import { OrdenTrabajoService } from '../service/orden-trabajo.service';

import { OrdenTrabajoFormService } from './orden-trabajo-form.service';
import { OrdenTrabajoUpdate } from './orden-trabajo-update';

describe('OrdenTrabajo Management Update Component', () => {
  let comp: OrdenTrabajoUpdate;
  let fixture: ComponentFixture<OrdenTrabajoUpdate>;
  let activatedRoute: ActivatedRoute;
  let ordenTrabajoFormService: OrdenTrabajoFormService;
  let ordenTrabajoService: OrdenTrabajoService;
  let vehiculoService: VehiculoService;

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

    fixture = TestBed.createComponent(OrdenTrabajoUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ordenTrabajoFormService = TestBed.inject(OrdenTrabajoFormService);
    ordenTrabajoService = TestBed.inject(OrdenTrabajoService);
    vehiculoService = TestBed.inject(VehiculoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Vehiculo query and add missing value', () => {
      const ordenTrabajo: IOrdenTrabajo = { id: 22981 };
      const vehiculo: IVehiculo = { id: 32266 };
      ordenTrabajo.vehiculo = vehiculo;

      const vehiculoCollection: IVehiculo[] = [{ id: 32266 }];
      vitest.spyOn(vehiculoService, 'query').mockReturnValue(of(new HttpResponse({ body: vehiculoCollection })));
      const additionalVehiculos = [vehiculo];
      const expectedCollection: IVehiculo[] = [...additionalVehiculos, ...vehiculoCollection];
      vitest.spyOn(vehiculoService, 'addVehiculoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ordenTrabajo });
      comp.ngOnInit();

      expect(vehiculoService.query).toHaveBeenCalled();
      expect(vehiculoService.addVehiculoToCollectionIfMissing).toHaveBeenCalledWith(
        vehiculoCollection,
        ...additionalVehiculos.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.vehiculosSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const ordenTrabajo: IOrdenTrabajo = { id: 22981 };
      const vehiculo: IVehiculo = { id: 32266 };
      ordenTrabajo.vehiculo = vehiculo;

      activatedRoute.data = of({ ordenTrabajo });
      comp.ngOnInit();

      expect(comp.vehiculosSharedCollection()).toContainEqual(vehiculo);
      expect(comp.ordenTrabajo).toEqual(ordenTrabajo);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IOrdenTrabajo>();
      const ordenTrabajo = { id: 14189 };
      vitest.spyOn(ordenTrabajoFormService, 'getOrdenTrabajo').mockReturnValue(ordenTrabajo);
      vitest.spyOn(ordenTrabajoService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ordenTrabajo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(ordenTrabajo);
      saveSubject.complete();

      // THEN
      expect(ordenTrabajoFormService.getOrdenTrabajo).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(ordenTrabajoService.update).toHaveBeenCalledWith(expect.objectContaining(ordenTrabajo));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IOrdenTrabajo>();
      const ordenTrabajo = { id: 14189 };
      vitest.spyOn(ordenTrabajoFormService, 'getOrdenTrabajo').mockReturnValue({ id: null });
      vitest.spyOn(ordenTrabajoService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ordenTrabajo: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(ordenTrabajo);
      saveSubject.complete();

      // THEN
      expect(ordenTrabajoFormService.getOrdenTrabajo).toHaveBeenCalled();
      expect(ordenTrabajoService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IOrdenTrabajo>();
      const ordenTrabajo = { id: 14189 };
      vitest.spyOn(ordenTrabajoService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ordenTrabajo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(ordenTrabajoService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareVehiculo', () => {
      it('should forward to vehiculoService', () => {
        const entity = { id: 32266 };
        const entity2 = { id: 25020 };
        vitest.spyOn(vehiculoService, 'compareVehiculo');
        comp.compareVehiculo(entity, entity2);
        expect(vehiculoService.compareVehiculo).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
