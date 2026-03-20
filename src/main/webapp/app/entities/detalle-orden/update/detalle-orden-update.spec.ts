import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IOrdenTrabajo } from 'app/entities/orden-trabajo/orden-trabajo.model';
import { OrdenTrabajoService } from 'app/entities/orden-trabajo/service/orden-trabajo.service';
import { IDetalleOrden } from '../detalle-orden.model';
import { DetalleOrdenService } from '../service/detalle-orden.service';

import { DetalleOrdenFormService } from './detalle-orden-form.service';
import { DetalleOrdenUpdate } from './detalle-orden-update';

describe('DetalleOrden Management Update Component', () => {
  let comp: DetalleOrdenUpdate;
  let fixture: ComponentFixture<DetalleOrdenUpdate>;
  let activatedRoute: ActivatedRoute;
  let detalleOrdenFormService: DetalleOrdenFormService;
  let detalleOrdenService: DetalleOrdenService;
  let ordenTrabajoService: OrdenTrabajoService;

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

    fixture = TestBed.createComponent(DetalleOrdenUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    detalleOrdenFormService = TestBed.inject(DetalleOrdenFormService);
    detalleOrdenService = TestBed.inject(DetalleOrdenService);
    ordenTrabajoService = TestBed.inject(OrdenTrabajoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call OrdenTrabajo query and add missing value', () => {
      const detalleOrden: IDetalleOrden = { id: 17841 };
      const ordenTrabajo: IOrdenTrabajo = { id: 14189 };
      detalleOrden.ordenTrabajo = ordenTrabajo;

      const ordenTrabajoCollection: IOrdenTrabajo[] = [{ id: 14189 }];
      vitest.spyOn(ordenTrabajoService, 'query').mockReturnValue(of(new HttpResponse({ body: ordenTrabajoCollection })));
      const additionalOrdenTrabajos = [ordenTrabajo];
      const expectedCollection: IOrdenTrabajo[] = [...additionalOrdenTrabajos, ...ordenTrabajoCollection];
      vitest.spyOn(ordenTrabajoService, 'addOrdenTrabajoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ detalleOrden });
      comp.ngOnInit();

      expect(ordenTrabajoService.query).toHaveBeenCalled();
      expect(ordenTrabajoService.addOrdenTrabajoToCollectionIfMissing).toHaveBeenCalledWith(
        ordenTrabajoCollection,
        ...additionalOrdenTrabajos.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.ordenTrabajosSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const detalleOrden: IDetalleOrden = { id: 17841 };
      const ordenTrabajo: IOrdenTrabajo = { id: 14189 };
      detalleOrden.ordenTrabajo = ordenTrabajo;

      activatedRoute.data = of({ detalleOrden });
      comp.ngOnInit();

      expect(comp.ordenTrabajosSharedCollection()).toContainEqual(ordenTrabajo);
      expect(comp.detalleOrden).toEqual(detalleOrden);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IDetalleOrden>();
      const detalleOrden = { id: 21649 };
      vitest.spyOn(detalleOrdenFormService, 'getDetalleOrden').mockReturnValue(detalleOrden);
      vitest.spyOn(detalleOrdenService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ detalleOrden });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(detalleOrden);
      saveSubject.complete();

      // THEN
      expect(detalleOrdenFormService.getDetalleOrden).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(detalleOrdenService.update).toHaveBeenCalledWith(expect.objectContaining(detalleOrden));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IDetalleOrden>();
      const detalleOrden = { id: 21649 };
      vitest.spyOn(detalleOrdenFormService, 'getDetalleOrden').mockReturnValue({ id: null });
      vitest.spyOn(detalleOrdenService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ detalleOrden: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(detalleOrden);
      saveSubject.complete();

      // THEN
      expect(detalleOrdenFormService.getDetalleOrden).toHaveBeenCalled();
      expect(detalleOrdenService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IDetalleOrden>();
      const detalleOrden = { id: 21649 };
      vitest.spyOn(detalleOrdenService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ detalleOrden });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(detalleOrdenService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareOrdenTrabajo', () => {
      it('should forward to ordenTrabajoService', () => {
        const entity = { id: 14189 };
        const entity2 = { id: 22981 };
        vitest.spyOn(ordenTrabajoService, 'compareOrdenTrabajo');
        comp.compareOrdenTrabajo(entity, entity2);
        expect(ordenTrabajoService.compareOrdenTrabajo).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
